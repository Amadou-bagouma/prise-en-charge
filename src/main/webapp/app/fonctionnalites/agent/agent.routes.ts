import { Routes } from '@angular/router';

import { DESC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';
import { Action } from 'app/shared/jhipster/actions.constants';
import { Authority } from 'app/shared/jhipster/constants';

import AgentResolve from './route/agent-routing-resolve.service';

const agentRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/agent').then(m => m.Agent),
    data: {
      authorities: [Action.AGENT_CONSULTER, Authority.ADMIN],
      defaultSort: `id,${DESC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/agent-detail').then(m => m.AgentDetail),
    resolve: {
      agent: AgentResolve,
    },
    data: {
      authorities: [Action.AGENT_CONSULTER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/agent-update').then(m => m.AgentUpdate),
    resolve: {
      agent: AgentResolve,
    },
    data: {
      authorities: [Action.AGENT_CREER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/agent-update').then(m => m.AgentUpdate),
    resolve: {
      agent: AgentResolve,
    },
    data: {
      authorities: [Action.AGENT_MODIFIER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
];

export default agentRoute;
