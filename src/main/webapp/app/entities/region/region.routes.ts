import { Routes } from '@angular/router';

import { DESC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';
import { Authority } from 'app/shared/jhipster/constants';

import RegionResolve from './route/region-routing-resolve.service';

const regionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/region').then(m => m.Region),
    data: {
      defaultSort: `id,${DESC}`,
      authorities: [Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/region-detail').then(m => m.RegionDetail),
    resolve: {
      region: RegionResolve,
    },
    data: {
      authorities: [Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/region-update').then(m => m.RegionUpdate),
    resolve: {
      region: RegionResolve,
    },
    data: {
      authorities: [Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/region-update').then(m => m.RegionUpdate),
    resolve: {
      region: RegionResolve,
    },
    data: {
      authorities: [Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
];

export default regionRoute;
