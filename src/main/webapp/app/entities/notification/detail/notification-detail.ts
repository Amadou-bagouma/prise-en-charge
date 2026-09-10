import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { INotification } from '../notification.model';

@Component({
  selector: 'jhi-notification-detail',
  templateUrl: './notification-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, RouterLink, FormatMediumDatetimePipe],
})
export class NotificationDetail {
  readonly notification = input<INotification | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
