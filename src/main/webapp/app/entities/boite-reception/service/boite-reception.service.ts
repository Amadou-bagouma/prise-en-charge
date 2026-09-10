import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { IBoiteReception, NewBoiteReception } from '../boite-reception.model';

export type PartialUpdateBoiteReception = Partial<IBoiteReception> & Pick<IBoiteReception, 'id'>;

type RestOf<T extends IBoiteReception | NewBoiteReception> = Omit<T, 'dateCreation' | 'dateDerniereLecture'> & {
  dateCreation?: string | null;
  dateDerniereLecture?: string | null;
};

export type RestBoiteReception = RestOf<IBoiteReception>;

export type NewRestBoiteReception = RestOf<NewBoiteReception>;

export type PartialUpdateRestBoiteReception = RestOf<PartialUpdateBoiteReception>;

@Service()
export class BoiteReceptionsService {
  readonly boiteReceptionsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly boiteReceptionsResource = httpResource<RestBoiteReception[]>(() => {
    const params = this.boiteReceptionsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of boiteReception that have been fetched. It is updated when the boiteReceptionsResource emits a new value.
   * In case of error while fetching the boiteReceptions, the signal is set to an empty array.
   */
  readonly boiteReceptions = computed(() =>
    (this.boiteReceptionsResource.hasValue() ? this.boiteReceptionsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly resourceUrl = `${serverApiUrl}api/boite-receptions`;

  protected convertValueFromServer(restBoiteReception: RestBoiteReception): IBoiteReception {
    return {
      ...restBoiteReception,
      dateCreation: restBoiteReception.dateCreation ? dayjs(restBoiteReception.dateCreation) : undefined,
      dateDerniereLecture: restBoiteReception.dateDerniereLecture ? dayjs(restBoiteReception.dateDerniereLecture) : undefined,
    };
  }
}

@Service()
export class BoiteReceptionService extends BoiteReceptionsService {
  protected readonly http = inject(HttpClient);

  create(boiteReception: NewBoiteReception): Observable<IBoiteReception> {
    const copy = this.convertValueFromClient(boiteReception);
    return this.http.post<RestBoiteReception>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(boiteReception: IBoiteReception): Observable<IBoiteReception> {
    const copy = this.convertValueFromClient(boiteReception);
    return this.http
      .put<RestBoiteReception>(`${this.resourceUrl}/${encodeURIComponent(this.getBoiteReceptionIdentifier(boiteReception))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(boiteReception: PartialUpdateBoiteReception): Observable<IBoiteReception> {
    const copy = this.convertValueFromClient(boiteReception);
    return this.http
      .patch<RestBoiteReception>(`${this.resourceUrl}/${encodeURIComponent(this.getBoiteReceptionIdentifier(boiteReception))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IBoiteReception> {
    return this.http
      .get<RestBoiteReception>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IBoiteReception[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestBoiteReception[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getBoiteReceptionIdentifier(boiteReception: Pick<IBoiteReception, 'id'>): number {
    return boiteReception.id;
  }

  compareBoiteReception(o1: Pick<IBoiteReception, 'id'> | null, o2: Pick<IBoiteReception, 'id'> | null): boolean {
    return o1 && o2 ? this.getBoiteReceptionIdentifier(o1) === this.getBoiteReceptionIdentifier(o2) : o1 === o2;
  }

  addBoiteReceptionToCollectionIfMissing<Type extends Pick<IBoiteReception, 'id'>>(
    boiteReceptionCollection: Type[],
    ...boiteReceptionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const boiteReceptions: Type[] = boiteReceptionsToCheck.filter(
      boiteReceptionItem => boiteReceptionItem !== null && boiteReceptionItem !== undefined,
    );
    if (boiteReceptions.length > 0) {
      const boiteReceptionCollectionIdentifiers = boiteReceptionCollection.map(boiteReceptionItem =>
        this.getBoiteReceptionIdentifier(boiteReceptionItem),
      );
      const boiteReceptionsToAdd = boiteReceptions.filter(boiteReceptionItem => {
        const boiteReceptionIdentifier = this.getBoiteReceptionIdentifier(boiteReceptionItem);
        if (boiteReceptionCollectionIdentifiers.includes(boiteReceptionIdentifier)) {
          return false;
        }
        boiteReceptionCollectionIdentifiers.push(boiteReceptionIdentifier);
        return true;
      });
      return [...boiteReceptionsToAdd, ...boiteReceptionCollection];
    }
    return boiteReceptionCollection;
  }

  protected convertValueFromClient<T extends IBoiteReception | NewBoiteReception | PartialUpdateBoiteReception>(
    boiteReception: T,
  ): RestOf<T> {
    return {
      ...boiteReception,
      dateCreation: boiteReception.dateCreation?.toJSON() ?? null,
      dateDerniereLecture: boiteReception.dateDerniereLecture?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestBoiteReception): IBoiteReception {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestBoiteReception[]): IBoiteReception[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
