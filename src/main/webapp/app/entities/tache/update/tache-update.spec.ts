import { beforeEach, describe, expect, it, vi } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IBoiteReception } from 'app/entities/boite-reception/boite-reception.model';
import { BoiteReceptionService } from 'app/entities/boite-reception/service/boite-reception.service';
import { IDemandePriseEnCharge } from 'app/entities/demande-prise-en-charge/demande-prise-en-charge.model';
import { DemandePriseEnChargeService } from 'app/entities/demande-prise-en-charge/service/demande-prise-en-charge.service';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { TacheService } from '../service/tache.service';
import { ITache } from '../tache.model';

import { TacheFormService } from './tache-form.service';
import { TacheUpdate } from './tache-update';

describe('Tache Management Update Component', () => {
  let comp: TacheUpdate;
  let fixture: ComponentFixture<TacheUpdate>;
  let activatedRoute: ActivatedRoute;
  let tacheFormService: TacheFormService;
  let tacheService: TacheService;
  let demandePriseEnChargeService: DemandePriseEnChargeService;
  let userService: UserService;
  let boiteReceptionService: BoiteReceptionService;

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

    fixture = TestBed.createComponent(TacheUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    tacheFormService = TestBed.inject(TacheFormService);
    tacheService = TestBed.inject(TacheService);
    demandePriseEnChargeService = TestBed.inject(DemandePriseEnChargeService);
    userService = TestBed.inject(UserService);
    boiteReceptionService = TestBed.inject(BoiteReceptionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call DemandePriseEnCharge query and add missing value', () => {
      const tache: ITache = { id: 26209 };
      const demande: IDemandePriseEnCharge = { id: 17525 };
      tache.demande = demande;

      const demandePriseEnChargeCollection: IDemandePriseEnCharge[] = [{ id: 17525 }];
      vi.spyOn(demandePriseEnChargeService, 'query').mockReturnValue(of(new HttpResponse({ body: demandePriseEnChargeCollection })));
      const additionalDemandePriseEnCharges = [demande];
      const expectedCollection: IDemandePriseEnCharge[] = [...additionalDemandePriseEnCharges, ...demandePriseEnChargeCollection];
      vi.spyOn(demandePriseEnChargeService, 'addDemandePriseEnChargeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ tache });
      comp.ngOnInit();

      expect(demandePriseEnChargeService.query).toHaveBeenCalled();
      expect(demandePriseEnChargeService.addDemandePriseEnChargeToCollectionIfMissing).toHaveBeenCalledWith(
        demandePriseEnChargeCollection,
        ...additionalDemandePriseEnCharges.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.demandePriseEnChargesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call User query and add missing value', () => {
      const tache: ITache = { id: 26209 };
      const utilisateur: IUser = { id: 3944 };
      tache.utilisateur = utilisateur;

      const userCollection: IUser[] = [{ id: 3944 }];
      vi.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [utilisateur];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      vi.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ tache });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.usersSharedCollection()).toEqual(expectedCollection);
    });

    it('should call BoiteReception query and add missing value', () => {
      const tache: ITache = { id: 26209 };
      const boiteReception: IBoiteReception = { id: 19151 };
      tache.boiteReception = boiteReception;

      const boiteReceptionCollection: IBoiteReception[] = [{ id: 19151 }];
      vi.spyOn(boiteReceptionService, 'query').mockReturnValue(of(new HttpResponse({ body: boiteReceptionCollection })));
      const additionalBoiteReceptions = [boiteReception];
      const expectedCollection: IBoiteReception[] = [...additionalBoiteReceptions, ...boiteReceptionCollection];
      vi.spyOn(boiteReceptionService, 'addBoiteReceptionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ tache });
      comp.ngOnInit();

      expect(boiteReceptionService.query).toHaveBeenCalled();
      expect(boiteReceptionService.addBoiteReceptionToCollectionIfMissing).toHaveBeenCalledWith(
        boiteReceptionCollection,
        ...additionalBoiteReceptions.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.boiteReceptionsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const tache: ITache = { id: 26209 };
      const demande: IDemandePriseEnCharge = { id: 17525 };
      tache.demande = demande;
      const utilisateur: IUser = { id: 3944 };
      tache.utilisateur = utilisateur;
      const boiteReception: IBoiteReception = { id: 19151 };
      tache.boiteReception = boiteReception;

      activatedRoute.data = of({ tache });
      comp.ngOnInit();

      expect(comp.demandePriseEnChargesSharedCollection()).toContainEqual(demande);
      expect(comp.usersSharedCollection()).toContainEqual(utilisateur);
      expect(comp.boiteReceptionsSharedCollection()).toContainEqual(boiteReception);
      expect(comp.tache).toEqual(tache);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITache>();
      const tache = { id: 18006 };
      vi.spyOn(tacheFormService, 'getTache').mockReturnValue(tache);
      vi.spyOn(tacheService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tache });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(tache);
      saveSubject.complete();

      // THEN
      expect(tacheFormService.getTache).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(tacheService.update).toHaveBeenCalledWith(expect.objectContaining(tache));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITache>();
      const tache = { id: 18006 };
      vi.spyOn(tacheFormService, 'getTache').mockReturnValue({ id: null });
      vi.spyOn(tacheService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tache: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(tache);
      saveSubject.complete();

      // THEN
      expect(tacheFormService.getTache).toHaveBeenCalled();
      expect(tacheService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ITache>();
      const tache = { id: 18006 };
      vi.spyOn(tacheService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tache });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(tacheService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareDemandePriseEnCharge', () => {
      it('should forward to demandePriseEnChargeService', () => {
        const entity = { id: 17525 };
        const entity2 = { id: 19327 };
        vi.spyOn(demandePriseEnChargeService, 'compareDemandePriseEnCharge');
        comp.compareDemandePriseEnCharge(entity, entity2);
        expect(demandePriseEnChargeService.compareDemandePriseEnCharge).toHaveBeenCalledWith(entity, entity2);
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

    describe('compareBoiteReception', () => {
      it('should forward to boiteReceptionService', () => {
        const entity = { id: 19151 };
        const entity2 = { id: 25149 };
        vi.spyOn(boiteReceptionService, 'compareBoiteReception');
        comp.compareBoiteReception(entity, entity2);
        expect(boiteReceptionService.compareBoiteReception).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
