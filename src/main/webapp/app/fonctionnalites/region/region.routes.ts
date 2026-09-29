import { Routes } from '@angular/router';

import { DESC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';
import { Action } from 'app/shared/jhipster/actions.constants';
import { Authority } from 'app/shared/jhipster/constants';

import RegionResolve from './route/region-routing-resolve.service';

const regionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/region').then(m => m.Region),
    data: {
      defaultSort: `id,${DESC}`,
      authorities: [Action.REFERENTIEL_CONSULTER, Authority.ADMIN],
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
      authorities: [Action.REFERENTIEL_CONSULTER, Authority.ADMIN],
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
      authorities: [Action.REFERENTIEL_CREER, Authority.ADMIN],
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
      authorities: [Action.REFERENTIEL_MODIFIER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
];

export default regionRoute;
