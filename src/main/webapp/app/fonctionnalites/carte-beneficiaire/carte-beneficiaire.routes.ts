import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';
import { Authority } from 'app/shared/jhipster/constants';

import CarteBeneficiaireResolve from './route/carte-beneficiaire-routing-resolve.service';

const carteBeneficiaireRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/carte-beneficiaire').then(m => m.CarteBeneficiaire),
    data: {
      defaultSort: `id,${ASC}`,
      authorities: [Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/carte-beneficiaire-detail').then(m => m.CarteBeneficiaireDetail),
    resolve: {
      carteBeneficiaire: CarteBeneficiaireResolve,
    },
    data: {
      authorities: [Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/print',
    loadComponent: () => import('./print/carte-beneficiaire-print').then(m => m.CarteBeneficiairePrint),
    resolve: {
      carteBeneficiaire: CarteBeneficiaireResolve,
    },
    data: {
      authorities: [Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/carte-beneficiaire-update').then(m => m.CarteBeneficiaireUpdate),
    resolve: {
      carteBeneficiaire: CarteBeneficiaireResolve,
    },
    data: {
      authorities: [Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/carte-beneficiaire-update').then(m => m.CarteBeneficiaireUpdate),
    resolve: {
      carteBeneficiaire: CarteBeneficiaireResolve,
    },
    data: {
      authorities: [Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
];

export default carteBeneficiaireRoute;
