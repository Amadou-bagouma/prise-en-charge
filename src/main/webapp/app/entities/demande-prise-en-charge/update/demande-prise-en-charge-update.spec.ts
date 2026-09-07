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
import { IEtablissementSante } from 'app/entities/etablissement-sante/etablissement-sante.model';
import { EtablissementSanteService } from 'app/entities/etablissement-sante/service/etablissement-sante.service';
import { TypeSoinService } from 'app/entities/type-soin/service/type-soin.service';
import { ITypeSoin } from 'app/entities/type-soin/type-soin.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { IDemandePriseEnCharge } from '../demande-prise-en-charge.model';
import { DemandePriseEnChargeService } from '../service/demande-prise-en-charge.service';

import { DemandePriseEnChargeFormService } from './demande-prise-en-charge-form.service';
import { DemandePriseEnChargeUpdate } from './demande-prise-en-charge-update';

describe('DemandePriseEnCharge Management Update Component', () => {
  let comp: DemandePriseEnChargeUpdate;
  let fixture: ComponentFixture<DemandePriseEnChargeUpdate>;
  let activatedRoute: ActivatedRoute;
  let demandePriseEnChargeFormService: DemandePriseEnChargeFormService;
  let demandePriseEnChargeService: DemandePriseEnChargeService;
  let agentService: AgentService;
  let ayantDroitService: AyantDroitService;
  let typeSoinService: TypeSoinService;
  let etablissementSanteService: EtablissementSanteService;
  let userService: UserService;

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

    fixture = TestBed.createComponent(DemandePriseEnChargeUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    demandePriseEnChargeFormService = TestBed.inject(DemandePriseEnChargeFormService);
    demandePriseEnChargeService = TestBed.inject(DemandePriseEnChargeService);
    agentService = TestBed.inject(AgentService);
    ayantDroitService = TestBed.inject(AyantDroitService);
    typeSoinService = TestBed.inject(TypeSoinService);
    etablissementSanteService = TestBed.inject(EtablissementSanteService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Agent query and add missing value', () => {
      const demandePriseEnCharge: IDemandePriseEnCharge = { id: 19327 };
      const agent: IAgent = { id: 25235 };
      demandePriseEnCharge.agent = agent;

      const agentCollection: IAgent[] = [{ id: 25235 }];
      vi.spyOn(agentService, 'query').mockReturnValue(of(new HttpResponse({ body: agentCollection })));
      const additionalAgents = [agent];
      const expectedCollection: IAgent[] = [...additionalAgents, ...agentCollection];
      vi.spyOn(agentService, 'addAgentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ demandePriseEnCharge });
      comp.ngOnInit();

      expect(agentService.query).toHaveBeenCalled();
      expect(agentService.addAgentToCollectionIfMissing).toHaveBeenCalledWith(
        agentCollection,
        ...additionalAgents.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.agentsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call AyantDroit query and add missing value', () => {
      const demandePriseEnCharge: IDemandePriseEnCharge = { id: 19327 };
      const ayantDroit: IAyantDroit = { id: 17970 };
      demandePriseEnCharge.ayantDroit = ayantDroit;

      const ayantDroitCollection: IAyantDroit[] = [{ id: 17970 }];
      vi.spyOn(ayantDroitService, 'query').mockReturnValue(of(new HttpResponse({ body: ayantDroitCollection })));
      const additionalAyantDroits = [ayantDroit];
      const expectedCollection: IAyantDroit[] = [...additionalAyantDroits, ...ayantDroitCollection];
      vi.spyOn(ayantDroitService, 'addAyantDroitToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ demandePriseEnCharge });
      comp.ngOnInit();

      expect(ayantDroitService.query).toHaveBeenCalled();
      expect(ayantDroitService.addAyantDroitToCollectionIfMissing).toHaveBeenCalledWith(
        ayantDroitCollection,
        ...additionalAyantDroits.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.ayantDroitsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call TypeSoin query and add missing value', () => {
      const demandePriseEnCharge: IDemandePriseEnCharge = { id: 19327 };
      const typeSoin: ITypeSoin = { id: 20111 };
      demandePriseEnCharge.typeSoin = typeSoin;

      const typeSoinCollection: ITypeSoin[] = [{ id: 20111 }];
      vi.spyOn(typeSoinService, 'query').mockReturnValue(of(new HttpResponse({ body: typeSoinCollection })));
      const additionalTypeSoins = [typeSoin];
      const expectedCollection: ITypeSoin[] = [...additionalTypeSoins, ...typeSoinCollection];
      vi.spyOn(typeSoinService, 'addTypeSoinToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ demandePriseEnCharge });
      comp.ngOnInit();

      expect(typeSoinService.query).toHaveBeenCalled();
      expect(typeSoinService.addTypeSoinToCollectionIfMissing).toHaveBeenCalledWith(
        typeSoinCollection,
        ...additionalTypeSoins.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.typeSoinsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call EtablissementSante query and add missing value', () => {
      const demandePriseEnCharge: IDemandePriseEnCharge = { id: 19327 };
      const etablissementSante: IEtablissementSante = { id: 2850 };
      demandePriseEnCharge.etablissementSante = etablissementSante;

      const etablissementSanteCollection: IEtablissementSante[] = [{ id: 2850 }];
      vi.spyOn(etablissementSanteService, 'query').mockReturnValue(of(new HttpResponse({ body: etablissementSanteCollection })));
      const additionalEtablissementSantes = [etablissementSante];
      const expectedCollection: IEtablissementSante[] = [...additionalEtablissementSantes, ...etablissementSanteCollection];
      vi.spyOn(etablissementSanteService, 'addEtablissementSanteToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ demandePriseEnCharge });
      comp.ngOnInit();

      expect(etablissementSanteService.query).toHaveBeenCalled();
      expect(etablissementSanteService.addEtablissementSanteToCollectionIfMissing).toHaveBeenCalledWith(
        etablissementSanteCollection,
        ...additionalEtablissementSantes.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.etablissementSantesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call User query and add missing value', () => {
      const demandePriseEnCharge: IDemandePriseEnCharge = { id: 19327 };
      const gestionnaireCreateur: IUser = { id: 3944 };
      demandePriseEnCharge.gestionnaireCreateur = gestionnaireCreateur;
      const assigneA: IUser = { id: 3944 };
      demandePriseEnCharge.assigneA = assigneA;

      const userCollection: IUser[] = [{ id: 3944 }];
      vi.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [gestionnaireCreateur, assigneA];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      vi.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ demandePriseEnCharge });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.usersSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const demandePriseEnCharge: IDemandePriseEnCharge = { id: 19327 };
      const agent: IAgent = { id: 25235 };
      demandePriseEnCharge.agent = agent;
      const ayantDroit: IAyantDroit = { id: 17970 };
      demandePriseEnCharge.ayantDroit = ayantDroit;
      const typeSoin: ITypeSoin = { id: 20111 };
      demandePriseEnCharge.typeSoin = typeSoin;
      const etablissementSante: IEtablissementSante = { id: 2850 };
      demandePriseEnCharge.etablissementSante = etablissementSante;
      const gestionnaireCreateur: IUser = { id: 3944 };
      demandePriseEnCharge.gestionnaireCreateur = gestionnaireCreateur;
      const assigneA: IUser = { id: 3944 };
      demandePriseEnCharge.assigneA = assigneA;

      activatedRoute.data = of({ demandePriseEnCharge });
      comp.ngOnInit();

      expect(comp.agentsSharedCollection()).toContainEqual(agent);
      expect(comp.ayantDroitsSharedCollection()).toContainEqual(ayantDroit);
      expect(comp.typeSoinsSharedCollection()).toContainEqual(typeSoin);
      expect(comp.etablissementSantesSharedCollection()).toContainEqual(etablissementSante);
      expect(comp.usersSharedCollection()).toContainEqual(gestionnaireCreateur);
      expect(comp.usersSharedCollection()).toContainEqual(assigneA);
      expect(comp.demandePriseEnCharge).toEqual(demandePriseEnCharge);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IDemandePriseEnCharge>();
      const demandePriseEnCharge = { id: 17525 };
      vi.spyOn(demandePriseEnChargeFormService, 'getDemandePriseEnCharge').mockReturnValue(demandePriseEnCharge);
      vi.spyOn(demandePriseEnChargeService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ demandePriseEnCharge });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(demandePriseEnCharge);
      saveSubject.complete();

      // THEN
      expect(demandePriseEnChargeFormService.getDemandePriseEnCharge).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(demandePriseEnChargeService.update).toHaveBeenCalledWith(expect.objectContaining(demandePriseEnCharge));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IDemandePriseEnCharge>();
      const demandePriseEnCharge = { id: 17525 };
      vi.spyOn(demandePriseEnChargeFormService, 'getDemandePriseEnCharge').mockReturnValue({ id: null });
      vi.spyOn(demandePriseEnChargeService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ demandePriseEnCharge: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(demandePriseEnCharge);
      saveSubject.complete();

      // THEN
      expect(demandePriseEnChargeFormService.getDemandePriseEnCharge).toHaveBeenCalled();
      expect(demandePriseEnChargeService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IDemandePriseEnCharge>();
      const demandePriseEnCharge = { id: 17525 };
      vi.spyOn(demandePriseEnChargeService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ demandePriseEnCharge });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(demandePriseEnChargeService.update).toHaveBeenCalled();
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

    describe('compareTypeSoin', () => {
      it('should forward to typeSoinService', () => {
        const entity = { id: 20111 };
        const entity2 = { id: 12086 };
        vi.spyOn(typeSoinService, 'compareTypeSoin');
        comp.compareTypeSoin(entity, entity2);
        expect(typeSoinService.compareTypeSoin).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareEtablissementSante', () => {
      it('should forward to etablissementSanteService', () => {
        const entity = { id: 2850 };
        const entity2 = { id: 20730 };
        vi.spyOn(etablissementSanteService, 'compareEtablissementSante');
        comp.compareEtablissementSante(entity, entity2);
        expect(etablissementSanteService.compareEtablissementSante).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareUser', () => {
      it('should forward to userService', () => {
        const entity = { id: 3944 };
        const entity2 = { id: 6275 };
        vi.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
