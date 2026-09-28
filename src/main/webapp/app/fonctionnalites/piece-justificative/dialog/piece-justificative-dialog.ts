import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';
import dayjs from 'dayjs/esm';
import { finalize } from 'rxjs';

import { IDemandePriseEnCharge } from 'app/fonctionnalites/demande-prise-en-charge/demande-prise-en-charge.model';
import { AlertError } from 'app/shared/alert';
import { TranslateDirective } from 'app/shared/language';
import { IPieceJustificative, NewPieceJustificative } from '../piece-justificative.model';
import { PieceJustificativeService } from '../service/piece-justificative.service';

/**
 * Joindre une pièce à un dossier, sans quitter le dossier.
 *
 * Le dossier n'est pas redemandé : on est dessus. Quitter l'écran pour un formulaire plein puis
 * y revenir fait perdre la liste déjà lue et la place dans la page, pour deux champs de saisie.
 *
 * La date d'ajout n'est pas saisie non plus : c'est la date du jour, et la laisser modifiable
 * n'offrirait que l'occasion d'antidater une pièce.
 */
@Component({
  selector: 'jhi-piece-justificative-dialog',
  templateUrl: './piece-justificative-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError, TranslateDirective],
})
export class PieceJustificativeDialog {
  /** Le dossier auquel rattacher la pièce, posé par l'appelant à l'ouverture. */
  demande!: Pick<IDemandePriseEnCharge, 'id' | 'reference'>;

  readonly nomFichier = signal('');
  readonly cheminFichier = signal('');
  readonly enCours = signal(false);

  readonly peutEnregistrer = computed(() => !!this.nomFichier().trim() && !!this.cheminFichier().trim() && !this.enCours());

  protected readonly activeModal = inject(NgbActiveModal);
  protected readonly pieceJustificativeService = inject(PieceJustificativeService);

  annuler(): void {
    this.activeModal.dismiss();
  }

  enregistrer(): void {
    if (!this.peutEnregistrer()) {
      return;
    }
    const piece: NewPieceJustificative = {
      id: null,
      nomFichier: this.nomFichier().trim(),
      cheminFichier: this.cheminFichier().trim(),
      dateAjout: dayjs(),
      demande: this.demande,
    };
    this.enCours.set(true);
    this.pieceJustificativeService
      .create(piece)
      .pipe(finalize(() => this.enCours.set(false)))
      .subscribe({
        next: (creee: IPieceJustificative) => this.activeModal.close(creee),
        error() {
          /* l'erreur est affichée par jhi-alert-error, le dialogue reste ouvert avec la saisie */
        },
      });
  }
}
