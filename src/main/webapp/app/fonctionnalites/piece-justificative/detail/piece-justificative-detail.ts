import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { IPieceJustificative } from '../piece-justificative.model';

@Component({
  selector: 'jhi-piece-justificative-detail',
  templateUrl: './piece-justificative-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, RouterLink, FormatMediumDatetimePipe],
})
export class PieceJustificativeDetail {
  readonly pieceJustificative = input<IPieceJustificative | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
