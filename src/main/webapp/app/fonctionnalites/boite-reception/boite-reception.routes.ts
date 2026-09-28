import { Routes } from '@angular/router';

import { userRouteAccessService } from 'app/core/auth';

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
    canActivate: [userRouteAccessService],
  },
];

export default boiteReceptionRoute;
