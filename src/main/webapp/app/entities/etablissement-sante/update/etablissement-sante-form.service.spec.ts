import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../etablissement-sante.test-samples';

import { EtablissementSanteFormService } from './etablissement-sante-form.service';

describe('EtablissementSante Form Service', () => {
  let service: EtablissementSanteFormService;

  beforeEach(() => {
    service = TestBed.inject(EtablissementSanteFormService);
  });

  describe('Service methods', () => {
    describe('createEtablissementSanteFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createEtablissementSanteFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            nom: expect.any(Object),
            adresse: expect.any(Object),
            telephone: expect.any(Object),
            actif: expect.any(Object),
          }),
        );
      });

      it('passing IEtablissementSante should create a new form with FormGroup', () => {
        const formGroup = service.createEtablissementSanteFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            nom: expect.any(Object),
            adresse: expect.any(Object),
            telephone: expect.any(Object),
            actif: expect.any(Object),
          }),
        );
      });
    });

    describe('getEtablissementSante', () => {
      it('should return NewEtablissementSante for default EtablissementSante initial value', () => {
        const formGroup = service.createEtablissementSanteFormGroup(sampleWithNewData);

        const etablissementSante = service.getEtablissementSante(formGroup);

        expect(etablissementSante).toMatchObject(sampleWithNewData);
      });

      it('should return NewEtablissementSante for empty EtablissementSante initial value', () => {
        const formGroup = service.createEtablissementSanteFormGroup();

        const etablissementSante = service.getEtablissementSante(formGroup);

        expect(etablissementSante).toMatchObject({});
      });

      it('should return IEtablissementSante', () => {
        const formGroup = service.createEtablissementSanteFormGroup(sampleWithRequiredData);

        const etablissementSante = service.getEtablissementSante(formGroup);

        expect(etablissementSante).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IEtablissementSante should not enable id FormControl', () => {
        const formGroup = service.createEtablissementSanteFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewEtablissementSante should disable id FormControl', () => {
        const formGroup = service.createEtablissementSanteFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
