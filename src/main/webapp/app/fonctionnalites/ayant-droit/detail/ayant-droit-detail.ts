import { NgClass } from '@angular/common';
import { Component, computed, inject, input, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import dayjs from 'dayjs/esm';

import { DataUtils } from 'app/core/util/data-util.service';
import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { ChangementStatutDialog, ResultatChangementStatut } from 'app/shared/statut/changement-statut-dialog';
import { STATUTS_AYANT_DROIT, libelleStatut, tonStatut } from 'app/shared/statut/statuts';
import { TranslateDirective } from 'app/shared/language';
import { AccountService } from 'app/core/auth/account.service';
import { Authority } from 'app/shared/jhipster/constants';
import { IAyantDroit } from '../ayant-droit.model';
import { enSaisie, libelleValidation, tonValidation } from '../validation-rattachement';
import { AyantDroitService } from '../service/ayant-droit.service';

@Component({
  selector: 'jhi-ayant-droit-detail',
  templateUrl: './ayant-droit-detail.html',
  imports: [NgClass, FontAwesomeModule, Alert, AlertError, TranslateDirective, RouterLink, FormatMediumDatePipe, FormatMediumDatetimePipe],
})
export class AyantDroitDetail {
  readonly ayantDroit = input<IAyantDroit | null>(null);

  /**
   * L'ayant droit tel qu'il doit s'afficher : celui que le serveur vient de rendre, sinon celui
   * qui a été chargé. Voir `AgentDetail.agentAffiche` — une entrée ne se réassigne pas.
   */
  readonly ayantDroitAffiche = computed(() => this.ayantDroitRemplace() ?? this.ayantDroit());

  readonly statutAffiche = computed(() => this.ayantDroitAffiche()?.statut ?? null);

  readonly motifStatut = computed(() => this.ayantDroitAffiche()?.motifStatut ?? null);

  readonly dateStatut = computed(() => this.ayantDroitAffiche()?.dateStatut ?? null);

  /** Vrai quand la situation vient d'une répercussion du statut de l'agent. */
  readonly repercuteDepuisAgent = computed(() => !!this.ayantDroitAffiche()?.statutAvantCascade);

  readonly identite = computed(() => {
    const ayantDroit = this.ayantDroit();
    if (!ayantDroit) {
      return '';
    }
    return [ayantDroit.prenom, ayantDroit.nom].filter(Boolean).join(' ') || (ayantDroit.codeAyantDroit ?? '');
  });

  /** Initiales pour la pastille d'identité, quand l'ayant droit n'a pas de photo. */
  readonly initiales = computed(() => {
    const ayantDroit = this.ayantDroit();
    if (!ayantDroit) {
      return '';
    }
    const prenom = ayantDroit.prenom?.trim().charAt(0) ?? '';
    const nom = ayantDroit.nom?.trim().charAt(0) ?? '';
    return (prenom + nom || ayantDroit.codeAyantDroit?.slice(0, 2) || '').toUpperCase();
  });

  /**
   * Âge en années révolues.
   *
   * C'est l'information qui décide de la couverture d'un enfant : l'afficher évite de compter
   * de tête à partir d'une date de naissance.
   */
  readonly age = computed(() => {
    const date = this.ayantDroit()?.dateNaissance;
    if (!date) {
      return null;
    }
    const annees = dayjs().diff(date, 'year');
    return annees >= 0 ? annees : null;
  });

  readonly statutValidation = computed(() => this.ayantDroitAffiche()?.statutValidation ?? 'EN_SAISIE');

  readonly libelleValidation = computed(() => libelleValidation(this.statutValidation()));

  readonly tonValidation = computed(() => tonValidation(this.statutValidation()));

  /**
   * Qui peut déclarer le rattachement vérifié.
   *
   * C'est le même travail que le contrôle d'un dossier : lire les pièces qui établissent le lien
   * de parenté. Le bouton disparaît une fois la vérification faite — elle ne se refait pas.
   */
  readonly peutValider = computed(
    () => enSaisie(this.statutValidation()) && this.accountService.hasAnyAuthority([Authority.VERIFICATEUR_RH, Authority.ADMIN]),
  );

  protected dataUtils = inject(DataUtils);
  protected readonly accountService = inject(AccountService);
  protected readonly ayantDroitService = inject(AyantDroitService);
  protected readonly modalService = inject(NgbModal);

  private readonly ayantDroitRemplace = signal<IAyantDroit | null>(null);

  libelleStatut(): string {
    return libelleStatut(STATUTS_AYANT_DROIT, this.statutAffiche());
  }

  tonStatut(): string {
    return tonStatut(this.statutAffiche());
  }

  /**
   * Change la situation de l'ayant droit.
   *
   * Une décision prise ici prime sur celle répercutée depuis l'agent : elle efface la mémoire de
   * la répercussion, de sorte que réactiver l'agent ne vienne pas défaire ce que l'on décide.
   */
  changerStatut(): void {
    const ayantDroit = this.ayantDroitAffiche();
    if (!ayantDroit?.id) {
      return;
    }
    const modalRef = this.modalService.open(ChangementStatutDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.intitule = `${this.identite()} — code ${ayantDroit.codeAyantDroit ?? ''}`;
    modalRef.componentInstance.statutCourant = this.statutAffiche();
    modalRef.componentInstance.libelleCourant = this.libelleStatut();
    modalRef.componentInstance.options = STATUTS_AYANT_DROIT;
    // `closed` seulement : un abandon ne doit rien changer à l'écran.
    modalRef.closed.subscribe((resultat: ResultatChangementStatut) => {
      this.ayantDroitService
        .changerStatut(ayantDroit.id, resultat.statut, resultat.motif)
        .subscribe(misAJour => this.ayantDroitRemplace.set(misAJour));
    });
  }

  /**
   * Déclare le rattachement vérifié.
   *
   * L'écran est remplacé par ce que rend le serveur, et non par ce que l'on croit avoir fait :
   * c'est lui qui pose la date de vérification.
   */
  validerRattachement(): void {
    const ayantDroit = this.ayantDroitAffiche();
    if (!ayantDroit?.id) {
      return;
    }
    this.ayantDroitService.valider(ayantDroit.id).subscribe(misAJour => this.ayantDroitRemplace.set(misAJour));
  }

  previousState(): void {
    globalThis.history.back();
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }
}
