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

  /**
   * Les droits proposés, avec leur description.
   *
   * Lus au serveur plutôt que codés en dur : la liste est administrable, et une liste figée ici
   * s'en écarterait au premier ajout. La description vient de `InitialisationHabilitations`.
   */
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly droitsDisponibles = computed(() => this.authorityService.authorities());

  /** Les droits actuellement cochés, lus dans le formulaire. */
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly droitsChoisis = computed(() => this.droitsCoches());

  // eslint-disable-next-line @typescript-eslint/member-ordering
  private readonly droitsCoches = signal<string[]>([]);

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

  /** Vrai si ce droit est accordé par le profil en cours d'édition. */
  estAccorde(nom: string): boolean {
    return this.droitsCoches().includes(nom);
  }

  /**
   * Coche ou décoche un droit, et reporte la liste dans le formulaire.
   *
   * Le contrôle est marqué modifié : sans cela, le message « au moins un droit » ne
   * s'afficherait jamais tant que l'agent n'aurait pas touché un autre champ.
   */
  basculerDroit(nom: string): void {
    const courant = this.droitsCoches();
    const suivant = courant.includes(nom) ? courant.filter(droit => droit !== nom) : [...courant, nom];
    this.droitsCoches.set(suivant);
    this.editForm.controls.authorities.setValue(suivant);
    this.editForm.controls.authorities.markAsDirty();
    this.editForm.controls.authorities.markAsTouched();
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
    this.droitsCoches.set([...(profil.authorities ?? [])]);
  }
}
