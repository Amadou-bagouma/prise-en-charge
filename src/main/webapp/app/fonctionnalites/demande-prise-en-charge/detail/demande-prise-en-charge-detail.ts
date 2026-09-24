import { NgClass } from '@angular/common';
import { Component, computed, effect, inject, input, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { catchError, filter, of, tap } from 'rxjs';

import { ITEM_DELETED_EVENT } from 'app/config';
import { Alert, AlertError } from 'app/shared/alert';
import { CONFIRMED_EVENT, ConfirmDialog } from 'app/shared/confirm';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { AccountService } from 'app/core/auth';
import { Authority } from 'app/shared/jhipster/constants';
import { IHistoriqueAction } from 'app/fonctionnalites/historique-action/historique-action.model';
import { HistoriqueActionService } from 'app/fonctionnalites/historique-action/service/historique-action.service';
import { IPieceJustificative } from 'app/fonctionnalites/piece-justificative/piece-justificative.model';
import { PieceJustificativeDeleteDialog } from 'app/fonctionnalites/piece-justificative/delete/piece-justificative-delete-dialog';
import { PieceJustificativeService } from 'app/fonctionnalites/piece-justificative/service/piece-justificative.service';
import { DemandePriseEnChargeDeleteDialog } from '../delete/demande-prise-en-charge-delete-dialog';
import { IDemandePriseEnCharge } from '../demande-prise-en-charge.model';
import { DemandePriseEnChargeService } from '../service/demande-prise-en-charge.service';
import { DEMANDE_REJECTED_EVENT, DemandePriseEnChargeRejectDialog } from '../reject/demande-prise-en-charge-reject-dialog';

/**
 * Le circuit d'une demande, dans l'ordre où elle le parcourt. Chaque étape regroupe les
 * statuts qui la désignent : la saisie couvre tout ce qui précède l'envoi en validation.
 */
const ETAPES_CIRCUIT: { libelle: string; statuts: string[] }[] = [
  {
    libelle: 'Saisie du dossier',
    statuts: ['NOUVELLE', 'EN_ATTENTE_PIECES', 'A_TRAITER', 'EN_COURS_TRAITEMENT', 'RETOURNEE'],
  },
  { libelle: 'Validation DRH', statuts: ['EN_ATTENTE_VALIDATION_DRH'] },
  {
    libelle: 'Validation infirmerie',
    statuts: ['EN_ATTENTE_VALIDATION_INFIRMERIE', 'EN_ATTENTE_AVIS_MEDICAL', 'EN_ATTENTE_DECISION'],
  },
  { libelle: 'Décision', statuts: ['VALIDEE', 'REJETEE'] },
];

@Component({
  selector: 'jhi-demande-prise-en-charge-detail',
  templateUrl: './demande-prise-en-charge-detail.html',
  imports: [NgClass, FontAwesomeModule, Alert, AlertError, TranslateDirective, RouterLink, FormatMediumDatetimePipe],
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
  readonly canSupprimer = computed(() => this.accountService.hasAnyAuthority(Authority.ADMIN));
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

  /** Ouvre la saisie d'une pièce avec le dossier déjà rattaché. */
  ajouterPiece(): void {
    const demandeId = this.current()?.id;
    if (demandeId) {
      this.router.navigate(['/piece-justificative', 'new'], { queryParams: { demande: demandeId } });
    }
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
  private chargerPieces(demandeId?: number): void {
    if (!demandeId) {
      this.pieces.set([]);
      return;
    }
    this.chargementPieces.set(true);
    this.pieceJustificativeService
      .query({ demandeId, size: 50, sort: ['dateAjout,desc'] })
      .pipe(catchError(() => of(null)))
      .subscribe(reponse => {
        this.pieces.set(reponse?.body ?? []);
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
      case 'EN_ATTENTE_VALIDATION_DRH':
      case 'EN_ATTENTE_VALIDATION_INFIRMERIE':
        return 'warn';
      case 'ANNULEE':
      case 'CLOTUREE':
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
    const etape = this.canValiderDrh() ? 'la validation DRH' : "la validation de l'infirmerie du personnel";
    const suite = this.canValiderDrh()
      ? "La demande passera ensuite en attente de validation de l'infirmerie du personnel."
      : 'La demande sera accordée et la prise en charge pourra être imprimée.';
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
        const objectUrl = URL.createObjectURL(blob);
        const anchor = document.createElement('a');
        anchor.href = objectUrl;
        anchor.download = `rapport-${demande.reference ?? demande.id}.pdf`;
        anchor.click();
        URL.revokeObjectURL(objectUrl);
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
