import { Component, computed, effect, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { NavigationEnd, Router, RouterLink } from '@angular/router';
import { filter } from 'rxjs';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbDropdown, NgbDropdownMenu, NgbDropdownToggle } from '@ng-bootstrap/ng-bootstrap/dropdown';

import { AccountService } from 'app/core/auth';
import { INotification } from 'app/fonctionnalites/notification/notification.model';
import { NotificationService } from 'app/fonctionnalites/notification/service/notification.service';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { AvisDiffuse, AvisTempsReelService } from './avis-temps-reel.service';

/** Au-delà, la liste devient un écran : le lien « tout voir » prend le relais. */
const AVIS_AFFICHES = 6;

/**
 * Les avis reçus, à portée de main dans l'en-tête.
 *
 * Ils vivaient dans la navigation latérale, entre deux écrans de gestion : on ne les voyait
 * qu'en allant les chercher, et un avis arrivé pendant qu'on instruisait un dossier attendait
 * le prochain changement d'écran. Ici ils sont visibles de partout, à côté de la langue et du
 * compte — ce qui relève de la personne, et non du travail en cours.
 */
@Component({
  selector: 'jhi-cloche-notifications',
  templateUrl: './cloche-notifications.html',
  imports: [FontAwesomeModule, RouterLink, FormatMediumDatetimePipe, NgbDropdown, NgbDropdownMenu, NgbDropdownToggle],
})
export class ClocheNotifications {
  /** Les derniers avis non lus, du plus récent au plus ancien. */
  readonly avis = signal<INotification[]>([]);

  readonly nonLus = signal(0);

  readonly enCours = signal(false);

  /** Au-delà de ce qui tient dans la liste, le nombre dit qu'il en reste. */
  readonly reste = computed(() => Math.max(0, this.nonLus() - this.avis().length));

  /** L'avis qui vient d'arriver, affiche en bandeau le temps d'etre lu. */
  readonly alerte = signal<AvisDiffuse | null>(null);

  private readonly notificationService = inject(NotificationService);
  private readonly tempsReel = inject(AvisTempsReelService);
  private readonly accountService = inject(AccountService);
  private readonly router = inject(Router);

  /** Chaque changement d'ecran est une occasion de reverifier ce qui attend. */
  private readonly navigation = toSignal(this.router.events.pipe(filter(e => e instanceof NavigationEnd)), { initialValue: null });

  constructor() {
    effect(() => {
      this.navigation();
      this.accountService.account();
      this.rafraichir();
    });

    // Un avis arrive : la cloche se met a jour, un bandeau le dit a l'ecran, et le navigateur
    // le signale meme si l'onglet est en arriere-plan.
    effect(() => {
      const avis = this.tempsReel.dernier();
      if (!avis) {
        return;
      }
      this.rafraichir();
      this.alerte.set(avis);
      this.signalerAuNavigateur(avis);
    });
  }

  /** Ferme le bandeau sans rien marquer comme lu : le fermer n'est pas le traiter. */
  fermerAlerte(): void {
    this.alerte.set(null);
  }

  /** Ouvre le dossier annonce par le bandeau, et le referme. */
  ouvrirAlerte(): void {
    const avis = this.alerte();
    this.alerte.set(null);
    if (!avis) {
      return;
    }
    this.notificationService.marquerLue(avis.id).subscribe({
      next: () => this.rafraichir(),
      error: () => this.rafraichir(),
    });
    void this.router.navigate(avis.demandeId ? ['/demande-prise-en-charge', avis.demandeId, 'view'] : ['/notification']);
  }

  /**
   * Demande au navigateur d'afficher l'avis, y compris hors de l'onglet.
   *
   * L'autorisation n'est demandee qu'a l'arrivee d'un premier avis, et non au chargement de
   * l'application : une demande de permission surgissant avant qu'on ait rien fait se refuse par
   * reflexe, et le refus est definitif.
   *
   * Un refus n'enleve rien : le bandeau et la cloche restent.
   */
  private signalerAuNavigateur(avis: AvisDiffuse): void {
    if (!('Notification' in globalThis)) {
      return;
    }
    const afficher = (): void => {
      try {
        // eslint-disable-next-line no-new
        new globalThis.Notification(avis.titre ?? 'Nouvel avis', {
          body: avis.message ?? '',
          tag: `avis-${avis.id}`,
        });
      } catch {
        // Certains navigateurs exigent un service worker : le bandeau suffit alors.
      }
    };
    if (globalThis.Notification.permission === 'granted') {
      afficher();
    } else if (globalThis.Notification.permission === 'default') {
      void globalThis.Notification.requestPermission().then(reponse => {
        if (reponse === 'granted') {
          afficher();
        }
      });
    }
  }

  /**
   * Recharge les avis non lus.
   *
   * Appelée à chaque changement d'écran et à l'arrivée d'un avis : un compteur périmé fait
   * perdre confiance dans tout le reste.
   */
  rafraichir(): void {
    if (!this.accountService.account()) {
      this.avis.set([]);
      this.nonLus.set(0);
      return;
    }
    this.enCours.set(true);
    this.notificationService
      .query({
        mesNotifications: true,
        'lu.equals': false,
        page: 0,
        size: AVIS_AFFICHES,
        sort: ['dateCreation,desc'],
      })
      .subscribe({
        next: reponse => {
          this.enCours.set(false);
          this.avis.set(reponse.body ?? []);
          // Le total vient de l'en-tête de pagination : la page n'en montre qu'une poignée.
          const total = Number(reponse.headers.get('X-Total-Count'));
          this.nonLus.set(Number.isNaN(total) ? (reponse.body?.length ?? 0) : total);
        },
        error: () => this.enCours.set(false),
      });
  }

  /**
   * Marque l'avis lu et va au dossier qu'il concerne.
   *
   * Un avis qui ne mène nulle part oblige à retrouver le dossier à la main, à partir de sa
   * seule référence.
   */
  ouvrir(avis: INotification): void {
    this.notificationService.marquerLue(avis.id).subscribe({
      next: () => this.rafraichir(),
      error: () => this.rafraichir(),
    });
    if (avis.demande?.id) {
      void this.router.navigate(['/demande-prise-en-charge', avis.demande.id, 'view']);
    } else {
      void this.router.navigate(['/notification']);
    }
  }

  toutMarquerLu(): void {
    this.notificationService.marquerToutLu().subscribe({
      next: () => this.rafraichir(),
      error: () => this.rafraichir(),
    });
  }
}
