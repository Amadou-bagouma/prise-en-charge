import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import TypeSoinResolve from './route/type-soin-routing-resolve.service';

const typeSoinRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/type-soin').then(m => m.TypeSoin),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/type-soin-detail').then(m => m.TypeSoinDetail),
    resolve: {
      typeSoin: TypeSoinResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/type-soin-update').then(m => m.TypeSoinUpdate),
    resolve: {
      typeSoin: TypeSoinResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/type-soin-update').then(m => m.TypeSoinUpdate),
    resolve: {
      typeSoin: TypeSoinResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default typeSoinRoute;
