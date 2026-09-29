import dayjs from 'dayjs/esm';

import { IDirection } from 'app/fonctionnalites/direction/direction.model';
import { IGestion } from 'app/fonctionnalites/gestion/gestion.model';

/** Situation d'un agent au regard de la couverture médicale. */
export type StatutAgent = 'ACTIF' | 'SUSPENDU' | 'RETRAITE' | 'RADIE' | 'DECEDE';

export interface IAgent {
  id: number;
  matricule?: string | null;
  /** Toujours renseignée : un dossier sans situation connue laisserait le guichet décider au cas par cas. */
  statut?: StatutAgent | null;
  dateStatut?: dayjs.Dayjs | null;
  motifStatut?: string | null;
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
}

export type NewAgent = Omit<IAgent, 'id'> & { id: null };
