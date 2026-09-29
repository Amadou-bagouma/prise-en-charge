import { Component, computed, inject, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { VisionneuseDocument, blobDepuisBase64 } from 'app/shared/document/visionneuse-document';
import { TranslateDirective } from 'app/shared/language';
import { IPieceJustificative } from '../piece-justificative.model';
import { PieceJustificativeService } from '../service/piece-justificative.service';

@Component({
  selector: 'jhi-piece-justificative-detail',
  templateUrl: './piece-justificative-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, RouterLink, FormatMediumDatetimePipe],
})
export class PieceJustificativeDetail {
  readonly pieceJustificative = input<IPieceJustificative | null>(null);

  /** Une pièce déposée avant que les fichiers existent n'a rien à montrer. */
  readonly aUnDocument = computed(() => !!this.pieceJustificative()?.contenuContentType);

  /** La taille en clair : « 2,4 Mo » se lit, « 2517294 » non. */
  readonly tailleLisible = computed(() => {
    const octets = this.pieceJustificative()?.tailleFichier;
    if (octets === null || octets === undefined) {
      return '';
    }
    if (octets < 1024) {
      return `${octets} o`;
    }
    if (octets < 1024 * 1024) {
      return `${(octets / 1024).toFixed(0)} Ko`;
    }
    return `${(octets / (1024 * 1024)).toFixed(1)} Mo`;
  });

  protected readonly modalService = inject(NgbModal);
  protected readonly pieceJustificativeService = inject(PieceJustificativeService);

  /**
   * Affiche le document dans une visionneuse.
   *
   * La pièce chargée avec l'écran ne porte pas forcément son contenu : il est redemandé, de
   * sorte que l'écran s'affiche sans attendre plusieurs méga-octets qui ne seront peut-être
   * jamais regardés.
   */
  consulter(): void {
    const piece = this.pieceJustificative();
    if (!piece?.id) {
      return;
    }
    this.pieceJustificativeService.find(piece.id).subscribe(complete => {
      if (!complete.contenu) {
        return;
      }
      const modalRef = this.modalService.open(VisionneuseDocument, {
        size: 'xl',
        backdrop: 'static',
        windowClass: 'pec-visionneuse',
      });
      modalRef.componentInstance.blob = blobDepuisBase64(complete.contenu, complete.contenuContentType);
      modalRef.componentInstance.titre = complete.nomFichier ?? 'Pièce justificative';
      modalRef.componentInstance.nomFichier = complete.nomFichier ?? `piece-${complete.id}`;
    });
  }

  previousState(): void {
    globalThis.history.back();
  }
}
