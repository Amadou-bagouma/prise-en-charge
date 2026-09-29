import { Routes } from '@angular/router';

import { DESC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';
import { Action } from 'app/shared/jhipster/actions.constants';
import { Authority } from 'app/shared/jhipster/constants';

import GestionResolve from './route/gestion-routing-resolve.service';

const gestionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/gestion').then(m => m.Gestion),
    data: {
      defaultSort: `id,${DESC}`,
      authorities: [Action.REFERENTIEL_CONSULTER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/gestion-detail').then(m => m.GestionDetail),
    resolve: {
      gestion: GestionResolve,
    },
    data: {
      authorities: [Action.REFERENTIEL_CONSULTER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/gestion-update').then(m => m.GestionUpdate),
    resolve: {
      gestion: GestionResolve,
    },
    data: {
      authorities: [Action.REFERENTIEL_CREER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/gestion-update').then(m => m.GestionUpdate),
    resolve: {
      gestion: GestionResolve,
    },
    data: {
      authorities: [Action.REFERENTIEL_MODIFIER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
];

export default gestionRoute;
