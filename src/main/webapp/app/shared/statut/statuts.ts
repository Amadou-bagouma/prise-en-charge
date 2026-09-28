import { OptionStatut } from './changement-statut-dialog';

/**
 * Les situations d'un agent, dans l'ordre où elles se présentent : l'activité d'abord, les
 * sorties ensuite, du plus réversible au plus définitif.
 *
 * Les valeurs reprennent exactement celles de `StatutAgent` côté serveur — c'est lui qui refuse
 * un libellé inconnu, cette liste ne fait que proposer.
 */
export const STATUTS_AGENT: OptionStatut[] = [
  { valeur: 'ACTIF', libelle: 'Actif', consequence: 'Ouvre droit à la prise en charge, pour lui et pour ses ayants droit.' },
  { valeur: 'SUSPENDU', libelle: 'Suspendu', consequence: 'Droits suspendus à titre temporaire. Le dossier reste ouvert.' },
  { valeur: 'RETRAITE', libelle: 'Retraité', consequence: 'La couverture relève désormais d’un autre régime.' },
  { valeur: 'RADIE', libelle: 'Radié des effectifs', consequence: 'N’ouvre plus droit à la prise en charge.' },
  { valeur: 'DECEDE', libelle: 'Décédé', consequence: 'N’ouvre plus droit à la prise en charge.' },
];

export const STATUTS_AYANT_DROIT: OptionStatut[] = [
  { valeur: 'ACTIF', libelle: 'Actif', consequence: 'Couvert, sous réserve que son agent le soit.' },
  { valeur: 'SUSPENDU', libelle: 'Suspendu', consequence: 'Droits suspendus à titre temporaire. Le rattachement reste en place.' },
  {
    valeur: 'RADIE',
    libelle: 'Radié',
    consequence: 'N’ouvre plus droit : enfant ayant dépassé la limite d’âge, divorce, retrait.',
  },
  { valeur: 'DECEDE', libelle: 'Décédé', consequence: 'N’ouvre plus droit à la prise en charge.' },
];

/** Le mot correspondant à une valeur, ou la valeur elle-même si elle est inconnue. */
export function libelleStatut(options: OptionStatut[], valeur?: string | null): string {
  if (!valeur) {
    return '';
  }
  return options.find(o => o.valeur === valeur)?.libelle ?? valeur;
}

/**
 * Le ton du système de design correspondant à une situation.
 *
 * Le statut porte toujours son mot à côté de la pastille : la couleur seule ne se lit pas, et
 * ne se lit pas du tout pour qui distingue mal le rouge du vert.
 */
export function tonStatut(valeur?: string | null): string {
  switch (valeur) {
    case 'ACTIF':
      return 'ok';
    case 'SUSPENDU':
      return 'warn';
    case 'RADIE':
    case 'DECEDE':
      return 'danger';
    case 'RETRAITE':
      return 'neutre';
    default:
      return 'neutre';
  }
}
