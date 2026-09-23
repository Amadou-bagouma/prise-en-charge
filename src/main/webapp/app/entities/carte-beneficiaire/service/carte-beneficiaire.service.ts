import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { DATE_FORMAT, serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { ICarteBeneficiaire, NewCarteBeneficiaire } from '../carte-beneficiaire.model';

export type PartialUpdateCarteBeneficiaire = Partial<ICarteBeneficiaire> & Pick<ICarteBeneficiaire, 'id'>;

type RestOf<T extends ICarteBeneficiaire | NewCarteBeneficiaire> = Omit<T, 'dateDebutValidite' | 'dateFinValidite' | 'dateEmission'> & {
  dateDebutValidite?: string | null;
  dateFinValidite?: string | null;
  dateEmission?: string | null;
};

export type RestCarteBeneficiaire = RestOf<ICarteBeneficiaire>;

export type NewRestCarteBeneficiaire = RestOf<NewCarteBeneficiaire>;

export type PartialUpdateRestCarteBeneficiaire = RestOf<PartialUpdateCarteBeneficiaire>;

@Service()
export class CarteBeneficiairesService {
  readonly carteBeneficiairesParams = signal<
    Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined
  >(undefined);
  readonly carteBeneficiairesResource = httpResource<RestCarteBeneficiaire[]>(() => {
    const params = this.carteBeneficiairesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of carteBeneficiaire that have been fetched. It is updated when the carteBeneficiairesResource emits a new value.
   * In case of error while fetching the carteBeneficiaires, the signal is set to an empty array.
   */
  readonly carteBeneficiaires = computed(() =>
    (this.carteBeneficiairesResource.hasValue() ? this.carteBeneficiairesResource.value() : []).map(item =>
      this.convertValueFromServer(item),
    ),
  );
  protected readonly resourceUrl = `${serverApiUrl}api/carte-beneficiaires`;

  protected convertValueFromServer(restCarteBeneficiaire: RestCarteBeneficiaire): ICarteBeneficiaire {
    return {
      ...restCarteBeneficiaire,
      dateDebutValidite: restCarteBeneficiaire.dateDebutValidite ? dayjs(restCarteBeneficiaire.dateDebutValidite) : undefined,
      dateFinValidite: restCarteBeneficiaire.dateFinValidite ? dayjs(restCarteBeneficiaire.dateFinValidite) : undefined,
      dateEmission: restCarteBeneficiaire.dateEmission ? dayjs(restCarteBeneficiaire.dateEmission) : undefined,
    };
  }
}

@Service()
export class CarteBeneficiaireService extends CarteBeneficiairesService {
  protected readonly http = inject(HttpClient);

  create(carteBeneficiaire: NewCarteBeneficiaire): Observable<ICarteBeneficiaire> {
    const copy = this.convertValueFromClient(carteBeneficiaire);
    return this.http.post<RestCarteBeneficiaire>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(carteBeneficiaire: ICarteBeneficiaire): Observable<ICarteBeneficiaire> {
    const copy = this.convertValueFromClient(carteBeneficiaire);
    return this.http
      .put<RestCarteBeneficiaire>(`${this.resourceUrl}/${encodeURIComponent(this.getCarteBeneficiaireIdentifier(carteBeneficiaire))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(carteBeneficiaire: PartialUpdateCarteBeneficiaire): Observable<ICarteBeneficiaire> {
    const copy = this.convertValueFromClient(carteBeneficiaire);
    return this.http
      .patch<RestCarteBeneficiaire>(
        `${this.resourceUrl}/${encodeURIComponent(this.getCarteBeneficiaireIdentifier(carteBeneficiaire))}`,
        copy,
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<ICarteBeneficiaire> {
    return this.http
      .get<RestCarteBeneficiaire>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<ICarteBeneficiaire[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCarteBeneficiaire[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getCarteBeneficiaireIdentifier(carteBeneficiaire: Pick<ICarteBeneficiaire, 'id'>): number {
    return carteBeneficiaire.id;
  }

  compareCarteBeneficiaire(o1: Pick<ICarteBeneficiaire, 'id'> | null, o2: Pick<ICarteBeneficiaire, 'id'> | null): boolean {
    return o1 && o2 ? this.getCarteBeneficiaireIdentifier(o1) === this.getCarteBeneficiaireIdentifier(o2) : o1 === o2;
  }

  addCarteBeneficiaireToCollectionIfMissing<Type extends Pick<ICarteBeneficiaire, 'id'>>(
    carteBeneficiaireCollection: Type[],
    ...carteBeneficiairesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const carteBeneficiaires: Type[] = carteBeneficiairesToCheck.filter(
      carteBeneficiaireItem => carteBeneficiaireItem !== null && carteBeneficiaireItem !== undefined,
    );
    if (carteBeneficiaires.length > 0) {
      const carteBeneficiaireCollectionIdentifiers = carteBeneficiaireCollection.map(carteBeneficiaireItem =>
        this.getCarteBeneficiaireIdentifier(carteBeneficiaireItem),
      );
      const carteBeneficiairesToAdd = carteBeneficiaires.filter(carteBeneficiaireItem => {
        const carteBeneficiaireIdentifier = this.getCarteBeneficiaireIdentifier(carteBeneficiaireItem);
        if (carteBeneficiaireCollectionIdentifiers.includes(carteBeneficiaireIdentifier)) {
          return false;
        }
        carteBeneficiaireCollectionIdentifiers.push(carteBeneficiaireIdentifier);
        return true;
      });
      return [...carteBeneficiairesToAdd, ...carteBeneficiaireCollection];
    }
    return carteBeneficiaireCollection;
  }

  protected convertValueFromClient<T extends ICarteBeneficiaire | NewCarteBeneficiaire | PartialUpdateCarteBeneficiaire>(
    carteBeneficiaire: T,
  ): RestOf<T> {
    return {
      ...carteBeneficiaire,
      dateDebutValidite: carteBeneficiaire.dateDebutValidite?.format(DATE_FORMAT) ?? null,
      dateFinValidite: carteBeneficiaire.dateFinValidite?.format(DATE_FORMAT) ?? null,
      dateEmission: carteBeneficiaire.dateEmission?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertResponseFromServer(res: RestCarteBeneficiaire): ICarteBeneficiaire {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestCarteBeneficiaire[]): ICarteBeneficiaire[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
