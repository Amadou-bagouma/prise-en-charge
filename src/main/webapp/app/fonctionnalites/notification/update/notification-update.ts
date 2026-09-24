import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { IDemandePriseEnCharge } from 'app/entities/demande-prise-en-charge/demande-prise-en-charge.model';
import { DemandePriseEnChargeService } from 'app/entities/demande-prise-en-charge/service/demande-prise-en-charge.service';
import { TacheService } from 'app/entities/tache/service/tache.service';
import { ITache } from 'app/entities/tache/tache.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { AlertError } from 'app/shared/alert';
import { ConfirmService } from 'app/shared/confirm';
import { TranslateDirective } from 'app/shared/language';

import { INotification } from '../notification.model';
import { NotificationService } from '../service/notification.service';

import { NotificationFormGroup, NotificationFormService } from './notification-form.service';
import { TypeNotification } from 'app/entities/enumerations/type-notification.model';

@Component({
  selector: 'jhi-notification-update',
  templateUrl: './notification-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class NotificationUpdate implements OnInit {
  readonly isSaving = signal(false);
  notification: INotification | null = null;
  typeNotificationValues = Object.keys(TypeNotification);

  usersSharedCollection = signal<IUser[]>([]);
  demandePriseEnChargesSharedCollection = signal<IDemandePriseEnCharge[]>([]);
  tachesSharedCollection = signal<ITache[]>([]);

  protected notificationService = inject(NotificationService);
  protected notificationFormService = inject(NotificationFormService);
  protected userService = inject(UserService);
  protected demandePriseEnChargeService = inject(DemandePriseEnChargeService);
  protected tacheService = inject(TacheService);
  protected activatedRoute = inject(ActivatedRoute);
  protected readonly confirmService = inject(ConfirmService);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: NotificationFormGroup = this.notificationFormService.createNotificationFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareDemandePriseEnCharge = (o1: IDemandePriseEnCharge | null, o2: IDemandePriseEnCharge | null): boolean =>
    this.demandePriseEnChargeService.compareDemandePriseEnCharge(o1, o2);

  compareTache = (o1: ITache | null, o2: ITache | null): boolean => this.tacheService.compareTache(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ notification }) => {
      this.notification = notification;
      if (notification) {
        this.updateForm(notification);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    const notification = this.notificationFormService.getNotification(this.editForm);
    this.confirmService
      .confirmerEnregistrement(notification.id === null, notification.id === null ? 'une notification' : 'cette notification')
      .subscribe(() => {
        this.isSaving.set(true);
        if (notification.id === null) {
          this.subscribeToSaveResponse(this.notificationService.create(notification));
        } else {
          this.subscribeToSaveResponse(this.notificationService.update(notification));
        }
      });
  }

  protected subscribeToSaveResponse(result: Observable<INotification | null>): void {
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

  protected updateForm(notification: INotification): void {
    this.notification = notification;
    this.notificationFormService.resetForm(this.editForm, notification);

    this.usersSharedCollection.update(users => this.userService.addUserToCollectionIfMissing<IUser>(users, notification.utilisateur));
    this.demandePriseEnChargesSharedCollection.update(demandePriseEnCharges =>
      this.demandePriseEnChargeService.addDemandePriseEnChargeToCollectionIfMissing<IDemandePriseEnCharge>(
        demandePriseEnCharges,
        notification.demande,
      ),
    );
    this.tachesSharedCollection.update(taches => this.tacheService.addTacheToCollectionIfMissing<ITache>(taches, notification.tache));
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.notification?.utilisateur)))
      .subscribe((users: IUser[]) => this.usersSharedCollection.set(users));

    this.demandePriseEnChargeService
      .query()
      .pipe(map((res: HttpResponse<IDemandePriseEnCharge[]>) => res.body ?? []))
      .pipe(
        map((demandePriseEnCharges: IDemandePriseEnCharge[]) =>
          this.demandePriseEnChargeService.addDemandePriseEnChargeToCollectionIfMissing<IDemandePriseEnCharge>(
            demandePriseEnCharges,
            this.notification?.demande,
          ),
        ),
      )
      .subscribe((demandePriseEnCharges: IDemandePriseEnCharge[]) => this.demandePriseEnChargesSharedCollection.set(demandePriseEnCharges));

    this.tacheService
      .query()
      .pipe(map((res: HttpResponse<ITache[]>) => res.body ?? []))
      .pipe(map((taches: ITache[]) => this.tacheService.addTacheToCollectionIfMissing<ITache>(taches, this.notification?.tache)))
      .subscribe((taches: ITache[]) => this.tachesSharedCollection.set(taches));
  }
}
