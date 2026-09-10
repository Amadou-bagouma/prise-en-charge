import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { IHistoriqueAction, NewHistoriqueAction } from '../historique-action.model';

export type PartialUpdateHistoriqueAction = Partial<IHistoriqueAction> & Pick<IHistoriqueAction, 'id'>;

type RestOf<T extends IHistoriqueAction | NewHistoriqueAction> = Omit<T, 'dateAction'> & {
  dateAction?: string | null;
};

export type RestHistoriqueAction = RestOf<IHistoriqueAction>;

export type NewRestHistoriqueAction = RestOf<NewHistoriqueAction>;

export type PartialUpdateRestHistoriqueAction = RestOf<PartialUpdateHistoriqueAction>;

@Service()
export class HistoriqueActionsService {
  readonly historiqueActionsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly historiqueActionsResource = httpResource<RestHistoriqueAction[]>(() => {
    const params = this.historiqueActionsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of historiqueAction that have been fetched. It is updated when the historiqueActionsResource emits a new value.
   * In case of error while fetching the historiqueActions, the signal is set to an empty array.
   */
  readonly historiqueActions = computed(() =>
    (this.historiqueActionsResource.hasValue() ? this.historiqueActionsResource.value() : []).map(item =>
      this.convertValueFromServer(item),
    ),
  );
  protected readonly resourceUrl = `${serverApiUrl}api/historique-actions`;

  protected convertValueFromServer(restHistoriqueAction: RestHistoriqueAction): IHistoriqueAction {
    return {
      ...restHistoriqueAction,
      dateAction: restHistoriqueAction.dateAction ? dayjs(restHistoriqueAction.dateAction) : undefined,
    };
  }
}

@Service()
export class HistoriqueActionService extends HistoriqueActionsService {
  protected readonly http = inject(HttpClient);

  create(historiqueAction: NewHistoriqueAction): Observable<IHistoriqueAction> {
    const copy = this.convertValueFromClient(historiqueAction);
    return this.http.post<RestHistoriqueAction>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(historiqueAction: IHistoriqueAction): Observable<IHistoriqueAction> {
    const copy = this.convertValueFromClient(historiqueAction);
    return this.http
      .put<RestHistoriqueAction>(`${this.resourceUrl}/${encodeURIComponent(this.getHistoriqueActionIdentifier(historiqueAction))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(historiqueAction: PartialUpdateHistoriqueAction): Observable<IHistoriqueAction> {
    const copy = this.convertValueFromClient(historiqueAction);
    return this.http
      .patch<RestHistoriqueAction>(`${this.resourceUrl}/${encodeURIComponent(this.getHistoriqueActionIdentifier(historiqueAction))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IHistoriqueAction> {
    return this.http
      .get<RestHistoriqueAction>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IHistoriqueAction[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestHistoriqueAction[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getHistoriqueActionIdentifier(historiqueAction: Pick<IHistoriqueAction, 'id'>): number {
    return historiqueAction.id;
  }

  compareHistoriqueAction(o1: Pick<IHistoriqueAction, 'id'> | null, o2: Pick<IHistoriqueAction, 'id'> | null): boolean {
    return o1 && o2 ? this.getHistoriqueActionIdentifier(o1) === this.getHistoriqueActionIdentifier(o2) : o1 === o2;
  }

  addHistoriqueActionToCollectionIfMissing<Type extends Pick<IHistoriqueAction, 'id'>>(
    historiqueActionCollection: Type[],
    ...historiqueActionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const historiqueActions: Type[] = historiqueActionsToCheck.filter(
      historiqueActionItem => historiqueActionItem !== null && historiqueActionItem !== undefined,
    );
    if (historiqueActions.length > 0) {
      const historiqueActionCollectionIdentifiers = historiqueActionCollection.map(historiqueActionItem =>
        this.getHistoriqueActionIdentifier(historiqueActionItem),
      );
      const historiqueActionsToAdd = historiqueActions.filter(historiqueActionItem => {
        const historiqueActionIdentifier = this.getHistoriqueActionIdentifier(historiqueActionItem);
        if (historiqueActionCollectionIdentifiers.includes(historiqueActionIdentifier)) {
          return false;
        }
        historiqueActionCollectionIdentifiers.push(historiqueActionIdentifier);
        return true;
      });
      return [...historiqueActionsToAdd, ...historiqueActionCollection];
    }
    return historiqueActionCollection;
  }

  protected convertValueFromClient<T extends IHistoriqueAction | NewHistoriqueAction | PartialUpdateHistoriqueAction>(
    historiqueAction: T,
  ): RestOf<T> {
    return {
      ...historiqueAction,
      dateAction: historiqueAction.dateAction?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestHistoriqueAction): IHistoriqueAction {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestHistoriqueAction[]): IHistoriqueAction[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
