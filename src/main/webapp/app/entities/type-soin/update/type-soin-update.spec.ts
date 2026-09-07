import { beforeEach, describe, expect, it, vi } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { TypeSoinService } from '../service/type-soin.service';
import { ITypeSoin } from '../type-soin.model';

import { TypeSoinFormService } from './type-soin-form.service';
import { TypeSoinUpdate } from './type-soin-update';

describe('TypeSoin Management Update Component', () => {
  let comp: TypeSoinUpdate;
  let fixture: ComponentFixture<TypeSoinUpdate>;
  let activatedRoute: ActivatedRoute;
  let typeSoinFormService: TypeSoinFormService;
  let typeSoinService: TypeSoinService;

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

    fixture = TestBed.createComponent(TypeSoinUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    typeSoinFormService = TestBed.inject(TypeSoinFormService);
    typeSoinService = TestBed.inject(TypeSoinService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const typeSoin: ITypeSoin = { id: 12086 };

      activatedRoute.data = of({ typeSoin });
      comp.ngOnInit();

      expect(comp.typeSoin).toEqual(typeSoin);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITypeSoin>();
      const typeSoin = { id: 20111 };
      vi.spyOn(typeSoinFormService, 'getTypeSoin').mockReturnValue(typeSoin);
      vi.spyOn(typeSoinService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ typeSoin });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(typeSoin);
      saveSubject.complete();

      // THEN
      expect(typeSoinFormService.getTypeSoin).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(typeSoinService.update).toHaveBeenCalledWith(expect.objectContaining(typeSoin));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITypeSoin>();
      const typeSoin = { id: 20111 };
      vi.spyOn(typeSoinFormService, 'getTypeSoin').mockReturnValue({ id: null });
      vi.spyOn(typeSoinService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ typeSoin: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(typeSoin);
      saveSubject.complete();

      // THEN
      expect(typeSoinFormService.getTypeSoin).toHaveBeenCalled();
      expect(typeSoinService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ITypeSoin>();
      const typeSoin = { id: 20111 };
      vi.spyOn(typeSoinService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ typeSoin });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(typeSoinService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
