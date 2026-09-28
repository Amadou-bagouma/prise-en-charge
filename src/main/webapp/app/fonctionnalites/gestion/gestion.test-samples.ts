import { IGestion, NewGestion } from './gestion.model';

export const sampleWithRequiredData: IGestion = {
  id: 6008,
  nom: 'ha oxidise',
  type: 'OA',
};

export const sampleWithPartialData: IGestion = {
  id: 27434,
  nom: 'failing successfully',
  type: 'AS',
};

export const sampleWithFullData: IGestion = {
  id: 28796,
  nom: 'ecstatic camouflage',
  type: 'AS',
};

export const sampleWithNewData: NewGestion = {
  nom: 'sleepily',
  type: 'AS',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
