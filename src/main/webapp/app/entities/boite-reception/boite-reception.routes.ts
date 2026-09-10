import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import BoiteReceptionResolve from './route/boite-reception-routing-resolve.service';

const boiteReceptionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/boite-reception').then(m => m.BoiteReception),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/boite-reception-detail').then(m => m.BoiteReceptionDetail),
    resolve: {
      boiteReception: BoiteReceptionResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/boite-reception-update').then(m => m.BoiteReceptionUpdate),
    resolve: {
      boiteReception: BoiteReceptionResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/boite-reception-update').then(m => m.BoiteReceptionUpdate),
    resolve: {
      boiteReception: BoiteReceptionResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default boiteReceptionRoute;
