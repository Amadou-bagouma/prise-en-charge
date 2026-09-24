import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt, faPlus, faTimes } from '@fortawesome/free-solid-svg-icons';
import { provideTranslateService } from '@ngx-translate/core';
import { of } from 'rxjs';

import { AgentDetail } from './agent-detail';

describe('Agent Management Detail Component', () => {
  let comp: AgentDetail;
  let fixture: ComponentFixture<AgentDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideTranslateService(),
        provideHttpClientTesting(),
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./agent-detail').then(m => m.AgentDetail),
              resolve: { agent: () => of({ id: 25235 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    });
    const library = TestBed.inject(FaIconLibrary);
    // L'ecran liste desormais les ayants droit de l'agent : il en porte les actions,
    // donc leurs icones.
    library.addIcons(faArrowLeft, faPencilAlt, faPlus, faTimes);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AgentDetail);
    comp = fixture.componentInstance;
  });

  /**
   * L'ecran va chercher les ayants droit de l'agent des son affichage. Les specs ne portent pas
   * sur cette liste : on solde l'appel pour que `verify()` ne bute pas dessus.
   */
  afterEach(() => {
    const httpMock = TestBed.inject(HttpTestingController);
    httpMock.match(req => req.url.includes('ayant-droits')).forEach(req => req.flush([]));
  });

  describe('OnInit', () => {
    it('should load agent on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', AgentDetail);

      // THEN
      expect(instance.agent()).toEqual(expect.objectContaining({ id: 25235 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      vi.spyOn(globalThis.history, 'back');
      comp.previousState();
      expect(globalThis.history.back).toHaveBeenCalled();
    });
  });
});
