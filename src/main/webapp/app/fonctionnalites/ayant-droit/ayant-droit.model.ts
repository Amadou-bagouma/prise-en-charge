import dayjs from 'dayjs/esm';

import { IAgent } from 'app/entities/agent/agent.model';
import { LienParente } from 'app/entities/enumerations/lien-parente.model';

export interface IAyantDroit {
  id: number;
  /** Code d'identification de l'ayant droit. Unique, exige par la base. */
  codeAyantDroit?: string | null;
  nom?: string | null;
  prenom?: string | null;
  dateNaissance?: dayjs.Dayjs | null;
  lien?: keyof typeof LienParente | null;
  photo?: string | null;
  photoContentType?: string | null;
  agent?: Pick<IAgent, 'id' | 'matricule'> | null;
}

export type NewAyantDroit = Omit<IAyantDroit, 'id'> & { id: null };
