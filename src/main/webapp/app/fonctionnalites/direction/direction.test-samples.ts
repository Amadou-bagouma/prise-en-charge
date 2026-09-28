import { IDirection, NewDirection } from './direction.model';

export const sampleWithRequiredData: IDirection = {
  id: 3215,
  code: 'powerfully who',
  nom: 'delirious',
};

export const sampleWithPartialData: IDirection = {
  id: 20207,
  code: 'sometimes fooey sustenance',
  nom: 'despite',
};

export const sampleWithFullData: IDirection = {
  id: 17735,
  code: 'editor gadzooks dishearten',
  nom: 'inborn besides',
};

export const sampleWithNewData: NewDirection = {
  code: 'lest since save',
  nom: 'although',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
