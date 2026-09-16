import { ChangeDetectorRef, Component, ElementRef, OnInit, computed, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize } from 'rxjs';

import { LANGUAGES } from 'app/config';
import { AuthorityService } from 'app/entities/admin/authority/service/authority.service';
import { AlertError } from 'app/shared/alert';
import { AlertService } from 'app/core/util/alert.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
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
  protected readonly authorityService = inject(AuthorityService);
  protected dataUtils = inject(DataUtils);
  protected alertService = inject(AlertService);
  protected elementRef = inject(ElementRef);
  protected cdr = inject(ChangeDetectorRef);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly authorities = computed(() => this.authorityService.authorities().map(authority => authority.name));

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: UserManagementFormGroup = this.userManagementFormService.createUserManagementFormGroup();

  ngOnInit(): void {
    // Load the authorities used by the profiles select.
    this.authorityService.authoritiesParams.set({});
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
    this.isSaving.set(true);
    const userManagement = this.userManagementFormService.getUserManagement(this.editForm);
    if (userManagement.id === null) {
      this.subscribeToSaveResponse(this.userManagementService.create(userManagement as NewUserManagement));
    } else {
      this.subscribeToSaveResponse(this.userManagementService.update(userManagement as IUserManagement));
    }
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
