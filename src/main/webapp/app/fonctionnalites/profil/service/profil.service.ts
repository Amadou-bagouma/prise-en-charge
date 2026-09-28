import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { IProfil, NewProfil } from '../profil.model';

export type PartialUpdateProfil = Partial<IProfil> & Pick<IProfil, 'id'>;

@Service()
export class ProfilsService {
  readonly profilsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly profilsResource = httpResource<IProfil[]>(() => {
    const params = this.profilsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of profil that have been fetched. It is updated when the profilsResource emits a new value.
   * In case of error while fetching the profils, the signal is set to an empty array.
   */
  readonly profils = computed(() => (this.profilsResource.hasValue() ? this.profilsResource.value() : []));
  protected readonly resourceUrl = `${serverApiUrl}api/profils`;
}

@Service()
export class ProfilService extends ProfilsService {
  protected readonly http = inject(HttpClient);

  create(profil: NewProfil): Observable<IProfil> {
    return this.http.post<IProfil>(this.resourceUrl, profil);
  }

  update(profil: IProfil): Observable<IProfil> {
    return this.http.put<IProfil>(`${this.resourceUrl}/${encodeURIComponent(this.getProfilIdentifier(profil))}`, profil);
  }

  partialUpdate(profil: PartialUpdateProfil): Observable<IProfil> {
    return this.http.patch<IProfil>(`${this.resourceUrl}/${encodeURIComponent(this.getProfilIdentifier(profil))}`, profil);
  }

  find(id: number): Observable<IProfil> {
    return this.http.get<IProfil>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IProfil[]>> {
    const options = createRequestOption(req);
    return this.http.get<IProfil[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getProfilIdentifier(profil: Pick<IProfil, 'id'>): number {
    return profil.id;
  }

  compareProfil(o1: Pick<IProfil, 'id'> | null, o2: Pick<IProfil, 'id'> | null): boolean {
    return o1 && o2 ? this.getProfilIdentifier(o1) === this.getProfilIdentifier(o2) : o1 === o2;
  }

  addProfilToCollectionIfMissing<Type extends Pick<IProfil, 'id'>>(
    profilCollection: Type[],
    ...profilsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const profils: Type[] = profilsToCheck.filter(profilItem => profilItem !== null && profilItem !== undefined);
    if (profils.length > 0) {
      const profilCollectionIdentifiers = profilCollection.map(profilItem => this.getProfilIdentifier(profilItem));
      const profilsToAdd = profils.filter(profilItem => {
        const profilIdentifier = this.getProfilIdentifier(profilItem);
        if (profilCollectionIdentifiers.includes(profilIdentifier)) {
          return false;
        }
        profilCollectionIdentifiers.push(profilIdentifier);
        return true;
      });
      return [...profilsToAdd, ...profilCollection];
    }
    return profilCollection;
  }
}
