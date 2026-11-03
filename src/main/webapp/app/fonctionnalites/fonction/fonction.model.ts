export interface IFonction {
  id: number;
  libelle?: string | null;
}

export type NewFonction = Omit<IFonction, 'id'> & { id: null };
