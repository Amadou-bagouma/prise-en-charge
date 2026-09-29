import { StatutValidationAyantDroit } from './ayant-droit.model';

/**
 * Le circuit d'enregistrement d'un ayant droit, dit en clair.
 *
 * Un rattachement se saisit d'abord, puis se vérifie sur pièces — acte de naissance, acte de
 * mariage, pièce d'identité. Tant qu'il n'est pas vérifié, il ne fonde aucune prise en charge :
 * la distinction est donc affichée partout où l'ayant droit se présente, et pas seulement sur
 * l'écran de celui qui la prononce.
 *
 * À ne pas confondre avec la situation (`STATUTS_AYANT_DROIT`), qui dit si la couverture joue.
 */
export function libelleValidation(statut?: StatutValidationAyantDroit | null): string {
  return statut === 'VALIDE' ? 'Rattachement vérifié' : 'Rattachement à vérifier';
}

/** Le ton du système de design. Le mot est toujours affiché à côté : la couleur seule ne se lit pas. */
export function tonValidation(statut?: StatutValidationAyantDroit | null): string {
  return statut === 'VALIDE' ? 'ok' : 'warn';
}

/** Vrai tant que le rattachement n'a pas été vérifié — donc tant qu'il reste corrigeable. */
export function enSaisie(statut?: StatutValidationAyantDroit | null): boolean {
  return statut !== 'VALIDE';
}
