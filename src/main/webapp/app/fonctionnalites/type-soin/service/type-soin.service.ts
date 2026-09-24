import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { ITypeSoin, NewTypeSoin } from '../type-soin.model';

export type PartialUpdateTypeSoin = Partial<ITypeSoin> & Pick<ITypeSoin, 'id'>;

@Service()
export class TypeSoinsService {
  readonly typeSoinsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly typeSoinsResource = httpResource<ITypeSoin[]>(() => {
    const params = this.typeSoinsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of typeSoin that have been fetched. It is updated when the typeSoinsResource emits a new value.
   * In case of error while fetching the typeSoins, the signal is set to an empty array.
   */
  readonly typeSoins = computed(() => (this.typeSoinsResource.hasValue() ? this.typeSoinsResource.value() : []));
  protected readonly resourceUrl = `${serverApiUrl}api/type-soins`;
}

@Service()
export class TypeSoinService extends TypeSoinsService {
  protected readonly http = inject(HttpClient);

  create(typeSoin: NewTypeSoin): Observable<ITypeSoin> {
    return this.http.post<ITypeSoin>(this.resourceUrl, typeSoin);
  }

  update(typeSoin: ITypeSoin): Observable<ITypeSoin> {
    return this.http.put<ITypeSoin>(`${this.resourceUrl}/${encodeURIComponent(this.getTypeSoinIdentifier(typeSoin))}`, typeSoin);
  }

  partialUpdate(typeSoin: PartialUpdateTypeSoin): Observable<ITypeSoin> {
    return this.http.patch<ITypeSoin>(`${this.resourceUrl}/${encodeURIComponent(this.getTypeSoinIdentifier(typeSoin))}`, typeSoin);
  }

  find(id: number): Observable<ITypeSoin> {
    return this.http.get<ITypeSoin>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  /**
   * Les types proposés à la saisie d'une demande, dans l'ordre de l'imprimé officiel.
   *
   * Sans pagination : le référentiel tient sur un écran, et la saisie a besoin de la liste
   * entière pour afficher ses cases à cocher.
   */
  actifs(): Observable<ITypeSoin[]> {
    return this.http.get<ITypeSoin[]>(`${this.resourceUrl}/actifs`);
  }

  query(req?: any): Observable<HttpResponse<ITypeSoin[]>> {
    const options = createRequestOption(req);
    return this.http.get<ITypeSoin[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getTypeSoinIdentifier(typeSoin: Pick<ITypeSoin, 'id'>): number {
    return typeSoin.id;
  }

  compareTypeSoin(o1: Pick<ITypeSoin, 'id'> | null, o2: Pick<ITypeSoin, 'id'> | null): boolean {
    return o1 && o2 ? this.getTypeSoinIdentifier(o1) === this.getTypeSoinIdentifier(o2) : o1 === o2;
  }

  addTypeSoinToCollectionIfMissing<Type extends Pick<ITypeSoin, 'id'>>(
    typeSoinCollection: Type[],
    ...typeSoinsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const typeSoins: Type[] = typeSoinsToCheck.filter(typeSoinItem => typeSoinItem !== null && typeSoinItem !== undefined);
    if (typeSoins.length > 0) {
      const typeSoinCollectionIdentifiers = typeSoinCollection.map(typeSoinItem => this.getTypeSoinIdentifier(typeSoinItem));
      const typeSoinsToAdd = typeSoins.filter(typeSoinItem => {
        const typeSoinIdentifier = this.getTypeSoinIdentifier(typeSoinItem);
        if (typeSoinCollectionIdentifiers.includes(typeSoinIdentifier)) {
          return false;
        }
        typeSoinCollectionIdentifiers.push(typeSoinIdentifier);
        return true;
      });
      return [...typeSoinsToAdd, ...typeSoinCollection];
    }
    return typeSoinCollection;
  }
}
