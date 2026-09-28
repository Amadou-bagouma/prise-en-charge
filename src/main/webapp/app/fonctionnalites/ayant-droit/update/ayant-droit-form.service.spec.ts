import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../ayant-droit.test-samples';

import { AyantDroitFormService } from './ayant-droit-form.service';

describe('AyantDroit Form Service', () => {
  let service: AyantDroitFormService;

  beforeEach(() => {
    service = TestBed.inject(AyantDroitFormService);
  });

  describe('Service methods', () => {
    describe('createAyantDroitFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createAyantDroitFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nom: expect.any(Object),
            prenom: expect.any(Object),
            dateNaissance: expect.any(Object),
            lien: expect.any(Object),
            agent: expect.any(Object),
          }),
        );
      });

      it('passing IAyantDroit should create a new form with FormGroup', () => {
        const formGroup = service.createAyantDroitFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nom: expect.any(Object),
            prenom: expect.any(Object),
            dateNaissance: expect.any(Object),
            lien: expect.any(Object),
            agent: expect.any(Object),
          }),
        );
      });
    });

    describe('getAyantDroit', () => {
      it('should return NewAyantDroit for default AyantDroit initial value', () => {
        const formGroup = service.createAyantDroitFormGroup(sampleWithNewData);

        const ayantDroit = service.getAyantDroit(formGroup);

        expect(ayantDroit).toMatchObject(sampleWithNewData);
      });

      it('should return NewAyantDroit for empty AyantDroit initial value', () => {
        const formGroup = service.createAyantDroitFormGroup();

        const ayantDroit = service.getAyantDroit(formGroup);

        expect(ayantDroit).toMatchObject({});
      });

      it('should return IAyantDroit', () => {
        const formGroup = service.createAyantDroitFormGroup(sampleWithRequiredData);

        const ayantDroit = service.getAyantDroit(formGroup);

        expect(ayantDroit).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IAyantDroit should not enable id FormControl', () => {
        const formGroup = service.createAyantDroitFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewAyantDroit should disable id FormControl', () => {
        const formGroup = service.createAyantDroitFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
