import { Routes } from '@angular/router';

import { DESC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';
import { Action } from 'app/shared/jhipster/actions.constants';
import { Authority } from 'app/shared/jhipster/constants';

import NotificationResolve from './route/notification-routing-resolve.service';

const notificationRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/notification').then(m => m.Notification),
    data: {
      authorities: [Action.NOTIFICATION_CONSULTER, Authority.ADMIN],
      defaultSort: `id,${DESC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/notification-detail').then(m => m.NotificationDetail),
    resolve: {
      notification: NotificationResolve,
    },
    data: {
      authorities: [Action.NOTIFICATION_CONSULTER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/notification-update').then(m => m.NotificationUpdate),
    resolve: {
      notification: NotificationResolve,
    },
    data: {
      authorities: [Action.NOTIFICATION_CONSULTER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/notification-update').then(m => m.NotificationUpdate),
    resolve: {
      notification: NotificationResolve,
    },
    data: {
      authorities: [Action.NOTIFICATION_CONSULTER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
];

export default notificationRoute;
