import { beforeEach, describe, expect, it, vi } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { provideTranslateService } from '@ngx-translate/core';
import { of } from 'rxjs';

import { PieceJustificativeDetail } from './piece-justificative-detail';

describe('PieceJustificative Management Detail Component', () => {
  let comp: PieceJustificativeDetail;
  let fixture: ComponentFixture<PieceJustificativeDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideTranslateService(),
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./piece-justificative-detail').then(m => m.PieceJustificativeDetail),
              resolve: { pieceJustificative: () => of({ id: 17302 }) },
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
    fixture = TestBed.createComponent(PieceJustificativeDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load pieceJustificative on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', PieceJustificativeDetail);

      // THEN
      expect(instance.pieceJustificative()).toEqual(expect.objectContaining({ id: 17302 }));
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
