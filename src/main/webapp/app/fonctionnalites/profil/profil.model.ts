export interface IProfil {
  id: number;
  nom?: string | null;
  description?: string | null;
  authorities?: string[] | null;
}

export type NewProfil = Omit<IProfil, 'id'> & { id: null };
