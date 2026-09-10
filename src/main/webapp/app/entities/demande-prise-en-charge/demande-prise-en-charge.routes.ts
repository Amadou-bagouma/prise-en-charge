import { Routes } from '@angular/router';

import { DESC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import DemandePriseEnChargeResolve from './route/demande-prise-en-charge-routing-resolve.service';

const demandePriseEnChargeRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/demande-prise-en-charge').then(m => m.DemandePriseEnCharge),
    data: {
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
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/demande-prise-en-charge-update').then(m => m.DemandePriseEnChargeUpdate),
    resolve: {
      demandePriseEnCharge: DemandePriseEnChargeResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/demande-prise-en-charge-update').then(m => m.DemandePriseEnChargeUpdate),
    resolve: {
      demandePriseEnCharge: DemandePriseEnChargeResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default demandePriseEnChargeRoute;
