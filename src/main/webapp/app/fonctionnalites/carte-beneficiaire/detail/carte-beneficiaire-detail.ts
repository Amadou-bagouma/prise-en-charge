import { NgClass } from '@angular/common';
import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { ICarteBeneficiaire } from '../carte-beneficiaire.model';
import { etatValidite, libelleValidite, tonValidite } from '../validite';

@Component({
  selector: 'jhi-carte-beneficiaire-detail',
  templateUrl: './carte-beneficiaire-detail.html',
  imports: [NgClass, FontAwesomeModule, Alert, AlertError, TranslateDirective, RouterLink, FormatMediumDatePipe],
})
export class CarteBeneficiaireDetail {
  readonly carteBeneficiaire = input<ICarteBeneficiaire | null>(null);

  /** Le mot qui dit si la carte est utilisable aujourd'hui. */
  libelleValidite = libelleValidite;

  /** Le ton correspondant, du système de design. */
  tonValidite = tonValidite;

  /**
   * L'état de validité, pour dire quoi faire plutôt que de laisser l'agent le déduire.
   *
   * Une carte expirée présentée au prestataire est refusée sur place : autant l'écrire sur la
   * fiche, à côté des dates, plutôt que de compter sur la comparaison mentale de deux dates.
   */
  etat = etatValidite;

  previousState(): void {
    globalThis.history.back();
  }
}
