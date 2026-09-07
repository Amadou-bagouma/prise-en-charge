import dayjs from 'dayjs/esm';

import { IAgent, NewAgent } from './agent.model';

export const sampleWithRequiredData: IAgent = {
  id: 10819,
  matricule: 'until brr ick',
  nom: 'deer out',
  prenom: 'closely enrage atop',
};

export const sampleWithPartialData: IAgent = {
  id: 5462,
  matricule: 'neatly buzzing ice-cream',
  nom: 'splay clearly',
  prenom: 'white regulate',
  fonction: 'fearless woot dependable',
};

export const sampleWithFullData: IAgent = {
  id: 15180,
  matricule: 'swiftly',
  nom: 'until yearly',
  prenom: 'amongst jungle',
  dateNaissance: dayjs('2023-12-25'),
  telephone: '405-749-2621 x0573',
  fonction: 'pro anxiously',
};

export const sampleWithNewData: NewAgent = {
  matricule: 'besmirch while',
  nom: 'whose instead',
  prenom: 'opposite courtroom gosh',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
