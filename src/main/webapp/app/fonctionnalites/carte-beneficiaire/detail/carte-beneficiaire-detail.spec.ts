import { beforeEach, describe, expect, it, vi } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt, faPrint } from '@fortawesome/free-solid-svg-icons';
import { provideTranslateService } from '@ngx-translate/core';
import { of } from 'rxjs';

import { CarteBeneficiaireDetail } from './carte-beneficiaire-detail';

describe('CarteBeneficiaire Management Detail Component', () => {
  let comp: CarteBeneficiaireDetail;
  let fixture: ComponentFixture<CarteBeneficiaireDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideTranslateService(),
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./carte-beneficiaire-detail').then(m => m.CarteBeneficiaireDetail),
              resolve: { carteBeneficiaire: () => of({ id: 194 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    });
    const library = TestBed.inject(FaIconLibrary);
    library.addIcons(faArrowLeft);
    library.addIcons(faPencilAlt);
    library.addIcons(faPrint);
    vi.spyOn(window, 'open').mockImplementation(() => null);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CarteBeneficiaireDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load carteBeneficiaire on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', CarteBeneficiaireDetail);

      // THEN
      expect(instance.carteBeneficiaire()).toEqual(expect.objectContaining({ id: 194 }));
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
