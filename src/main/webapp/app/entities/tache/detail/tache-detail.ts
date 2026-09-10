import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { ITache } from '../tache.model';

@Component({
  selector: 'jhi-tache-detail',
  templateUrl: './tache-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, RouterLink, FormatMediumDatetimePipe],
})
export class TacheDetail {
  readonly tache = input<ITache | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
