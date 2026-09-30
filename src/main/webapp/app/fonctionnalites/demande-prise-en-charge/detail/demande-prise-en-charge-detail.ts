import { NgClass } from '@angular/common';
import { Component, computed, effect, inject, input, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbDropdown, NgbDropdownItem, NgbDropdownMenu, NgbDropdownToggle } from '@ng-bootstrap/ng-bootstrap/dropdown';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { catchError, filter, of, tap } from 'rxjs';

import { ITEM_DELETED_EVENT } from 'app/config';
import { Alert, AlertError } from 'app/shared/alert';
import { CONFIRMED_EVENT, ConfirmDialog } from 'app/shared/confirm';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { VisionneuseDocument, blobDepuisBase64 } from 'app/shared/document/visionneuse-document';
import { TranslateDirective } from 'app/shared/language';
import { AccountService } from 'app/core/auth';
import { DataUtils } from 'app/core/util/data-util.service';
import { Authority } from 'app/shared/jhipster/constants';
import { IHistoriqueAction } from 'app/fonctionnalites/historique-action/historique-action.model';
import { HistoriqueActionService } from 'app/fonctionnalites/historique-action/service/historique-action.service';
import { IPieceJustificative } from 'app/fonctionnalites/piece-justificative/piece-justificative.model';
import { PieceJustificativeDeleteDialog } from 'app/fonctionnalites/piece-justificative/delete/piece-justificative-delete-dialog';
import { PieceJustificativeDialog } from 'app/fonctionnalites/piece-justificative/dialog/piece-justificative-dialog';
import { PieceJustificativeService } from 'app/fonctionnalites/piece-justificative/service/piece-justificative.service';
import { DemandePriseEnChargeDeleteDialog } from '../delete/demande-prise-en-charge-delete-dialog';
import { IDemandePriseEnCharge } from '../demande-prise-en-charge.model';
import { DemandePriseEnChargeService } from '../service/demande-prise-en-charge.service';
import { DEMANDE_REJECTED_EVENT, DemandePriseEnChargeRejectDialog } from '../reject/demande-prise-en-charge-reject-dialog';

/**
 * Le circuit d'une demande, dans l'ordre où elle le parcourt. Chaque étape regroupe les
 * statuts qui la désignent : la saisie couvre tout ce qui précède l'envoi au contrôle.
 *
 * Un dossier retourné pour correction revient à l'étape de saisie : c'est bien là qu'il attend,
 * et l'afficher plus loin laisserait croire qu'il progresse.
 */
const ETAPES_CIRCUIT: { libelle: string; statuts: string[] }[] = [
  {
    libelle: 'Saisie du dossier',
    statuts: ['EN_SAISIE', 'NOUVELLE', 'EN_ATTENTE_PIECES', 'A_TRAITER', 'EN_COURS_TRAITEMENT', 'RETOURNEE'],
  },
  {
    libelle: 'Validation infirmerie',
    statuts: ['EN_ATTENTE_VALIDATION_INFIRMERIE', 'EN_ATTENTE_AVIS_MEDICAL', 'EN_ATTENTE_DECISION'],
  },
  { libelle: 'Vérification RH', statuts: ['EN_VERIFICATION_RH'] },
  { libelle: 'Validation DRH', statuts: ['EN_ATTENTE_VALIDATION_DRH'] },
  { libelle: 'Décision', statuts: ['VALIDEE', 'REJETEE'] },
];

@Component({
  selector: 'jhi-demande-prise-en-charge-detail',
  templateUrl: './demande-prise-en-charge-detail.html',
  imports: [
    NgClass,
    FontAwesomeModule,
    Alert,
    AlertError,
    TranslateDirective,
    RouterLink,
    FormatMediumDatetimePipe,
    NgbDropdown,
    NgbDropdownItem,
    NgbDropdownMenu,
    NgbDropdownToggle,
  ],
})
export class DemandePriseEnChargeDetail {
  readonly demandePriseEnCharge = input<IDemandePriseEnCharge | null>(null);

  readonly current = signal<IDemandePriseEnCharge | null>(null);
  readonly isSaving = signal(false);

  protected readonly demandePriseEnChargeService = inject(DemandePriseEnChargeService);
  protected readonly accountService = inject(AccountService);
  protected readonly modalService = inject(NgbModal);
  protected readonly router = inject(Router);
  protected readonly historiqueActionService = inject(HistoriqueActionService);
  protected readonly pieceJustificativeService = inject(PieceJustificativeService);
  protected readonly dataUtils = inject(DataUtils);

  /**
   * Le dossier est encore en saisie et m'appartient : je peux le soumettre au contrôle.
   *
   * C'est le geste qui le fait sortir du brouillon. Avant, il n'attend personne et peut être
   * supprimé ; après, il entre dans le circuit et ne peut plus qu'être annulé.
   */
  readonly canSoumettre = computed(() => {
    const demande = this.current();
    const account = this.accountService.account();
    return demande?.statut === 'EN_SAISIE' && !!account && demande.gestionnaireCreateur?.login === account.login;
  });

  /** Le contrôleur RH ne vise pas son propre dossier : c'est le principe du double regard. */
  readonly canVerifier = computed(() => {
    const demande = this.current();
    const account = this.accountService.account();
    return (
      demande?.statut === 'EN_VERIFICATION_RH' &&
      this.accountService.hasAnyAuthority(Authority.VERIFICATEUR_RH) &&
      !!account &&
      demande.gestionnaireCreateur?.login !== account.login
    );
  });

  readonly canValiderDrh = computed(
    () => this.current()?.statut === 'EN_ATTENTE_VALIDATION_DRH' && this.accountService.hasAnyAuthority(Authority.VALIDATEUR_DRH),
  );
  readonly canValiderInfirmerie = computed(
    () =>
      this.current()?.statut === 'EN_ATTENTE_VALIDATION_INFIRMERIE' && this.accountService.hasAnyAuthority(Authority.VALIDATEUR_INFIRMERIE),
  );
  readonly canValiderOuRejeter = computed(() => this.canValiderDrh() || this.canValiderInfirmerie());
  readonly canSaisir = computed(() => this.accountService.hasAnyAuthority([Authority.USER, Authority.VALIDATEUR_DRH]));
  readonly canResoumettre = computed(() => {
    const demande = this.current();
    const account = this.accountService.account();
    return demande?.statut === 'RETOURNEE' && !!account && demande.gestionnaireCreateur?.login === account.login;
  });
  // Ces deux actions vivaient dans le menu de la liste, que le design system proscrit
  // (« Pas de menu à trois points : si le dossier a plusieurs actions, elles sont sur l'écran
  // de détail »). Elles ont donc été déplacées ici, avec les mêmes conditions d'accès.
  readonly canImprimer = computed(() => this.current()?.statut === 'VALIDEE' && this.accountService.hasAnyAuthority(Authority.USER));

  /**
   * La notification de décision s'édite dès qu'une décision est prise, favorable ou non.
   *
   * C'est ce qui la distingue de l'imprimé de prise en charge : un refus sans document
   * opposable ne se conteste pas — l'agent n'a aucune trace de ce qu'on lui a dit, ni du motif.
   */
  /**
   * Les pièces ne se modifient que tant que le dossier est en saisie.
   *
   * Le serveur le refuse déjà ; l'écran retire les boutons plutôt que de les laisser échouer,
   * parce qu'un bouton qui échoue toujours apprend à se méfier de tout l'écran.
   */
  readonly piecesModifiables = computed(() => {
    const statut = this.current()?.statut;
    return statut === 'EN_SAISIE' || statut === 'NOUVELLE';
  });

  /**
   * Un dossier ne se modifie que tant qu'il n'a été soumis au jugement de personne.
   *
   * Une fois soumis, le modifier ferait valider autre chose que ce qui a été lu, et une
   * validation déjà donnée porterait sur un texte qui n'existe plus. `RETOURNEE` en fait partie :
   * c'est l'étape où le dossier revient à son auteur précisément pour être corrigé.
   *
   * Le serveur refuse de la même manière ; l'écran retire le bouton plutôt que de le laisser
   * échouer.
   */
  readonly canModifier = computed(() => {
    const demande = this.current();
    const account = this.accountService.account();
    const statut = demande?.statut;
    const modifiable = statut === 'EN_SAISIE' || statut === 'NOUVELLE' || statut === 'RETOURNEE';
    if (!demande || !modifiable || !account) {
      return false;
    }
    return demande.gestionnaireCreateur?.login === account.login || this.accountService.hasAnyAuthority(Authority.ADMIN);
  });

  readonly canEditerNotification = computed(() => {
    const statut = this.current()?.statut;
    return statut === 'VALIDEE' || statut === 'REJETEE' || statut === 'RETOURNEE';
  });
  /**
   * Un dossier ne se supprime qu'en saisie : au-delà il porte des décisions, et les effacer
   * effacerait la trace de ce qui a été décidé. Le serveur le refuse déjà — le bouton ne
   * s'affiche pas, plutôt que d'échouer à chaque fois.
   */
  readonly canSupprimer = computed(() => {
    const statut = this.current()?.statut;
    return this.accountService.hasAnyAuthority(Authority.ADMIN) && (statut === 'EN_SAISIE' || statut === 'NOUVELLE');
  });
  readonly isDownloading = signal(false);

  /** Journal des actions du dossier, affiché dans la colonne de contexte. */
  readonly historique = signal<IHistoriqueAction[]>([]);

  /**
   * Les pièces justificatives jointes au dossier.
   *
   * Elles se gèrent depuis la demande, pas depuis une liste générale : c'est en instruisant un
   * dossier qu'on vérifie ce qui a été fourni, et qu'on joint ce qui manque.
   */
  readonly pieces = signal<IPieceJustificative[]>([]);
  readonly chargementPieces = signal(false);

  /**
   * Le circuit de la prise en charge, de la saisie à la décision. Il occupe le haut du
   * dossier parce que la première question d'un agent qui ouvre un dossier est « où en
   * est-il, et qu'est-ce qui m'attend ».
   */
  readonly etapes = computed(() => {
    const statut = this.current()?.statut;
    const rejete = statut === 'REJETEE' || statut === 'RETOURNEE';
    const rang = ETAPES_CIRCUIT.findIndex(etape => etape.statuts.includes(statut ?? ''));
    // Un statut hors circuit (Annulée, Clôturée…) : on considère le circuit parcouru.
    const courante = rang === -1 ? ETAPES_CIRCUIT.length : rang;
    return ETAPES_CIRCUIT.map((etape, index) => ({
      libelle: etape.libelle,
      numero: index + 1,
      etat: index < courante ? 'faite' : index === courante ? (rejete ? 'rejet' : 'active') : 'a-venir',
    }));
  });

  /**
   * Le motif n'est une alerte que tant qu'il demande quelque chose à quelqu'un. Sur un dossier
   * reparti dans le circuit, un bandeau rouge ferait croire à un blocage qui n'existe plus :
   * le motif redescend alors dans les champs du dossier.
   */
  readonly motifEnCours = computed(() => {
    const demande = this.current();
    if (!demande?.motifRejet) {
      return null;
    }
    if (demande.statut === 'RETOURNEE') {
      return { titre: "Dossier retourné à l'auteur", texte: demande.motifRejet };
    }
    if (demande.statut === 'REJETEE') {
      return { titre: 'Demande rejetée', texte: demande.motifRejet };
    }
    return null;
  });

  /** Identité du bénéficiaire, telle qu'elle s'affiche dans la carte de contexte. */
  readonly beneficiaire = computed(() => {
    const demande = this.current();
    if (!demande) {
      return null;
    }
    const estAyantDroit = demande.typeBeneficiaire === 'AYANT_DROIT';
    const nom = estAyantDroit ? demande.ayantDroit?.nom : demande.agent?.matricule;
    return {
      nom: nom ?? 'Bénéficiaire non renseigné',
      matricule: demande.agent?.matricule ?? '—',
      nature: estAyantDroit ? "Ayant droit d'un agent" : 'Agent de la Caisse',
      // Pas de nom, pas de pastille : deux tirets dans un carré ne désignent personne.
      initiales: nom ? nom.slice(0, 2).toUpperCase() : null,
    };
  });

  constructor() {
    effect(() => {
      const demande = this.demandePriseEnCharge();
      this.current.set(demande);
      this.chargerHistorique(demande?.id);
      this.chargerPieces(demande?.id);
    });
  }

  /**
   * Joint une pièce dans un dialogue, sans quitter le dossier.
   *
   * La ligne est insérée à sa place puis la liste relue en arrière-plan : l'insertion immédiate
   * évite le clignotement d'un rechargement complet, et la relecture garde l'écran d'accord avec
   * la base.
   */
  ajouterPiece(): void {
    const demande = this.current();
    if (!demande?.id) {
      return;
    }
    const modalRef = this.modalService.open(PieceJustificativeDialog, { size: 'lg', backdrop: 'static', scrollable: true });
    modalRef.componentInstance.demande = { id: demande.id, reference: demande.reference };
    // `closed` seulement : un abandon ne doit rien changer à l'écran.
    modalRef.closed.subscribe((creee: IPieceJustificative) => {
      if (!creee) {
        return;
      }
      this.pieces.update(liste => [creee, ...liste]);
      this.chargerPieces(demande.id, { discret: true });
    });
  }

  supprimerPiece(piece: IPieceJustificative): void {
    const modalRef = this.modalService.open(PieceJustificativeDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.pieceJustificative = piece;
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => this.chargerPieces(this.current()?.id)),
      )
      .subscribe();
  }

  /** L'absence de pièces n'empêche pas d'instruire : un échec reste silencieux. */
  private chargerPieces(demandeId?: number, options?: { discret: boolean }): void {
    if (!demandeId) {
      this.pieces.set([]);
      return;
    }
    // En mode discret, le voyant de chargement reste éteint : la liste est déjà à l'écran et la
    // remplacer par « Chargement… » ferait clignoter ce que l'on vient d'ajouter.
    if (!options?.discret) {
      this.chargementPieces.set(true);
    }
    this.pieceJustificativeService
      .query({ demandeId, size: 50, sort: ['dateAjout,desc'] })
      .pipe(catchError(() => of(null)))
      .subscribe(reponse => {
        // Une relecture en échec laisse la liste en place : la pièce est enregistrée, l'effacer
        // de l'écran serait mentir.
        if (reponse?.body) {
          this.pieces.set(reponse.body);
        } else if (!options?.discret) {
          this.pieces.set([]);
        }
        this.chargementPieces.set(false);
      });
  }

  /** Le ton du badge de statut, aligné sur celui de la liste. */
  statutTon(statut?: string | null): string {
    switch (statut) {
      case 'VALIDEE':
        return 'ok';
      case 'RETOURNEE':
      case 'REJETEE':
        return 'danger';
      case 'EN_VERIFICATION_RH':
      case 'EN_ATTENTE_VALIDATION_DRH':
      case 'EN_ATTENTE_VALIDATION_INFIRMERIE':
        return 'warn';
      // Un brouillon n'attend personne : il ne doit pas attirer l'œil comme une étape en cours.
      case 'EN_SAISIE':
        return 'neutre';
      case 'ANNULEE':
      case 'CLOTUREE':
      // Un dossier expiré est clos, pas en alerte : il n'appelle plus aucune action.
      case 'EXPIREE':
        return 'neutre';
      default:
        return 'info';
    }
  }

  /** Le journal n'est pas indispensable à l'instruction : son absence reste silencieuse. */
  private chargerHistorique(demandeId?: number): void {
    if (!demandeId) {
      this.historique.set([]);
      return;
    }
    this.historiqueActionService
      .query({ 'demandeId.equals': demandeId, size: 20, sort: ['dateAction,desc'] })
      .pipe(catchError(() => of(null)))
      .subscribe(reponse => this.historique.set(reponse?.body ?? []));
  }

  previousState(): void {
    globalThis.history.back();
  }

  valider(): void {
    const demande = this.current();
    if (!demande) {
      return;
    }
    const etape = this.canValiderDrh() ? 'la validation DRH' : "l'avis de l'infirmerie du personnel";
    // Dire ce qui suit, pas seulement ce que l'on signe : le validateur doit savoir s'il clôt
    // le circuit ou s'il le fait avancer d'un cran.
    const suite = this.canValiderDrh()
      ? 'La demande sera accordée et la prise en charge pourra être imprimée.'
      : 'Le dossier passera ensuite au contrôle des pièces par la direction des ressources humaines.';
    this.confirmer(
      {
        titre: 'Accorder la prise en charge',
        message: `Vous vous apprêtez à effectuer ${etape} de la demande ${demande.reference ?? ''}. ${suite}`,
        libelleConfirmer: 'Accorder la prise en charge',
        ton: 'primaire',
      },
      () => {
        this.isSaving.set(true);
        this.demandePriseEnChargeService.valider(demande.id).subscribe({
          next: updated => {
            this.isSaving.set(false);
            this.current.set(updated);
          },
          error: () => this.isSaving.set(false),
        });
      },
    );
  }

  soumettre(): void {
    const demande = this.current();
    if (!demande) {
      return;
    }
    this.confirmer(
      {
        titre: "Soumettre à l'avis de l'infirmerie",
        message: `Le dossier ${demande.reference ?? ''} sera transmis à l'infirmerie du personnel, qui se prononce la première sur la prise en charge du soin. Il ne pourra plus être supprimé, seulement annulé.`,
        libelleConfirmer: "Soumettre à l'infirmerie",
        ton: 'primaire',
      },
      () => {
        this.isSaving.set(true);
        this.demandePriseEnChargeService.soumettre(demande.id).subscribe({
          next: misAJour => {
            this.isSaving.set(false);
            this.current.set(misAJour);
            this.chargerHistorique(demande.id);
          },
          error: () => this.isSaving.set(false),
        });
      },
    );
  }

  verifier(): void {
    const demande = this.current();
    if (!demande) {
      return;
    }
    this.confirmer(
      {
        titre: 'Déclarer le contrôle fait',
        message: `Vous attestez que le dossier ${demande.reference ?? ''} est complet et que ses pièces sont conformes. Il passera en attente de validation DRH.`,
        libelleConfirmer: 'Déclarer le contrôle fait',
        ton: 'primaire',
      },
      () => {
        this.isSaving.set(true);
        this.demandePriseEnChargeService.verifier(demande.id).subscribe({
          next: misAJour => {
            this.isSaving.set(false);
            this.current.set(misAJour);
            this.chargerHistorique(demande.id);
          },
          error: () => this.isSaving.set(false),
        });
      },
    );
  }

  /**
   * Ouvre la pièce dans un nouvel onglet.
   *
   * La liste ne rapatrie pas les contenus — dix pièces feraient plusieurs méga-octets pour
   * n'afficher que des noms — donc la pièce est relue à la demande.
   */
  /**
   * Affiche la piece sans quitter le dossier.
   *
   * La liste ne porte pas les contenus - dix pieces rapatrieraient plusieurs mega-octets pour
   * n'afficher que des noms - donc la piece complete est demandee au moment de l'ouvrir.
   */
  ouvrirPiece(piece: IPieceJustificative): void {
    this.pieceJustificativeService.find(piece.id).subscribe(complete => {
      if (!complete.contenu) {
        return;
      }
      this.afficherDocument(
        blobDepuisBase64(complete.contenu, complete.contenuContentType),
        complete.nomFichier ?? 'Piece justificative',
        complete.nomFichier ?? `piece-${complete.id}`,
      );
    });
  }

  /**
   * Ouvre la visionneuse sur un document deja en memoire.
   *
   * Le document reste consultable a l'ecran : le telechargement est propose dans la visionneuse
   * pour qui veut le garder, mais il n'est plus le seul moyen de le voir.
   */
  private afficherDocument(document: Blob, titre: string, nomFichier: string): void {
    const modalRef = this.modalService.open(VisionneuseDocument, {
      size: 'xl',
      backdrop: 'static',
      windowClass: 'pec-visionneuse',
    });
    modalRef.componentInstance.blob = document;
    modalRef.componentInstance.titre = titre;
    modalRef.componentInstance.nomFichier = nomFichier;
  }

  /** Édite la notification de décision et la remet à l'agent. */
  editerNotification(): void {
    const demande = this.current();
    if (!demande) {
      return;
    }
    this.isDownloading.set(true);
    this.demandePriseEnChargeService.telechargerNotification(demande.id).subscribe({
      next: blob => {
        this.isDownloading.set(false);
        this.afficherDocument(
          blob,
          `Notification de decision ${demande.reference ?? ''}`.trim(),
          `notification-${demande.reference ?? demande.id}.pdf`,
        );
      },
      error: () => this.isDownloading.set(false),
    });
  }

  /** Ouvre le dialogue de confirmation, et n'exécute l'action que si l'agent confirme. */
  private confirmer(
    options: { titre: string; message: string; libelleConfirmer: string; ton: 'primaire' | 'danger' },
    action: () => void,
  ): void {
    const modalRef = this.modalService.open(ConfirmDialog, { size: 'md', backdrop: 'static' });
    Object.assign(modalRef.componentInstance, options);
    modalRef.closed.pipe(filter(reason => reason === CONFIRMED_EVENT)).subscribe(() => action());
  }

  rejeter(): void {
    const demande = this.current();
    if (!demande) {
      return;
    }
    const modalRef = this.modalService.open(DemandePriseEnChargeRejectDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.demandePriseEnCharge = demande;
    modalRef.closed
      .pipe(
        filter(reason => reason === DEMANDE_REJECTED_EVENT),
        tap(() => this.demandePriseEnChargeService.find(demande.id).subscribe(updated => this.current.set(updated))),
      )
      .subscribe();
  }

  resoumettre(): void {
    const demande = this.current();
    if (!demande) {
      return;
    }
    this.confirmer(
      {
        titre: 'Resoumettre la demande',
        message: `La demande ${demande.reference ?? ''} repartira en validation DRH, au début du circuit. Le motif de retour sera effacé.`,
        libelleConfirmer: 'Resoumettre la demande',
        ton: 'primaire',
      },
      () => {
        this.isSaving.set(true);
        this.demandePriseEnChargeService.resoumettre(demande.id).subscribe({
          next: updated => {
            this.isSaving.set(false);
            this.current.set(updated);
          },
          error: () => this.isSaving.set(false),
        });
      },
    );
  }

  imprimer(): void {
    const demande = this.current();
    if (!demande) {
      return;
    }
    this.isDownloading.set(true);
    this.demandePriseEnChargeService.telechargerRapport(demande.id).subscribe({
      next: blob => {
        this.isDownloading.set(false);
        this.afficherDocument(
          blob,
          `Prise en charge ${demande.reference ?? ''}`.trim(),
          `prise-en-charge-${demande.reference ?? demande.id}.pdf`,
        );
      },
      error: () => this.isDownloading.set(false),
    });
  }

  supprimer(): void {
    const demande = this.current();
    if (!demande) {
      return;
    }
    const modalRef = this.modalService.open(DemandePriseEnChargeDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.demandePriseEnCharge = demande;
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => this.router.navigate(['/demande-prise-en-charge'])),
      )
      .subscribe();
  }
}
