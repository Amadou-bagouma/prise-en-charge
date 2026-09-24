import { beforeEach, describe, expect, it, vi } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IAgent } from 'app/entities/agent/agent.model';
import { AgentService } from 'app/entities/agent/service/agent.service';
import { ConfirmService } from 'app/shared/confirm';

import { IAyantDroit } from '../ayant-droit.model';
import { AyantDroitService } from '../service/ayant-droit.service';

import { AyantDroitFormService } from './ayant-droit-form.service';
import { AyantDroitUpdate } from './ayant-droit-update';

describe('AyantDroit Management Update Component', () => {
  let comp: AyantDroitUpdate;
  let fixture: ComponentFixture<AyantDroitUpdate>;
  let activatedRoute: ActivatedRoute;
  let ayantDroitFormService: AyantDroitFormService;
  let ayantDroitService: AyantDroitService;
  let agentService: AgentService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        // La confirmation est une exigence d'interface : ici elle répond « oui » d'office,
        // pour que le spec teste l'enregistrement et non le dialogue.
        { provide: ConfirmService, useValue: { confirmer: () => of(undefined), confirmerEnregistrement: () => of(undefined) } },
        provideTranslateService(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    });

    fixture = TestBed.createComponent(AyantDroitUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    ayantDroitFormService = TestBed.inject(AyantDroitFormService);
    ayantDroitService = TestBed.inject(AyantDroitService);
    agentService = TestBed.inject(AgentService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Agent query and add missing value', () => {
      const ayantDroit: IAyantDroit = { id: 27188 };
      const agent: IAgent = { id: 25235 };
      ayantDroit.agent = agent;

      const agentCollection: IAgent[] = [{ id: 25235 }];
      vi.spyOn(agentService, 'query').mockReturnValue(of(new HttpResponse({ body: agentCollection })));
      const additionalAgents = [agent];
      const expectedCollection: IAgent[] = [...additionalAgents, ...agentCollection];
      vi.spyOn(agentService, 'addAgentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ ayantDroit });
      comp.ngOnInit();

      expect(agentService.query).toHaveBeenCalled();
      expect(agentService.addAgentToCollectionIfMissing).toHaveBeenCalledWith(
        agentCollection,
        ...additionalAgents.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.agentsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const ayantDroit: IAyantDroit = { id: 27188 };
      const agent: IAgent = { id: 25235 };
      ayantDroit.agent = agent;

      activatedRoute.data = of({ ayantDroit });
      comp.ngOnInit();

      expect(comp.agentsSharedCollection()).toContainEqual(agent);
      expect(comp.ayantDroit).toEqual(ayantDroit);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IAyantDroit>();
      const ayantDroit = { id: 17970 };
      vi.spyOn(ayantDroitFormService, 'getAyantDroit').mockReturnValue(ayantDroit);
      vi.spyOn(ayantDroitService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ayantDroit });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(ayantDroit);
      saveSubject.complete();

      // THEN
      expect(ayantDroitFormService.getAyantDroit).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(ayantDroitService.update).toHaveBeenCalledWith(expect.objectContaining(ayantDroit));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IAyantDroit>();
      const ayantDroit = { id: 17970 };
      vi.spyOn(ayantDroitFormService, 'getAyantDroit').mockReturnValue({ id: null });
      vi.spyOn(ayantDroitService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ayantDroit: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(ayantDroit);
      saveSubject.complete();

      // THEN
      expect(ayantDroitFormService.getAyantDroit).toHaveBeenCalled();
      expect(ayantDroitService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IAyantDroit>();
      const ayantDroit = { id: 17970 };
      vi.spyOn(ayantDroitService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ayantDroit });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(ayantDroitService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareAgent', () => {
      it('should forward to agentService', () => {
        const entity = { id: 25235 };
        const entity2 = { id: 18913 };
        vi.spyOn(agentService, 'compareAgent');
        comp.compareAgent(entity, entity2);
        expect(agentService.compareAgent).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
