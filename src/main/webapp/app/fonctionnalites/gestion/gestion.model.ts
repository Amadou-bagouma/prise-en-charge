import { TypeGestion } from 'app/entities/enumerations/type-gestion.model';

export interface IGestion {
  id: number;
  nom?: string | null;
  type?: keyof typeof TypeGestion | null;
}

export type NewGestion = Omit<IGestion, 'id'> & { id: null };
