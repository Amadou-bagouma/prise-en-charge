import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../agent.test-samples';

import { AgentFormService } from './agent-form.service';

describe('Agent Form Service', () => {
  let service: AgentFormService;

  beforeEach(() => {
    service = TestBed.inject(AgentFormService);
  });

  describe('Service methods', () => {
    describe('createAgentFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createAgentFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            matricule: expect.any(Object),
            nom: expect.any(Object),
            prenom: expect.any(Object),
            dateNaissance: expect.any(Object),
            telephone: expect.any(Object),
            fonction: expect.any(Object),
            direction: expect.any(Object),
            gestion: expect.any(Object),
            user: expect.any(Object),
          }),
        );
      });

      it('passing IAgent should create a new form with FormGroup', () => {
        const formGroup = service.createAgentFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            matricule: expect.any(Object),
            nom: expect.any(Object),
            prenom: expect.any(Object),
            dateNaissance: expect.any(Object),
            telephone: expect.any(Object),
            fonction: expect.any(Object),
            direction: expect.any(Object),
            gestion: expect.any(Object),
            user: expect.any(Object),
          }),
        );
      });
    });

    describe('getAgent', () => {
      it('should return NewAgent for default Agent initial value', () => {
        const formGroup = service.createAgentFormGroup(sampleWithNewData);

        const agent = service.getAgent(formGroup);

        expect(agent).toMatchObject(sampleWithNewData);
      });

      it('should return NewAgent for empty Agent initial value', () => {
        const formGroup = service.createAgentFormGroup();

        const agent = service.getAgent(formGroup);

        expect(agent).toMatchObject({});
      });

      it('should return IAgent', () => {
        const formGroup = service.createAgentFormGroup(sampleWithRequiredData);

        const agent = service.getAgent(formGroup);

        expect(agent).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IAgent should not enable id FormControl', () => {
        const formGroup = service.createAgentFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewAgent should disable id FormControl', () => {
        const formGroup = service.createAgentFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
