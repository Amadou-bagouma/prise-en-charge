import { Component, inject, signal } from '@angular/core';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

/**
 * Remet à l'administrateur le mot de passe provisoire d'un compte.
 *
 * Il n'est affiché qu'ici, et qu'une fois : le serveur ne le conserve que chiffré, et rouvrir
 * cet écran ne le retrouverait pas. C'est dit explicitement, sans quoi on fermerait la fenêtre
 * en pensant pouvoir y revenir.
 */
@Component({
  selector: 'jhi-mot-de-passe-provisoire-dialog',
  templateUrl: './mot-de-passe-provisoire-dialog.html',
  imports: [FontAwesomeModule],
})
export class MotDePasseProvisoireDialog {
  /** Le compte concerné, rappelé pour ne pas remettre le mot de passe d'un agent à un autre. */
  login = '';

  /** Le mot de passe en clair, tel que le serveur vient de le rendre. */
  motDePasse = '';

  readonly copie = signal(false);

  protected readonly activeModal = inject(NgbActiveModal);

  /** Le recopier à la main invite à la faute de frappe, et la faute ne se verra qu'à la connexion. */
  copier(): void {
    navigator.clipboard.writeText(this.motDePasse).then(
      () => this.copie.set(true),
      () => this.copie.set(false),
    );
  }

  fermer(): void {
    this.activeModal.close();
  }
}
