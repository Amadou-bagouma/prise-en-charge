import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { IFonction, NewFonction } from '../fonction.model';

export type PartialUpdateFonction = Partial<IFonction> & Pick<IFonction, 'id'>;

@Service()
export class FonctionsService {
  readonly fonctionsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly fonctionsResource = httpResource<IFonction[]>(() => {
    const params = this.fonctionsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of fonction that have been fetched. It is updated when the fonctionsResource emits a new value.
   * In case of error while fetching the fonctions, the signal is set to an empty array.
   */
  readonly fonctions = computed(() => (this.fonctionsResource.hasValue() ? this.fonctionsResource.value() : []));
  protected readonly resourceUrl = `${serverApiUrl}api/fonctions`;
}

@Service()
export class FonctionService extends FonctionsService {
  protected readonly http = inject(HttpClient);

  create(fonction: NewFonction): Observable<IFonction> {
    return this.http.post<IFonction>(this.resourceUrl, fonction);
  }

  update(fonction: IFonction): Observable<IFonction> {
    return this.http.put<IFonction>(`${this.resourceUrl}/${encodeURIComponent(this.getFonctionIdentifier(fonction))}`, fonction);
  }

  partialUpdate(fonction: PartialUpdateFonction): Observable<IFonction> {
    return this.http.patch<IFonction>(`${this.resourceUrl}/${encodeURIComponent(this.getFonctionIdentifier(fonction))}`, fonction);
  }

  find(id: number): Observable<IFonction> {
    return this.http.get<IFonction>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IFonction[]>> {
    const options = createRequestOption(req);
    return this.http.get<IFonction[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getFonctionIdentifier(fonction: Pick<IFonction, 'id'>): number {
    return fonction.id;
  }

  compareFonction(o1: Pick<IFonction, 'id'> | null, o2: Pick<IFonction, 'id'> | null): boolean {
    return o1 && o2 ? this.getFonctionIdentifier(o1) === this.getFonctionIdentifier(o2) : o1 === o2;
  }

  addFonctionToCollectionIfMissing<Type extends Pick<IFonction, 'id'>>(
    fonctionCollection: Type[],
    ...fonctionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const fonctions: Type[] = fonctionsToCheck.filter(fonctionItem => fonctionItem !== null && fonctionItem !== undefined);
    if (fonctions.length > 0) {
      const fonctionCollectionIdentifiers = fonctionCollection.map(fonctionItem => this.getFonctionIdentifier(fonctionItem));
      const fonctionsToAdd = fonctions.filter(fonctionItem => {
        const fonctionIdentifier = this.getFonctionIdentifier(fonctionItem);
        if (fonctionCollectionIdentifiers.includes(fonctionIdentifier)) {
          return false;
        }
        fonctionCollectionIdentifiers.push(fonctionIdentifier);
        return true;
      });
      return [...fonctionsToAdd, ...fonctionCollection];
    }
    return fonctionCollection;
  }
}
