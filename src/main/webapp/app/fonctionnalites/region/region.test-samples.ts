import { IRegion, NewRegion } from './region.model';

export const sampleWithRequiredData: IRegion = {
  id: 20776,
  nom: 'next past sit',
};

export const sampleWithPartialData: IRegion = {
  id: 16206,
  nom: 'immediate tank downshift',
};

export const sampleWithFullData: IRegion = {
  id: 32233,
  nom: 'a',
};

export const sampleWithNewData: NewRegion = {
  nom: 'towards motor blah',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
