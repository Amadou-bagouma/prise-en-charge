import dayjs from 'dayjs/esm';

import { LANGUAGES } from 'app/config';
import { IAgent } from 'app/fonctionnalites/agent/agent.model';
import { IProfil } from 'app/fonctionnalites/profil/profil.model';

export interface IUserManagement {
  id?: number | null;
  login: string;
  firstName?: string | null;
  lastName?: string | null;
  email?: string | null;
  activated?: boolean | null;
  /** Initial password set by an admin on creation. Write-only: never returned by the API. */
  password?: string | null;
  mustChangePassword?: boolean | null;
  langKey?: (typeof LANGUAGES)[number] | null;
  imageUrl?: string | null;
  /** Signature image of a DRH/infirmerie validator, printed on the demande's PDF report. */
  signature?: string | null;
  signatureContentType?: string | null;
  createdBy?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
  /** Read-only: the authorities granted by {@link profil}, returned by the API for display. */
  authorities?: string[] | null;
  /** The profil (bundle of authorities) assigned to this user. Mandatory. */
  profil?: Pick<IProfil, 'id' | 'nom' | 'authorities'> | null;
  /**
   * L'agent dont ce compte est l'accès personnel, s'il en est un.
   *
   * Facultatif : la plupart des comptes sont ceux du personnel administratif, qui instruit les
   * dossiers des autres sans être lui-même bénéficiaire. Renseigné, il décide de ce que l'espace
   * personnel montre — le serveur borne alors les listes à ce seul agent.
   */
  agent?: Pick<IAgent, 'id' | 'matricule' | 'nom' | 'prenom'> | null;
}

export type NewUserManagement = Omit<IUserManagement, 'login'> & { login: null };
