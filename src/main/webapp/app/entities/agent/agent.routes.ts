import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import AgentResolve from './route/agent-routing-resolve.service';

const agentRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/agent').then(m => m.Agent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/agent-detail').then(m => m.AgentDetail),
    resolve: {
      agent: AgentResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/agent-update').then(m => m.AgentUpdate),
    resolve: {
      agent: AgentResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/agent-update').then(m => m.AgentUpdate),
    resolve: {
      agent: AgentResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default agentRoute;
