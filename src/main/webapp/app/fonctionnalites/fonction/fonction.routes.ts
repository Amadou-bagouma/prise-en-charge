import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';
import { Action } from 'app/shared/jhipster/actions.constants';
import { Authority } from 'app/shared/jhipster/constants';

import FonctionResolve from './route/fonction-routing-resolve.service';

const fonctionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/fonction').then(m => m.Fonction),
    data: {
      defaultSort: `libelle,${ASC}`,
      authorities: [Action.REFERENTIEL_CONSULTER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/fonction-detail').then(m => m.FonctionDetail),
    resolve: {
      fonction: FonctionResolve,
    },
    data: {
      authorities: [Action.REFERENTIEL_CONSULTER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/fonction-update').then(m => m.FonctionUpdate),
    resolve: {
      fonction: FonctionResolve,
    },
    data: {
      authorities: [Action.REFERENTIEL_CREER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/fonction-update').then(m => m.FonctionUpdate),
    resolve: {
      fonction: FonctionResolve,
    },
    data: {
      authorities: [Action.REFERENTIEL_MODIFIER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
];

export default fonctionRoute;
