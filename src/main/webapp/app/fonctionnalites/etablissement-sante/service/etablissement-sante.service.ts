import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { IEtablissementSante, NewEtablissementSante } from '../etablissement-sante.model';

export type PartialUpdateEtablissementSante = Partial<IEtablissementSante> & Pick<IEtablissementSante, 'id'>;

@Service()
export class EtablissementSantesService {
  readonly etablissementSantesParams = signal<
    Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined
  >(undefined);
  readonly etablissementSantesResource = httpResource<IEtablissementSante[]>(() => {
    const params = this.etablissementSantesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of etablissementSante that have been fetched. It is updated when the etablissementSantesResource emits a new value.
   * In case of error while fetching the etablissementSantes, the signal is set to an empty array.
   */
  readonly etablissementSantes = computed(() =>
    this.etablissementSantesResource.hasValue() ? this.etablissementSantesResource.value() : [],
  );
  protected readonly resourceUrl = `${serverApiUrl}api/etablissement-santes`;
}

@Service()
export class EtablissementSanteService extends EtablissementSantesService {
  protected readonly http = inject(HttpClient);

  create(etablissementSante: NewEtablissementSante): Observable<IEtablissementSante> {
    return this.http.post<IEtablissementSante>(this.resourceUrl, etablissementSante);
  }

  update(etablissementSante: IEtablissementSante): Observable<IEtablissementSante> {
    return this.http.put<IEtablissementSante>(
      `${this.resourceUrl}/${encodeURIComponent(this.getEtablissementSanteIdentifier(etablissementSante))}`,
      etablissementSante,
    );
  }

  partialUpdate(etablissementSante: PartialUpdateEtablissementSante): Observable<IEtablissementSante> {
    return this.http.patch<IEtablissementSante>(
      `${this.resourceUrl}/${encodeURIComponent(this.getEtablissementSanteIdentifier(etablissementSante))}`,
      etablissementSante,
    );
  }

  find(id: number): Observable<IEtablissementSante> {
    return this.http.get<IEtablissementSante>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IEtablissementSante[]>> {
    const options = createRequestOption(req);
    return this.http.get<IEtablissementSante[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getEtablissementSanteIdentifier(etablissementSante: Pick<IEtablissementSante, 'id'>): number {
    return etablissementSante.id;
  }

  compareEtablissementSante(o1: Pick<IEtablissementSante, 'id'> | null, o2: Pick<IEtablissementSante, 'id'> | null): boolean {
    return o1 && o2 ? this.getEtablissementSanteIdentifier(o1) === this.getEtablissementSanteIdentifier(o2) : o1 === o2;
  }

  addEtablissementSanteToCollectionIfMissing<Type extends Pick<IEtablissementSante, 'id'>>(
    etablissementSanteCollection: Type[],
    ...etablissementSantesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const etablissementSantes: Type[] = etablissementSantesToCheck.filter(
      etablissementSanteItem => etablissementSanteItem !== null && etablissementSanteItem !== undefined,
    );
    if (etablissementSantes.length > 0) {
      const etablissementSanteCollectionIdentifiers = etablissementSanteCollection.map(etablissementSanteItem =>
        this.getEtablissementSanteIdentifier(etablissementSanteItem),
      );
      const etablissementSantesToAdd = etablissementSantes.filter(etablissementSanteItem => {
        const etablissementSanteIdentifier = this.getEtablissementSanteIdentifier(etablissementSanteItem);
        if (etablissementSanteCollectionIdentifiers.includes(etablissementSanteIdentifier)) {
          return false;
        }
        etablissementSanteCollectionIdentifiers.push(etablissementSanteIdentifier);
        return true;
      });
      return [...etablissementSantesToAdd, ...etablissementSanteCollection];
    }
    return etablissementSanteCollection;
  }
}
