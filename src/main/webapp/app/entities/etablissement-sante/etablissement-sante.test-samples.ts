import { IEtablissementSante, NewEtablissementSante } from './etablissement-sante.model';

export const sampleWithRequiredData: IEtablissementSante = {
  id: 14692,
  code: 'gadzooks couch rectangular',
  nom: 'worse juggernaut',
  actif: false,
};

export const sampleWithPartialData: IEtablissementSante = {
  id: 25256,
  code: 'tight needily yearningly',
  nom: 'dial',
  telephone: '278.565.4238 x330',
  actif: true,
};

export const sampleWithFullData: IEtablissementSante = {
  id: 19691,
  code: 'through towards',
  nom: 'over',
  adresse: 'merrily ouch critical',
  telephone: '1-299-231-7506 x7605',
  actif: true,
};

export const sampleWithNewData: NewEtablissementSante = {
  code: 'pine yawningly',
  nom: 'tenant',
  actif: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
