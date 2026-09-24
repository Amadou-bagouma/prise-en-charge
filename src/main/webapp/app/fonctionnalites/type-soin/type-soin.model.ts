export interface ITypeSoin {
  id: number;
  /**
   * Clé stable du référentiel. Elle ne change pas quand le libellé est reformulé, et c'est
   * elle que le rapport PDF interroge pour cocher les cases de l'imprimé officiel.
   */
  code?: string | null;
  libelle?: string | null;
  description?: string | null;
  /** Rang d'affichage : l'ordre de l'imprimé officiel fait foi, pas l'alphabet. */
  ordre?: number | null;
  /** Un type désactivé reste sur les dossiers où il a servi, mais ne se propose plus. */
  actif?: boolean | null;
}

export type NewTypeSoin = Omit<ITypeSoin, 'id'> & { id: null };
