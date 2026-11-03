import { IFonction, NewFonction } from './fonction.model';

export const sampleWithRequiredData: IFonction = {
  id: 6008,
  libelle: 'Comptable',
};

export const sampleWithPartialData: IFonction = {
  id: 27434,
  libelle: 'Chauffeur',
};

export const sampleWithFullData: IFonction = {
  id: 28796,
  libelle: 'Secrétaire de direction',
};

export const sampleWithNewData: NewFonction = {
  libelle: 'Infirmier',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
