import dayjs from 'dayjs/esm';

import { IPieceJustificative, NewPieceJustificative } from './piece-justificative.model';

export const sampleWithRequiredData: IPieceJustificative = {
  id: 31843,
  nomFichier: 'persecute dental baa',
  cheminFichier: 'supposing ravel',
  dateAjout: dayjs('2023-12-24T08:37'),
};

export const sampleWithPartialData: IPieceJustificative = {
  id: 12612,
  nomFichier: 'for',
  cheminFichier: 'government aside habit',
  dateAjout: dayjs('2023-12-24T08:46'),
};

export const sampleWithFullData: IPieceJustificative = {
  id: 13316,
  nomFichier: 'gosh',
  cheminFichier: 'highly dwell selfish',
  dateAjout: dayjs('2023-12-24T19:58'),
};

export const sampleWithNewData: NewPieceJustificative = {
  nomFichier: 'er meadow',
  cheminFichier: 'ugh until unexpectedly',
  dateAjout: dayjs('2023-12-25T00:51'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
