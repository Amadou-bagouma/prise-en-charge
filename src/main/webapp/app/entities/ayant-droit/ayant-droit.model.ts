import dayjs from 'dayjs/esm';

import { IAgent } from 'app/entities/agent/agent.model';
import { LienParente } from 'app/entities/enumerations/lien-parente.model';

export interface IAyantDroit {
  id: number;
  nom?: string | null;
  prenom?: string | null;
  dateNaissance?: dayjs.Dayjs | null;
  lien?: keyof typeof LienParente | null;
  agent?: Pick<IAgent, 'id'> | null;
}

export type NewAyantDroit = Omit<IAyantDroit, 'id'> & { id: null };
