import dayjs from 'dayjs/esm';

import { IBoiteReception } from 'app/fonctionnalites/boite-reception/boite-reception.model';
import { IDemandePriseEnCharge } from 'app/fonctionnalites/demande-prise-en-charge/demande-prise-en-charge.model';
import { PrioriteTache } from 'app/fonctionnalites/enumerations/priorite-tache.model';
import { StatutTache } from 'app/fonctionnalites/enumerations/statut-tache.model';
import { IUser } from 'app/fonctionnalites/user/user.model';

export interface ITache {
  id: number;
  titre?: string | null;
  description?: string | null;
  dateCreation?: dayjs.Dayjs | null;
  dateAssignation?: dayjs.Dayjs | null;
  dateEcheance?: dayjs.Dayjs | null;
  dateTerminaison?: dayjs.Dayjs | null;
  statut?: keyof typeof StatutTache | null;
  priorite?: keyof typeof PrioriteTache | null;
  lu?: boolean | null;
  commentaire?: string | null;
  demande?: Pick<IDemandePriseEnCharge, 'id' | 'reference'> | null;
  utilisateur?: Pick<IUser, 'id' | 'login'> | null;
  boiteReception?: Pick<IBoiteReception, 'id'> | null;
}

export type NewTache = Omit<ITache, 'id'> & { id: null };
