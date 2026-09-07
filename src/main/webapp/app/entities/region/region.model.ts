export interface IRegion {
  id: number;
  nom?: string | null;
}

export type NewRegion = Omit<IRegion, 'id'> & { id: null };
