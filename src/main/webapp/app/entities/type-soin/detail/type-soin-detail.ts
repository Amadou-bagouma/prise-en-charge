import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { TranslateDirective } from 'app/shared/language';
import { ITypeSoin } from '../type-soin.model';

@Component({
  selector: 'jhi-type-soin-detail',
  templateUrl: './type-soin-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, RouterLink],
})
export class TypeSoinDetail {
  readonly typeSoin = input<ITypeSoin | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
