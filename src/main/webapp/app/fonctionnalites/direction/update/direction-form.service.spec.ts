import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../direction.test-samples';

import { DirectionFormService } from './direction-form.service';

describe('Direction Form Service', () => {
  let service: DirectionFormService;

  beforeEach(() => {
    service = TestBed.inject(DirectionFormService);
  });

  describe('Service methods', () => {
    describe('createDirectionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDirectionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            nom: expect.any(Object),
            region: expect.any(Object),
          }),
        );
      });

      it('passing IDirection should create a new form with FormGroup', () => {
        const formGroup = service.createDirectionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            nom: expect.any(Object),
            region: expect.any(Object),
          }),
        );
      });
    });

    describe('getDirection', () => {
      it('should return NewDirection for default Direction initial value', () => {
        const formGroup = service.createDirectionFormGroup(sampleWithNewData);

        const direction = service.getDirection(formGroup);

        expect(direction).toMatchObject(sampleWithNewData);
      });

      it('should return NewDirection for empty Direction initial value', () => {
        const formGroup = service.createDirectionFormGroup();

        const direction = service.getDirection(formGroup);

        expect(direction).toMatchObject({});
      });

      it('should return IDirection', () => {
        const formGroup = service.createDirectionFormGroup(sampleWithRequiredData);

        const direction = service.getDirection(formGroup);

        expect(direction).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDirection should not enable id FormControl', () => {
        const formGroup = service.createDirectionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDirection should disable id FormControl', () => {
        const formGroup = service.createDirectionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
