import dayjs from 'dayjs/esm';

import { IAyantDroit, NewAyantDroit } from './ayant-droit.model';

export const sampleWithRequiredData: IAyantDroit = {
  id: 14694,
  nom: 'boggle certify before',
  prenom: 'pasta hopeful',
  lien: 'ENFANT',
};

export const sampleWithPartialData: IAyantDroit = {
  id: 28749,
  nom: 'coin sheepishly',
  prenom: 'reassuringly aside',
  dateNaissance: dayjs('2023-12-24'),
  lien: 'AUTRE',
};

export const sampleWithFullData: IAyantDroit = {
  id: 16341,
  nom: 'lest',
  prenom: 'all however defrag',
  dateNaissance: dayjs('2023-12-24'),
  lien: 'AUTRE',
};

export const sampleWithNewData: NewAyantDroit = {
  nom: 'usually violent',
  prenom: 'mild beyond what',
  lien: 'CONJOINT',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
