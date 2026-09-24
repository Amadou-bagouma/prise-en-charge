import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize } from 'rxjs';

import { TypeGestion } from 'app/entities/enumerations/type-gestion.model';
import { AlertError } from 'app/shared/alert';
import { ConfirmService } from 'app/shared/confirm';
import { TranslateDirective } from 'app/shared/language';
import { IGestion } from '../gestion.model';
import { GestionService } from '../service/gestion.service';

import { GestionFormGroup, GestionFormService } from './gestion-form.service';

@Component({
  selector: 'jhi-gestion-update',
  templateUrl: './gestion-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class GestionUpdate implements OnInit {
  readonly isSaving = signal(false);
  gestion: IGestion | null = null;
  typeGestionValues = Object.keys(TypeGestion);

  protected gestionService = inject(GestionService);
  protected gestionFormService = inject(GestionFormService);
  protected activatedRoute = inject(ActivatedRoute);
  protected readonly confirmService = inject(ConfirmService);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: GestionFormGroup = this.gestionFormService.createGestionFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ gestion }) => {
      this.gestion = gestion;
      if (gestion) {
        this.updateForm(gestion);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    const gestion = this.gestionFormService.getGestion(this.editForm);
    this.confirmService
      .confirmerEnregistrement(gestion.id === null, gestion.id === null ? 'une gestion' : 'cette gestion')
      .subscribe(() => {
        this.isSaving.set(true);
        if (gestion.id === null) {
          this.subscribeToSaveResponse(this.gestionService.create(gestion));
        } else {
          this.subscribeToSaveResponse(this.gestionService.update(gestion));
        }
      });
  }

  protected subscribeToSaveResponse(result: Observable<IGestion | null>): void {
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

  protected updateForm(gestion: IGestion): void {
    this.gestion = gestion;
    this.gestionFormService.resetForm(this.editForm, gestion);
  }
}
