import { Routes } from '@angular/router';

import { userRouteAccessService } from 'app/core/auth';
import { Action } from 'app/shared/jhipster/actions.constants';
import { Authority } from 'app/shared/jhipster/constants';

const parametreRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./parametre').then(m => m.Parametre),
    data: {
      // Les réglages se consultent avec les référentiels ; les modifier relève du même droit.
      authorities: [Action.REFERENTIEL_CONSULTER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
];

export default parametreRoute;
