import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { DATE_FORMAT, serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { IAyantDroit, NewAyantDroit } from '../ayant-droit.model';

export type PartialUpdateAyantDroit = Partial<IAyantDroit> & Pick<IAyantDroit, 'id'>;

type RestOf<T extends IAyantDroit | NewAyantDroit> = Omit<T, 'dateNaissance'> & {
  dateNaissance?: string | null;
};

export type RestAyantDroit = RestOf<IAyantDroit>;

export type NewRestAyantDroit = RestOf<NewAyantDroit>;

export type PartialUpdateRestAyantDroit = RestOf<PartialUpdateAyantDroit>;

@Service()
export class AyantDroitsService {
  readonly ayantDroitsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly ayantDroitsResource = httpResource<RestAyantDroit[]>(() => {
    const params = this.ayantDroitsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of ayantDroit that have been fetched. It is updated when the ayantDroitsResource emits a new value.
   * In case of error while fetching the ayantDroits, the signal is set to an empty array.
   */
  readonly ayantDroits = computed(() =>
    (this.ayantDroitsResource.hasValue() ? this.ayantDroitsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly resourceUrl = `${serverApiUrl}api/ayant-droits`;

  protected convertValueFromServer(restAyantDroit: RestAyantDroit): IAyantDroit {
    return {
      ...restAyantDroit,
      dateNaissance: restAyantDroit.dateNaissance ? dayjs(restAyantDroit.dateNaissance) : undefined,
    };
  }
}

@Service()
export class AyantDroitService extends AyantDroitsService {
  protected readonly http = inject(HttpClient);

  create(ayantDroit: NewAyantDroit): Observable<IAyantDroit> {
    const copy = this.convertValueFromClient(ayantDroit);
    return this.http.post<RestAyantDroit>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(ayantDroit: IAyantDroit): Observable<IAyantDroit> {
    const copy = this.convertValueFromClient(ayantDroit);
    return this.http
      .put<RestAyantDroit>(`${this.resourceUrl}/${encodeURIComponent(this.getAyantDroitIdentifier(ayantDroit))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(ayantDroit: PartialUpdateAyantDroit): Observable<IAyantDroit> {
    const copy = this.convertValueFromClient(ayantDroit);
    return this.http
      .patch<RestAyantDroit>(`${this.resourceUrl}/${encodeURIComponent(this.getAyantDroitIdentifier(ayantDroit))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IAyantDroit> {
    return this.http
      .get<RestAyantDroit>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IAyantDroit[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestAyantDroit[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getAyantDroitIdentifier(ayantDroit: Pick<IAyantDroit, 'id'>): number {
    return ayantDroit.id;
  }

  compareAyantDroit(o1: Pick<IAyantDroit, 'id'> | null, o2: Pick<IAyantDroit, 'id'> | null): boolean {
    return o1 && o2 ? this.getAyantDroitIdentifier(o1) === this.getAyantDroitIdentifier(o2) : o1 === o2;
  }

  addAyantDroitToCollectionIfMissing<Type extends Pick<IAyantDroit, 'id'>>(
    ayantDroitCollection: Type[],
    ...ayantDroitsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const ayantDroits: Type[] = ayantDroitsToCheck.filter(ayantDroitItem => ayantDroitItem !== null && ayantDroitItem !== undefined);
    if (ayantDroits.length > 0) {
      const ayantDroitCollectionIdentifiers = ayantDroitCollection.map(ayantDroitItem => this.getAyantDroitIdentifier(ayantDroitItem));
      const ayantDroitsToAdd = ayantDroits.filter(ayantDroitItem => {
        const ayantDroitIdentifier = this.getAyantDroitIdentifier(ayantDroitItem);
        if (ayantDroitCollectionIdentifiers.includes(ayantDroitIdentifier)) {
          return false;
        }
        ayantDroitCollectionIdentifiers.push(ayantDroitIdentifier);
        return true;
      });
      return [...ayantDroitsToAdd, ...ayantDroitCollection];
    }
    return ayantDroitCollection;
  }

  protected convertValueFromClient<T extends IAyantDroit | NewAyantDroit | PartialUpdateAyantDroit>(ayantDroit: T): RestOf<T> {
    return {
      ...ayantDroit,
      dateNaissance: ayantDroit.dateNaissance?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertResponseFromServer(res: RestAyantDroit): IAyantDroit {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestAyantDroit[]): IAyantDroit[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
