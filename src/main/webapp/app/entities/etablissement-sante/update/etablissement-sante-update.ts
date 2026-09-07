import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize } from 'rxjs';

import { AlertError } from 'app/shared/alert';
import { TranslateDirective } from 'app/shared/language';
import { IEtablissementSante } from '../etablissement-sante.model';
import { EtablissementSanteService } from '../service/etablissement-sante.service';

import { EtablissementSanteFormGroup, EtablissementSanteFormService } from './etablissement-sante-form.service';

@Component({
  selector: 'jhi-etablissement-sante-update',
  templateUrl: './etablissement-sante-update.html',
  imports: [TranslateDirective, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class EtablissementSanteUpdate implements OnInit {
  readonly isSaving = signal(false);
  etablissementSante: IEtablissementSante | null = null;

  protected etablissementSanteService = inject(EtablissementSanteService);
  protected etablissementSanteFormService = inject(EtablissementSanteFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: EtablissementSanteFormGroup = this.etablissementSanteFormService.createEtablissementSanteFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ etablissementSante }) => {
      this.etablissementSante = etablissementSante;
      if (etablissementSante) {
        this.updateForm(etablissementSante);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const etablissementSante = this.etablissementSanteFormService.getEtablissementSante(this.editForm);
    if (etablissementSante.id === null) {
      this.subscribeToSaveResponse(this.etablissementSanteService.create(etablissementSante));
    } else {
      this.subscribeToSaveResponse(this.etablissementSanteService.update(etablissementSante));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IEtablissementSante | null>): void {
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

  protected updateForm(etablissementSante: IEtablissementSante): void {
    this.etablissementSante = etablissementSante;
    this.etablissementSanteFormService.resetForm(this.editForm, etablissementSante);
  }
}
