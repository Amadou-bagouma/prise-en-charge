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

  /** L'aperçu de chaque image déposée, par code de réglage. */
  readonly images = signal<Record<string, string>>({});

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
        parametres.filter(p => p.valeurBinaireContentType).forEach(p => this.chargerImage(p.code));
      },
      error: () => this.chargement.set(false),
    });
  }

  /**
   * L'image d'un réglage, pour l'afficher telle qu'elle s'imprimera : `undefined` tant qu'elle
   * se charge, vide si elle n'a pas pu l'être.
   */
  apercu(parametre: IParametre): string | undefined {
    return this.images()[parametre.code];
  }

  enregistrer(parametre: IParametre): void {
    this.enregistrement.set(parametre.id);
    this.enregistre.set(null);
    this.parametreService.update(parametre).subscribe({
      next: misAJour => {
        this.enregistrement.set(null);
        this.enregistre.set(misAJour.id);
        this.parametres.update(liste => liste.map(p => (p.id === misAJour.id ? { ...misAJour, valeurBinaire: null } : p)));
        // Recharger l'aperçu après un dépôt : sans cela, l'écran montrerait l'image précédente,
        // et l'on croirait le dépôt sans effet.
        if (misAJour.valeurBinaireContentType) {
          this.chargerImage(misAJour.code);
        }
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
      const resultat = typeof lecteur.result === 'string' ? lecteur.result : '';
      this.enregistrer({
        ...parametre,
        valeurBinaire: resultat.slice(resultat.indexOf(',') + 1),
        valeurBinaireContentType: fichier.type,
      });
    };
    lecteur.onerror = () => this.refus.set("Ce fichier n'a pas pu être lu.");
    lecteur.readAsDataURL(fichier);
  }

  /**
   * Charge l'aperçu d'une image avec le jeton de connexion : une balise `<img>` pointée sur
   * l'adresse de l'image partirait sans lui, et le serveur la refuserait.
   */
  private chargerImage(code: string): void {
    this.parametreService.imageEnDonnees(code).subscribe({
      next: image => this.images.update(images => ({ ...images, [code]: image })),
      error: () => this.images.update(images => ({ ...images, [code]: '' })),
    });
  }
}
