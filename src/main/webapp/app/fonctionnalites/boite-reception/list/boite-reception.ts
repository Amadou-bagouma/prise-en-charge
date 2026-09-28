import { NgClass } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbPagination } from '@ng-bootstrap/ng-bootstrap/pagination';

import { ITEMS_PER_PAGE, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config';
import { AccountService } from 'app/core/auth';
import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { ItemCount } from 'app/shared/pagination';
import { ITache } from 'app/fonctionnalites/tache/tache.model';
import { IBoiteReception } from '../boite-reception.model';
import { BoiteReceptionService } from '../service/boite-reception.service';

/** Les deux vues d'une boîte : ce qui reste à traiter, et tout ce qui lui est adressé. */
type Vue = 'aTraiter' | 'toutes';

@Component({
  selector: 'jhi-boite-reception',
  templateUrl: './boite-reception.html',
  imports: [
    NgClass,
    RouterLink,
    FontAwesomeModule,
    AlertError,
    Alert,
    TranslateDirective,
    FormatMediumDatetimePipe,
    NgbPagination,
    ItemCount,
  ],
})
export class BoiteReception {
  readonly boite = signal<IBoiteReception | undefined>(undefined);
  readonly taches = signal<ITache[]>([]);
  readonly vue = signal<Vue>('aTraiter');

  readonly itemsPerPage = signal(ITEMS_PER_PAGE);
  readonly totalItems = signal(0);
  readonly page = signal(1);
  readonly isLoading = signal(false);
  /** Vrai seulement au tout premier chargement : un rafraîchissement ne doit pas vider l'écran. */
  readonly premierChargement = signal(true);

  private readonly boiteReceptionService = inject(BoiteReceptionService);
  private readonly accountService = inject(AccountService);

  constructor() {
    this.charger();
    // La consultation est enregistrée une fois, à l'ouverture : c'est la date que verra
    // l'administrateur pour savoir si un rôle suit sa boîte.
    this.boiteReceptionService.marquerConsultee().subscribe({
      next: boite => this.boite.set(boite),
      error() {
        /* la boîte reste affichée sans sa date de consultation */
      },
    });
  }

  charger(): void {
    this.isLoading.set(true);
    // Le tri et le filtre sont demandés au serveur : filtrer après pagination afficherait huit
    // lignes sous un compteur qui en annonce dix.
    this.boiteReceptionService
      .mesTaches({
        page: this.page() - 1,
        size: this.itemsPerPage(),
        sort: ['dateCreation,desc'],
        ouvertes: this.vue() === 'aTraiter',
      })
      .subscribe({
        next: reponse => {
          this.totalItems.set(Number(reponse.headers.get(TOTAL_COUNT_RESPONSE_HEADER) ?? 0));
          this.taches.set(reponse.body ?? []);
          this.isLoading.set(false);
          this.premierChargement.set(false);
        },
        error: () => {
          this.isLoading.set(false);
          this.premierChargement.set(false);
        },
      });
    this.boiteReceptionService.maBoite().subscribe({
      next: boite => this.boite.set(boite),
      error() {
        /* les compteurs restent vides plutôt que faux */
      },
    });
  }

  changerVue(vue: Vue): void {
    if (this.vue() === vue) {
      return;
    }
    this.vue.set(vue);
    // Changer de vue change le jeu de résultats : on repart de la première page, sinon on
    // atterrit sur une page qui n'existe plus.
    this.page.set(1);
    this.charger();
  }

  changerPage(page: number): void {
    this.page.set(page);
    this.charger();
  }

  /**
   * Marque la tâche lue sur place, sans recharger la liste : recharger ferait sauter la ligne
   * sous le curseur au moment même où on la lit.
   */
  marquerLue(tache: ITache): void {
    if (tache.lu) {
      return;
    }
    this.boiteReceptionService.marquerTacheLue(tache.id).subscribe(misAJour => {
      this.taches.update(taches => taches.map(courante => (courante.id === misAJour.id ? { ...courante, lu: true } : courante)));
      this.boite.update(boite => (boite ? { ...boite, nombreNonLus: Math.max(0, (boite.nombreNonLus ?? 1) - 1) } : boite));
    });
  }

  /** Vrai si la tâche est déjà prise en charge par quelqu'un d'autre que moi. */
  priseParUnAutre(tache: ITache): boolean {
    const moi = this.accountService.account()?.login;
    return !!tache.utilisateur && tache.utilisateur.login !== moi;
  }

  /** Vrai si c'est moi qui l'ai prise. */
  priseParMoi(tache: ITache): boolean {
    return !!tache.utilisateur && tache.utilisateur.login === this.accountService.account()?.login;
  }

  /**
   * S'attribuer la tâche, et le montrer sur place.
   *
   * La ligne est remplacée par ce que rend le serveur plutôt que rechargée : c'est lui qui
   * arbitre si quelqu'un l'a prise entre-temps, et recharger ferait sauter la liste sous le
   * curseur au moment où l'on clique.
   */
  prendreEnCharge(tache: ITache): void {
    this.boiteReceptionService.prendreEnCharge(tache.id).subscribe({
      next: misAJour => this.remplacer(misAJour),
      error: () => this.charger(),
    });
  }

  relacher(tache: ITache): void {
    this.boiteReceptionService.relacher(tache.id).subscribe({
      next: misAJour => this.remplacer(misAJour),
      error: () => this.charger(),
    });
  }

  trackId(tache: ITache): number {
    return tache.id;
  }

  /** Une échéance dépassée se voit sans avoir à comparer des dates de tête. */
  estEnRetard(tache: ITache): boolean {
    if (!tache.dateEcheance || tache.statut === 'TERMINEE' || tache.statut === 'ANNULEE') {
      return false;
    }
    return tache.dateEcheance.isBefore(new Date());
  }

  /** Les tons sont ceux du système de design : ok, warn, danger, info, neutre. */
  tonStatut(statut: ITache['statut']): string {
    switch (statut) {
      case 'TERMINEE':
        return 'ok';
      case 'ANNULEE':
        return 'neutre';
      case 'EN_ATTENTE':
        return 'warn';
      default:
        return 'info';
    }
  }

  tonPriorite(priorite: ITache['priorite']): string {
    switch (priorite) {
      case 'URGENTE':
        return 'danger';
      case 'IMPORTANTE':
        return 'warn';
      default:
        return 'neutre';
    }
  }
  private remplacer(misAJour: ITache): void {
    this.taches.update(taches => taches.map(courante => (courante.id === misAJour.id ? misAJour : courante)));
  }
}
