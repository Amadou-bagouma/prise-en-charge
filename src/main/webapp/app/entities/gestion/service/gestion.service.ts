import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { IGestion, NewGestion } from '../gestion.model';

export type PartialUpdateGestion = Partial<IGestion> & Pick<IGestion, 'id'>;

@Service()
export class GestionsService {
  readonly gestionsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly gestionsResource = httpResource<IGestion[]>(() => {
    const params = this.gestionsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of gestion that have been fetched. It is updated when the gestionsResource emits a new value.
   * In case of error while fetching the gestions, the signal is set to an empty array.
   */
  readonly gestions = computed(() => (this.gestionsResource.hasValue() ? this.gestionsResource.value() : []));
  protected readonly resourceUrl = `${serverApiUrl}api/gestions`;
}

@Service()
export class GestionService extends GestionsService {
  protected readonly http = inject(HttpClient);

  create(gestion: NewGestion): Observable<IGestion> {
    return this.http.post<IGestion>(this.resourceUrl, gestion);
  }

  update(gestion: IGestion): Observable<IGestion> {
    return this.http.put<IGestion>(`${this.resourceUrl}/${encodeURIComponent(this.getGestionIdentifier(gestion))}`, gestion);
  }

  partialUpdate(gestion: PartialUpdateGestion): Observable<IGestion> {
    return this.http.patch<IGestion>(`${this.resourceUrl}/${encodeURIComponent(this.getGestionIdentifier(gestion))}`, gestion);
  }

  find(id: number): Observable<IGestion> {
    return this.http.get<IGestion>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IGestion[]>> {
    const options = createRequestOption(req);
    return this.http.get<IGestion[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getGestionIdentifier(gestion: Pick<IGestion, 'id'>): number {
    return gestion.id;
  }

  compareGestion(o1: Pick<IGestion, 'id'> | null, o2: Pick<IGestion, 'id'> | null): boolean {
    return o1 && o2 ? this.getGestionIdentifier(o1) === this.getGestionIdentifier(o2) : o1 === o2;
  }

  addGestionToCollectionIfMissing<Type extends Pick<IGestion, 'id'>>(
    gestionCollection: Type[],
    ...gestionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const gestions: Type[] = gestionsToCheck.filter(gestionItem => gestionItem !== null && gestionItem !== undefined);
    if (gestions.length > 0) {
      const gestionCollectionIdentifiers = gestionCollection.map(gestionItem => this.getGestionIdentifier(gestionItem));
      const gestionsToAdd = gestions.filter(gestionItem => {
        const gestionIdentifier = this.getGestionIdentifier(gestionItem);
        if (gestionCollectionIdentifiers.includes(gestionIdentifier)) {
          return false;
        }
        gestionCollectionIdentifiers.push(gestionIdentifier);
        return true;
      });
      return [...gestionsToAdd, ...gestionCollection];
    }
    return gestionCollection;
  }
}
