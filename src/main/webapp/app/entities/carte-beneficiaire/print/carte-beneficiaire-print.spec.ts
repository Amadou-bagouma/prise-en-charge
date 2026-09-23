import { beforeEach, describe, expect, it, vi } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPrint } from '@fortawesome/free-solid-svg-icons';
import { provideTranslateService } from '@ngx-translate/core';
import { of } from 'rxjs';

import { AgentService } from 'app/entities/agent/service/agent.service';
import { AyantDroitService } from 'app/entities/ayant-droit/service/ayant-droit.service';

import { CarteBeneficiairePrint } from './carte-beneficiaire-print';

describe('CarteBeneficiaire Management Print Component', () => {
  let comp: CarteBeneficiairePrint;
  let fixture: ComponentFixture<CarteBeneficiairePrint>;
  let agentService: AgentService;
  let ayantDroitService: AyantDroitService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideTranslateService(),
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./carte-beneficiaire-print').then(m => m.CarteBeneficiairePrint),
              resolve: { carteBeneficiaire: () => of({ id: 194, agent: { id: 5, matricule: 'M001' } }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    });
    const library = TestBed.inject(FaIconLibrary);
    library.addIcons(faArrowLeft);
    library.addIcons(faPrint);
    agentService = TestBed.inject(AgentService);
    ayantDroitService = TestBed.inject(AyantDroitService);
    vi.spyOn(agentService, 'find').mockReturnValue(of({ id: 5, matricule: 'M001', nom: 'Doe', prenom: 'John' }));
    vi.spyOn(ayantDroitService, 'find').mockReturnValue(of({ id: 7, nom: 'Doe', prenom: 'Jane' }));
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CarteBeneficiairePrint);
    comp = fixture.componentInstance;
  });

  it('should load the linked agent on init', async () => {
    const harness = await RouterTestingHarness.create();
    await harness.navigateByUrl('/', CarteBeneficiairePrint);

    expect(agentService.find).toHaveBeenCalledWith(5);
  });

  describe('previousState', () => {
    it('should navigate to previous state', () => {
      vi.spyOn(globalThis.history, 'back');
      comp.previousState();
      expect(globalThis.history.back).toHaveBeenCalled();
    });
  });

  describe('print', () => {
    it('should call window.print', () => {
      vi.spyOn(globalThis, 'print').mockImplementation(() => undefined);
      comp.print();
      expect(globalThis.print).toHaveBeenCalled();
    });
  });
});
