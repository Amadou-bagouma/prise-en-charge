import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { TranslateDirective } from 'app/shared/language';
import { IProfil } from '../profil.model';

@Component({
  selector: 'jhi-profil-detail',
  templateUrl: './profil-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, RouterLink],
})
export class ProfilDetail {
  readonly profil = input<IProfil | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
