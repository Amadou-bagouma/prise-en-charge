import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize } from 'rxjs';

import { AlertError } from 'app/shared/alert';
import { ConfirmService } from 'app/shared/confirm';
import { TranslateDirective } from 'app/shared/language';
import { IFonction } from '../fonction.model';
import { FonctionService } from '../service/fonction.service';

import { FonctionFormGroup, FonctionFormService } from './fonction-form.service';

@Component({
  selector: 'jhi-fonction-update',
  templateUrl: './fonction-update.html',
  imports: [TranslateDirective, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class FonctionUpdate implements OnInit {
  readonly isSaving = signal(false);
  fonction: IFonction | null = null;

  protected fonctionService = inject(FonctionService);
  protected fonctionFormService = inject(FonctionFormService);
  protected activatedRoute = inject(ActivatedRoute);
  protected readonly confirmService = inject(ConfirmService);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: FonctionFormGroup = this.fonctionFormService.createFonctionFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ fonction }) => {
      this.fonction = fonction;
      if (fonction) {
        this.updateForm(fonction);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    const fonction = this.fonctionFormService.getFonction(this.editForm);
    this.confirmService
      .confirmerEnregistrement(fonction.id === null, fonction.id === null ? 'une fonction' : 'cette fonction')
      .subscribe(() => {
        this.isSaving.set(true);
        if (fonction.id === null) {
          this.subscribeToSaveResponse(this.fonctionService.create(fonction));
        } else {
          this.subscribeToSaveResponse(this.fonctionService.update(fonction));
        }
      });
  }

  protected subscribeToSaveResponse(result: Observable<IFonction | null>): void {
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

  protected updateForm(fonction: IFonction): void {
    this.fonction = fonction;
    this.fonctionFormService.resetForm(this.editForm, fonction);
  }
}
