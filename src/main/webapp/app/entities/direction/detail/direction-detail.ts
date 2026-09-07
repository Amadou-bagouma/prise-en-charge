import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { TranslateDirective } from 'app/shared/language';
import { IDirection } from '../direction.model';

@Component({
  selector: 'jhi-direction-detail',
  templateUrl: './direction-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, RouterLink],
})
export class DirectionDetail {
  readonly direction = input<IDirection | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
