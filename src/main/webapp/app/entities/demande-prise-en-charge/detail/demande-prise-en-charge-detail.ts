import { NgClass } from '@angular/common';
import { Component, computed, effect, inject, input, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { filter, tap } from 'rxjs';

import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { AccountService } from 'app/core/auth';
import { Authority } from 'app/shared/jhipster/constants';
import { IDemandePriseEnCharge } from '../demande-prise-en-charge.model';
import { DemandePriseEnChargeService } from '../service/demande-prise-en-charge.service';
import { DEMANDE_REJECTED_EVENT, DemandePriseEnChargeRejectDialog } from '../reject/demande-prise-en-charge-reject-dialog';

@Component({
  selector: 'jhi-demande-prise-en-charge-detail',
  templateUrl: './demande-prise-en-charge-detail.html',
  imports: [NgClass, FontAwesomeModule, Alert, AlertError, TranslateDirective, RouterLink, FormatMediumDatetimePipe],
})
export class DemandePriseEnChargeDetail {
  readonly demandePriseEnCharge = input<IDemandePriseEnCharge | null>(null);

  readonly current = signal<IDemandePriseEnCharge | null>(null);
  readonly isSaving = signal(false);

  protected readonly demandePriseEnChargeService = inject(DemandePriseEnChargeService);
  protected readonly accountService = inject(AccountService);
  protected readonly modalService = inject(NgbModal);

  readonly canValiderDrh = computed(
    () => this.current()?.statut === 'EN_ATTENTE_VALIDATION_DRH' && this.accountService.hasAnyAuthority(Authority.VALIDATEUR_DRH),
  );
  readonly canValiderInfirmerie = computed(
    () =>
      this.current()?.statut === 'EN_ATTENTE_VALIDATION_INFIRMERIE' && this.accountService.hasAnyAuthority(Authority.VALIDATEUR_INFIRMERIE),
  );
  readonly canValiderOuRejeter = computed(() => this.canValiderDrh() || this.canValiderInfirmerie());
  readonly canSaisir = computed(() => this.accountService.hasAnyAuthority([Authority.USER, Authority.VALIDATEUR_DRH]));
  readonly canResoumettre = computed(() => {
    const demande = this.current();
    const account = this.accountService.account();
    return demande?.statut === 'RETOURNEE' && !!account && demande.gestionnaireCreateur?.login === account.login;
  });

  constructor() {
    effect(() => this.current.set(this.demandePriseEnCharge()));
  }

  previousState(): void {
    globalThis.history.back();
  }

  valider(): void {
    const demande = this.current();
    if (!demande) {
      return;
    }
    this.isSaving.set(true);
    this.demandePriseEnChargeService.valider(demande.id).subscribe({
      next: updated => {
        this.isSaving.set(false);
        this.current.set(updated);
      },
      error: () => this.isSaving.set(false),
    });
  }

  rejeter(): void {
    const demande = this.current();
    if (!demande) {
      return;
    }
    const modalRef = this.modalService.open(DemandePriseEnChargeRejectDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.demandePriseEnCharge = demande;
    modalRef.closed
      .pipe(
        filter(reason => reason === DEMANDE_REJECTED_EVENT),
        tap(() => this.demandePriseEnChargeService.find(demande.id).subscribe(updated => this.current.set(updated))),
      )
      .subscribe();
  }

  resoumettre(): void {
    const demande = this.current();
    if (!demande) {
      return;
    }
    this.isSaving.set(true);
    this.demandePriseEnChargeService.resoumettre(demande.id).subscribe({
      next: updated => {
        this.isSaving.set(false);
        this.current.set(updated);
      },
      error: () => this.isSaving.set(false),
    });
  }
}
