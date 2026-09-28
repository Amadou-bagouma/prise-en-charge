import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../boite-reception.test-samples';

import { BoiteReceptionFormService } from './boite-reception-form.service';

describe('BoiteReception Form Service', () => {
  let service: BoiteReceptionFormService;

  beforeEach(() => {
    service = TestBed.inject(BoiteReceptionFormService);
  });

  describe('Service methods', () => {
    describe('createBoiteReceptionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createBoiteReceptionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            dateCreation: expect.any(Object),
            dateDerniereLecture: expect.any(Object),
            nombreNonLus: expect.any(Object),
            actif: expect.any(Object),
            utilisateur: expect.any(Object),
          }),
        );
      });

      it('passing IBoiteReception should create a new form with FormGroup', () => {
        const formGroup = service.createBoiteReceptionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            dateCreation: expect.any(Object),
            dateDerniereLecture: expect.any(Object),
            nombreNonLus: expect.any(Object),
            actif: expect.any(Object),
            utilisateur: expect.any(Object),
          }),
        );
      });
    });

    describe('getBoiteReception', () => {
      it('should return NewBoiteReception for default BoiteReception initial value', () => {
        const formGroup = service.createBoiteReceptionFormGroup(sampleWithNewData);

        const boiteReception = service.getBoiteReception(formGroup);

        expect(boiteReception).toMatchObject(sampleWithNewData);
      });

      it('should return NewBoiteReception for empty BoiteReception initial value', () => {
        const formGroup = service.createBoiteReceptionFormGroup();

        const boiteReception = service.getBoiteReception(formGroup);

        expect(boiteReception).toMatchObject({});
      });

      it('should return IBoiteReception', () => {
        const formGroup = service.createBoiteReceptionFormGroup(sampleWithRequiredData);

        const boiteReception = service.getBoiteReception(formGroup);

        expect(boiteReception).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IBoiteReception should not enable id FormControl', () => {
        const formGroup = service.createBoiteReceptionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewBoiteReception should disable id FormControl', () => {
        const formGroup = service.createBoiteReceptionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
