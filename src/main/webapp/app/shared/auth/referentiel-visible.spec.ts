import { describe, expect, it } from 'vitest';

import { AccountService } from 'app/core/auth';
import { Action } from 'app/shared/jhipster/actions.constants';
import { Authority } from 'app/shared/jhipster/constants';

import { voitReferentiel } from './referentiel-visible';

/** Un compte réduit à ses droits : c'est tout ce que la règle regarde. */
function compte(...droits: string[]): AccountService {
  return {
    hasAnyAuthority: (attendus: string | string[]) => (Array.isArray(attendus) ? attendus : [attendus]).some(d => droits.includes(d)),
  } as AccountService;
}

describe('voitReferentiel', () => {
  it("cache le référentiel à l'agent de saisie prod, qui le lit sans boîte de réception", () => {
    expect(voitReferentiel(compte(Authority.USER, Action.AGENT_CREER, Action.REFERENTIEL_CONSULTER))).toBe(false);
  });

  it("cache le référentiel à l'agent, même s'il a une boîte", () => {
    expect(voitReferentiel(compte(Action.ESPACE_AGENT, Action.BOITE_CONSULTER, Action.REFERENTIEL_CONSULTER))).toBe(false);
  });

  it('le montre à qui instruit des dossiers', () => {
    expect(voitReferentiel(compte(Action.BOITE_CONSULTER, Action.REFERENTIEL_CONSULTER))).toBe(true);
  });

  it('le montre à qui le tient, et à l’administrateur', () => {
    expect(voitReferentiel(compte(Action.REFERENTIEL_MODIFIER))).toBe(true);
    expect(voitReferentiel(compte(Authority.ADMIN))).toBe(true);
  });
});
