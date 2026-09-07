import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { IAyantDroit } from '../ayant-droit.model';

@Component({
  selector: 'jhi-ayant-droit-detail',
  templateUrl: './ayant-droit-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, RouterLink, FormatMediumDatePipe],
})
export class AyantDroitDetail {
  readonly ayantDroit = input<IAyantDroit | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
