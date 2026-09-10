import dayjs from 'dayjs/esm';

import { IDemandePriseEnCharge, NewDemandePriseEnCharge } from './demande-prise-en-charge.model';

export const sampleWithRequiredData: IDemandePriseEnCharge = {
  id: 25439,
  reference: 'showboat',
  dateCreation: dayjs('2023-12-24T13:27'),
  typeBeneficiaire: 'AYANT_DROIT',
  statut: 'EN_ATTENTE_AVIS_MEDICAL',
  priorite: 'URGENTE',
};

export const sampleWithPartialData: IDemandePriseEnCharge = {
  id: 28349,
  reference: 'that whenever limp',
  dateCreation: dayjs('2023-12-24T09:32'),
  dateModification: dayjs('2023-12-24T20:58'),
  typeBeneficiaire: 'AGENT',
  description: 'definite',
  statut: 'EN_ATTENTE_PIECES',
  priorite: 'NORMALE',
  dateAssignation: dayjs('2023-12-25T04:21'),
  dateEcheance: dayjs('2023-12-24T07:42'),
  motifRejet: 'potentially abacus',
};

export const sampleWithFullData: IDemandePriseEnCharge = {
  id: 7321,
  reference: 'lasting uh-huh',
  dateCreation: dayjs('2023-12-24T21:34'),
  dateModification: dayjs('2023-12-24T19:36'),
  typeBeneficiaire: 'AGENT',
  description: 'since wealthy',
  statut: 'EN_COURS_TRAITEMENT',
  priorite: 'URGENTE',
  dateAssignation: dayjs('2023-12-24T23:44'),
  dateEcheance: dayjs('2023-12-24T10:15'),
  motifRejet: 'anenst',
  observation: 'uh-huh elegantly questionably',
};

export const sampleWithNewData: NewDemandePriseEnCharge = {
  reference: 'bemuse shore coarse',
  dateCreation: dayjs('2023-12-24T09:39'),
  typeBeneficiaire: 'AYANT_DROIT',
  statut: 'A_TRAITER',
  priorite: 'IMPORTANTE',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
