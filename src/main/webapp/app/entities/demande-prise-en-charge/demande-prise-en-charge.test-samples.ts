import dayjs from 'dayjs/esm';

import { IDemandePriseEnCharge, NewDemandePriseEnCharge } from './demande-prise-en-charge.model';

export const sampleWithRequiredData: IDemandePriseEnCharge = {
  id: 25439,
  reference: 'showboat',
  dateCreation: dayjs('2023-12-24T13:27'),
  typeBeneficiaire: 'AYANT_DROIT',
  statut: 'EN_VALIDATION',
};

export const sampleWithPartialData: IDemandePriseEnCharge = {
  id: 3640,
  reference: 'as',
  dateCreation: dayjs('2023-12-24T10:25'),
  dateModification: dayjs('2023-12-25T02:19'),
  typeBeneficiaire: 'AYANT_DROIT',
  description: 'an',
  statut: 'EN_ATTENTE_PIECES',
  dateAssignation: dayjs('2023-12-24T14:17'),
};

export const sampleWithFullData: IDemandePriseEnCharge = {
  id: 7321,
  reference: 'lasting uh-huh',
  dateCreation: dayjs('2023-12-24T21:34'),
  dateModification: dayjs('2023-12-24T19:36'),
  typeBeneficiaire: 'AGENT',
  description: 'since wealthy',
  statut: 'EN_VALIDATION',
  dateAssignation: dayjs('2023-12-25T04:36'),
};

export const sampleWithNewData: NewDemandePriseEnCharge = {
  reference: 'bemuse shore coarse',
  dateCreation: dayjs('2023-12-24T09:39'),
  typeBeneficiaire: 'AYANT_DROIT',
  statut: 'EN_ATTENTE_PIECES',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
