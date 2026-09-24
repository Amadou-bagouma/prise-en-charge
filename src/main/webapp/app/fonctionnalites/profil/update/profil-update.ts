import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize } from 'rxjs';

import { AuthorityService } from 'app/fonctionnalites/admin/authority/service/authority.service';
import { AlertError } from 'app/shared/alert';
import { ConfirmService } from 'app/shared/confirm';
import { TranslateDirective } from 'app/shared/language';
import { IProfil } from '../profil.model';
import { ProfilService } from '../service/profil.service';

import { ProfilFormGroup, ProfilFormService } from './profil-form.service';

@Component({
  selector: 'jhi-profil-update',
  templateUrl: './profil-update.html',
  imports: [TranslateDirective, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class ProfilUpdate implements OnInit {
  readonly isSaving = signal(false);
  profil: IProfil | null = null;

  protected profilService = inject(ProfilService);
  protected profilFormService = inject(ProfilFormService);
  protected activatedRoute = inject(ActivatedRoute);
  protected readonly confirmService = inject(ConfirmService);
  protected readonly authorityService = inject(AuthorityService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly authorities = computed(() => this.authorityService.authorities().map(authority => authority.name));

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ProfilFormGroup = this.profilFormService.createProfilFormGroup();

  ngOnInit(): void {
    this.authorityService.authoritiesParams.set({});
    this.activatedRoute.data.subscribe(({ profil }) => {
      this.profil = profil;
      if (profil) {
        this.updateForm(profil);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    const profil = this.profilFormService.getProfil(this.editForm);
    this.confirmService.confirmerEnregistrement(profil.id === null, profil.id === null ? 'un profil' : 'ce profil').subscribe(() => {
      this.isSaving.set(true);
      if (profil.id === null) {
        this.subscribeToSaveResponse(this.profilService.create(profil));
      } else {
        this.subscribeToSaveResponse(this.profilService.update(profil));
      }
    });
  }

  protected subscribeToSaveResponse(result: Observable<IProfil | null>): void {
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

  protected updateForm(profil: IProfil): void {
    this.profil = profil;
    this.profilFormService.resetForm(this.editForm, profil);
  }
}
