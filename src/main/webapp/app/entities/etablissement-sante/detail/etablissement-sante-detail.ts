import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { TranslateDirective } from 'app/shared/language';
import { IEtablissementSante } from '../etablissement-sante.model';

@Component({
  selector: 'jhi-etablissement-sante-detail',
  templateUrl: './etablissement-sante-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, RouterLink],
})
export class EtablissementSanteDetail {
  readonly etablissementSante = input<IEtablissementSante | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
