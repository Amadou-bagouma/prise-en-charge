import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config';
import { AlertError } from 'app/shared/alert';
import { TranslateDirective } from 'app/shared/language';
import { ICarteBeneficiaire } from '../carte-beneficiaire.model';
import { CarteBeneficiaireService } from '../service/carte-beneficiaire.service';

@Component({
  templateUrl: './carte-beneficiaire-delete-dialog.html',
  imports: [TranslateDirective, FormsModule, FontAwesomeModule, AlertError],
})
export class CarteBeneficiaireDeleteDialog {
  carteBeneficiaire?: ICarteBeneficiaire;

  protected readonly carteBeneficiaireService = inject(CarteBeneficiaireService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.carteBeneficiaireService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
