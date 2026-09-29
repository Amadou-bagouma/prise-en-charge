import { Routes } from '@angular/router';

import { DESC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';
import { Action } from 'app/shared/jhipster/actions.constants';
import { Authority } from 'app/shared/jhipster/constants';

import HistoriqueActionResolve from './route/historique-action-routing-resolve.service';

const historiqueActionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/historique-action').then(m => m.HistoriqueAction),
    data: {
      authorities: [Action.HISTORIQUE_CONSULTER, Authority.ADMIN],
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
    data: {
      authorities: [Action.HISTORIQUE_CONSULTER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/historique-action-update').then(m => m.HistoriqueActionUpdate),
    resolve: {
      historiqueAction: HistoriqueActionResolve,
    },
    data: {
      authorities: [Action.HISTORIQUE_CONSULTER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/historique-action-update').then(m => m.HistoriqueActionUpdate),
    resolve: {
      historiqueAction: HistoriqueActionResolve,
    },
    data: {
      authorities: [Action.HISTORIQUE_CONSULTER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
];

export default historiqueActionRoute;
