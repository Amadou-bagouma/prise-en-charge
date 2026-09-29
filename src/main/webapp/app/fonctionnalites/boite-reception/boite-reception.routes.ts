import { Routes } from '@angular/router';

import { userRouteAccessService } from 'app/core/auth';
import { Action } from 'app/shared/jhipster/actions.constants';
import { Authority } from 'app/shared/jhipster/constants';

/**
 * Une seule route : la boite de reception de son propre profil.
 *
 * Il n'y a ni creation, ni modification, ni suppression - une boite nait avec son profil - et
 * aucune route ne prend d'identifiant de boite : c'est le serveur qui resout le profil du compte
 * appelant, de sorte qu'aucune URL ne permet d'aller lire la boite d'un autre role.
 */
const boiteReceptionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/boite-reception').then(m => m.BoiteReception),
    data: {
      authorities: [Action.BOITE_CONSULTER, Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
  },
];

export default boiteReceptionRoute;
