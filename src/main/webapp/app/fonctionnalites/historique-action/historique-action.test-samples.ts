import dayjs from 'dayjs/esm';

import { IHistoriqueAction, NewHistoriqueAction } from './historique-action.model';

export const sampleWithRequiredData: IHistoriqueAction = {
  id: 829,
  action: 'injunction siege illustrious',
  dateAction: dayjs('2026-09-09T22:27'),
};

export const sampleWithPartialData: IHistoriqueAction = {
  id: 11454,
  action: 'deselect talkative',
  description: 'foolish gadzooks fake',
  dateAction: dayjs('2026-09-10T03:25'),
};

export const sampleWithFullData: IHistoriqueAction = {
  id: 1268,
  action: 'inconsequential',
  description: 'unimportant',
  dateAction: dayjs('2026-09-10T01:17'),
};

export const sampleWithNewData: NewHistoriqueAction = {
  action: 'abaft bar',
  dateAction: dayjs('2026-09-09T11:11'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
