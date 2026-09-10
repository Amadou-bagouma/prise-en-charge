import dayjs from 'dayjs/esm';

import { IDemandePriseEnCharge } from 'app/entities/demande-prise-en-charge/demande-prise-en-charge.model';
import { TypeNotification } from 'app/entities/enumerations/type-notification.model';
import { ITache } from 'app/entities/tache/tache.model';
import { IUser } from 'app/entities/user/user.model';

export interface INotification {
  id: number;
  titre?: string | null;
  message?: string | null;
  dateCreation?: dayjs.Dayjs | null;
  dateLecture?: dayjs.Dayjs | null;
  lu?: boolean | null;
  type?: keyof typeof TypeNotification | null;
  utilisateur?: Pick<IUser, 'id' | 'login'> | null;
  demande?: Pick<IDemandePriseEnCharge, 'id' | 'reference'> | null;
  tache?: Pick<ITache, 'id'> | null;
}

export type NewNotification = Omit<INotification, 'id'> & { id: null };
