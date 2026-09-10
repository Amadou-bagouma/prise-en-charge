import { Routes } from '@angular/router';

import { DESC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import DirectionResolve from './route/direction-routing-resolve.service';

const directionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/direction').then(m => m.Direction),
    data: {
      defaultSort: `id,${DESC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/direction-detail').then(m => m.DirectionDetail),
    resolve: {
      direction: DirectionResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/direction-update').then(m => m.DirectionUpdate),
    resolve: {
      direction: DirectionResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/direction-update').then(m => m.DirectionUpdate),
    resolve: {
      direction: DirectionResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default directionRoute;
