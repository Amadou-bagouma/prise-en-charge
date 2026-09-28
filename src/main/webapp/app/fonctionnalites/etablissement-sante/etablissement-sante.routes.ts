import { Routes } from '@angular/router';

import { DESC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import EtablissementSanteResolve from './route/etablissement-sante-routing-resolve.service';

const etablissementSanteRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/etablissement-sante').then(m => m.EtablissementSante),
    data: {
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
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/etablissement-sante-update').then(m => m.EtablissementSanteUpdate),
    resolve: {
      etablissementSante: EtablissementSanteResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/etablissement-sante-update').then(m => m.EtablissementSanteUpdate),
    resolve: {
      etablissementSante: EtablissementSanteResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default etablissementSanteRoute;
