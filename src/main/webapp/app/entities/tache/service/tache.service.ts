import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { ITache, NewTache } from '../tache.model';

export type PartialUpdateTache = Partial<ITache> & Pick<ITache, 'id'>;

type RestOf<T extends ITache | NewTache> = Omit<T, 'dateCreation' | 'dateAssignation' | 'dateEcheance' | 'dateTerminaison'> & {
  dateCreation?: string | null;
  dateAssignation?: string | null;
  dateEcheance?: string | null;
  dateTerminaison?: string | null;
};

export type RestTache = RestOf<ITache>;

export type NewRestTache = RestOf<NewTache>;

export type PartialUpdateRestTache = RestOf<PartialUpdateTache>;

@Service()
export class TachesService {
  readonly tachesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(undefined);
  readonly tachesResource = httpResource<RestTache[]>(() => {
    const params = this.tachesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of tache that have been fetched. It is updated when the tachesResource emits a new value.
   * In case of error while fetching the taches, the signal is set to an empty array.
   */
  readonly taches = computed(() =>
    (this.tachesResource.hasValue() ? this.tachesResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly resourceUrl = `${serverApiUrl}api/taches`;

  protected convertValueFromServer(restTache: RestTache): ITache {
    return {
      ...restTache,
      dateCreation: restTache.dateCreation ? dayjs(restTache.dateCreation) : undefined,
      dateAssignation: restTache.dateAssignation ? dayjs(restTache.dateAssignation) : undefined,
      dateEcheance: restTache.dateEcheance ? dayjs(restTache.dateEcheance) : undefined,
      dateTerminaison: restTache.dateTerminaison ? dayjs(restTache.dateTerminaison) : undefined,
    };
  }
}

@Service()
export class TacheService extends TachesService {
  protected readonly http = inject(HttpClient);

  create(tache: NewTache): Observable<ITache> {
    const copy = this.convertValueFromClient(tache);
    return this.http.post<RestTache>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(tache: ITache): Observable<ITache> {
    const copy = this.convertValueFromClient(tache);
    return this.http
      .put<RestTache>(`${this.resourceUrl}/${encodeURIComponent(this.getTacheIdentifier(tache))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(tache: PartialUpdateTache): Observable<ITache> {
    const copy = this.convertValueFromClient(tache);
    return this.http
      .patch<RestTache>(`${this.resourceUrl}/${encodeURIComponent(this.getTacheIdentifier(tache))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<ITache> {
    return this.http.get<RestTache>(`${this.resourceUrl}/${encodeURIComponent(id)}`).pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<ITache[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTache[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getTacheIdentifier(tache: Pick<ITache, 'id'>): number {
    return tache.id;
  }

  compareTache(o1: Pick<ITache, 'id'> | null, o2: Pick<ITache, 'id'> | null): boolean {
    return o1 && o2 ? this.getTacheIdentifier(o1) === this.getTacheIdentifier(o2) : o1 === o2;
  }

  addTacheToCollectionIfMissing<Type extends Pick<ITache, 'id'>>(
    tacheCollection: Type[],
    ...tachesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const taches: Type[] = tachesToCheck.filter(tacheItem => tacheItem !== null && tacheItem !== undefined);
    if (taches.length > 0) {
      const tacheCollectionIdentifiers = tacheCollection.map(tacheItem => this.getTacheIdentifier(tacheItem));
      const tachesToAdd = taches.filter(tacheItem => {
        const tacheIdentifier = this.getTacheIdentifier(tacheItem);
        if (tacheCollectionIdentifiers.includes(tacheIdentifier)) {
          return false;
        }
        tacheCollectionIdentifiers.push(tacheIdentifier);
        return true;
      });
      return [...tachesToAdd, ...tacheCollection];
    }
    return tacheCollection;
  }

  protected convertValueFromClient<T extends ITache | NewTache | PartialUpdateTache>(tache: T): RestOf<T> {
    return {
      ...tache,
      dateCreation: tache.dateCreation?.toJSON() ?? null,
      dateAssignation: tache.dateAssignation?.toJSON() ?? null,
      dateEcheance: tache.dateEcheance?.toJSON() ?? null,
      dateTerminaison: tache.dateTerminaison?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestTache): ITache {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestTache[]): ITache[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
