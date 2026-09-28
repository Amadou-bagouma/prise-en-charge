import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../carte-beneficiaire.test-samples';

import { CarteBeneficiaireFormService } from './carte-beneficiaire-form.service';

describe('CarteBeneficiaire Form Service', () => {
  let service: CarteBeneficiaireFormService;

  beforeEach(() => {
    service = TestBed.inject(CarteBeneficiaireFormService);
  });

  describe('Service methods', () => {
    describe('createCarteBeneficiaireFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCarteBeneficiaireFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            numeroCarte: expect.any(Object),
            typeBeneficiaire: expect.any(Object),
            dateDebutValidite: expect.any(Object),
            dateFinValidite: expect.any(Object),
            dateEmission: expect.any(Object),
            agent: expect.any(Object),
            ayantDroit: expect.any(Object),
          }),
        );
      });

      it('passing ICarteBeneficiaire should create a new form with FormGroup', () => {
        const formGroup = service.createCarteBeneficiaireFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            numeroCarte: expect.any(Object),
            typeBeneficiaire: expect.any(Object),
            dateDebutValidite: expect.any(Object),
            dateFinValidite: expect.any(Object),
            dateEmission: expect.any(Object),
            agent: expect.any(Object),
            ayantDroit: expect.any(Object),
          }),
        );
      });
    });

    describe('getCarteBeneficiaire', () => {
      it('should return NewCarteBeneficiaire for default CarteBeneficiaire initial value', () => {
        const formGroup = service.createCarteBeneficiaireFormGroup(sampleWithNewData);

        const carteBeneficiaire = service.getCarteBeneficiaire(formGroup);

        expect(carteBeneficiaire).toMatchObject(sampleWithNewData);
      });

      it('should return NewCarteBeneficiaire for empty CarteBeneficiaire initial value', () => {
        const formGroup = service.createCarteBeneficiaireFormGroup();

        const carteBeneficiaire = service.getCarteBeneficiaire(formGroup);

        expect(carteBeneficiaire).toMatchObject({});
      });

      it('should return ICarteBeneficiaire', () => {
        const formGroup = service.createCarteBeneficiaireFormGroup(sampleWithRequiredData);

        const carteBeneficiaire = service.getCarteBeneficiaire(formGroup);

        expect(carteBeneficiaire).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICarteBeneficiaire should not enable id FormControl', () => {
        const formGroup = service.createCarteBeneficiaireFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCarteBeneficiaire should disable id FormControl', () => {
        const formGroup = service.createCarteBeneficiaireFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
