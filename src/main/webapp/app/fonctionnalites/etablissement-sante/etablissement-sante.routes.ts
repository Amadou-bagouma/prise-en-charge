import { Routes } from '@angular/router';

import { DESC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';
import { Action } from 'app/shared/jhipster/actions.constants';
import { Authority } from 'app/shared/jhipster/constants';

import EtablissementSanteResolve from './route/etablissement-sante-routing-resolve.service';

const etablissementSanteRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/etablissement-sante').then(m => m.EtablissementSante),
    data: {
      authorities: [Action.REFERENTIEL_CONSULTER, Authority.ADMIN],
      defaultSort: `id,${DESC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/etablissement-sante-detail').then(m => m.EtablissementSanteDetail),
    resolve: {
      etablissementSante: EtablissementSanteResolve,
    },
    data: {
      authorities: [Action.REFERENTIEL_CONSULTER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/etablissement-sante-update').then(m => m.EtablissementSanteUpdate),
    resolve: {
      etablissementSante: EtablissementSanteResolve,
    },
    data: {
      authorities: [Action.REFERENTIEL_CREER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/etablissement-sante-update').then(m => m.EtablissementSanteUpdate),
    resolve: {
      etablissementSante: EtablissementSanteResolve,
    },
    data: {
      authorities: [Action.REFERENTIEL_MODIFIER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
];

export default etablissementSanteRoute;
