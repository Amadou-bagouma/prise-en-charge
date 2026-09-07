import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../gestion.test-samples';

import { GestionFormService } from './gestion-form.service';

describe('Gestion Form Service', () => {
  let service: GestionFormService;

  beforeEach(() => {
    service = TestBed.inject(GestionFormService);
  });

  describe('Service methods', () => {
    describe('createGestionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createGestionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nom: expect.any(Object),
            type: expect.any(Object),
          }),
        );
      });

      it('passing IGestion should create a new form with FormGroup', () => {
        const formGroup = service.createGestionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nom: expect.any(Object),
            type: expect.any(Object),
          }),
        );
      });
    });

    describe('getGestion', () => {
      it('should return NewGestion for default Gestion initial value', () => {
        const formGroup = service.createGestionFormGroup(sampleWithNewData);

        const gestion = service.getGestion(formGroup);

        expect(gestion).toMatchObject(sampleWithNewData);
      });

      it('should return NewGestion for empty Gestion initial value', () => {
        const formGroup = service.createGestionFormGroup();

        const gestion = service.getGestion(formGroup);

        expect(gestion).toMatchObject({});
      });

      it('should return IGestion', () => {
        const formGroup = service.createGestionFormGroup(sampleWithRequiredData);

        const gestion = service.getGestion(formGroup);

        expect(gestion).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IGestion should not enable id FormControl', () => {
        const formGroup = service.createGestionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewGestion should disable id FormControl', () => {
        const formGroup = service.createGestionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
