import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import AyantDroitResolve from './route/ayant-droit-routing-resolve.service';

const ayantDroitRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/ayant-droit').then(m => m.AyantDroit),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/ayant-droit-detail').then(m => m.AyantDroitDetail),
    resolve: {
      ayantDroit: AyantDroitResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/ayant-droit-update').then(m => m.AyantDroitUpdate),
    resolve: {
      ayantDroit: AyantDroitResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/ayant-droit-update').then(m => m.AyantDroitUpdate),
    resolve: {
      ayantDroit: AyantDroitResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default ayantDroitRoute;
