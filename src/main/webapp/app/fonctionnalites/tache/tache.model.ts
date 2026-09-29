import dayjs from 'dayjs/esm';

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
  /** L'habilitation qui donne la charge de cette tâche, par exemple `ROLE_VALIDATEUR_DRH`. */
  droitRequis?: string | null;
  /** Qui a pris la tâche en charge. Nul tant que personne ne s'en est saisi. */
  utilisateur?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewTache = Omit<ITache, 'id'> & { id: null };
