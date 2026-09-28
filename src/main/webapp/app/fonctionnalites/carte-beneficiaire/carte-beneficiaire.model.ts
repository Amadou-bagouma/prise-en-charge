import dayjs from 'dayjs/esm';

import { IAgent } from 'app/fonctionnalites/agent/agent.model';
import { IAyantDroit } from 'app/fonctionnalites/ayant-droit/ayant-droit.model';
import { TypeBeneficiaire } from 'app/fonctionnalites/enumerations/type-beneficiaire.model';

export interface ICarteBeneficiaire {
  id: number;
  numeroCarte?: string | null;
  typeBeneficiaire?: keyof typeof TypeBeneficiaire | null;
  dateDebutValidite?: dayjs.Dayjs | null;
  dateFinValidite?: dayjs.Dayjs | null;
  dateEmission?: dayjs.Dayjs | null;
  agent?: Pick<IAgent, 'id' | 'matricule'> | null;
  ayantDroit?: Pick<IAyantDroit, 'id' | 'nom'> | null;
}

export type NewCarteBeneficiaire = Omit<ICarteBeneficiaire, 'id'> & { id: null };
