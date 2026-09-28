import { HttpClient } from '@angular/common/http';
import { Service, inject, signal } from '@angular/core';

import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { serverApiUrl } from 'app/config';
import { AccountService } from 'app/core/auth';
import { Authority } from 'app/shared/jhipster/constants';

/**
 * Charge de travail du jour, affichée en compteurs dans la navigation.
 *
 * Les trois compteurs répondent à « qu'est-ce qui m'attend » :
 * - les demandes en attente de MON étape de validation (pour un validateur), ou mes demandes
 *   retournées à corriger (pour un gestionnaire) ;
 * - mes tâches encore ouvertes ;
 * - mes notifications non lues.
 *
 * Le backend restreint déjà tâches et notifications à l'utilisateur courant, et les demandes
 * à son périmètre : les comptes reflètent donc ce que l'agent peut réellement traiter.
 *
 * Un compteur périmé fait perdre confiance dans tout le reste : ils sont rafraîchis à chaque
 * changement d'écran (voir Navbar).
 */
@Service()
export class ChargeTravailService {
  readonly demandes = signal<number | undefined>(undefined);
  readonly taches = signal<number | undefined>(undefined);
  readonly notifications = signal<number | undefined>(undefined);

  private readonly http = inject(HttpClient);
  private readonly accountService = inject(AccountService);

  rafraichir(): void {
    if (!this.accountService.account()) {
      return;
    }

    const statutsAttendus = this.statutsQuiMAttendent();

    forkJoin({
      demandes: this.compter('demande-prise-en-charges', statutsAttendus.length ? { 'statut.in': statutsAttendus } : {}),
      taches: this.compter('taches', { 'statut.in': ['A_FAIRE', 'EN_COURS', 'EN_ATTENTE'] }),
      // Le meme perimetre que la boite de reception : mes notifications, pas celles de tous.
      notifications: this.compter('notifications', { mesNotifications: 'true', 'lu.equals': 'false' }),
    }).subscribe(comptes => {
      this.demandes.set(comptes.demandes);
      this.taches.set(comptes.taches);
      this.notifications.set(comptes.notifications);
    });
  }

  /** Les statuts sur lesquels l'agent courant a la main. */
  private statutsQuiMAttendent(): string[] {
    const statuts: string[] = [];
    if (this.accountService.hasAnyAuthority(Authority.VALIDATEUR_DRH)) {
      statuts.push('EN_ATTENTE_VALIDATION_DRH');
    }
    if (this.accountService.hasAnyAuthority(Authority.VALIDATEUR_INFIRMERIE)) {
      statuts.push('EN_ATTENTE_VALIDATION_INFIRMERIE');
    }
    // Un gestionnaire sans rôle de validation suit ce qui lui revient à corriger.
    if (statuts.length === 0) {
      statuts.push('RETOURNEE');
    }
    return statuts;
  }

  /** Un compteur indisponible ne casse pas la navigation : il reste simplement vide. */
  private compter(ressource: string, params: Record<string, string | string[]>) {
    return this.http
      .get<number>(`${serverApiUrl}api/${ressource}/count`, { params })
      .pipe(catchError(() => of(undefined as number | undefined)));
  }
}
