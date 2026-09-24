import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { INotification, NewNotification } from '../notification.model';

export type PartialUpdateNotification = Partial<INotification> & Pick<INotification, 'id'>;

type RestOf<T extends INotification | NewNotification> = Omit<T, 'dateCreation' | 'dateLecture'> & {
  dateCreation?: string | null;
  dateLecture?: string | null;
};

export type RestNotification = RestOf<INotification>;

export type NewRestNotification = RestOf<NewNotification>;

export type PartialUpdateRestNotification = RestOf<PartialUpdateNotification>;

@Service()
export class NotificationsService {
  readonly notificationsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly notificationsResource = httpResource<RestNotification[]>(() => {
    const params = this.notificationsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of notification that have been fetched. It is updated when the notificationsResource emits a new value.
   * In case of error while fetching the notifications, the signal is set to an empty array.
   */
  readonly notifications = computed(() =>
    (this.notificationsResource.hasValue() ? this.notificationsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly resourceUrl = `${serverApiUrl}api/notifications`;

  protected convertValueFromServer(restNotification: RestNotification): INotification {
    return {
      ...restNotification,
      dateCreation: restNotification.dateCreation ? dayjs(restNotification.dateCreation) : undefined,
      dateLecture: restNotification.dateLecture ? dayjs(restNotification.dateLecture) : undefined,
    };
  }
}

@Service()
export class NotificationService extends NotificationsService {
  protected readonly http = inject(HttpClient);

  create(notification: NewNotification): Observable<INotification> {
    const copy = this.convertValueFromClient(notification);
    return this.http.post<RestNotification>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(notification: INotification): Observable<INotification> {
    const copy = this.convertValueFromClient(notification);
    return this.http
      .put<RestNotification>(`${this.resourceUrl}/${encodeURIComponent(this.getNotificationIdentifier(notification))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(notification: PartialUpdateNotification): Observable<INotification> {
    const copy = this.convertValueFromClient(notification);
    return this.http
      .patch<RestNotification>(`${this.resourceUrl}/${encodeURIComponent(this.getNotificationIdentifier(notification))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<INotification> {
    return this.http
      .get<RestNotification>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<INotification[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestNotification[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  /** Marque une notification comme lue. Idempotent : la date de lecture ne bouge plus ensuite. */
  marquerLue(id: number): Observable<INotification> {
    return this.http
      .put<RestNotification>(`${this.resourceUrl}/${encodeURIComponent(id)}/lue`, {})
      .pipe(map(rest => this.convertValueFromServer(rest)));
  }

  /**
   * Marque lues toutes les notifications de l'agent courant.
   *
   * Le serveur travaille sur l'agent authentifié : on ne lui envoie rien, et on ne peut pas
   * solder la boîte de quelqu'un d'autre.
   */
  marquerToutLu(): Observable<number> {
    return this.http.put<number>(`${this.resourceUrl}/lire-tout`, {});
  }

  /** Compte les notifications répondant aux critères, sans ramener les lignes. */
  count(req?: any): Observable<number> {
    const options = createRequestOption(req);
    return this.http.get<number>(`${this.resourceUrl}/count`, { params: options });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getNotificationIdentifier(notification: Pick<INotification, 'id'>): number {
    return notification.id;
  }

  compareNotification(o1: Pick<INotification, 'id'> | null, o2: Pick<INotification, 'id'> | null): boolean {
    return o1 && o2 ? this.getNotificationIdentifier(o1) === this.getNotificationIdentifier(o2) : o1 === o2;
  }

  addNotificationToCollectionIfMissing<Type extends Pick<INotification, 'id'>>(
    notificationCollection: Type[],
    ...notificationsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const notifications: Type[] = notificationsToCheck.filter(
      notificationItem => notificationItem !== null && notificationItem !== undefined,
    );
    if (notifications.length > 0) {
      const notificationCollectionIdentifiers = notificationCollection.map(notificationItem =>
        this.getNotificationIdentifier(notificationItem),
      );
      const notificationsToAdd = notifications.filter(notificationItem => {
        const notificationIdentifier = this.getNotificationIdentifier(notificationItem);
        if (notificationCollectionIdentifiers.includes(notificationIdentifier)) {
          return false;
        }
        notificationCollectionIdentifiers.push(notificationIdentifier);
        return true;
      });
      return [...notificationsToAdd, ...notificationCollection];
    }
    return notificationCollection;
  }

  protected convertValueFromClient<T extends INotification | NewNotification | PartialUpdateNotification>(notification: T): RestOf<T> {
    return {
      ...notification,
      dateCreation: notification.dateCreation?.toJSON() ?? null,
      dateLecture: notification.dateLecture?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestNotification): INotification {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestNotification[]): INotification[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
