import { Routes } from '@angular/router';

import { userRouteAccessService } from 'app/core/auth';
import { Action } from 'app/shared/jhipster/actions.constants';

const espaceAgentRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./espace-agent').then(m => m.EspaceAgent),
    data: {
      // Seule l'habilitation d'espace personnel ouvre cet ecran. L'administrateur n'y est pas
      // admis : borne a rien, il y verrait une page vide, et son travail est ailleurs.
      authorities: [Action.ESPACE_AGENT],
    },
    canActivate: [userRouteAccessService],
  },
];

export default espaceAgentRoute;
