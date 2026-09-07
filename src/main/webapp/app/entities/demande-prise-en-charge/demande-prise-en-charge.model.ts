import dayjs from 'dayjs/esm';

import { IAgent } from 'app/entities/agent/agent.model';
import { IAyantDroit } from 'app/entities/ayant-droit/ayant-droit.model';
import { StatutDemande } from 'app/entities/enumerations/statut-demande.model';
import { TypeBeneficiaire } from 'app/entities/enumerations/type-beneficiaire.model';
import { IEtablissementSante } from 'app/entities/etablissement-sante/etablissement-sante.model';
import { ITypeSoin } from 'app/entities/type-soin/type-soin.model';
import { IUser } from 'app/entities/user/user.model';

export interface IDemandePriseEnCharge {
  id: number;
  reference?: string | null;
  dateCreation?: dayjs.Dayjs | null;
  dateModification?: dayjs.Dayjs | null;
  typeBeneficiaire?: keyof typeof TypeBeneficiaire | null;
  description?: string | null;
  statut?: keyof typeof StatutDemande | null;
  dateAssignation?: dayjs.Dayjs | null;
  agent?: Pick<IAgent, 'id'> | null;
  ayantDroit?: Pick<IAyantDroit, 'id'> | null;
  typeSoin?: Pick<ITypeSoin, 'id'> | null;
  etablissementSante?: Pick<IEtablissementSante, 'id'> | null;
  gestionnaireCreateur?: Pick<IUser, 'id' | 'login'> | null;
  assigneA?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewDemandePriseEnCharge = Omit<IDemandePriseEnCharge, 'id'> & { id: null };
