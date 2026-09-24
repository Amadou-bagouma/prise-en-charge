import { ChangeDetectorRef, Component, ElementRef, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize } from 'rxjs';

import { LANGUAGES } from 'app/config';
import { AlertError } from 'app/shared/alert';
import { ConfirmService } from 'app/shared/confirm';
import { AlertService } from 'app/core/util/alert.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { ProfilService } from 'app/fonctionnalites/profil/service/profil.service';
import { FindLanguageFromKeyPipe, TranslateDirective } from 'app/shared/language';
import { UserManagementService } from '../service/user-management.service';
import { IUserManagement, NewUserManagement } from '../user-management.model';

import { UserManagementFormGroup, UserManagementFormService } from './user-management-form.service';

@Component({
  selector: 'jhi-user-management-update',
  templateUrl: './user-management-update.html',
  imports: [TranslateDirective, FindLanguageFromKeyPipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class UserManagementUpdate implements OnInit {
  readonly isSaving = signal(false);
  userManagement: IUserManagement | null = null;
  langKeyValues = LANGUAGES;

  protected userManagementService = inject(UserManagementService);
  protected userManagementFormService = inject(UserManagementFormService);
  protected activatedRoute = inject(ActivatedRoute);
  protected readonly confirmService = inject(ConfirmService);
  protected readonly profilService = inject(ProfilService);
  protected dataUtils = inject(DataUtils);
  protected alertService = inject(AlertService);
  protected elementRef = inject(ElementRef);
  protected cdr = inject(ChangeDetectorRef);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly profils = this.profilService.profils;
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly compareProfil = this.profilService.compareProfil.bind(this.profilService);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: UserManagementFormGroup = this.userManagementFormService.createUserManagementFormGroup();

  ngOnInit(): void {
    // Load the profils used by the profil select - a plain reference-data list, so a single
    // large page (matching the Gestion/Region/Direction picker convention) is enough.
    this.profilService.profilsParams.set({ size: 100 });
    this.activatedRoute.data.subscribe(({ userManagement }) => {
      this.userManagement = userManagement;
      if (userManagement) {
        this.updateForm(userManagement);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  byteSize(base64String: string | null | undefined): string {
    return base64String ? this.dataUtils.byteSize(base64String) : '';
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      next: () => this.cdr.markForCheck(),
      error: (err: FileLoadError) =>
        this.alertService.addAlert({
          type: 'danger',
          translationKey: `error.file.${err.key}`,
          translationParams: err.params,
        }),
    });
  }

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    const inputElement = this.elementRef.nativeElement.querySelector(`#${idInput}`);
    if (inputElement) {
      inputElement.value = null;
    }
  }

  save(): void {
    const userManagement = this.userManagementFormService.getUserManagement(this.editForm);
    this.confirmService
      .confirmerEnregistrement(userManagement.id === null, userManagement.id === null ? 'un utilisateur' : 'cet utilisateur')
      .subscribe(() => {
        this.isSaving.set(true);
        if (userManagement.id === null) {
          this.subscribeToSaveResponse(this.userManagementService.create(userManagement as NewUserManagement));
        } else {
          this.subscribeToSaveResponse(this.userManagementService.update(userManagement as IUserManagement));
        }
      });
  }

  protected subscribeToSaveResponse(result: Observable<IUserManagement | null>): void {
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

  protected updateForm(userManagement: IUserManagement): void {
    this.userManagement = userManagement;
    this.userManagementFormService.resetForm(this.editForm, userManagement);
  }
}
