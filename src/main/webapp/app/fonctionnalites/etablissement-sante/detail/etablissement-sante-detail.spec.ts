import { beforeEach, describe, expect, it, vi } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { provideTranslateService } from '@ngx-translate/core';
import { of } from 'rxjs';

import { EtablissementSanteDetail } from './etablissement-sante-detail';

describe('EtablissementSante Management Detail Component', () => {
  let comp: EtablissementSanteDetail;
  let fixture: ComponentFixture<EtablissementSanteDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideTranslateService(),
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./etablissement-sante-detail').then(m => m.EtablissementSanteDetail),
              resolve: { etablissementSante: () => of({ id: 2850 }) },
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
    fixture = TestBed.createComponent(EtablissementSanteDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load etablissementSante on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', EtablissementSanteDetail);

      // THEN
      expect(instance.etablissementSante()).toEqual(expect.objectContaining({ id: 2850 }));
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
