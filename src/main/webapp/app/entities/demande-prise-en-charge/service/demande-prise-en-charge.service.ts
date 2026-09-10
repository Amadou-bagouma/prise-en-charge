import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { IDemandePriseEnCharge, NewDemandePriseEnCharge } from '../demande-prise-en-charge.model';

export type PartialUpdateDemandePriseEnCharge = Partial<IDemandePriseEnCharge> & Pick<IDemandePriseEnCharge, 'id'>;

type RestOf<T extends IDemandePriseEnCharge | NewDemandePriseEnCharge> = Omit<
  T,
  'dateCreation' | 'dateModification' | 'dateAssignation' | 'dateEcheance'
> & {
  dateCreation?: string | null;
  dateModification?: string | null;
  dateAssignation?: string | null;
  dateEcheance?: string | null;
};

export type RestDemandePriseEnCharge = RestOf<IDemandePriseEnCharge>;

export type NewRestDemandePriseEnCharge = RestOf<NewDemandePriseEnCharge>;

export type PartialUpdateRestDemandePriseEnCharge = RestOf<PartialUpdateDemandePriseEnCharge>;

@Service()
export class DemandePriseEnChargesService {
  readonly demandePriseEnChargesParams = signal<
    Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined
  >(undefined);
  readonly demandePriseEnChargesResource = httpResource<RestDemandePriseEnCharge[]>(() => {
    const params = this.demandePriseEnChargesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of demandePriseEnCharge that have been fetched. It is updated when the demandePriseEnChargesResource emits a new value.
   * In case of error while fetching the demandePriseEnCharges, the signal is set to an empty array.
   */
  readonly demandePriseEnCharges = computed(() =>
    (this.demandePriseEnChargesResource.hasValue() ? this.demandePriseEnChargesResource.value() : []).map(item =>
      this.convertValueFromServer(item),
    ),
  );
  protected readonly resourceUrl = `${serverApiUrl}api/demande-prise-en-charges`;

  protected convertValueFromServer(restDemandePriseEnCharge: RestDemandePriseEnCharge): IDemandePriseEnCharge {
    return {
      ...restDemandePriseEnCharge,
      dateCreation: restDemandePriseEnCharge.dateCreation ? dayjs(restDemandePriseEnCharge.dateCreation) : undefined,
      dateModification: restDemandePriseEnCharge.dateModification ? dayjs(restDemandePriseEnCharge.dateModification) : undefined,
      dateAssignation: restDemandePriseEnCharge.dateAssignation ? dayjs(restDemandePriseEnCharge.dateAssignation) : undefined,
      dateEcheance: restDemandePriseEnCharge.dateEcheance ? dayjs(restDemandePriseEnCharge.dateEcheance) : undefined,
    };
  }
}

@Service()
export class DemandePriseEnChargeService extends DemandePriseEnChargesService {
  protected readonly http = inject(HttpClient);

  create(demandePriseEnCharge: NewDemandePriseEnCharge): Observable<IDemandePriseEnCharge> {
    const copy = this.convertValueFromClient(demandePriseEnCharge);
    return this.http.post<RestDemandePriseEnCharge>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(demandePriseEnCharge: IDemandePriseEnCharge): Observable<IDemandePriseEnCharge> {
    const copy = this.convertValueFromClient(demandePriseEnCharge);
    return this.http
      .put<RestDemandePriseEnCharge>(
        `${this.resourceUrl}/${encodeURIComponent(this.getDemandePriseEnChargeIdentifier(demandePriseEnCharge))}`,
        copy,
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(demandePriseEnCharge: PartialUpdateDemandePriseEnCharge): Observable<IDemandePriseEnCharge> {
    const copy = this.convertValueFromClient(demandePriseEnCharge);
    return this.http
      .patch<RestDemandePriseEnCharge>(
        `${this.resourceUrl}/${encodeURIComponent(this.getDemandePriseEnChargeIdentifier(demandePriseEnCharge))}`,
        copy,
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IDemandePriseEnCharge> {
    return this.http
      .get<RestDemandePriseEnCharge>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IDemandePriseEnCharge[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestDemandePriseEnCharge[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getDemandePriseEnChargeIdentifier(demandePriseEnCharge: Pick<IDemandePriseEnCharge, 'id'>): number {
    return demandePriseEnCharge.id;
  }

  compareDemandePriseEnCharge(o1: Pick<IDemandePriseEnCharge, 'id'> | null, o2: Pick<IDemandePriseEnCharge, 'id'> | null): boolean {
    return o1 && o2 ? this.getDemandePriseEnChargeIdentifier(o1) === this.getDemandePriseEnChargeIdentifier(o2) : o1 === o2;
  }

  addDemandePriseEnChargeToCollectionIfMissing<Type extends Pick<IDemandePriseEnCharge, 'id'>>(
    demandePriseEnChargeCollection: Type[],
    ...demandePriseEnChargesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const demandePriseEnCharges: Type[] = demandePriseEnChargesToCheck.filter(
      demandePriseEnChargeItem => demandePriseEnChargeItem !== null && demandePriseEnChargeItem !== undefined,
    );
    if (demandePriseEnCharges.length > 0) {
      const demandePriseEnChargeCollectionIdentifiers = demandePriseEnChargeCollection.map(demandePriseEnChargeItem =>
        this.getDemandePriseEnChargeIdentifier(demandePriseEnChargeItem),
      );
      const demandePriseEnChargesToAdd = demandePriseEnCharges.filter(demandePriseEnChargeItem => {
        const demandePriseEnChargeIdentifier = this.getDemandePriseEnChargeIdentifier(demandePriseEnChargeItem);
        if (demandePriseEnChargeCollectionIdentifiers.includes(demandePriseEnChargeIdentifier)) {
          return false;
        }
        demandePriseEnChargeCollectionIdentifiers.push(demandePriseEnChargeIdentifier);
        return true;
      });
      return [...demandePriseEnChargesToAdd, ...demandePriseEnChargeCollection];
    }
    return demandePriseEnChargeCollection;
  }

  protected convertValueFromClient<T extends IDemandePriseEnCharge | NewDemandePriseEnCharge | PartialUpdateDemandePriseEnCharge>(
    demandePriseEnCharge: T,
  ): RestOf<T> {
    return {
      ...demandePriseEnCharge,
      dateCreation: demandePriseEnCharge.dateCreation?.toJSON() ?? null,
      dateModification: demandePriseEnCharge.dateModification?.toJSON() ?? null,
      dateAssignation: demandePriseEnCharge.dateAssignation?.toJSON() ?? null,
      dateEcheance: demandePriseEnCharge.dateEcheance?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestDemandePriseEnCharge): IDemandePriseEnCharge {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestDemandePriseEnCharge[]): IDemandePriseEnCharge[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
