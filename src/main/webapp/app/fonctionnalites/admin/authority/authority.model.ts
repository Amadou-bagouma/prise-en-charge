export interface IAuthority {
  name: string;
  /**
   * Ce que l'habilitation autorise, en français administratif.
   *
   * C'est ce texte qui permet de composer un profil sans deviner : un nom technique seul
   * n'apprend rien à qui attribue le droit. Renseigné au démarrage par
   * `InitialisationHabilitations`, et modifiable ensuite.
   */
  description?: string | null;
}

export type NewAuthority = Omit<IAuthority, 'name'> & { name: null };
