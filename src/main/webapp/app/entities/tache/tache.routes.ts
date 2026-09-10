import { Routes } from '@angular/router';

import { DESC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import TacheResolve from './route/tache-routing-resolve.service';

const tacheRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/tache').then(m => m.Tache),
    data: {
      defaultSort: `id,${DESC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/tache-detail').then(m => m.TacheDetail),
    resolve: {
      tache: TacheResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/tache-update').then(m => m.TacheUpdate),
    resolve: {
      tache: TacheResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/tache-update').then(m => m.TacheUpdate),
    resolve: {
      tache: TacheResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default tacheRoute;
