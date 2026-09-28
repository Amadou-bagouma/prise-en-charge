import dayjs from 'dayjs/esm';

import { ICarteBeneficiaire, NewCarteBeneficiaire } from './carte-beneficiaire.model';

export const sampleWithRequiredData: ICarteBeneficiaire = {
  id: 3862,
  numeroCarte: 'contourner membre de l’équipe',
  typeBeneficiaire: 'AYANT_DROIT',
  dateDebutValidite: dayjs('2026-09-23'),
  dateFinValidite: dayjs('2026-09-22'),
  dateEmission: dayjs('2026-09-23'),
};

export const sampleWithPartialData: ICarteBeneficiaire = {
  id: 24655,
  numeroCarte: 'aux alentours de dans la mesure où',
  typeBeneficiaire: 'AYANT_DROIT',
  dateDebutValidite: dayjs('2026-09-23'),
  dateFinValidite: dayjs('2026-09-22'),
  dateEmission: dayjs('2026-09-22'),
};

export const sampleWithFullData: ICarteBeneficiaire = {
  id: 14891,
  numeroCarte: 'réveiller en guise de tantôt',
  typeBeneficiaire: 'AGENT',
  dateDebutValidite: dayjs('2026-09-23'),
  dateFinValidite: dayjs('2026-09-23'),
  dateEmission: dayjs('2026-09-23'),
};

export const sampleWithNewData: NewCarteBeneficiaire = {
  numeroCarte: 'gens',
  typeBeneficiaire: 'AGENT',
  dateDebutValidite: dayjs('2026-09-23'),
  dateFinValidite: dayjs('2026-09-22'),
  dateEmission: dayjs('2026-09-23'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
