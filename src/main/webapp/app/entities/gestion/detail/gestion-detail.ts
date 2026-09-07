import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { TranslateDirective } from 'app/shared/language';
import { IGestion } from '../gestion.model';

@Component({
  selector: 'jhi-gestion-detail',
  templateUrl: './gestion-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, RouterLink],
})
export class GestionDetail {
  readonly gestion = input<IGestion | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
