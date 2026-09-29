import { ChangeDetectorRef, Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';
import dayjs from 'dayjs/esm';
import { finalize } from 'rxjs';

import { AlertService } from 'app/core/util/alert.service';
import { IDemandePriseEnCharge } from 'app/fonctionnalites/demande-prise-en-charge/demande-prise-en-charge.model';
import { AlertError } from 'app/shared/alert';
import { TranslateDirective } from 'app/shared/language';
import { IPieceJustificative, NewPieceJustificative } from '../piece-justificative.model';
import { PieceJustificativeService } from '../service/piece-justificative.service';

/** Les formats qu'un guichet reçoit réellement : photo d'ordonnance, PDF scanné, document Word. */
const TYPES_ACCEPTES = [
  'image/jpeg',
  'image/png',
  'image/webp',
  'image/heic',
  'application/pdf',
  'application/msword',
  'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
];

/** Au-delà, c'est un scan à reprendre, pas une pièce à joindre. Même limite qu'au serveur. */
const TAILLE_MAX_OCTETS = 10 * 1024 * 1024;

/**
 * Joindre une pièce à un dossier, sans quitter le dossier.
 *
 * Le fichier est réellement déposé : la pièce ne se réduit plus à un nom et une référence de
 * classement, qui n'apprenaient à personne si le document existait et ne permettaient pas de le
 * consulter en instruisant le dossier.
 *
 * Les contrôles de type et de taille sont repris ici pour que l'agent le sache avant d'envoyer,
 * pas pour remplacer ceux du serveur — c'est lui qui refuse.
 */
@Component({
  selector: 'jhi-piece-justificative-dialog',
  templateUrl: './piece-justificative-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError, TranslateDirective],
})
export class PieceJustificativeDialog {
  /** Le dossier auquel rattacher la pièce, posé par l'appelant à l'ouverture. */
  demande!: Pick<IDemandePriseEnCharge, 'id' | 'reference'>;

  readonly nomFichier = signal('');
  readonly enCours = signal(false);

  /** Le fichier choisi, encodé pour le transport. */
  readonly contenu = signal<string | null>(null);
  readonly contenuContentType = signal<string | null>(null);
  readonly tailleFichier = signal<number | null>(null);
  readonly nomOriginal = signal<string | null>(null);

  /** Ce qui empêche l'envoi, dit avant d'essayer. */
  readonly refus = signal<string | null>(null);

  readonly accept = TYPES_ACCEPTES.join(',');

  readonly peutEnregistrer = computed(() => !!this.nomFichier().trim() && !!this.contenu() && !this.refus() && !this.enCours());

  protected readonly activeModal = inject(NgbActiveModal);
  protected readonly pieceJustificativeService = inject(PieceJustificativeService);
  protected readonly alertService = inject(AlertService);
  protected readonly cdr = inject(ChangeDetectorRef);

  annuler(): void {
    this.activeModal.dismiss();
  }

  /** La taille en clair : « 2,4 Mo » se lit, « 2517294 » non. */
  tailleLisible(): string {
    const octets = this.tailleFichier();
    if (octets === null) {
      return '';
    }
    if (octets < 1024) {
      return `${octets} o`;
    }
    if (octets < 1024 * 1024) {
      return `${(octets / 1024).toFixed(0)} Ko`;
    }
    return `${(octets / (1024 * 1024)).toFixed(1)} Mo`;
  }

  choisirFichier(event: Event): void {
    const cible = event.target as HTMLInputElement;
    const fichier = cible.files?.[0];
    this.refus.set(null);
    if (!fichier) {
      this.viderFichier();
      return;
    }
    if (!TYPES_ACCEPTES.includes(fichier.type)) {
      this.viderFichier();
      this.refus.set(`Ce format n'est pas accepté. Joignez une image, un PDF ou un document Word.`);
      return;
    }
    if (fichier.size > TAILLE_MAX_OCTETS) {
      this.viderFichier();
      this.refus.set(`Ce fichier dépasse ${TAILLE_MAX_OCTETS / (1024 * 1024)} Mo. Réduisez la résolution du scan.`);
      return;
    }

    const lecteur = new FileReader();
    lecteur.onload = () => {
      // `readAsDataURL` rend « data:<type>;base64,<contenu> » : seule la part encodée est envoyée.
      const resultat = String(lecteur.result ?? '');
      this.contenu.set(resultat.slice(resultat.indexOf(',') + 1));
      this.contenuContentType.set(fichier.type);
      this.tailleFichier.set(fichier.size);
      this.nomOriginal.set(fichier.name);
      // Le nom du fichier est proposé comme libellé : le retaper à l'identique n'apprend rien.
      if (!this.nomFichier().trim()) {
        this.nomFichier.set(fichier.name.replace(/\.[^.]+$/, ''));
      }
      this.cdr.markForCheck();
    };
    lecteur.onerror = () => {
      this.viderFichier();
      this.refus.set("Ce fichier n'a pas pu être lu.");
      this.cdr.markForCheck();
    };
    lecteur.readAsDataURL(fichier);
  }

  enregistrer(): void {
    if (!this.peutEnregistrer()) {
      return;
    }
    const piece: NewPieceJustificative = {
      id: null,
      nomFichier: this.nomFichier().trim(),
      contenu: this.contenu(),
      contenuContentType: this.contenuContentType(),
      tailleFichier: this.tailleFichier(),
      dateAjout: dayjs(),
      demande: this.demande,
    };
    this.enCours.set(true);
    this.pieceJustificativeService
      .create(piece)
      .pipe(finalize(() => this.enCours.set(false)))
      .subscribe({
        next: (creee: IPieceJustificative) => this.activeModal.close(creee),
        error() {
          /* l'erreur est affichée par jhi-alert-error, le dialogue reste ouvert avec la saisie */
        },
      });
  }

  private viderFichier(): void {
    this.contenu.set(null);
    this.contenuContentType.set(null);
    this.tailleFichier.set(null);
    this.nomOriginal.set(null);
  }
}
