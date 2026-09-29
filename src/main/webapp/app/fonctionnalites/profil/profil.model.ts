export interface IProfil {
  id: number;
  nom?: string | null;
  description?: string | null;
  authorities?: string[] | null;

  /**
   * Combien d'agents portent ce profil.
   *
   * Posé par le serveur, jamais envoyé : c'est un décompte, pas un champ du profil. Il dit ce
   * qu'une modification va toucher, et si la suppression est seulement envisageable.
   */
  nombreTitulaires?: number | null;
}

export type NewProfil = Omit<IProfil, 'id'> & { id: null };
