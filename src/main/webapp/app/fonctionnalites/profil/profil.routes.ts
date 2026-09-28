import { Routes } from '@angular/router';

import { DESC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';
import { Authority } from 'app/shared/jhipster/constants';

import ProfilResolve from './route/profil-routing-resolve.service';

const profilRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/profil').then(m => m.Profil),
    data: {
      defaultSort: `id,${DESC}`,
      authorities: [Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/profil-detail').then(m => m.ProfilDetail),
    resolve: {
      profil: ProfilResolve,
    },
    data: {
      authorities: [Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/profil-update').then(m => m.ProfilUpdate),
    resolve: {
      profil: ProfilResolve,
    },
    data: {
      authorities: [Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/profil-update').then(m => m.ProfilUpdate),
    resolve: {
      profil: ProfilResolve,
    },
    data: {
      authorities: [Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
];

export default profilRoute;
