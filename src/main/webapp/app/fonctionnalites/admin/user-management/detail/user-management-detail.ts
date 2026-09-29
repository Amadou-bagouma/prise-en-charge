import { NgClass } from '@angular/common';
import { Component, computed, inject, input, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { filter } from 'rxjs';

import { AccountService } from 'app/core/auth';
import { Alert, AlertError } from 'app/shared/alert';
import { CONFIRMED_EVENT, ConfirmDialog } from 'app/shared/confirm';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { MotDePasseProvisoireDialog } from '../mot-de-passe/mot-de-passe-provisoire-dialog';
import { UserManagementService } from '../service/user-management.service';
import { IUserManagement } from '../user-management.model';

/**
 * La fiche d'un compte, et ce que l'administration peut en faire.
 *
 * Deux gestes y vivent, qui n'ont pas leur place ailleurs : rendre l'accès à quelqu'un qui ne
 * peut plus entrer, et fermer celui d'un agent parti. Fermer n'est pas supprimer — les dossiers
 * instruits gardent leur auteur, et l'historique reste lisible.
 */
@Component({
  selector: 'jhi-user-management-detail',
  templateUrl: './user-management-detail.html',
  imports: [NgClass, FontAwesomeModule, Alert, AlertError, TranslateDirective, RouterLink, FormatMediumDatetimePipe],
})
export class UserManagementDetail {
  readonly userManagement = input<IUserManagement | null>(null);

  /**
   * Le compte tel qu'il doit s'afficher : celui que le serveur vient de rendre, sinon celui qui
   * a été chargé. Une entrée ne se réassigne pas, d'où le signal de remplacement.
   */
  readonly compteAffiche = computed(() => this.compteRemplace() ?? this.userManagement());

  readonly identite = computed(() => {
    const compte = this.compteAffiche();
    if (!compte) {
      return '';
    }
    return [compte.firstName, compte.lastName].filter(Boolean).join(' ') || compte.login;
  });

  /** Se fermer son propre compte revient à se mettre dehors : l'action ne s'offre pas. */
  readonly estSoiMeme = computed(() => {
    const compte = this.compteAffiche();
    const courant = this.accountService.account();
    return !!compte && !!courant && courant.login === compte.login;
  });

  protected readonly accountService = inject(AccountService);
  protected readonly modalService = inject(NgbModal);
  protected readonly userManagementService = inject(UserManagementService);

  private readonly compteRemplace = signal<IUserManagement | null>(null);

  /**
   * Donne un mot de passe provisoire au compte.
   *
   * La confirmation vient d'abord : l'ancien mot de passe cesse de fonctionner à l'instant où
   * celui-ci est posé, et l'intéressé se retrouve dehors s'il n'a pas été prévenu.
   */
  reinitialiserMotDePasse(): void {
    const compte = this.compteAffiche();
    if (!compte) {
      return;
    }
    const modalRef = this.modalService.open(ConfirmDialog, { size: 'md', backdrop: 'static' });
    Object.assign(modalRef.componentInstance, {
      titre: 'Réinitialiser le mot de passe',
      message: `Le mot de passe actuel de ${compte.login} cessera aussitôt de fonctionner. Un mot de passe provisoire vous sera remis, à lui transmettre ; il devra en choisir un autre à sa prochaine connexion.`,
      libelleConfirmer: 'Réinitialiser',
      ton: 'danger',
    });
    modalRef.closed.pipe(filter(reason => reason === CONFIRMED_EVENT)).subscribe(() => {
      this.userManagementService.reinitialiserMotDePasse(compte.login).subscribe(resultat => {
        const fenetre = this.modalService.open(MotDePasseProvisoireDialog, { size: 'md', backdrop: 'static' });
        fenetre.componentInstance.login = resultat.login;
        fenetre.componentInstance.motDePasse = resultat.motDePasseProvisoire;
        // La fiche est rechargée : elle doit porter « doit changer son mot de passe ».
        fenetre.closed.subscribe(() => this.userManagementService.find(compte.login).subscribe(a => this.compteRemplace.set(a)));
      });
    });
  }

  changerActivation(actif: boolean): void {
    const compte = this.compteAffiche();
    if (!compte) {
      return;
    }
    const modalRef = this.modalService.open(ConfirmDialog, { size: 'md', backdrop: 'static' });
    Object.assign(modalRef.componentInstance, {
      titre: actif ? 'Ouvrir le compte' : 'Fermer le compte',
      message: actif
        ? `Le compte ${compte.login} pourra de nouveau se connecter.`
        : `Le compte ${compte.login} ne pourra plus se connecter. Ses dossiers et son historique sont conservés.`,
      libelleConfirmer: actif ? 'Ouvrir le compte' : 'Fermer le compte',
      ton: actif ? 'primaire' : 'danger',
    });
    modalRef.closed
      .pipe(filter(reason => reason === CONFIRMED_EVENT))
      .subscribe(() =>
        this.userManagementService.changerActivation(compte.login, actif).subscribe(misAJour => this.compteRemplace.set(misAJour)),
      );
  }

  previousState(): void {
    globalThis.history.back();
  }
}
