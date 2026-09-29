import dayjs from 'dayjs/esm';

import { IDemandePriseEnCharge } from 'app/entities/demande-prise-en-charge/demande-prise-en-charge.model';

export interface IPieceJustificative {
  id: number;
  nomFichier?: string | null;
  /** Référence de classement du dossier papier, si celui-ci existe encore. Facultative. */
  cheminFichier?: string | null;
  /** Le fichier déposé, encodé. Absent des listes : seule la consultation d'une pièce le rapatrie. */
  contenu?: string | null;
  contenuContentType?: string | null;
  tailleFichier?: number | null;
  dateAjout?: dayjs.Dayjs | null;
  demande?: Pick<IDemandePriseEnCharge, 'id' | 'reference'> | null;
}

export type NewPieceJustificative = Omit<IPieceJustificative, 'id'> & { id: null };
