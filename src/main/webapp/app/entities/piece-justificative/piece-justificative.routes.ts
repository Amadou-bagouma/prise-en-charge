import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import PieceJustificativeResolve from './route/piece-justificative-routing-resolve.service';

const pieceJustificativeRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/piece-justificative').then(m => m.PieceJustificative),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/piece-justificative-detail').then(m => m.PieceJustificativeDetail),
    resolve: {
      pieceJustificative: PieceJustificativeResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/piece-justificative-update').then(m => m.PieceJustificativeUpdate),
    resolve: {
      pieceJustificative: PieceJustificativeResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/piece-justificative-update').then(m => m.PieceJustificativeUpdate),
    resolve: {
      pieceJustificative: PieceJustificativeResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default pieceJustificativeRoute;
