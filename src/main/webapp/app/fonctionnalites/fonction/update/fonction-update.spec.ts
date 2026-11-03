import { beforeEach, describe, expect, it, vi } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ConfirmService } from 'app/shared/confirm';

import { IFonction } from '../fonction.model';
import { FonctionService } from '../service/fonction.service';

import { FonctionFormService } from './fonction-form.service';
import { FonctionUpdate } from './fonction-update';

describe('Fonction Management Update Component', () => {
  let comp: FonctionUpdate;
  let fixture: ComponentFixture<FonctionUpdate>;
  let activatedRoute: ActivatedRoute;
  let fonctionFormService: FonctionFormService;
  let fonctionService: FonctionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        // La confirmation est une exigence d'interface : ici elle répond « oui » d'office,
        // pour que le spec teste l'enregistrement et non le dialogue.
        { provide: ConfirmService, useValue: { confirmer: () => of(undefined), confirmerEnregistrement: () => of(undefined) } },
        provideTranslateService(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    });

    fixture = TestBed.createComponent(FonctionUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fonctionFormService = TestBed.inject(FonctionFormService);
    fonctionService = TestBed.inject(FonctionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const fonction: IFonction = { id: 13557 };

      activatedRoute.data = of({ fonction });
      comp.ngOnInit();

      expect(comp.fonction).toEqual(fonction);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IFonction>();
      const fonction = { id: 26677 };
      vi.spyOn(fonctionFormService, 'getFonction').mockReturnValue(fonction);
      vi.spyOn(fonctionService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ fonction });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(fonction);
      saveSubject.complete();

      // THEN
      expect(fonctionFormService.getFonction).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(fonctionService.update).toHaveBeenCalledWith(expect.objectContaining(fonction));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IFonction>();
      const fonction = { id: 26677 };
      vi.spyOn(fonctionFormService, 'getFonction').mockReturnValue({ id: null });
      vi.spyOn(fonctionService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ fonction: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(fonction);
      saveSubject.complete();

      // THEN
      expect(fonctionFormService.getFonction).toHaveBeenCalled();
      expect(fonctionService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IFonction>();
      const fonction = { id: 26677 };
      vi.spyOn(fonctionService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ fonction });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(fonctionService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
