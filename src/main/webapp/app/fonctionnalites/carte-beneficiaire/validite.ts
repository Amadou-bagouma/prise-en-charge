import dayjs from 'dayjs/esm';

import { ICarteBeneficiaire } from './carte-beneficiaire.model';

/**
 * L'état d'une carte au regard de sa période de validité.
 *
 * C'est la seule chose qu'on vient vérifier sur une carte : elle est calculée une fois, ici,
 * plutôt que reconstituée de tête à partir de deux dates dans chaque écran.
 */
export type EtatValidite = 'valide' | 'expiree' | 'a-venir' | 'expire-bientot' | 'inconnue';

/** En deçà de ce délai, une carte encore valide mérite d'être signalée : elle va devoir être renouvelée. */
const JOURS_AVANT_ALERTE = 30;

export function etatValidite(carte: Pick<ICarteBeneficiaire, 'dateDebutValidite' | 'dateFinValidite'>): EtatValidite {
  const debut = carte.dateDebutValidite;
  const fin = carte.dateFinValidite;
  if (!debut || !fin) {
    return 'inconnue';
  }
  const aujourdhui = dayjs();
  if (aujourdhui.isBefore(debut, 'day')) {
    return 'a-venir';
  }
  if (aujourdhui.isAfter(fin, 'day')) {
    return 'expiree';
  }
  return fin.diff(aujourdhui, 'day') <= JOURS_AVANT_ALERTE ? 'expire-bientot' : 'valide';
}

/** Le mot qui accompagne toujours la pastille : la couleur seule ne se lit pas. */
export function libelleValidite(carte: Pick<ICarteBeneficiaire, 'dateDebutValidite' | 'dateFinValidite'>): string {
  switch (etatValidite(carte)) {
    case 'valide':
      return 'Valide';
    case 'expire-bientot':
      return 'Expire bientôt';
    case 'expiree':
      return 'Expirée';
    case 'a-venir':
      return 'Pas encore valide';
    default:
      return 'Validité non renseignée';
  }
}

/** Le ton du système de design correspondant. */
export function tonValidite(carte: Pick<ICarteBeneficiaire, 'dateDebutValidite' | 'dateFinValidite'>): string {
  switch (etatValidite(carte)) {
    case 'valide':
      return 'ok';
    case 'expire-bientot':
      return 'warn';
    case 'expiree':
      return 'danger';
    default:
      return 'neutre';
  }
}
