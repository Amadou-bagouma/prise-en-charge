import { HttpClient, HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, catchError, finalize, map, of } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { IDemandePriseEnCharge } from 'app/entities/demande-prise-en-charge/demande-prise-en-charge.model';
import { DemandePriseEnChargeService } from 'app/entities/demande-prise-en-charge/service/demande-prise-en-charge.service';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { AlertError } from 'app/shared/alert';
import { ConfirmService } from 'app/shared/confirm';
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

  /**
   * Les habilitations auxquelles une tâche peut être adressée.
   *
   * Lues au serveur plutôt que codées en dur : la liste des droits est administrable, et une
   * liste figée ici s'en écarterait au premier ajout.
   */
  readonly droitsDisponibles = signal<{ name: string; description?: string | null }[]>([]);

  protected tacheService = inject(TacheService);
  protected tacheFormService = inject(TacheFormService);
  protected demandePriseEnChargeService = inject(DemandePriseEnChargeService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);
  protected readonly http = inject(HttpClient);
  protected readonly confirmService = inject(ConfirmService);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TacheFormGroup = this.tacheFormService.createTacheFormGroup();

  compareDemandePriseEnCharge = (o1: IDemandePriseEnCharge | null, o2: IDemandePriseEnCharge | null): boolean =>
    this.demandePriseEnChargeService.compareDemandePriseEnCharge(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tache }) => {
      this.tache = tache;
      if (tache) {
        this.updateForm(tache);
      }

      this.loadRelationshipsOptions();
      this.chargerDroits();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    const tache = this.tacheFormService.getTache(this.editForm);
    this.confirmService.confirmerEnregistrement(tache.id === null, tache.id === null ? 'une tâche' : 'cette tâche').subscribe(() => {
      this.isSaving.set(true);
      if (tache.id === null) {
        this.subscribeToSaveResponse(this.tacheService.create(tache));
      } else {
        this.subscribeToSaveResponse(this.tacheService.update(tache));
      }
    });
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
  }

  /** Un échec laisse la liste vide : le champ reste saisissable, rien n'est inventé. */
  private chargerDroits(): void {
    this.http
      .get<{ name: string; description?: string | null }[]>(`${serverApiUrl}api/authorities/attribuables`)
      .pipe(catchError(() => of([])))
      .subscribe(droits => this.droitsDisponibles.set(droits));
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
  }
}
