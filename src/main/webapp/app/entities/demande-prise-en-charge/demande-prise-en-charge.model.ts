import dayjs from 'dayjs/esm';

import { IAgent } from 'app/entities/agent/agent.model';
import { IAyantDroit } from 'app/entities/ayant-droit/ayant-droit.model';
import { PrioriteDemande } from 'app/entities/enumerations/priorite-demande.model';
import { StatutDemande } from 'app/entities/enumerations/statut-demande.model';
import { TypeBeneficiaire } from 'app/entities/enumerations/type-beneficiaire.model';
import { TypeSoin } from 'app/entities/enumerations/type-soin.model';
import { IEtablissementSante } from 'app/entities/etablissement-sante/etablissement-sante.model';
import { IUser } from 'app/entities/user/user.model';

export interface IDemandePriseEnCharge {
  id: number;
  reference?: string | null;
  dateCreation?: dayjs.Dayjs | null;
  dateModification?: dayjs.Dayjs | null;
  typeBeneficiaire?: keyof typeof TypeBeneficiaire | null;
  description?: string | null;
  statut?: keyof typeof StatutDemande | null;
  priorite?: keyof typeof PrioriteDemande | null;
  dateAssignation?: dayjs.Dayjs | null;
  dateEcheance?: dayjs.Dayjs | null;
  motifRejet?: string | null;
  observation?: string | null;
  agent?: Pick<IAgent, 'id' | 'matricule'> | null;
  ayantDroit?: Pick<IAyantDroit, 'id' | 'nom'> | null;
  typeSoins?: (keyof typeof TypeSoin)[] | null;
  etablissementSante?: Pick<IEtablissementSante, 'id' | 'nom'> | null;
  gestionnaireCreateur?: Pick<IUser, 'id' | 'login'> | null;
  assigneA?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewDemandePriseEnCharge = Omit<IDemandePriseEnCharge, 'id'> & { id: null };
