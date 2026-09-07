import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import GestionResolve from './route/gestion-routing-resolve.service';

const gestionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/gestion').then(m => m.Gestion),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/gestion-detail').then(m => m.GestionDetail),
    resolve: {
      gestion: GestionResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/gestion-update').then(m => m.GestionUpdate),
    resolve: {
      gestion: GestionResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/gestion-update').then(m => m.GestionUpdate),
    resolve: {
      gestion: GestionResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default gestionRoute;
