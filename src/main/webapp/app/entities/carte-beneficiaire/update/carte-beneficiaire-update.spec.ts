import { beforeEach, describe, expect, it, vi } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IAgent } from 'app/entities/agent/agent.model';
import { AgentService } from 'app/entities/agent/service/agent.service';
import { IAyantDroit } from 'app/entities/ayant-droit/ayant-droit.model';
import { AyantDroitService } from 'app/entities/ayant-droit/service/ayant-droit.service';
import { ICarteBeneficiaire } from '../carte-beneficiaire.model';
import { CarteBeneficiaireService } from '../service/carte-beneficiaire.service';

import { CarteBeneficiaireFormService } from './carte-beneficiaire-form.service';
import { CarteBeneficiaireUpdate } from './carte-beneficiaire-update';

describe('CarteBeneficiaire Management Update Component', () => {
  let comp: CarteBeneficiaireUpdate;
  let fixture: ComponentFixture<CarteBeneficiaireUpdate>;
  let activatedRoute: ActivatedRoute;
  let carteBeneficiaireFormService: CarteBeneficiaireFormService;
  let carteBeneficiaireService: CarteBeneficiaireService;
  let agentService: AgentService;
  let ayantDroitService: AyantDroitService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
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

    fixture = TestBed.createComponent(CarteBeneficiaireUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    carteBeneficiaireFormService = TestBed.inject(CarteBeneficiaireFormService);
    carteBeneficiaireService = TestBed.inject(CarteBeneficiaireService);
    agentService = TestBed.inject(AgentService);
    ayantDroitService = TestBed.inject(AyantDroitService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Agent query and add missing value', () => {
      const carteBeneficiaire: ICarteBeneficiaire = { id: 29825 };
      const agent: IAgent = { id: 25235 };
      carteBeneficiaire.agent = agent;

      const agentCollection: IAgent[] = [{ id: 25235 }];
      vi.spyOn(agentService, 'query').mockReturnValue(of(new HttpResponse({ body: agentCollection })));
      const additionalAgents = [agent];
      const expectedCollection: IAgent[] = [...additionalAgents, ...agentCollection];
      vi.spyOn(agentService, 'addAgentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ carteBeneficiaire });
      comp.ngOnInit();

      expect(agentService.query).toHaveBeenCalled();
      expect(agentService.addAgentToCollectionIfMissing).toHaveBeenCalledWith(
        agentCollection,
        ...additionalAgents.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.agentsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call AyantDroit query and add missing value', () => {
      const carteBeneficiaire: ICarteBeneficiaire = { id: 29825 };
      const ayantDroit: IAyantDroit = { id: 17970 };
      carteBeneficiaire.ayantDroit = ayantDroit;

      const ayantDroitCollection: IAyantDroit[] = [{ id: 17970 }];
      vi.spyOn(ayantDroitService, 'query').mockReturnValue(of(new HttpResponse({ body: ayantDroitCollection })));
      const additionalAyantDroits = [ayantDroit];
      const expectedCollection: IAyantDroit[] = [...additionalAyantDroits, ...ayantDroitCollection];
      vi.spyOn(ayantDroitService, 'addAyantDroitToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ carteBeneficiaire });
      comp.ngOnInit();

      expect(ayantDroitService.query).toHaveBeenCalled();
      expect(ayantDroitService.addAyantDroitToCollectionIfMissing).toHaveBeenCalledWith(
        ayantDroitCollection,
        ...additionalAyantDroits.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.ayantDroitsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const carteBeneficiaire: ICarteBeneficiaire = { id: 29825 };
      const agent: IAgent = { id: 25235 };
      carteBeneficiaire.agent = agent;
      const ayantDroit: IAyantDroit = { id: 17970 };
      carteBeneficiaire.ayantDroit = ayantDroit;

      activatedRoute.data = of({ carteBeneficiaire });
      comp.ngOnInit();

      expect(comp.agentsSharedCollection()).toContainEqual(agent);
      expect(comp.ayantDroitsSharedCollection()).toContainEqual(ayantDroit);
      expect(comp.carteBeneficiaire).toEqual(carteBeneficiaire);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICarteBeneficiaire>();
      const carteBeneficiaire = { id: 194 };
      vi.spyOn(carteBeneficiaireFormService, 'getCarteBeneficiaire').mockReturnValue(carteBeneficiaire);
      vi.spyOn(carteBeneficiaireService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ carteBeneficiaire });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(carteBeneficiaire);
      saveSubject.complete();

      // THEN
      expect(carteBeneficiaireFormService.getCarteBeneficiaire).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(carteBeneficiaireService.update).toHaveBeenCalledWith(expect.objectContaining(carteBeneficiaire));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICarteBeneficiaire>();
      const carteBeneficiaire = { id: 194 };
      vi.spyOn(carteBeneficiaireFormService, 'getCarteBeneficiaire').mockReturnValue({ id: null });
      vi.spyOn(carteBeneficiaireService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ carteBeneficiaire: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(carteBeneficiaire);
      saveSubject.complete();

      // THEN
      expect(carteBeneficiaireFormService.getCarteBeneficiaire).toHaveBeenCalled();
      expect(carteBeneficiaireService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ICarteBeneficiaire>();
      const carteBeneficiaire = { id: 194 };
      vi.spyOn(carteBeneficiaireService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ carteBeneficiaire });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(carteBeneficiaireService.update).toHaveBeenCalled();
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

    describe('compareAyantDroit', () => {
      it('should forward to ayantDroitService', () => {
        const entity = { id: 17970 };
        const entity2 = { id: 27188 };
        vi.spyOn(ayantDroitService, 'compareAyantDroit');
        comp.compareAyantDroit(entity, entity2);
        expect(ayantDroitService.compareAyantDroit).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
