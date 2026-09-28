import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { TranslateDirective } from 'app/shared/language';

/** Émis à la fermeture quand l'agent a confirmé l'opération. */
export const CONFIRMED_EVENT = 'confirmed';

/** Ton du dialogue : `danger` pour ce qui détruit ou refuse, `primaire` sinon. */
export type ConfirmTon = 'primaire' | 'danger';

/**
 * Dialogue de confirmation réutilisable, posé devant toute opération qui engage :
 * validation, retour à l'auteur, resoumission, suppression, activation d'un compte.
 *
 * Les libellés sont fournis par l'appelant et nomment l'action ({@link libelleConfirmer} :
 * « Accorder la prise en charge », pas « OK »), conformément aux règles de rédaction du
 * design system.
 */
@Component({
  selector: 'jhi-confirm-dialog',
  templateUrl: './confirm-dialog.html',
  // `FormsModule` est indispensable : sans lui, `(ngSubmit)` du gabarit se lie a un evenement
  // DOM inexistant au lieu de la directive de formulaire, et la confirmation ne part jamais.
  // Le dialogue s'affichait, le bouton repondait, et l'enregistrement etait perdu en silence.
  imports: [FormsModule, FontAwesomeModule, TranslateDirective],
})
export class ConfirmDialog {
  /** Titre du dialogue : ce qui va se passer, sans point d'exclamation. */
  titre = '';
  /** Ce que l'agent doit savoir avant de trancher, y compris ce qui est irréversible. */
  message = '';
  /** Libellé du bouton de confirmation : il nomme l'action. */
  libelleConfirmer = '';
  ton: ConfirmTon = 'primaire';

  readonly isBusy = signal(false);

  protected readonly activeModal = inject(NgbActiveModal);

  annuler(): void {
    this.activeModal.dismiss();
  }

  confirmer(): void {
    this.isBusy.set(true);
    this.activeModal.close(CONFIRMED_EVENT);
  }
}
