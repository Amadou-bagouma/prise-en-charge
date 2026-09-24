import dayjs from 'dayjs/esm';

import { ITache, NewTache } from './tache.model';

export const sampleWithRequiredData: ITache = {
  id: 655,
  titre: 'consequently daily common',
  dateCreation: dayjs('2026-09-09T10:10'),
  statut: 'ANNULEE',
  priorite: 'URGENTE',
  lu: false,
};

export const sampleWithPartialData: ITache = {
  id: 19886,
  titre: 'whenever frantically devil',
  description: 'elementary gee',
  dateCreation: dayjs('2026-09-09T12:17'),
  dateEcheance: dayjs('2026-09-09T12:39'),
  statut: 'ANNULEE',
  priorite: 'IMPORTANTE',
  lu: false,
  commentaire: 'although whoever confused',
};

export const sampleWithFullData: ITache = {
  id: 30830,
  titre: 'along nicely cone',
  description: 'scientific',
  dateCreation: dayjs('2026-09-09T16:26'),
  dateAssignation: dayjs('2026-09-09T20:34'),
  dateEcheance: dayjs('2026-09-10T05:38'),
  dateTerminaison: dayjs('2026-09-09T22:27'),
  statut: 'EN_COURS',
  priorite: 'IMPORTANTE',
  lu: true,
  commentaire: 'amid fooey',
};

export const sampleWithNewData: NewTache = {
  titre: 'victoriously',
  dateCreation: dayjs('2026-09-09T20:44'),
  statut: 'EN_COURS',
  priorite: 'IMPORTANTE',
  lu: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
