import { Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { AccountService } from 'app/core/auth';

import PasswordStrengthBar from './password-strength-bar/password-strength-bar';
import { PasswordService } from './password.service';

/** Longueur minimale, la même qu'au serveur : l'annoncer ici évite un refus après coup. */
const LONGUEUR_MINIMALE = 8;

/** Au-delà, le serveur refuse. La borne est haute : elle n'interdit qu'un collage accidentel. */
const LONGUEUR_MAXIMALE = 50;

/**
 * Changer son mot de passe.
 *
 * Deux chemins mènent ici : le choix de l'agent, ou le renvoi forcé après une réinitialisation
 * par l'administration — l'application est alors fermée tant que le mot de passe provisoire n'a
 * pas été remplacé. L'écran dit dans quel cas on se trouve, sans quoi on chercherait pourquoi
 * plus rien ne répond.
 *
 * Les règles sont montrées pendant la saisie, et non en reproche après l'envoi : un refus à
 * l'enregistrement fait tout recommencer sans dire ce qui manquait.
 */
@Component({
  selector: 'jhi-password',
  imports: [ReactiveFormsModule, FontAwesomeModule, PasswordStrengthBar],
  templateUrl: './password.html',
})
export default class Password {
  readonly doNotMatch = signal(false);
  readonly error = signal(false);
  readonly success = signal(false);

  /** Un mot de passe que l'on ne peut pas relire se tape deux fois de travers. */
  readonly voirActuel = signal(false);
  readonly voirNouveau = signal(false);

  private readonly accountService = inject(AccountService);
  readonly account = this.accountService.account;

  passwordForm = new FormGroup({
    currentPassword: new FormControl('', { nonNullable: true, validators: Validators.required }),
    newPassword: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(LONGUEUR_MINIMALE), Validators.maxLength(LONGUEUR_MAXIMALE)],
    }),
    confirmPassword: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(LONGUEUR_MINIMALE), Validators.maxLength(LONGUEUR_MAXIMALE)],
    }),
  });

  /** La saisie en cours, suivie pas à pas pour que les règles se cochent en temps réel. */
  readonly nouveau = toSignal(this.passwordForm.controls.newPassword.valueChanges, { initialValue: '' });
  readonly confirmation = toSignal(this.passwordForm.controls.confirmPassword.valueChanges, { initialValue: '' });
  readonly actuel = toSignal(this.passwordForm.controls.currentPassword.valueChanges, { initialValue: '' });

  readonly regles = computed(() => {
    const valeur = this.nouveau();
    return [
      { libelle: `Au moins ${LONGUEUR_MINIMALE} caractères`, respectee: valeur.length >= LONGUEUR_MINIMALE },
      { libelle: 'Une lettre au moins', respectee: /[a-zA-Z]/.test(valeur) },
      { libelle: 'Un chiffre au moins', respectee: /\d/.test(valeur) },
      // Se retaper son propre mot de passe est la faute la plus courante après une
      // réinitialisation : on le signale plutôt que de le refuser sans explication.
      { libelle: 'Différent du mot de passe actuel', respectee: valeur.length > 0 && valeur !== this.actuel() },
      { libelle: 'Les deux saisies correspondent', respectee: valeur.length > 0 && valeur === this.confirmation() },
    ];
  });

  /**
   * Vrai quand l'enregistrement peut aboutir.
   *
   * Les règles sont lues en premier, et le résultat mis de côté avant d'interroger le
   * formulaire : `passwordForm.valid` n'est pas un signal, et l'écrire à gauche d'un `&&` ferait
   * court-circuiter la lecture de `regles()` tant que le formulaire est vide. Le calcul
   * n'enregistrerait alors aucune dépendance, et resterait figé sur « non » — le bouton ne
   * s'activerait jamais, quoi qu'on saisisse.
   */
  readonly peutEnregistrer = computed(() => {
    const toutesRespectees = this.regles().every(regle => regle.respectee);
    return toutesRespectees && this.passwordForm.valid;
  });

  private readonly passwordService = inject(PasswordService);

  /** Vrai quand un champ est fautif et que l'agent y est déjà passé : pas de reproche anticipé. */
  enErreur(nom: 'currentPassword' | 'newPassword' | 'confirmPassword'): boolean {
    const controle = this.passwordForm.get(nom)!;
    return controle.invalid && (controle.dirty || controle.touched);
  }

  changePassword(): void {
    this.error.set(false);
    this.success.set(false);
    this.doNotMatch.set(false);

    const { newPassword, confirmPassword, currentPassword } = this.passwordForm.getRawValue();
    if (newPassword !== confirmPassword) {
      this.doNotMatch.set(true);
      return;
    }
    this.passwordService.save(newPassword, currentPassword).subscribe({
      next: () => {
        this.success.set(true);
        this.passwordForm.reset();
        // Le compte est rechargé : sans cela l'indicateur « changement obligatoire » resterait
        // posé, et le garde de route renverrait indéfiniment sur cet écran.
        this.accountService.identity(true).subscribe();
      },
      error: () => this.error.set(true),
    });
  }
}
