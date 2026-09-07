import { beforeEach, describe, expect, it, vi } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { provideTranslateService } from '@ngx-translate/core';
import { of } from 'rxjs';

import { DemandePriseEnChargeDetail } from './demande-prise-en-charge-detail';

describe('DemandePriseEnCharge Management Detail Component', () => {
  let comp: DemandePriseEnChargeDetail;
  let fixture: ComponentFixture<DemandePriseEnChargeDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideTranslateService(),
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./demande-prise-en-charge-detail').then(m => m.DemandePriseEnChargeDetail),
              resolve: { demandePriseEnCharge: () => of({ id: 17525 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    });
    const library = TestBed.inject(FaIconLibrary);
    library.addIcons(faArrowLeft);
    library.addIcons(faPencilAlt);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DemandePriseEnChargeDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load demandePriseEnCharge on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', DemandePriseEnChargeDetail);

      // THEN
      expect(instance.demandePriseEnCharge()).toEqual(expect.objectContaining({ id: 17525 }));
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
