import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { IPieceJustificative, NewPieceJustificative } from '../piece-justificative.model';

export type PartialUpdatePieceJustificative = Partial<IPieceJustificative> & Pick<IPieceJustificative, 'id'>;

type RestOf<T extends IPieceJustificative | NewPieceJustificative> = Omit<T, 'dateAjout'> & {
  dateAjout?: string | null;
};

export type RestPieceJustificative = RestOf<IPieceJustificative>;

export type NewRestPieceJustificative = RestOf<NewPieceJustificative>;

export type PartialUpdateRestPieceJustificative = RestOf<PartialUpdatePieceJustificative>;

@Service()
export class PieceJustificativesService {
  readonly pieceJustificativesParams = signal<
    Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined
  >(undefined);
  readonly pieceJustificativesResource = httpResource<RestPieceJustificative[]>(() => {
    const params = this.pieceJustificativesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of pieceJustificative that have been fetched. It is updated when the pieceJustificativesResource emits a new value.
   * In case of error while fetching the pieceJustificatives, the signal is set to an empty array.
   */
  readonly pieceJustificatives = computed(() =>
    (this.pieceJustificativesResource.hasValue() ? this.pieceJustificativesResource.value() : []).map(item =>
      this.convertValueFromServer(item),
    ),
  );
  protected readonly resourceUrl = `${serverApiUrl}api/piece-justificatives`;

  protected convertValueFromServer(restPieceJustificative: RestPieceJustificative): IPieceJustificative {
    return {
      ...restPieceJustificative,
      dateAjout: restPieceJustificative.dateAjout ? dayjs(restPieceJustificative.dateAjout) : undefined,
    };
  }
}

@Service()
export class PieceJustificativeService extends PieceJustificativesService {
  protected readonly http = inject(HttpClient);

  create(pieceJustificative: NewPieceJustificative): Observable<IPieceJustificative> {
    const copy = this.convertValueFromClient(pieceJustificative);
    return this.http.post<RestPieceJustificative>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(pieceJustificative: IPieceJustificative): Observable<IPieceJustificative> {
    const copy = this.convertValueFromClient(pieceJustificative);
    return this.http
      .put<RestPieceJustificative>(
        `${this.resourceUrl}/${encodeURIComponent(this.getPieceJustificativeIdentifier(pieceJustificative))}`,
        copy,
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(pieceJustificative: PartialUpdatePieceJustificative): Observable<IPieceJustificative> {
    const copy = this.convertValueFromClient(pieceJustificative);
    return this.http
      .patch<RestPieceJustificative>(
        `${this.resourceUrl}/${encodeURIComponent(this.getPieceJustificativeIdentifier(pieceJustificative))}`,
        copy,
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IPieceJustificative> {
    return this.http
      .get<RestPieceJustificative>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IPieceJustificative[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPieceJustificative[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getPieceJustificativeIdentifier(pieceJustificative: Pick<IPieceJustificative, 'id'>): number {
    return pieceJustificative.id;
  }

  comparePieceJustificative(o1: Pick<IPieceJustificative, 'id'> | null, o2: Pick<IPieceJustificative, 'id'> | null): boolean {
    return o1 && o2 ? this.getPieceJustificativeIdentifier(o1) === this.getPieceJustificativeIdentifier(o2) : o1 === o2;
  }

  addPieceJustificativeToCollectionIfMissing<Type extends Pick<IPieceJustificative, 'id'>>(
    pieceJustificativeCollection: Type[],
    ...pieceJustificativesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const pieceJustificatives: Type[] = pieceJustificativesToCheck.filter(
      pieceJustificativeItem => pieceJustificativeItem !== null && pieceJustificativeItem !== undefined,
    );
    if (pieceJustificatives.length > 0) {
      const pieceJustificativeCollectionIdentifiers = pieceJustificativeCollection.map(pieceJustificativeItem =>
        this.getPieceJustificativeIdentifier(pieceJustificativeItem),
      );
      const pieceJustificativesToAdd = pieceJustificatives.filter(pieceJustificativeItem => {
        const pieceJustificativeIdentifier = this.getPieceJustificativeIdentifier(pieceJustificativeItem);
        if (pieceJustificativeCollectionIdentifiers.includes(pieceJustificativeIdentifier)) {
          return false;
        }
        pieceJustificativeCollectionIdentifiers.push(pieceJustificativeIdentifier);
        return true;
      });
      return [...pieceJustificativesToAdd, ...pieceJustificativeCollection];
    }
    return pieceJustificativeCollection;
  }

  protected convertValueFromClient<T extends IPieceJustificative | NewPieceJustificative | PartialUpdatePieceJustificative>(
    pieceJustificative: T,
  ): RestOf<T> {
    return {
      ...pieceJustificative,
      dateAjout: pieceJustificative.dateAjout?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestPieceJustificative): IPieceJustificative {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestPieceJustificative[]): IPieceJustificative[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
