import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { ICarteBeneficiaire } from '../carte-beneficiaire.model';

@Component({
  selector: 'jhi-carte-beneficiaire-detail',
  templateUrl: './carte-beneficiaire-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, RouterLink, FormatMediumDatePipe],
})
export class CarteBeneficiaireDetail {
  readonly carteBeneficiaire = input<ICarteBeneficiaire | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
