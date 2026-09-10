import { beforeEach, describe, expect, it, vi } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IDemandePriseEnCharge } from 'app/entities/demande-prise-en-charge/demande-prise-en-charge.model';
import { DemandePriseEnChargeService } from 'app/entities/demande-prise-en-charge/service/demande-prise-en-charge.service';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { IHistoriqueAction } from '../historique-action.model';
import { HistoriqueActionService } from '../service/historique-action.service';

import { HistoriqueActionFormService } from './historique-action-form.service';
import { HistoriqueActionUpdate } from './historique-action-update';

describe('HistoriqueAction Management Update Component', () => {
  let comp: HistoriqueActionUpdate;
  let fixture: ComponentFixture<HistoriqueActionUpdate>;
  let activatedRoute: ActivatedRoute;
  let historiqueActionFormService: HistoriqueActionFormService;
  let historiqueActionService: HistoriqueActionService;
  let demandePriseEnChargeService: DemandePriseEnChargeService;
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

    fixture = TestBed.createComponent(HistoriqueActionUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    historiqueActionFormService = TestBed.inject(HistoriqueActionFormService);
    historiqueActionService = TestBed.inject(HistoriqueActionService);
    demandePriseEnChargeService = TestBed.inject(DemandePriseEnChargeService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call DemandePriseEnCharge query and add missing value', () => {
      const historiqueAction: IHistoriqueAction = { id: 10985 };
      const demande: IDemandePriseEnCharge = { id: 17525 };
      historiqueAction.demande = demande;

      const demandePriseEnChargeCollection: IDemandePriseEnCharge[] = [{ id: 17525 }];
      vi.spyOn(demandePriseEnChargeService, 'query').mockReturnValue(of(new HttpResponse({ body: demandePriseEnChargeCollection })));
      const additionalDemandePriseEnCharges = [demande];
      const expectedCollection: IDemandePriseEnCharge[] = [...additionalDemandePriseEnCharges, ...demandePriseEnChargeCollection];
      vi.spyOn(demandePriseEnChargeService, 'addDemandePriseEnChargeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ historiqueAction });
      comp.ngOnInit();

      expect(demandePriseEnChargeService.query).toHaveBeenCalled();
      expect(demandePriseEnChargeService.addDemandePriseEnChargeToCollectionIfMissing).toHaveBeenCalledWith(
        demandePriseEnChargeCollection,
        ...additionalDemandePriseEnCharges.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.demandePriseEnChargesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call User query and add missing value', () => {
      const historiqueAction: IHistoriqueAction = { id: 10985 };
      const utilisateur: IUser = { id: 3944 };
      historiqueAction.utilisateur = utilisateur;

      const userCollection: IUser[] = [{ id: 3944 }];
      vi.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [utilisateur];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      vi.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ historiqueAction });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.usersSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const historiqueAction: IHistoriqueAction = { id: 10985 };
      const demande: IDemandePriseEnCharge = { id: 17525 };
      historiqueAction.demande = demande;
      const utilisateur: IUser = { id: 3944 };
      historiqueAction.utilisateur = utilisateur;

      activatedRoute.data = of({ historiqueAction });
      comp.ngOnInit();

      expect(comp.demandePriseEnChargesSharedCollection()).toContainEqual(demande);
      expect(comp.usersSharedCollection()).toContainEqual(utilisateur);
      expect(comp.historiqueAction).toEqual(historiqueAction);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IHistoriqueAction>();
      const historiqueAction = { id: 7263 };
      vi.spyOn(historiqueActionFormService, 'getHistoriqueAction').mockReturnValue(historiqueAction);
      vi.spyOn(historiqueActionService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ historiqueAction });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(historiqueAction);
      saveSubject.complete();

      // THEN
      expect(historiqueActionFormService.getHistoriqueAction).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(historiqueActionService.update).toHaveBeenCalledWith(expect.objectContaining(historiqueAction));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IHistoriqueAction>();
      const historiqueAction = { id: 7263 };
      vi.spyOn(historiqueActionFormService, 'getHistoriqueAction').mockReturnValue({ id: null });
      vi.spyOn(historiqueActionService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ historiqueAction: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(historiqueAction);
      saveSubject.complete();

      // THEN
      expect(historiqueActionFormService.getHistoriqueAction).toHaveBeenCalled();
      expect(historiqueActionService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IHistoriqueAction>();
      const historiqueAction = { id: 7263 };
      vi.spyOn(historiqueActionService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ historiqueAction });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(historiqueActionService.update).toHaveBeenCalled();
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
  });
});
