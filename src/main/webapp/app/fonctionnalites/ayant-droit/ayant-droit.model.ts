import dayjs from 'dayjs/esm';

import { IAgent } from 'app/entities/agent/agent.model';
import { LienParente } from 'app/entities/enumerations/lien-parente.model';

/** Situation d'un ayant droit au regard de la couverture médicale. */
export type StatutAyantDroit = 'ACTIF' | 'SUSPENDU' | 'RADIE' | 'DECEDE';

export interface IAyantDroit {
  id: number;
  /** Code d'identification de l'ayant droit. Unique, exige par la base. */
  codeAyantDroit?: string | null;
  statut?: StatutAyantDroit | null;
  dateStatut?: dayjs.Dayjs | null;
  motifStatut?: string | null;
  /**
   * Non nul quand le statut courant vient d'une répercussion du statut de l'agent : l'écran peut
   * alors le dire, au lieu de laisser croire à une décision prise sur l'ayant droit.
   */
  statutAvantCascade?: StatutAyantDroit | null;
  nom?: string | null;
  prenom?: string | null;
  dateNaissance?: dayjs.Dayjs | null;
  lien?: keyof typeof LienParente | null;
  photo?: string | null;
  photoContentType?: string | null;
  agent?: Pick<IAgent, 'id' | 'matricule'> | null;
}

export type NewAyantDroit = Omit<IAyantDroit, 'id'> & { id: null };
