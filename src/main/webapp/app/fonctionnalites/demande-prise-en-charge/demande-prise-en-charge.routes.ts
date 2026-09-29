import { Routes } from '@angular/router';

import { DESC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';
import { Action } from 'app/shared/jhipster/actions.constants';
import { Authority } from 'app/shared/jhipster/constants';

import DemandePriseEnChargeResolve from './route/demande-prise-en-charge-routing-resolve.service';

const demandePriseEnChargeRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/demande-prise-en-charge').then(m => m.DemandePriseEnCharge),
    data: {
      authorities: [Action.DEMANDE_CONSULTER, Authority.ADMIN],
      defaultSort: `id,${DESC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/demande-prise-en-charge-detail').then(m => m.DemandePriseEnChargeDetail),
    resolve: {
      demandePriseEnCharge: DemandePriseEnChargeResolve,
    },
    data: {
      authorities: [Action.DEMANDE_CONSULTER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/demande-prise-en-charge-update').then(m => m.DemandePriseEnChargeUpdate),
    resolve: {
      demandePriseEnCharge: DemandePriseEnChargeResolve,
    },
    data: {
      authorities: [Authority.USER, Authority.VALIDATEUR_DRH],
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/demande-prise-en-charge-update').then(m => m.DemandePriseEnChargeUpdate),
    resolve: {
      demandePriseEnCharge: DemandePriseEnChargeResolve,
    },
    data: {
      authorities: [Authority.USER, Authority.VALIDATEUR_DRH],
    },
    canActivate: [userRouteAccessService],
  },
];

export default demandePriseEnChargeRoute;
