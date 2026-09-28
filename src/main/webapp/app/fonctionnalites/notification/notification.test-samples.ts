import dayjs from 'dayjs/esm';

import { INotification, NewNotification } from './notification.model';

export const sampleWithRequiredData: INotification = {
  id: 10110,
  titre: 'reclassify makeover',
  message: 'muscat',
  dateCreation: dayjs('2026-09-09T14:04'),
  lu: true,
  type: 'DEMANDE_MODIFIEE',
};

export const sampleWithPartialData: INotification = {
  id: 24804,
  titre: 'ha',
  message: 'ouch next',
  dateCreation: dayjs('2026-09-10T04:07'),
  lu: true,
  type: 'ECHEANCE_PROCHE',
};

export const sampleWithFullData: INotification = {
  id: 5787,
  titre: 'some extra-large',
  message: 'bruised',
  dateCreation: dayjs('2026-09-09T20:27'),
  dateLecture: dayjs('2026-09-09T11:41'),
  lu: false,
  type: 'INFORMATION',
};

export const sampleWithNewData: NewNotification = {
  titre: 'consequently voluntarily ew',
  message: 'honesty depart acquaintance',
  dateCreation: dayjs('2026-09-10T07:34'),
  lu: true,
  type: 'DEMANDE_VALIDEE',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
