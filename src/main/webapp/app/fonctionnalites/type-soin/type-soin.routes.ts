import { Routes } from '@angular/router';

import { userRouteAccessService } from 'app/core/auth';
import { Action } from 'app/shared/jhipster/actions.constants';
import { Authority } from 'app/shared/jhipster/constants';

import TypeSoinResolve from './route/type-soin-routing-resolve.service';

const typeSoinRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/type-soin').then(m => m.TypeSoin),
    data: {
      // Le referentiel se lit dans l'ordre de l'imprime officiel, pas par ordre alphabetique.
      defaultSort: 'ordre,asc',
      authorities: [Action.REFERENTIEL_CONSULTER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/type-soin-detail').then(m => m.TypeSoinDetail),
    resolve: {
      typeSoin: TypeSoinResolve,
    },
    data: {
      authorities: [Action.REFERENTIEL_CONSULTER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/type-soin-update').then(m => m.TypeSoinUpdate),
    resolve: {
      typeSoin: TypeSoinResolve,
    },
    data: {
      authorities: [Action.REFERENTIEL_CREER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/type-soin-update').then(m => m.TypeSoinUpdate),
    resolve: {
      typeSoin: TypeSoinResolve,
    },
    data: {
      authorities: [Action.REFERENTIEL_MODIFIER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
];

export default typeSoinRoute;
