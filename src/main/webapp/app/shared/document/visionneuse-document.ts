import { Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

/** Ce que le visionneur sait afficher lui-même. Le reste se télécharge. */
const TYPES_AFFICHABLES = ['application/pdf', 'image/jpeg', 'image/png', 'image/webp', 'image/gif'];

/**
 * Affiche un document sans quitter le dossier.
 *
 * Jusqu'ici une pièce ou un imprimé s'ouvrait dans un onglet ou tombait dans le dossier des
 * téléchargements : pour vérifier qu'une ordonnance correspond bien au dossier, il fallait
 * passer d'une fenêtre à l'autre, et l'on revenait au dossier sans le document sous les yeux.
 *
 * Le cadre est large et haut à dessein — un A4 illisible ne se vérifie pas, et un document que
 * l'on doit agrandir à chaque ouverture n'est pas consulté, il est deviné.
 *
 * Ce qui ne s'affiche pas — un document Word, par exemple — le dit et se télécharge : afficher
 * un cadre vide laisserait croire que la pièce est absente.
 */
@Component({
  selector: 'jhi-visionneuse-document',
  templateUrl: './visionneuse-document.html',
  imports: [FontAwesomeModule],
})
export class VisionneuseDocument {
  /** Ce que l'on regarde, écrit en toutes lettres dans l'en-tête. */
  titre = 'Document';

  /** Sous quel nom le document sera enregistré si on le télécharge. */
  nomFichier = 'document.pdf';

  readonly url = signal<SafeResourceUrl | null>(null);

  readonly typeMime = signal('application/octet-stream');

  readonly estPdf = computed(() => this.typeMime().startsWith('application/pdf'));

  readonly estImage = computed(() => this.typeMime().startsWith('image/'));

  readonly estAffichable = computed(() => TYPES_AFFICHABLES.some(type => this.typeMime().startsWith(type)));

  /** L'adresse brute, gardée pour le téléchargement et pour la révoquer à la fermeture. */
  private readonly urlBrute = signal<string | null>(null);

  protected readonly activeModal = inject(NgbActiveModal);
  private readonly sanitizer = inject(DomSanitizer);
  private readonly destroyRef = inject(DestroyRef);

  constructor() {
    // Sans cela chaque consultation laisserait le document en mémoire jusqu'au rechargement de
    // la page — dix pièces ouvertes, dix documents retenus.
    this.destroyRef.onDestroy(() => {
      const brute = this.urlBrute();
      if (brute) {
        URL.revokeObjectURL(brute);
      }
    });
  }

  /**
   * Le document lui-même, posé par l'appelant après l'ouverture de la fenêtre.
   *
   * C'est un mutateur et non un champ : ng-bootstrap construit le composant — et exécute donc
   * son `ngOnInit` — avant que l'appelant n'ait pu renseigner quoi que ce soit. Un montage qui
   * lirait le document à l'initialisation ne trouverait rien, et la fenêtre s'ouvrirait vide
   * sans que rien ne le signale.
   */
  set blob(valeur: Blob) {
    const url = URL.createObjectURL(valeur);
    this.urlBrute.set(url);
    // L'adresse vient d'être fabriquée ici, à partir d'un contenu déjà en mémoire : elle ne
    // désigne rien d'extérieur, et Angular ne peut pas le savoir seul.
    this.url.set(this.sanitizer.bypassSecurityTrustResourceUrl(url));
    this.typeMime.set(valeur.type || 'application/octet-stream');
  }

  /** Remet le document à qui veut le garder : il n'est pas toujours consulté à l'écran. */
  telecharger(): void {
    const url = this.urlBrute();
    if (!url) {
      return;
    }
    const lien = document.createElement('a');
    lien.href = url;
    lien.download = this.nomFichier;
    lien.click();
  }

  /** Une pleine page pour lire, ou pour imprimer depuis le navigateur. */
  ouvrirDansUnOnglet(): void {
    const url = this.urlBrute();
    if (url) {
      globalThis.open(url, '_blank');
    }
  }

  fermer(): void {
    this.activeModal.dismiss();
  }
}

/**
 * Reconstitue un document à partir de ce que rend le serveur pour une pièce jointe.
 *
 * Les pièces voyagent encodées dans le JSON du dossier, les imprimés arrivent déjà en binaire :
 * le visionneur ne connaît que la seconde forme, la conversion est faite ici une fois pour
 * toutes plutôt que dans chaque écran.
 */
export function blobDepuisBase64(contenu: string, typeMime?: string | null): Blob {
  const binaire = atob(contenu);
  const octets = new Uint8Array(binaire.length);
  for (let i = 0; i < binaire.length; i++) {
    octets[i] = binaire.charCodeAt(i);
  }
  return new Blob([octets], { type: typeMime ?? 'application/octet-stream' });
}
