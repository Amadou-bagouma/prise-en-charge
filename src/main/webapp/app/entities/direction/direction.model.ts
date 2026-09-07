import { IRegion } from 'app/entities/region/region.model';

export interface IDirection {
  id: number;
  code?: string | null;
  nom?: string | null;
  region?: Pick<IRegion, 'id'> | null;
}

export type NewDirection = Omit<IDirection, 'id'> & { id: null };
