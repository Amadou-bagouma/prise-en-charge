import { NgClass } from '@angular/common';
import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { IBoiteReception } from '../boite-reception.model';

@Component({
  selector: 'jhi-boite-reception-detail',
  templateUrl: './boite-reception-detail.html',
  imports: [NgClass, FontAwesomeModule, Alert, AlertError, TranslateDirective, RouterLink, FormatMediumDatetimePipe],
})
export class BoiteReceptionDetail {
  readonly boiteReception = input<IBoiteReception | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
