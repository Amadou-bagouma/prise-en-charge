import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { IHistoriqueAction } from '../historique-action.model';

@Component({
  selector: 'jhi-historique-action-detail',
  templateUrl: './historique-action-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, RouterLink, FormatMediumDatetimePipe],
})
export class HistoriqueActionDetail {
  readonly historiqueAction = input<IHistoriqueAction | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
