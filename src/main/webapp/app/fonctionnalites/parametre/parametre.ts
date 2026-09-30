import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { IParametre } from './parametre.model';
import { ParametreService } from './service/parametre.service';

/** Au-delà, c'est une image à reprendre plutôt qu'une signature à déposer. */
const TAILLE_MAX_IMAGE = 2 * 1024 * 1024;

/** Ce qu'une signature peut être : une image, et rien d'autre. */
const TYPES_IMAGE = ['image/png', 'image/jpeg', 'image/webp'];

/**
 * Les réglages de l'application, ajustables sans livraison.
 *
 * Un seul écran, sans création ni suppression : le programme interroge des codes qu'il connaît.
 * Chaque ligne se modifie sur place — ouvrir un formulaire pour changer un nombre ferait trois
 * écrans là où il en faut zéro.
 *
 * Le code technique est affiché en petit à côté du libellé : c'est lui qu'on cite quand on
 * demande de l'aide, et le chercher dans le code n'est pas donné à tout le monde.
 */
@Component({
  selector: 'jhi-parametre',
  templateUrl: './parametre.html',
  imports: [FormsModule, FontAwesomeModule, Alert, AlertError],
})
export class Parametre {
  readonly parametres = signal<IParametre[]>([]);
  readonly chargement = signal(true);

  /** Le réglage en cours d'enregistrement, pour que la ligne dise ce qui se passe. */
  readonly enregistrement = signal<number | null>(null);

  /** Le réglage qui vient d'être enregistré : la confirmation se lit sur sa ligne. */
  readonly enregistre = signal<number | null>(null);

  /** Ce qui empêche de déposer une image, dit avant l'envoi. */
  readonly refus = signal<string | null>(null);

  readonly accept = TYPES_IMAGE.join(',');

  protected readonly parametreService = inject(ParametreService);

  constructor() {
    this.charger();
  }

  charger(): void {
    this.chargement.set(true);
    this.parametreService.query().subscribe({
      next: parametres => {
        this.parametres.set(parametres);
        this.chargement.set(false);
      },
      error: () => this.chargement.set(false),
    });
  }

  /** L'adresse de l'image d'un réglage, pour l'afficher telle qu'elle s'imprimera. */
  urlImage(parametre: IParametre): string {
    // L'horodatage force le navigateur à recharger après un dépôt : sans lui, il rendrait
    // l'image précédente, et l'on croirait le dépôt sans effet.
    return `${this.parametreService.urlImage(parametre.code)}?v=${this.enregistre() === parametre.id ? Date.now() : 0}`;
  }

  enregistrer(parametre: IParametre): void {
    this.enregistrement.set(parametre.id);
    this.enregistre.set(null);
    this.parametreService.update(parametre).subscribe({
      next: misAJour => {
        this.enregistrement.set(null);
        this.enregistre.set(misAJour.id);
        this.parametres.update(liste => liste.map(p => (p.id === misAJour.id ? { ...misAJour, valeurBinaire: null } : p)));
      },
      // Le refus — un nombre attendu, une valeur négative — est affiché par jhi-alert-error.
      error: () => this.enregistrement.set(null),
    });
  }

  /**
   * Dépose une image pour un réglage qui en porte une.
   *
   * L'image est envoyée aussitôt : demander un second geste après avoir choisi un fichier fait
   * croire le dépôt fait, et l'on quitte l'écran sans avoir rien enregistré.
   */
  choisirImage(parametre: IParametre, event: Event): void {
    const cible = event.target as HTMLInputElement;
    const fichier = cible.files?.[0];
    this.refus.set(null);
    if (!fichier) {
      return;
    }
    if (!TYPES_IMAGE.includes(fichier.type)) {
      this.refus.set("Ce format n'est pas accepté. Déposez une image PNG, JPEG ou WebP.");
      return;
    }
    if (fichier.size > TAILLE_MAX_IMAGE) {
      this.refus.set('Cette image dépasse 2 Mo. Réduisez sa résolution.');
      return;
    }

    const lecteur = new FileReader();
    lecteur.onload = () => {
      // `readAsDataURL` rend « data:<type>;base64,<contenu> » : seule la part encodée est envoyée.
      const resultat = String(lecteur.result ?? '');
      this.enregistrer({
        ...parametre,
        valeurBinaire: resultat.slice(resultat.indexOf(',') + 1),
        valeurBinaireContentType: fichier.type,
      });
    };
    lecteur.onerror = () => this.refus.set("Ce fichier n'a pas pu être lu.");
    lecteur.readAsDataURL(fichier);
  }
}
