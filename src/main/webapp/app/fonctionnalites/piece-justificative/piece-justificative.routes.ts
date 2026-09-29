import { Routes } from '@angular/router';

import { DESC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';
import { Action } from 'app/shared/jhipster/actions.constants';
import { Authority } from 'app/shared/jhipster/constants';

import PieceJustificativeResolve from './route/piece-justificative-routing-resolve.service';

const pieceJustificativeRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/piece-justificative').then(m => m.PieceJustificative),
    data: {
      authorities: [Action.PIECE_CONSULTER, Authority.ADMIN],
      defaultSort: `id,${DESC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/piece-justificative-detail').then(m => m.PieceJustificativeDetail),
    resolve: {
      pieceJustificative: PieceJustificativeResolve,
    },
    data: {
      authorities: [Action.PIECE_CONSULTER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/piece-justificative-update').then(m => m.PieceJustificativeUpdate),
    resolve: {
      pieceJustificative: PieceJustificativeResolve,
    },
    data: {
      authorities: [Action.PIECE_AJOUTER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/piece-justificative-update').then(m => m.PieceJustificativeUpdate),
    resolve: {
      pieceJustificative: PieceJustificativeResolve,
    },
    data: {
      authorities: [Action.PIECE_MODIFIER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
];

export default pieceJustificativeRoute;
