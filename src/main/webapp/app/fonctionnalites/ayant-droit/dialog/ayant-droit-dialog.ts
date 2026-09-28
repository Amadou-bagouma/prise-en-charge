import { ChangeDetectorRef, Component, ElementRef, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { NgbInputDatepicker } from '@ng-bootstrap/ng-bootstrap/datepicker';
import { TranslatePipe } from '@ngx-translate/core';
import { catchError, finalize, of } from 'rxjs';

import { AlertService } from 'app/core/util/alert.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IAgent } from 'app/fonctionnalites/agent/agent.model';
import { LienParente } from 'app/fonctionnalites/enumerations/lien-parente.model';
import { AlertError } from 'app/shared/alert';
import { TranslateDirective } from 'app/shared/language';
import { IAyantDroit, NewAyantDroit } from '../ayant-droit.model';
import { AyantDroitService } from '../service/ayant-droit.service';
import { AyantDroitFormGroup, AyantDroitFormService } from '../update/ayant-droit-form.service';

/**
 * Ajout d'un ayant droit depuis la fiche de son agent.
 *
 * Le dialogue existe parce que l'ajout est un geste rattaché à une fiche : quitter l'écran pour
 * un formulaire plein puis y revenir fait perdre le contexte — la liste déjà lue, la position
 * dans la page — pour une saisie de cinq champs.
 *
 * L'agent n'est pas redemandé, il est affiché. On est sur sa fiche : le rouvrir dans une liste
 * déroulante n'offrirait que l'occasion de se tromper.
 *
 * La confirmation d'enregistrement du reste de l'application est volontairement absente ici :
 * ouvrir le dialogue est déjà le geste délibéré, et empiler une boîte sur une boîte pour le
 * confirmer ferait deux fenêtres à lire pour un seul ajout.
 */
@Component({
  selector: 'jhi-ayant-droit-dialog',
  templateUrl: './ayant-droit-dialog.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule, NgbInputDatepicker],
})
export class AyantDroitDialog {
  /**
   * L'agent auquel rattacher l'ayant droit, posé par l'appelant à l'ouverture.
   *
   * Propriété simple et non entrée signal : ng-bootstrap affecte `componentInstance`
   * directement, ce qui ne renseignerait pas une entrée signal.
   */
  agent!: IAgent;

  readonly isSaving = signal(false);
  readonly lienParenteValues = Object.keys(LienParente);

  protected readonly activeModal = inject(NgbActiveModal);
  protected readonly dataUtils = inject(DataUtils);
  protected readonly alertService = inject(AlertService);
  protected readonly ayantDroitService = inject(AyantDroitService);
  protected readonly ayantDroitFormService = inject(AyantDroitFormService);
  protected readonly elementRef = inject(ElementRef);
  protected readonly cdr = inject(ChangeDetectorRef);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly editForm: AyantDroitFormGroup = this.ayantDroitFormService.createAyantDroitFormGroup();

  constructor() {
    // Le lien seul fait varier le code : l'agent, lui, ne bougera pas de ce dialogue.
    this.editForm.controls.lien.valueChanges.subscribe(() => this.rafraichirCodeSuggere());
  }

  annuler(): void {
    this.activeModal.dismiss();
  }

  byteSize(base64String: string | null | undefined): string {
    return base64String ? this.dataUtils.byteSize(base64String) : '';
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      next: () => this.cdr.markForCheck(),
      error: (err: FileLoadError) =>
        this.alertService.addAlert({
          type: 'danger',
          translationKey: `error.file.${err.key}`,
          translationParams: err.params,
        }),
    });
  }

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({ [field]: null, [fieldContentType]: null });
    const inputElement = this.elementRef.nativeElement.querySelector(`#${idInput}`);
    if (inputElement) {
      inputElement.value = null;
    }
  }

  /**
   * Enregistre et rend l'ayant droit créé à l'appelant, qui l'insère dans sa liste sans
   * recharger l'écran.
   */
  enregistrer(): void {
    if (this.editForm.invalid || this.isSaving()) {
      return;
    }
    const ayantDroit = { ...this.ayantDroitFormService.getAyantDroit(this.editForm), agent: this.agent } as NewAyantDroit;
    this.isSaving.set(true);
    this.ayantDroitService
      .create(ayantDroit)
      .pipe(finalize(() => this.isSaving.set(false)))
      .subscribe({
        next: (cree: IAyantDroit) => this.activeModal.close(cree),
        error() {
          /* l'erreur est affichée par jhi-alert-error, le dialogue reste ouvert avec la saisie */
        },
      });
  }

  /**
   * Aperçu du code attribué par le serveur.
   *
   * Un échec reste silencieux : l'aperçu est un confort, et c'est de toute façon le serveur qui
   * attribue le code à l'enregistrement.
   */
  private rafraichirCodeSuggere(): void {
    const lien = this.editForm.controls.lien.value;
    if (!lien) {
      this.editForm.controls.codeAyantDroit.setValue(null);
      return;
    }
    this.ayantDroitService
      .codeSuggere(this.agent.id, lien)
      .pipe(catchError(() => of(null)))
      .subscribe(code => this.editForm.controls.codeAyantDroit.setValue(code));
  }
}
