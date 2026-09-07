import { ITypeSoin, NewTypeSoin } from './type-soin.model';

export const sampleWithRequiredData: ITypeSoin = {
  id: 19928,
  libelle: 'ack ouch',
  actif: false,
};

export const sampleWithPartialData: ITypeSoin = {
  id: 6336,
  libelle: 'brr',
  actif: true,
};

export const sampleWithFullData: ITypeSoin = {
  id: 3236,
  libelle: 'whose',
  description: 'harmful',
  actif: false,
};

export const sampleWithNewData: NewTypeSoin = {
  libelle: 'respectful without',
  actif: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
