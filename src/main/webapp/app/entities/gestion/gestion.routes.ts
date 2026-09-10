import { Routes } from '@angular/router';

import { DESC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';
import { Authority } from 'app/shared/jhipster/constants';

import GestionResolve from './route/gestion-routing-resolve.service';

const gestionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/gestion').then(m => m.Gestion),
    data: {
      defaultSort: `id,${DESC}`,
      authorities: [Authority.ADMIN],
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
      authorities: [Authority.ADMIN],
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
      authorities: [Authority.ADMIN],
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
      authorities: [Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
];

export default gestionRoute;
