import dayjs from 'dayjs/esm';

import { IAgent } from 'app/entities/agent/agent.model';
import { LienParente } from 'app/entities/enumerations/lien-parente.model';

/** Situation d'un ayant droit au regard de la couverture médicale. */
export type StatutAyantDroit = 'ACTIF' | 'SUSPENDU' | 'RADIE' | 'DECEDE';

/**
 * État du rattachement dans le circuit d'enregistrement.
 *
 * À ne pas confondre avec `StatutAyantDroit`, qui dit si la couverture joue : celui-ci dit si le
 * lien de parenté a été vérifié sur pièces. Un rattachement non vérifié ne fonde pas de dossier.
 */
export type StatutValidationAyantDroit = 'EN_SAISIE' | 'VALIDE';

export interface IAyantDroit {
  id: number;
  /** Code d'identification de l'ayant droit. Unique, exige par la base. */
  codeAyantDroit?: string | null;
  statut?: StatutAyantDroit | null;
  statutValidation?: StatutValidationAyantDroit | null;
  dateValidation?: dayjs.Dayjs | null;
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
