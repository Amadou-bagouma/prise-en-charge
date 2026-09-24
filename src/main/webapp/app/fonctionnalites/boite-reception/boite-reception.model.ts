import dayjs from 'dayjs/esm';

import { IUser } from 'app/entities/user/user.model';

export interface IBoiteReception {
  id: number;
  dateCreation?: dayjs.Dayjs | null;
  dateDerniereLecture?: dayjs.Dayjs | null;
  nombreNonLus?: number | null;
  actif?: boolean | null;
  utilisateur?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewBoiteReception = Omit<IBoiteReception, 'id'> & { id: null };
