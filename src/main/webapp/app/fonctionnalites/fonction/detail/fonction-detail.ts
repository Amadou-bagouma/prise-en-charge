import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { HasAnyAuthorityDirective } from 'app/shared/auth';
import { TranslateDirective } from 'app/shared/language';
import { IFonction } from '../fonction.model';

@Component({
  selector: 'jhi-fonction-detail',
  templateUrl: './fonction-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, RouterLink, HasAnyAuthorityDirective],
})
export class FonctionDetail {
  readonly fonction = input<IFonction | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
