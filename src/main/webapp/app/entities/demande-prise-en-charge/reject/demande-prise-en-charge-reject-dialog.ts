import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { AlertError } from 'app/shared/alert';
import { TranslateDirective } from 'app/shared/language';
import { IDemandePriseEnCharge } from '../demande-prise-en-charge.model';
import { DemandePriseEnChargeService } from '../service/demande-prise-en-charge.service';

export const DEMANDE_REJECTED_EVENT = 'demandeRejected';

@Component({
  templateUrl: './demande-prise-en-charge-reject-dialog.html',
  imports: [TranslateDirective, FormsModule, FontAwesomeModule, AlertError],
})
export class DemandePriseEnChargeRejectDialog {
  demandePriseEnCharge?: IDemandePriseEnCharge;
  motif = '';
  readonly isSaving = signal(false);

  protected readonly demandePriseEnChargeService = inject(DemandePriseEnChargeService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmReject(id: number): void {
    if (!this.motif.trim()) {
      return;
    }
    this.isSaving.set(true);
    this.demandePriseEnChargeService.rejeter(id, this.motif.trim()).subscribe({
      next: () => {
        this.isSaving.set(false);
        this.activeModal.close(DEMANDE_REJECTED_EVENT);
      },
      error: () => this.isSaving.set(false),
    });
  }
}
