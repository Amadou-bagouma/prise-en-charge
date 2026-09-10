import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { AlertError } from 'app/shared/alert';
import { TranslateDirective } from 'app/shared/language';
import { IBoiteReception } from '../boite-reception.model';
import { BoiteReceptionService } from '../service/boite-reception.service';

import { BoiteReceptionFormGroup, BoiteReceptionFormService } from './boite-reception-form.service';

@Component({
  selector: 'jhi-boite-reception-update',
  templateUrl: './boite-reception-update.html',
  imports: [TranslateDirective, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class BoiteReceptionUpdate implements OnInit {
  readonly isSaving = signal(false);
  boiteReception: IBoiteReception | null = null;

  usersSharedCollection = signal<IUser[]>([]);

  protected boiteReceptionService = inject(BoiteReceptionService);
  protected boiteReceptionFormService = inject(BoiteReceptionFormService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: BoiteReceptionFormGroup = this.boiteReceptionFormService.createBoiteReceptionFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ boiteReception }) => {
      this.boiteReception = boiteReception;
      if (boiteReception) {
        this.updateForm(boiteReception);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const boiteReception = this.boiteReceptionFormService.getBoiteReception(this.editForm);
    if (boiteReception.id === null) {
      this.subscribeToSaveResponse(this.boiteReceptionService.create(boiteReception));
    } else {
      this.subscribeToSaveResponse(this.boiteReceptionService.update(boiteReception));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IBoiteReception | null>): void {
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

  protected updateForm(boiteReception: IBoiteReception): void {
    this.boiteReception = boiteReception;
    this.boiteReceptionFormService.resetForm(this.editForm, boiteReception);

    this.usersSharedCollection.update(users => this.userService.addUserToCollectionIfMissing<IUser>(users, boiteReception.utilisateur));
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.boiteReception?.utilisateur)))
      .subscribe((users: IUser[]) => this.usersSharedCollection.set(users));
  }
}
