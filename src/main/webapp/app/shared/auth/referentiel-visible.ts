import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { map } from 'rxjs';

import { AccountService } from 'app/core/auth';
import { Action } from 'app/shared/jhipster/actions.constants';
import { Authority } from 'app/shared/jhipster/constants';

/**
 * Vrai pour qui tient le référentiel, ou instruit des dossiers.
 *
 * Un compte sans boîte de réception — l'agent, l'agent de saisie prod — ne lit le référentiel
 * que pour remplir les listes de ses formulaires : il garde ce droit de lecture côté serveur,
 * mais ni le menu, ni les écrans du référentiel ne lui sont ouverts.
 */
export function voitReferentiel(accountService: AccountService): boolean {
  if (
    accountService.hasAnyAuthority([Authority.ADMIN, Action.REFERENTIEL_CREER, Action.REFERENTIEL_MODIFIER, Action.REFERENTIEL_SUPPRIMER])
  ) {
    return true;
  }
  return accountService.hasAnyAuthority(Action.BOITE_CONSULTER) && !accountService.hasAnyAuthority(Action.ESPACE_AGENT);
}

/**
 * Ferme les écrans du référentiel à qui ne doit pas les voir, même en tapant l'adresse.
 *
 * Un visiteur non connecté passe : c'est `userRouteAccessService` qui le renvoie à la connexion.
 */
export const referentielVisibleGuard: CanActivateFn = () => {
  const accountService = inject(AccountService);
  const router = inject(Router);
  return accountService.identity().pipe(map(account => !account || voitReferentiel(accountService) || router.parseUrl('/accessdenied')));
};
