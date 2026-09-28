import dayjs from 'dayjs/esm';

import { IBoiteReception, NewBoiteReception } from './boite-reception.model';

export const sampleWithRequiredData: IBoiteReception = {
  id: 13812,
  dateCreation: dayjs('2026-09-09T16:58'),
  nombreNonLus: 470,
  actif: true,
};

export const sampleWithPartialData: IBoiteReception = {
  id: 8151,
  dateCreation: dayjs('2026-09-09T08:22'),
  nombreNonLus: 31684,
  actif: false,
};

export const sampleWithFullData: IBoiteReception = {
  id: 13556,
  dateCreation: dayjs('2026-09-10T01:53'),
  dateDerniereLecture: dayjs('2026-09-10T02:49'),
  nombreNonLus: 10136,
  actif: true,
};

export const sampleWithNewData: NewBoiteReception = {
  dateCreation: dayjs('2026-09-10T01:26'),
  nombreNonLus: 3785,
  actif: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
