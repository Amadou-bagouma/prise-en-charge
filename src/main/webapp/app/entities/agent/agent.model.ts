import dayjs from 'dayjs/esm';

import { IDirection } from 'app/entities/direction/direction.model';
import { IGestion } from 'app/entities/gestion/gestion.model';
import { IUser } from 'app/entities/user/user.model';

export interface IAgent {
  id: number;
  matricule?: string | null;
  nom?: string | null;
  prenom?: string | null;
  dateNaissance?: dayjs.Dayjs | null;
  dateEmbauche?: dayjs.Dayjs | null;
  telephone?: string | null;
  fonction?: string | null;
  photo?: string | null;
  photoContentType?: string | null;
  direction?: Pick<IDirection, 'id' | 'nom'> | null;
  gestion?: Pick<IGestion, 'id' | 'nom'> | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewAgent = Omit<IAgent, 'id'> & { id: null };
