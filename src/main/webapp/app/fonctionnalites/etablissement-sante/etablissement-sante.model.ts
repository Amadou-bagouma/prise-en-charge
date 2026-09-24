export interface IEtablissementSante {
  id: number;
  code?: string | null;
  nom?: string | null;
  adresse?: string | null;
  telephone?: string | null;
  actif?: boolean | null;
}

export type NewEtablissementSante = Omit<IEtablissementSante, 'id'> & { id: null };
