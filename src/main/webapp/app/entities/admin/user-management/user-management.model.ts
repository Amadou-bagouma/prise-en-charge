import dayjs from 'dayjs/esm';

import { LANGUAGES } from 'app/config';

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
  authorities?: string[] | null;
}

export type NewUserManagement = Omit<IUserManagement, 'login'> & { login: null };
