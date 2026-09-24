import dayjs from 'dayjs/esm';

import { IDemandePriseEnCharge } from 'app/entities/demande-prise-en-charge/demande-prise-en-charge.model';
import { IUser } from 'app/entities/user/user.model';

export interface IHistoriqueAction {
  id: number;
  action?: string | null;
  description?: string | null;
  dateAction?: dayjs.Dayjs | null;
  demande?: Pick<IDemandePriseEnCharge, 'id' | 'reference'> | null;
  utilisateur?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewHistoriqueAction = Omit<IHistoriqueAction, 'id'> & { id: null };
