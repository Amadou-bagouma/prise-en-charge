/** Comment lire la valeur d'un réglage. Miroir de `TypeParametre` côté serveur. */
export type TypeParametre = 'ENTIER' | 'TEXTE' | 'BOOLEEN' | 'IMAGE';

/**
 * Un réglage de l'application, modifiable sans livraison.
 *
 * La durée de validité d'une prise en charge, celle d'une carte, le nom du directeur général et
 * sa signature vivaient dans des constantes : les changer supposait une livraison, alors que ce
 * sont des décisions de service, révisables.
 */
export interface IParametre {
  id: number;
  /** Le code interrogé par le programme : stable, jamais traduit, jamais modifiable à l'écran. */
  code: string;
  libelle: string;
  description?: string | null;
  valeur?: string | null;
  type: TypeParametre;
  /** L'image n'est pas rendue dans la liste : elle se récupère par `/parametres/{code}/image`. */
  valeurBinaire?: string | null;
  valeurBinaireContentType?: string | null;
  /** Posé par l'application : sa valeur se modifie, le réglage lui-même ne se supprime pas. */
  socle?: boolean | null;
}
