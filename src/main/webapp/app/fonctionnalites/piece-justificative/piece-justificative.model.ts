import dayjs from 'dayjs/esm';

import { IDemandePriseEnCharge } from 'app/entities/demande-prise-en-charge/demande-prise-en-charge.model';

export interface IPieceJustificative {
  id: number;
  nomFichier?: string | null;
  cheminFichier?: string | null;
  dateAjout?: dayjs.Dayjs | null;
  demande?: Pick<IDemandePriseEnCharge, 'id' | 'reference'> | null;
}

export type NewPieceJustificative = Omit<IPieceJustificative, 'id'> & { id: null };
