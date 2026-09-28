import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { AlertError } from 'app/shared/alert';
import { TranslateDirective } from 'app/shared/language';

/** Une situation proposée au choix, avec le mot qui l'explique. */
export interface OptionStatut {
  valeur: string;
  libelle: string;
  /** Ce que la situation entraîne, dit à l'agent avant qu'il choisisse. */
  consequence?: string;
}

export interface ResultatChangementStatut {
  statut: string;
  motif: string;
}

/**
 * Changement de situation d'un agent ou d'un ayant droit.
 *
 * Le motif est exigé dès que l'on retire le droit, comme au serveur : une radiation sans raison
 * écrite est inexplicable six mois plus tard, et incontestable au guichet. La règle est reprise
 * ici pour que l'agent la voie avant d'envoyer, pas pour la remplacer — c'est le serveur qui
 * refuse.
 */
@Component({
  selector: 'jhi-changement-statut-dialog',
  templateUrl: './changement-statut-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError, TranslateDirective],
})
export class ChangementStatutDialog {
  /** Ce dont on change la situation, nommé : « Hadiza SOULEY — matricule 3060 ». */
  intitule = '';

  /** La situation actuelle, rappelée pour que le choix se fasse en connaissance de cause. */
  statutCourant?: string | null;

  /** Le mot correspondant à la situation actuelle. */
  libelleCourant?: string | null;

  options: OptionStatut[] = [];

  /** Averti que le changement se répercutera : vrai pour un agent, faux pour un ayant droit. */
  repercute = false;

  readonly statutChoisi = signal<string>('');
  readonly motif = signal<string>('');
  readonly enCours = signal(false);

  /** Le motif n'est obligatoire que pour sortir de l'activité. */
  readonly motifObligatoire = computed(() => !!this.statutChoisi() && this.statutChoisi() !== 'ACTIF');

  readonly peutValider = computed(
    () => !!this.statutChoisi() && this.statutChoisi() !== this.statutCourant && (!this.motifObligatoire() || !!this.motif().trim()),
  );

  readonly consequence = computed(() => this.options.find(o => o.valeur === this.statutChoisi())?.consequence ?? '');

  protected readonly activeModal = inject(NgbActiveModal);

  annuler(): void {
    this.activeModal.dismiss();
  }

  valider(): void {
    if (!this.peutValider() || this.enCours()) {
      return;
    }
    this.enCours.set(true);
    this.activeModal.close({ statut: this.statutChoisi(), motif: this.motif().trim() } satisfies ResultatChangementStatut);
  }
}
