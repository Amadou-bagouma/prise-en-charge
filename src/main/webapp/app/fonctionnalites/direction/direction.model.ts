import { IRegion } from 'app/fonctionnalites/region/region.model';

export interface IDirection {
  id: number;
  code?: string | null;
  nom?: string | null;
  region?: Pick<IRegion, 'id' | 'nom'> | null;
}

export type NewDirection = Omit<IDirection, 'id'> & { id: null };
