import { Routes } from '@angular/router';

import { DESC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';
import { Action } from 'app/shared/jhipster/actions.constants';
import { Authority } from 'app/shared/jhipster/constants';

import AyantDroitResolve from './route/ayant-droit-routing-resolve.service';

const ayantDroitRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/ayant-droit').then(m => m.AyantDroit),
    data: {
      authorities: [Action.AYANT_DROIT_CONSULTER, Authority.ADMIN],
      defaultSort: `id,${DESC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/ayant-droit-detail').then(m => m.AyantDroitDetail),
    resolve: {
      ayantDroit: AyantDroitResolve,
    },
    data: {
      authorities: [Action.AYANT_DROIT_CONSULTER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/ayant-droit-update').then(m => m.AyantDroitUpdate),
    resolve: {
      ayantDroit: AyantDroitResolve,
    },
    data: {
      authorities: [Action.AYANT_DROIT_CREER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/ayant-droit-update').then(m => m.AyantDroitUpdate),
    resolve: {
      ayantDroit: AyantDroitResolve,
    },
    data: {
      authorities: [Action.AYANT_DROIT_MODIFIER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
];

export default ayantDroitRoute;
