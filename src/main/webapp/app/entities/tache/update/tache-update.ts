import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { IBoiteReception } from 'app/entities/boite-reception/boite-reception.model';
import { BoiteReceptionService } from 'app/entities/boite-reception/service/boite-reception.service';
import { IDemandePriseEnCharge } from 'app/entities/demande-prise-en-charge/demande-prise-en-charge.model';
import { DemandePriseEnChargeService } from 'app/entities/demande-prise-en-charge/service/demande-prise-en-charge.service';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { AlertError } from 'app/shared/alert';
import { TranslateDirective } from 'app/shared/language';

import { TacheService } from '../service/tache.service';
import { ITache } from '../tache.model';

import { TacheFormGroup, TacheFormService } from './tache-form.service';
import { StatutTache } from 'app/entities/enumerations/statut-tache.model';
import { PrioriteTache } from 'app/entities/enumerations/priorite-tache.model';

@Component({
  selector: 'jhi-tache-update',
  templateUrl: './tache-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class TacheUpdate implements OnInit {
  readonly isSaving = signal(false);
  tache: ITache | null = null;
  statutTacheValues = Object.keys(StatutTache);
  prioriteTacheValues = Object.keys(PrioriteTache);

  demandePriseEnChargesSharedCollection = signal<IDemandePriseEnCharge[]>([]);
  usersSharedCollection = signal<IUser[]>([]);
  boiteReceptionsSharedCollection = signal<IBoiteReception[]>([]);

  protected tacheService = inject(TacheService);
  protected tacheFormService = inject(TacheFormService);
  protected demandePriseEnChargeService = inject(DemandePriseEnChargeService);
  protected userService = inject(UserService);
  protected boiteReceptionService = inject(BoiteReceptionService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TacheFormGroup = this.tacheFormService.createTacheFormGroup();

  compareDemandePriseEnCharge = (o1: IDemandePriseEnCharge | null, o2: IDemandePriseEnCharge | null): boolean =>
    this.demandePriseEnChargeService.compareDemandePriseEnCharge(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareBoiteReception = (o1: IBoiteReception | null, o2: IBoiteReception | null): boolean =>
    this.boiteReceptionService.compareBoiteReception(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tache }) => {
      this.tache = tache;
      if (tache) {
        this.updateForm(tache);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const tache = this.tacheFormService.getTache(this.editForm);
    if (tache.id === null) {
      this.subscribeToSaveResponse(this.tacheService.create(tache));
    } else {
      this.subscribeToSaveResponse(this.tacheService.update(tache));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ITache | null>): void {
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

  protected updateForm(tache: ITache): void {
    this.tache = tache;
    this.tacheFormService.resetForm(this.editForm, tache);

    this.demandePriseEnChargesSharedCollection.update(demandePriseEnCharges =>
      this.demandePriseEnChargeService.addDemandePriseEnChargeToCollectionIfMissing<IDemandePriseEnCharge>(
        demandePriseEnCharges,
        tache.demande,
      ),
    );
    this.usersSharedCollection.update(users => this.userService.addUserToCollectionIfMissing<IUser>(users, tache.utilisateur));
    this.boiteReceptionsSharedCollection.update(boiteReceptions =>
      this.boiteReceptionService.addBoiteReceptionToCollectionIfMissing<IBoiteReception>(boiteReceptions, tache.boiteReception),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.demandePriseEnChargeService
      .query()
      .pipe(map((res: HttpResponse<IDemandePriseEnCharge[]>) => res.body ?? []))
      .pipe(
        map((demandePriseEnCharges: IDemandePriseEnCharge[]) =>
          this.demandePriseEnChargeService.addDemandePriseEnChargeToCollectionIfMissing<IDemandePriseEnCharge>(
            demandePriseEnCharges,
            this.tache?.demande,
          ),
        ),
      )
      .subscribe((demandePriseEnCharges: IDemandePriseEnCharge[]) => this.demandePriseEnChargesSharedCollection.set(demandePriseEnCharges));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.tache?.utilisateur)))
      .subscribe((users: IUser[]) => this.usersSharedCollection.set(users));

    this.boiteReceptionService
      .query()
      .pipe(map((res: HttpResponse<IBoiteReception[]>) => res.body ?? []))
      .pipe(
        map((boiteReceptions: IBoiteReception[]) =>
          this.boiteReceptionService.addBoiteReceptionToCollectionIfMissing<IBoiteReception>(boiteReceptions, this.tache?.boiteReception),
        ),
      )
      .subscribe((boiteReceptions: IBoiteReception[]) => this.boiteReceptionsSharedCollection.set(boiteReceptions));
  }
}
