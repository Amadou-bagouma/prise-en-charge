export interface ITypeSoin {
  id: number;
  libelle?: string | null;
  description?: string | null;
  actif?: boolean | null;
}

export type NewTypeSoin = Omit<ITypeSoin, 'id'> & { id: null };
