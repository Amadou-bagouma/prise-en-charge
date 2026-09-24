import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config';
import { AlertError } from 'app/shared/alert';
import { TranslateDirective } from 'app/shared/language';
import { IDemandePriseEnCharge } from '../demande-prise-en-charge.model';
import { DemandePriseEnChargeService } from '../service/demande-prise-en-charge.service';

@Component({
  templateUrl: './demande-prise-en-charge-delete-dialog.html',
  imports: [TranslateDirective, FormsModule, FontAwesomeModule, AlertError],
})
export class DemandePriseEnChargeDeleteDialog {
  demandePriseEnCharge?: IDemandePriseEnCharge;

  protected readonly demandePriseEnChargeService = inject(DemandePriseEnChargeService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.demandePriseEnChargeService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
