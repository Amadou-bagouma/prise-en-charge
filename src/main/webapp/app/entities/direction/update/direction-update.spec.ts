import { beforeEach, describe, expect, it, vi } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IRegion } from 'app/entities/region/region.model';
import { RegionService } from 'app/entities/region/service/region.service';
import { IDirection } from '../direction.model';
import { DirectionService } from '../service/direction.service';

import { DirectionFormService } from './direction-form.service';
import { DirectionUpdate } from './direction-update';

describe('Direction Management Update Component', () => {
  let comp: DirectionUpdate;
  let fixture: ComponentFixture<DirectionUpdate>;
  let activatedRoute: ActivatedRoute;
  let directionFormService: DirectionFormService;
  let directionService: DirectionService;
  let regionService: RegionService;

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

    fixture = TestBed.createComponent(DirectionUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    directionFormService = TestBed.inject(DirectionFormService);
    directionService = TestBed.inject(DirectionService);
    regionService = TestBed.inject(RegionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Region query and add missing value', () => {
      const direction: IDirection = { id: 10870 };
      const region: IRegion = { id: 3454 };
      direction.region = region;

      const regionCollection: IRegion[] = [{ id: 3454 }];
      vi.spyOn(regionService, 'query').mockReturnValue(of(new HttpResponse({ body: regionCollection })));
      const additionalRegions = [region];
      const expectedCollection: IRegion[] = [...additionalRegions, ...regionCollection];
      vi.spyOn(regionService, 'addRegionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ direction });
      comp.ngOnInit();

      expect(regionService.query).toHaveBeenCalled();
      expect(regionService.addRegionToCollectionIfMissing).toHaveBeenCalledWith(
        regionCollection,
        ...additionalRegions.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.regionsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const direction: IDirection = { id: 10870 };
      const region: IRegion = { id: 3454 };
      direction.region = region;

      activatedRoute.data = of({ direction });
      comp.ngOnInit();

      expect(comp.regionsSharedCollection()).toContainEqual(region);
      expect(comp.direction).toEqual(direction);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IDirection>();
      const direction = { id: 3524 };
      vi.spyOn(directionFormService, 'getDirection').mockReturnValue(direction);
      vi.spyOn(directionService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ direction });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(direction);
      saveSubject.complete();

      // THEN
      expect(directionFormService.getDirection).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(directionService.update).toHaveBeenCalledWith(expect.objectContaining(direction));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IDirection>();
      const direction = { id: 3524 };
      vi.spyOn(directionFormService, 'getDirection').mockReturnValue({ id: null });
      vi.spyOn(directionService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ direction: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(direction);
      saveSubject.complete();

      // THEN
      expect(directionFormService.getDirection).toHaveBeenCalled();
      expect(directionService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IDirection>();
      const direction = { id: 3524 };
      vi.spyOn(directionService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ direction });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(directionService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareRegion', () => {
      it('should forward to regionService', () => {
        const entity = { id: 3454 };
        const entity2 = { id: 30405 };
        vi.spyOn(regionService, 'compareRegion');
        comp.compareRegion(entity, entity2);
        expect(regionService.compareRegion).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
