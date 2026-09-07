import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { IAgent } from '../agent.model';

@Component({
  selector: 'jhi-agent-detail',
  templateUrl: './agent-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, RouterLink, FormatMediumDatePipe],
})
export class AgentDetail {
  readonly agent = input<IAgent | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
