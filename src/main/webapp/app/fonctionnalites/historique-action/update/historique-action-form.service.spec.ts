import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../historique-action.test-samples';

import { HistoriqueActionFormService } from './historique-action-form.service';

describe('HistoriqueAction Form Service', () => {
  let service: HistoriqueActionFormService;

  beforeEach(() => {
    service = TestBed.inject(HistoriqueActionFormService);
  });

  describe('Service methods', () => {
    describe('createHistoriqueActionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createHistoriqueActionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            action: expect.any(Object),
            description: expect.any(Object),
            dateAction: expect.any(Object),
            demande: expect.any(Object),
            utilisateur: expect.any(Object),
          }),
        );
      });

      it('passing IHistoriqueAction should create a new form with FormGroup', () => {
        const formGroup = service.createHistoriqueActionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            action: expect.any(Object),
            description: expect.any(Object),
            dateAction: expect.any(Object),
            demande: expect.any(Object),
            utilisateur: expect.any(Object),
          }),
        );
      });
    });

    describe('getHistoriqueAction', () => {
      it('should return NewHistoriqueAction for default HistoriqueAction initial value', () => {
        const formGroup = service.createHistoriqueActionFormGroup(sampleWithNewData);

        const historiqueAction = service.getHistoriqueAction(formGroup);

        expect(historiqueAction).toMatchObject(sampleWithNewData);
      });

      it('should return NewHistoriqueAction for empty HistoriqueAction initial value', () => {
        const formGroup = service.createHistoriqueActionFormGroup();

        const historiqueAction = service.getHistoriqueAction(formGroup);

        expect(historiqueAction).toMatchObject({});
      });

      it('should return IHistoriqueAction', () => {
        const formGroup = service.createHistoriqueActionFormGroup(sampleWithRequiredData);

        const historiqueAction = service.getHistoriqueAction(formGroup);

        expect(historiqueAction).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IHistoriqueAction should not enable id FormControl', () => {
        const formGroup = service.createHistoriqueActionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewHistoriqueAction should disable id FormControl', () => {
        const formGroup = service.createHistoriqueActionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
