import { Routes } from '@angular/router';

import { DESC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import HistoriqueActionResolve from './route/historique-action-routing-resolve.service';

const historiqueActionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/historique-action').then(m => m.HistoriqueAction),
    data: {
      defaultSort: `id,${DESC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/historique-action-detail').then(m => m.HistoriqueActionDetail),
    resolve: {
      historiqueAction: HistoriqueActionResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/historique-action-update').then(m => m.HistoriqueActionUpdate),
    resolve: {
      historiqueAction: HistoriqueActionResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/historique-action-update').then(m => m.HistoriqueActionUpdate),
    resolve: {
      historiqueAction: HistoriqueActionResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default historiqueActionRoute;
