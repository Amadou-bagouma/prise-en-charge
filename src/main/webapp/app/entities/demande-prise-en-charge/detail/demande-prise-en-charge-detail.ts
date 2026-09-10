import { NgClass } from '@angular/common';
import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { IDemandePriseEnCharge } from '../demande-prise-en-charge.model';

@Component({
  selector: 'jhi-demande-prise-en-charge-detail',
  templateUrl: './demande-prise-en-charge-detail.html',
  imports: [NgClass, FontAwesomeModule, Alert, AlertError, TranslateDirective, RouterLink, FormatMediumDatetimePipe],
})
export class DemandePriseEnChargeDetail {
  readonly demandePriseEnCharge = input<IDemandePriseEnCharge | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
