import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgClass } from '@angular/common';

import { AccountService } from 'app/core/auth';
import { IAgent } from 'app/fonctionnalites/agent/agent.model';
import { AgentService } from 'app/fonctionnalites/agent/service/agent.service';
import { IAyantDroit } from 'app/fonctionnalites/ayant-droit/ayant-droit.model';
import { AyantDroitService } from 'app/fonctionnalites/ayant-droit/service/ayant-droit.service';
import { libelleValidation, tonValidation } from 'app/fonctionnalites/ayant-droit/validation-rattachement';
import { ICarteBeneficiaire } from 'app/fonctionnalites/carte-beneficiaire/carte-beneficiaire.model';
import { CarteBeneficiaireService } from 'app/fonctionnalites/carte-beneficiaire/service/carte-beneficiaire.service';
import { libelleValidite, tonValidite } from 'app/fonctionnalites/carte-beneficiaire/validite';
import { IDemandePriseEnCharge } from 'app/fonctionnalites/demande-prise-en-charge/demande-prise-en-charge.model';
import { DemandePriseEnChargeService } from 'app/fonctionnalites/demande-prise-en-charge/service/demande-prise-en-charge.service';
import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { STATUTS_AGENT, STATUTS_AYANT_DROIT, libelleStatut, tonStatut } from 'app/shared/statut/statuts';

/** Les derniers dossiers suffisent à savoir où l'on en est ; la liste complète est à un clic. */
const DOSSIERS_AFFICHES = 5;

/**
 * Ce qu'un agent vient voir : où en sont ses dossiers, qui est couvert, et jusqu'à quand.
 *
 * Il ne s'agit pas d'un écran d'instruction. L'agent ne valide rien et ne consulte le dossier de
 * personne : le serveur borne déjà ce que les listes lui rapportent, cet écran les rassemble en
 * une page pour qu'il n'ait pas à parcourir un menu conçu pour le personnel administratif.
 *
 * Un compte au profil « Agent » mais rattaché à aucun agent arrive ici sur une page qui le dit :
 * une page vide se lit comme une panne, et l'on cherche du côté de l'application ce qui relève
 * de l'administration des comptes.
 */
@Component({
  selector: 'jhi-espace-agent',
  templateUrl: './espace-agent.html',
  imports: [NgClass, RouterLink, FontAwesomeModule, Alert, AlertError, TranslateDirective, FormatMediumDatePipe, FormatMediumDatetimePipe],
})
export class EspaceAgent {
  readonly moi = signal<IAgent | null>(null);
  readonly ayantsDroit = signal<IAyantDroit[]>([]);
  readonly dossiers = signal<IDemandePriseEnCharge[]>([]);
  readonly carte = signal<ICarteBeneficiaire | null>(null);

  readonly chargement = signal(true);

  private readonly accountService = inject(AccountService);
  private readonly agentService = inject(AgentService);
  private readonly ayantDroitService = inject(AyantDroitService);
  private readonly demandeService = inject(DemandePriseEnChargeService);
  private readonly carteService = inject(CarteBeneficiaireService);

  /** Le compte est-il rattaché à un agent ? Sans cela l'espace n'a rien à montrer. */
  readonly rattache = computed(() => !!this.accountService.account()?.agent?.id);

  readonly identite = computed(() => {
    const agent = this.moi();
    if (!agent) {
      const compte = this.accountService.account()?.agent;
      return compte ? [compte.prenom, compte.nom].filter(Boolean).join(' ') : '';
    }
    return [agent.prenom, agent.nom].filter(Boolean).join(' ');
  });

  /** Combien de dossiers attendent encore une décision : c'est la question qu'on vient poser. */
  readonly enCours = computed(
    () => this.dossiers().filter(d => !['VALIDEE', 'REJETEE', 'ANNULEE', 'CLOTUREE', 'EXPIREE'].includes(d.statut ?? '')).length,
  );

  constructor() {
    this.charger();
  }

  /** La situation de l'agent : ses valeurs ne sont pas celles d'un ayant droit (retraite). */
  libelleMaSituation = (): string => libelleStatut(STATUTS_AGENT, this.moi()?.statut);

  tonMaSituation = (): string => tonStatut(this.moi()?.statut);

  libelleStatutAyantDroit = (ayantDroit: IAyantDroit): string => libelleStatut(STATUTS_AYANT_DROIT, ayantDroit.statut);

  tonStatutAyantDroit = (ayantDroit: IAyantDroit): string => tonStatut(ayantDroit.statut);

  libelleValidationAyantDroit = (ayantDroit: IAyantDroit): string => libelleValidation(ayantDroit.statutValidation);

  tonValidationAyantDroit = (ayantDroit: IAyantDroit): string => tonValidation(ayantDroit.statutValidation);

  libelleValiditeCarte = (): string => {
    const carte = this.carte();
    return carte ? libelleValidite(carte) : '';
  };

  tonValiditeCarte = (): string => {
    const carte = this.carte();
    return carte ? tonValidite(carte) : 'neutre';
  };

  /**
   * Rassemble les quatre volets de l'espace.
   *
   * Aucun identifiant n'est envoyé : le serveur borne lui-même les listes au compte connecté.
   * Les passer d'ici laisserait croire que c'est l'écran qui décide de ce qui est visible.
   */
  private charger(): void {
    const agentId = this.accountService.account()?.agent?.id;
    if (!agentId) {
      this.chargement.set(false);
      return;
    }

    this.agentService.find(agentId).subscribe({
      next: agent => this.moi.set(agent),
      error: () => this.moi.set(null),
    });

    this.ayantDroitService.query({ size: 50, sort: ['nom,asc'] }).subscribe({
      next: reponse => this.ayantsDroit.set(reponse.body ?? []),
      error: () => this.ayantsDroit.set([]),
    });

    this.demandeService.query({ size: DOSSIERS_AFFICHES, sort: ['dateCreation,desc'] }).subscribe({
      next: reponse => {
        this.dossiers.set(reponse.body ?? []);
        this.chargement.set(false);
      },
      error: () => this.chargement.set(false),
    });

    this.carteService.query({ size: 1, sort: ['dateFinValidite,desc'] }).subscribe({
      next: reponse => this.carte.set(reponse.body?.[0] ?? null),
      error: () => this.carte.set(null),
    });
  }
}
