import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../demande-prise-en-charge.test-samples';

import { DemandePriseEnChargeFormService } from './demande-prise-en-charge-form.service';

describe('DemandePriseEnCharge Form Service', () => {
  let service: DemandePriseEnChargeFormService;

  beforeEach(() => {
    service = TestBed.inject(DemandePriseEnChargeFormService);
  });

  describe('Service methods', () => {
    describe('createDemandePriseEnChargeFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDemandePriseEnChargeFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reference: expect.any(Object),
            dateCreation: expect.any(Object),
            dateModification: expect.any(Object),
            typeBeneficiaire: expect.any(Object),
            description: expect.any(Object),
            statut: expect.any(Object),
            priorite: expect.any(Object),
            dateAssignation: expect.any(Object),
            dateEcheance: expect.any(Object),
            motifRejet: expect.any(Object),
            observation: expect.any(Object),
            agent: expect.any(Object),
            ayantDroit: expect.any(Object),
            typeSoins: expect.any(Object),
            etablissementSante: expect.any(Object),
            gestionnaireCreateur: expect.any(Object),
            assigneA: expect.any(Object),
          }),
        );
      });

      it('passing IDemandePriseEnCharge should create a new form with FormGroup', () => {
        const formGroup = service.createDemandePriseEnChargeFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reference: expect.any(Object),
            dateCreation: expect.any(Object),
            dateModification: expect.any(Object),
            typeBeneficiaire: expect.any(Object),
            description: expect.any(Object),
            statut: expect.any(Object),
            priorite: expect.any(Object),
            dateAssignation: expect.any(Object),
            dateEcheance: expect.any(Object),
            motifRejet: expect.any(Object),
            observation: expect.any(Object),
            agent: expect.any(Object),
            ayantDroit: expect.any(Object),
            typeSoins: expect.any(Object),
            etablissementSante: expect.any(Object),
            gestionnaireCreateur: expect.any(Object),
            assigneA: expect.any(Object),
          }),
        );
      });
    });

    describe('getDemandePriseEnCharge', () => {
      it('should return NewDemandePriseEnCharge for default DemandePriseEnCharge initial value', () => {
        const formGroup = service.createDemandePriseEnChargeFormGroup(sampleWithNewData);

        const demandePriseEnCharge = service.getDemandePriseEnCharge(formGroup);

        expect(demandePriseEnCharge).toMatchObject(sampleWithNewData);
      });

      it('should return NewDemandePriseEnCharge for empty DemandePriseEnCharge initial value', () => {
        const formGroup = service.createDemandePriseEnChargeFormGroup();

        const demandePriseEnCharge = service.getDemandePriseEnCharge(formGroup);

        expect(demandePriseEnCharge).toMatchObject({});
      });

      it('should return IDemandePriseEnCharge', () => {
        const formGroup = service.createDemandePriseEnChargeFormGroup(sampleWithRequiredData);

        const demandePriseEnCharge = service.getDemandePriseEnCharge(formGroup);

        expect(demandePriseEnCharge).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDemandePriseEnCharge should not enable id FormControl', () => {
        const formGroup = service.createDemandePriseEnChargeFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDemandePriseEnCharge should disable id FormControl', () => {
        const formGroup = service.createDemandePriseEnChargeFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
