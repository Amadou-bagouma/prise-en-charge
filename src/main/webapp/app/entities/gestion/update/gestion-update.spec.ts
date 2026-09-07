import { beforeEach, describe, expect, it, vi } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IGestion } from '../gestion.model';
import { GestionService } from '../service/gestion.service';

import { GestionFormService } from './gestion-form.service';
import { GestionUpdate } from './gestion-update';

describe('Gestion Management Update Component', () => {
  let comp: GestionUpdate;
  let fixture: ComponentFixture<GestionUpdate>;
  let activatedRoute: ActivatedRoute;
  let gestionFormService: GestionFormService;
  let gestionService: GestionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
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

    fixture = TestBed.createComponent(GestionUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    gestionFormService = TestBed.inject(GestionFormService);
    gestionService = TestBed.inject(GestionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const gestion: IGestion = { id: 13557 };

      activatedRoute.data = of({ gestion });
      comp.ngOnInit();

      expect(comp.gestion).toEqual(gestion);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IGestion>();
      const gestion = { id: 26677 };
      vi.spyOn(gestionFormService, 'getGestion').mockReturnValue(gestion);
      vi.spyOn(gestionService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ gestion });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(gestion);
      saveSubject.complete();

      // THEN
      expect(gestionFormService.getGestion).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(gestionService.update).toHaveBeenCalledWith(expect.objectContaining(gestion));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IGestion>();
      const gestion = { id: 26677 };
      vi.spyOn(gestionFormService, 'getGestion').mockReturnValue({ id: null });
      vi.spyOn(gestionService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ gestion: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(gestion);
      saveSubject.complete();

      // THEN
      expect(gestionFormService.getGestion).toHaveBeenCalled();
      expect(gestionService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IGestion>();
      const gestion = { id: 26677 };
      vi.spyOn(gestionService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ gestion });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(gestionService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
