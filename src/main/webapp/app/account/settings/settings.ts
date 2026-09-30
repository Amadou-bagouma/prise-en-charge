import { NgClass } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateService } from '@ngx-translate/core';

import { LANGUAGES } from 'app/config';
import { Account, AccountService } from 'app/core/auth';
import { AlertError } from 'app/shared/alert';
import { FindLanguageFromKeyPipe } from 'app/shared/language';

const initialAccount: Account = {} as Account;

/** Au-delà, la liste devient un écran. Le nombre restant dit qu'il y en a d'autres. */
const DROITS_AFFICHES = 8;

/**
 * Le nom d'un droit, en clair.
 *
 * `ROLE_DEMANDE_VALIDER_DRH` n'apprend rien à qui cherche pourquoi un bouton lui manque : il
 * faut lire le geste, pas la constante. Les libellés complets vivent au serveur ; ici on se
 * contente de rendre le nom lisible, sans un appel de plus sur un écran de consultation.
 */
function intituleCourt(droit: string): string {
  const mots = droit
    .replace(/^ROLE_/, '')
    .toLowerCase()
    .split('_')
    .join(' ');
  return mots.charAt(0).toUpperCase() + mots.slice(1);
}

/**
 * Ce que l'agent peut changer lui-même, et ce qui est décidé pour lui.
 *
 * Les deux sont séparés à dessein : le formulaire à gauche se modifie, la colonne de droite se
 * lit. Mêler les deux fait chercher un bouton d'enregistrement pour un profil qui n'en a pas —
 * et laisse croire qu'on peut s'accorder un droit soi-même.
 */
@Component({
  selector: 'jhi-settings',
  imports: [NgClass, FontAwesomeModule, RouterLink, FindLanguageFromKeyPipe, AlertError, ReactiveFormsModule],
  templateUrl: './settings.html',
})
export default class Settings implements OnInit {
  readonly success = signal(false);
  languages = LANGUAGES;

  settingsForm = new FormGroup({
    firstName: new FormControl(initialAccount.firstName, {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(1), Validators.maxLength(50)],
    }),
    lastName: new FormControl(initialAccount.lastName, {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(1), Validators.maxLength(50)],
    }),
    email: new FormControl(initialAccount.email, {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(5), Validators.maxLength(254), Validators.email],
    }),
    langKey: new FormControl(initialAccount.langKey, { nonNullable: true }),
    activated: new FormControl(initialAccount.activated, { nonNullable: true }),
    authorities: new FormControl(initialAccount.authorities, { nonNullable: true }),
    imageUrl: new FormControl(initialAccount.imageUrl, { nonNullable: true }),
    login: new FormControl(initialAccount.login, { nonNullable: true }),
  });

  private readonly accountService = inject(AccountService);
  private readonly translateService = inject(TranslateService);

  /** Le compte tel que le serveur le rend : c'est lui qui porte le profil et les droits. */
  private readonly compte = this.accountService.account;

  readonly actif = computed(() => this.compte()?.activated ?? false);

  readonly profil = computed(() => this.compte()?.profil?.nom ?? null);

  readonly droits = computed(() =>
    (this.compte()?.authorities ?? [])
      .map(intituleCourt)
      .sort((a, b) => a.localeCompare(b, 'fr'))
      .slice(0, DROITS_AFFICHES),
  );

  readonly resteDroits = computed(() => Math.max(0, (this.compte()?.authorities ?? []).length - DROITS_AFFICHES));

  ngOnInit(): void {
    this.accountService.identity().subscribe(account => {
      if (account) {
        this.settingsForm.patchValue(account);
      }
    });
  }

  /** Vrai quand un champ est fautif et que l'agent y est déjà passé : pas de reproche anticipé. */
  enErreur(nom: 'firstName' | 'lastName' | 'email'): boolean {
    const controle = this.settingsForm.get(nom)!;
    return controle.invalid && (controle.dirty || controle.touched);
  }

  save(): void {
    this.success.set(false);

    const account = this.settingsForm.getRawValue();
    this.accountService.save(account).subscribe({
      next: () => {
        this.success.set(true);

        this.accountService.authenticate(account);

        if (account.langKey !== this.translateService.getCurrentLang()) {
          this.translateService.use(account.langKey);
        }
      },
      error() {
        // Handled by interceptor.
      },
    });
  }
}
