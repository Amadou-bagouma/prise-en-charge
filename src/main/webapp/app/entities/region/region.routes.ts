import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import RegionResolve from './route/region-routing-resolve.service';

const regionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/region').then(m => m.Region),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/region-detail').then(m => m.RegionDetail),
    resolve: {
      region: RegionResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/region-update').then(m => m.RegionUpdate),
    resolve: {
      region: RegionResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/region-update').then(m => m.RegionUpdate),
    resolve: {
      region: RegionResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default regionRoute;
