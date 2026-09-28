import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize } from 'rxjs';

import { AlertError } from 'app/shared/alert';
import { ConfirmService } from 'app/shared/confirm';
import { TranslateDirective } from 'app/shared/language';
import { ITypeSoin } from '../type-soin.model';
import { TypeSoinService } from '../service/type-soin.service';

import { TypeSoinFormGroup, TypeSoinFormService } from './type-soin-form.service';

@Component({
  selector: 'jhi-type-soin-update',
  templateUrl: './type-soin-update.html',
  imports: [TranslateDirective, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class TypeSoinUpdate implements OnInit {
  readonly isSaving = signal(false);
  typeSoin: ITypeSoin | null = null;

  protected typeSoinService = inject(TypeSoinService);
  protected typeSoinFormService = inject(TypeSoinFormService);
  protected activatedRoute = inject(ActivatedRoute);
  protected readonly confirmService = inject(ConfirmService);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TypeSoinFormGroup = this.typeSoinFormService.createTypeSoinFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ typeSoin }) => {
      this.typeSoin = typeSoin;
      if (typeSoin) {
        this.updateForm(typeSoin);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    const typeSoin = this.typeSoinFormService.getTypeSoin(this.editForm);
    this.confirmService
      .confirmerEnregistrement(typeSoin.id === null, typeSoin.id === null ? 'une région' : 'cette région')
      .subscribe(() => {
        this.isSaving.set(true);
        if (typeSoin.id === null) {
          this.subscribeToSaveResponse(this.typeSoinService.create(typeSoin));
        } else {
          this.subscribeToSaveResponse(this.typeSoinService.update(typeSoin));
        }
      });
  }

  protected subscribeToSaveResponse(result: Observable<ITypeSoin | null>): void {
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

  protected updateForm(typeSoin: ITypeSoin): void {
    this.typeSoin = typeSoin;
    this.typeSoinFormService.resetForm(this.editForm, typeSoin);
  }
}
