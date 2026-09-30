import { NgClass } from '@angular/common';
import { Component, computed, effect, inject, input, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbDropdown, NgbDropdownItem, NgbDropdownMenu, NgbDropdownToggle } from '@ng-bootstrap/ng-bootstrap/dropdown';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import dayjs from 'dayjs/esm';
import { catchError, filter, of, tap } from 'rxjs';

import { ITEM_DELETED_EVENT } from 'app/config';
import { DataUtils } from 'app/core/util/data-util.service';
import { AyantDroitDeleteDialog } from 'app/fonctionnalites/ayant-droit/delete/ayant-droit-delete-dialog';
import { AyantDroitDialog } from 'app/fonctionnalites/ayant-droit/dialog/ayant-droit-dialog';
import { AgentService } from 'app/fonctionnalites/agent/service/agent.service';
import { ICarteBeneficiaire } from 'app/fonctionnalites/carte-beneficiaire/carte-beneficiaire.model';
import { CarteBeneficiaireService } from 'app/fonctionnalites/carte-beneficiaire/service/carte-beneficiaire.service';
import { ChangementStatutDialog, ResultatChangementStatut } from 'app/shared/statut/changement-statut-dialog';
import { STATUTS_AGENT, STATUTS_AYANT_DROIT, libelleStatut, tonStatut } from 'app/shared/statut/statuts';
import { IAyantDroit } from 'app/fonctionnalites/ayant-droit/ayant-droit.model';
import { AyantDroitService } from 'app/fonctionnalites/ayant-droit/service/ayant-droit.service';
import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { enSaisie, libelleValidation, tonValidation } from 'app/fonctionnalites/ayant-droit/validation-rattachement';
import { AccountService } from 'app/core/auth';
import { Action } from 'app/shared/jhipster/actions.constants';
import { Authority } from 'app/shared/jhipster/constants';
import { IAgent } from '../agent.model';

/**
 * Nombre d'années révolues depuis une date, ou `null` si la date manque.
 *
 * Une date future rendrait un nombre négatif, qui ne veut rien dire sur un dossier : on
 * préfère ne rien afficher plutôt qu'afficher une absurdité.
 */
function anneesRevolues(date?: dayjs.Dayjs | null): number | null {
  if (!date) {
    return null;
  }
  const annees = dayjs().diff(date, 'year');
  return annees >= 0 ? annees : null;
}

@Component({
  selector: 'jhi-agent-detail',
  templateUrl: './agent-detail.html',
  imports: [
    NgClass,
    FontAwesomeModule,
    Alert,
    AlertError,
    TranslateDirective,
    RouterLink,
    FormatMediumDatePipe,
    FormatMediumDatetimePipe,
    NgbDropdown,
    NgbDropdownItem,
    NgbDropdownMenu,
    NgbDropdownToggle,
  ],
})
export class AgentDetail {
  readonly agent = input<IAgent | null>(null);

  /**
   * Les ayants droit de l'agent, à sa fiche.
   *
   * Un agent et ses ayants droit se consultent ensemble : c'est de là que part une demande de
   * prise en charge, et c'est là qu'on vérifie qui est couvert. Les faire chercher dans une
   * liste générale, filtre à la main, revient à faire deux fois le même travail.
   */
  readonly ayantsDroit = signal<IAyantDroit[]>([]);
  readonly chargementAyantsDroit = signal(false);

  readonly identite = computed(() => {
    const agent = this.agent();
    if (!agent) {
      return '';
    }
    return [agent.prenom, agent.nom].filter(Boolean).join(' ') || (agent.matricule ?? '');
  });

  /** Initiales pour la pastille d'identité, quand l'agent n'a pas de photo. */
  readonly initiales = computed(() => {
    const agent = this.agent();
    if (!agent) {
      return '';
    }
    const prenom = agent.prenom?.trim().charAt(0) ?? '';
    const nom = agent.nom?.trim().charAt(0) ?? '';
    return (prenom + nom || agent.matricule?.slice(0, 2) || '').toUpperCase();
  });

  /**
   * Âge de l'agent, en années révolues.
   *
   * Calculé plutôt qu'affiché brut : sur un dossier de protection sociale, c'est l'âge qui
   * décide, pas la date. La laisser seule oblige l'agent à compter de tête.
   */
  readonly age = computed(() => anneesRevolues(this.agent()?.dateNaissance));

  /** Ancienneté dans l'institution, même raisonnement que pour l'âge. */
  readonly anciennete = computed(() => anneesRevolues(this.agent()?.dateEmbauche));

  protected dataUtils = inject(DataUtils);
  protected readonly accountService = inject(AccountService);
  protected readonly carteService = inject(CarteBeneficiaireService);
  protected readonly routeur = inject(Router);

  /** Établir une carte relève du même droit que la créer au formulaire. */
  readonly peutEtablirCarte = computed(() => this.accountService.hasAnyAuthority([Action.CARTE_CREER, Authority.ADMIN]));

  readonly carteEnCours = signal(false);

  /**
   * La carte en cours de validite, s'il y en a une.
   *
   * Chargee a l'ouverture de la fiche : sans elle, l'ecran proposerait d'etablir une carte a qui
   * en a deja une, et le refus n'arriverait qu'apres le geste.
   */
  readonly carteValide = signal<ICarteBeneficiaire | null>(null);

  /** Le controle RH prononce la verification d'un rattachement ; les autres la lisent. */
  readonly peutVerifier = computed(() => this.accountService.hasAnyAuthority([Authority.VERIFICATEUR_RH, Authority.ADMIN]));

  private readonly estAdmin = computed(() => this.accountService.hasAnyAuthority(Authority.ADMIN));
  protected readonly ayantDroitService = inject(AyantDroitService);
  protected readonly agentService = inject(AgentService);
  protected readonly modalService = inject(NgbModal);

  private readonly agentRemplace = signal<IAgent | null>(null);

  constructor() {
    effect(() => this.chargerAyantsDroit(this.agent()?.id));
    // La carte en cours, pour ne pas proposer d'en etablir une a qui en a deja une.
    effect(() => {
      this.agent();
      this.chargerCarte();
    });
  }

  /**
   * L'agent tel qu'il doit s'afficher : celui que le serveur vient de rendre, sinon celui qui a
   * été chargé à l'ouverture.
   *
   * Un signal local plutôt qu'une écriture dans l'entrée : une entrée ne se réassigne pas, et
   * recharger l'écran entier pour un changement de situation ferait clignoter toute la fiche.
   * Lire la situation dans l'entrée laisserait l'écran afficher l'état d'avant - et proposerait
   * de nouveau, dans le dialogue, la situation que l'on vient de quitter.
   */
  readonly agentAffiche = computed(() => this.agentRemplace() ?? this.agent());

  readonly statutAffiche = computed(() => this.agentAffiche()?.statut ?? null);

  readonly motifStatut = computed(() => this.agentAffiche()?.motifStatut ?? null);

  readonly dateStatut = computed(() => this.agentAffiche()?.dateStatut ?? null);

  /** Le mot de la situation, pour que la pastille ne soit jamais seule. */
  libelleStatut(): string {
    return libelleStatut(STATUTS_AGENT, this.statutAffiche());
  }

  tonStatut(): string {
    return tonStatut(this.statutAffiche());
  }

  libelleStatutAyantDroit(ayantDroit: IAyantDroit): string {
    return libelleStatut(STATUTS_AYANT_DROIT, ayantDroit.statut);
  }

  tonStatutAyantDroit(ayantDroit: IAyantDroit): string {
    return tonStatut(ayantDroit.statut);
  }

  /**
   * Change la situation de l'agent, en disant d'abord ce que cela entraîne pour ses ayants
   * droit : leur couverture dérive de la sienne, et une radiation les emporte.
   */
  changerStatut(): void {
    const agent = this.agent();
    if (!agent?.id) {
      return;
    }
    const modalRef = this.modalService.open(ChangementStatutDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.intitule = `${this.identite()} — matricule ${agent.matricule ?? ''}`;
    modalRef.componentInstance.statutCourant = this.statutAffiche();
    modalRef.componentInstance.libelleCourant = this.libelleStatut();
    modalRef.componentInstance.options = STATUTS_AGENT;
    modalRef.componentInstance.repercute = true;
    modalRef.closed.subscribe((resultat: ResultatChangementStatut) => {
      if (!resultat) {
        return;
      }
      this.agentService.changerStatut(agent.id, resultat.statut, resultat.motif).subscribe(misAJour => {
        this.agentRemplace.set(misAJour);
        // La répercussion a pu changer les ayants droit : la liste est relue, sans vider l'écran.
        this.chargerAyantsDroit(agent.id, { discret: true });
      });
    });
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

  /**
   * Ouvre la saisie d'un ayant droit dans un dialogue, sans quitter la fiche.
   *
   * L'ajout se fait à côté de la liste qu'il alimente : on voit la ligne apparaître là où on
   * l'attend. Quitter l'écran pour un formulaire plein puis y revenir ferait perdre la lecture
   * en cours pour cinq champs de saisie.
   */
  libelleValidationAyantDroit = (ayantDroit: IAyantDroit): string => libelleValidation(ayantDroit.statutValidation);

  tonValidationAyantDroit = (ayantDroit: IAyantDroit): string => tonValidation(ayantDroit.statutValidation);

  /** Vrai tant que le rattachement est a verifier - l'action n'a plus de sens ensuite. */
  estAVerifier = (ayantDroit: IAyantDroit): boolean => enSaisie(ayantDroit.statutValidation);

  /**
   * Vrai tant que la suppression reste un geste de correction de saisie.
   *
   * Une fois le rattachement verifie, des dossiers ont pu s'appuyer dessus : le supprimer les
   * priverait de leur beneficiaire. Il se radie alors, depuis sa fiche.
   */
  estSupprimable = (ayantDroit: IAyantDroit): boolean => enSaisie(ayantDroit.statutValidation) || this.estAdmin();

  /** Declare le rattachement verifie, puis recharge la liste : elle doit montrer ce qui a ete fait. */
  validerAyantDroit(ayantDroit: IAyantDroit): void {
    if (!ayantDroit.id) {
      return;
    }
    this.ayantDroitService.valider(ayantDroit.id).subscribe(() => this.chargerAyantsDroit(this.agent()?.id, { discret: true }));
  }

  /**
   * Établit la carte de l'agent, puis l'ouvre pour impression.
   *
   * Rien n'est demandé : le serveur pose le numéro, la période et la date d'émission. Passer
   * par le formulaire obligerait à saisir quatre champs dont aucun ne relève d'un choix.
   */
  /** Recherche la carte en cours de validite du titulaire. */
  private chargerCarte(): void {
    const id = this.agent()?.id;
    if (!id) {
      this.carteValide.set(null);
      return;
    }
    this.carteService.query({ 'agentId.equals': id, size: 20, sort: ['dateFinValidite,desc'] }).subscribe({
      next: reponse => {
        const aujourdhui = dayjs();
        this.carteValide.set(
          (reponse.body ?? []).find(carte => !!carte.dateFinValidite && !carte.dateFinValidite.isBefore(aujourdhui, 'day')) ?? null,
        );
      },
      error: () => this.carteValide.set(null),
    });
  }

  etablirCarte(): void {
    const agent = this.agent();
    if (!agent?.id || this.carteEnCours()) {
      return;
    }
    this.carteEnCours.set(true);
    this.carteService.generer('AGENT', agent.id).subscribe({
      next: carte => {
        this.carteEnCours.set(false);
        this.carteValide.set(carte);
        void this.routeur.navigate(['/carte-beneficiaire', carte.id, 'print']);
      },
      // Le refus - une carte valide existe deja - est affiche par l'intercepteur d'alertes.
      error: () => this.carteEnCours.set(false),
    });
  }

  ajouterAyantDroit(): void {
    const agent = this.agent();
    if (!agent?.id) {
      return;
    }
    const modalRef = this.modalService.open(AyantDroitDialog, { size: 'lg', backdrop: 'static', scrollable: true });
    modalRef.componentInstance.agent = agent;
    // `closed` seulement : un abandon ne doit rien changer à l'écran.
    modalRef.closed.subscribe((cree: IAyantDroit) => this.integrerAyantDroit(cree));
  }

  supprimerAyantDroit(ayantDroit: IAyantDroit): void {
    const modalRef = this.modalService.open(AyantDroitDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.ayantDroit = ayantDroit;
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => this.chargerAyantsDroit(this.agent()?.id)),
      )
      .subscribe();
  }

  /**
   * Insère le nouvel ayant droit à sa place, puis resynchronise en arrière-plan.
   *
   * L'insertion immédiate évite le clignotement d'un rechargement complet — c'est le serveur
   * qui a attribué le code, la ligne affichée est donc déjà la bonne. La relecture qui suit ne
   * sert qu'à rester d'accord avec la base, et elle se fait sans vider la liste.
   */
  private integrerAyantDroit(cree?: IAyantDroit): void {
    if (!cree) {
      return;
    }
    this.ayantsDroit.update(liste =>
      [...liste, cree].sort((a, b) => (a.nom ?? '').localeCompare(b.nom ?? '') || (a.prenom ?? '').localeCompare(b.prenom ?? '')),
    );
    this.chargerAyantsDroit(this.agent()?.id, { discret: true });
  }

  /** Le rattachement se lit sur le serveur : `agentId.equals` existe déjà sur les critères. */
  private chargerAyantsDroit(agentId?: number, options?: { discret: boolean }): void {
    if (!agentId) {
      this.ayantsDroit.set([]);
      return;
    }
    // En mode discret, le voyant de chargement reste éteint : la liste est déjà à l'écran et
    // la remplacer par « Chargement… » ferait clignoter ce que l'on vient d'ajouter.
    if (!options?.discret) {
      this.chargementAyantsDroit.set(true);
    }
    this.ayantDroitService
      .query({ 'agentId.equals': agentId, size: 50, sort: ['nom,asc', 'prenom,asc'] })
      .pipe(catchError(() => of(null)))
      .subscribe(reponse => {
        // Une relecture en échec laisse la liste en place plutôt que de la vider : ce qui vient
        // d'être ajouté est déjà enregistré, l'effacer de l'écran serait mentir.
        if (reponse?.body) {
          this.ayantsDroit.set(reponse.body);
        } else if (!options?.discret) {
          this.ayantsDroit.set([]);
        }
        this.chargementAyantsDroit.set(false);
      });
  }
}
