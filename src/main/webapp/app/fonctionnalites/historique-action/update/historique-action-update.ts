import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { IDemandePriseEnCharge } from 'app/entities/demande-prise-en-charge/demande-prise-en-charge.model';
import { DemandePriseEnChargeService } from 'app/entities/demande-prise-en-charge/service/demande-prise-en-charge.service';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { AlertError } from 'app/shared/alert';
import { ConfirmService } from 'app/shared/confirm';
import { TranslateDirective } from 'app/shared/language';
import { IHistoriqueAction } from '../historique-action.model';
import { HistoriqueActionService } from '../service/historique-action.service';

import { HistoriqueActionFormGroup, HistoriqueActionFormService } from './historique-action-form.service';

@Component({
  selector: 'jhi-historique-action-update',
  templateUrl: './historique-action-update.html',
  imports: [TranslateDirective, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class HistoriqueActionUpdate implements OnInit {
  readonly isSaving = signal(false);
  historiqueAction: IHistoriqueAction | null = null;

  demandePriseEnChargesSharedCollection = signal<IDemandePriseEnCharge[]>([]);
  usersSharedCollection = signal<IUser[]>([]);

  protected historiqueActionService = inject(HistoriqueActionService);
  protected historiqueActionFormService = inject(HistoriqueActionFormService);
  protected demandePriseEnChargeService = inject(DemandePriseEnChargeService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);
  protected readonly confirmService = inject(ConfirmService);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: HistoriqueActionFormGroup = this.historiqueActionFormService.createHistoriqueActionFormGroup();

  compareDemandePriseEnCharge = (o1: IDemandePriseEnCharge | null, o2: IDemandePriseEnCharge | null): boolean =>
    this.demandePriseEnChargeService.compareDemandePriseEnCharge(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ historiqueAction }) => {
      this.historiqueAction = historiqueAction;
      if (historiqueAction) {
        this.updateForm(historiqueAction);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    const historiqueAction = this.historiqueActionFormService.getHistoriqueAction(this.editForm);
    this.confirmService
      .confirmerEnregistrement(historiqueAction.id === null, historiqueAction.id === null ? 'une action' : 'cette action')
      .subscribe(() => {
        this.isSaving.set(true);
        if (historiqueAction.id === null) {
          this.subscribeToSaveResponse(this.historiqueActionService.create(historiqueAction));
        } else {
          this.subscribeToSaveResponse(this.historiqueActionService.update(historiqueAction));
        }
      });
  }

  protected subscribeToSaveResponse(result: Observable<IHistoriqueAction | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(historiqueAction: IHistoriqueAction): void {
    this.historiqueAction = historiqueAction;
    this.historiqueActionFormService.resetForm(this.editForm, historiqueAction);

    this.demandePriseEnChargesSharedCollection.update(demandePriseEnCharges =>
      this.demandePriseEnChargeService.addDemandePriseEnChargeToCollectionIfMissing<IDemandePriseEnCharge>(
        demandePriseEnCharges,
        historiqueAction.demande,
      ),
    );
    this.usersSharedCollection.update(users => this.userService.addUserToCollectionIfMissing<IUser>(users, historiqueAction.utilisateur));
  }

  protected loadRelationshipsOptions(): void {
    this.demandePriseEnChargeService
      .query()
      .pipe(map((res: HttpResponse<IDemandePriseEnCharge[]>) => res.body ?? []))
      .pipe(
        map((demandePriseEnCharges: IDemandePriseEnCharge[]) =>
          this.demandePriseEnChargeService.addDemandePriseEnChargeToCollectionIfMissing<IDemandePriseEnCharge>(
            demandePriseEnCharges,
            this.historiqueAction?.demande,
          ),
        ),
      )
      .subscribe((demandePriseEnCharges: IDemandePriseEnCharge[]) => this.demandePriseEnChargesSharedCollection.set(demandePriseEnCharges));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.historiqueAction?.utilisateur)))
      .subscribe((users: IUser[]) => this.usersSharedCollection.set(users));
  }
}
