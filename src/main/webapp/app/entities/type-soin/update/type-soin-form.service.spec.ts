import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../type-soin.test-samples';

import { TypeSoinFormService } from './type-soin-form.service';

describe('TypeSoin Form Service', () => {
  let service: TypeSoinFormService;

  beforeEach(() => {
    service = TestBed.inject(TypeSoinFormService);
  });

  describe('Service methods', () => {
    describe('createTypeSoinFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTypeSoinFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            libelle: expect.any(Object),
            description: expect.any(Object),
            actif: expect.any(Object),
          }),
        );
      });

      it('passing ITypeSoin should create a new form with FormGroup', () => {
        const formGroup = service.createTypeSoinFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            libelle: expect.any(Object),
            description: expect.any(Object),
            actif: expect.any(Object),
          }),
        );
      });
    });

    describe('getTypeSoin', () => {
      it('should return NewTypeSoin for default TypeSoin initial value', () => {
        const formGroup = service.createTypeSoinFormGroup(sampleWithNewData);

        const typeSoin = service.getTypeSoin(formGroup);

        expect(typeSoin).toMatchObject(sampleWithNewData);
      });

      it('should return NewTypeSoin for empty TypeSoin initial value', () => {
        const formGroup = service.createTypeSoinFormGroup();

        const typeSoin = service.getTypeSoin(formGroup);

        expect(typeSoin).toMatchObject({});
      });

      it('should return ITypeSoin', () => {
        const formGroup = service.createTypeSoinFormGroup(sampleWithRequiredData);

        const typeSoin = service.getTypeSoin(formGroup);

        expect(typeSoin).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITypeSoin should not enable id FormControl', () => {
        const formGroup = service.createTypeSoinFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTypeSoin should disable id FormControl', () => {
        const formGroup = service.createTypeSoinFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
