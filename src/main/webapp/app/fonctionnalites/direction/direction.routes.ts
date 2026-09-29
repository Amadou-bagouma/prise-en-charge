import { Routes } from '@angular/router';

import { DESC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';
import { Action } from 'app/shared/jhipster/actions.constants';
import { Authority } from 'app/shared/jhipster/constants';

import DirectionResolve from './route/direction-routing-resolve.service';

const directionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/direction').then(m => m.Direction),
    data: {
      defaultSort: `id,${DESC}`,
      authorities: [Action.REFERENTIEL_CONSULTER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/direction-detail').then(m => m.DirectionDetail),
    resolve: {
      direction: DirectionResolve,
    },
    data: {
      authorities: [Action.REFERENTIEL_CONSULTER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/direction-update').then(m => m.DirectionUpdate),
    resolve: {
      direction: DirectionResolve,
    },
    data: {
      authorities: [Action.REFERENTIEL_CREER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/direction-update').then(m => m.DirectionUpdate),
    resolve: {
      direction: DirectionResolve,
    },
    data: {
      authorities: [Action.REFERENTIEL_MODIFIER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
];

export default directionRoute;
