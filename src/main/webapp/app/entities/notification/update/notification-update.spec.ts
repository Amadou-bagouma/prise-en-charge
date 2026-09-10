import { beforeEach, describe, expect, it, vi } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IDemandePriseEnCharge } from 'app/entities/demande-prise-en-charge/demande-prise-en-charge.model';
import { DemandePriseEnChargeService } from 'app/entities/demande-prise-en-charge/service/demande-prise-en-charge.service';
import { TacheService } from 'app/entities/tache/service/tache.service';
import { ITache } from 'app/entities/tache/tache.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { INotification } from '../notification.model';
import { NotificationService } from '../service/notification.service';

import { NotificationFormService } from './notification-form.service';
import { NotificationUpdate } from './notification-update';

describe('Notification Management Update Component', () => {
  let comp: NotificationUpdate;
  let fixture: ComponentFixture<NotificationUpdate>;
  let activatedRoute: ActivatedRoute;
  let notificationFormService: NotificationFormService;
  let notificationService: NotificationService;
  let userService: UserService;
  let demandePriseEnChargeService: DemandePriseEnChargeService;
  let tacheService: TacheService;

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

    fixture = TestBed.createComponent(NotificationUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    notificationFormService = TestBed.inject(NotificationFormService);
    notificationService = TestBed.inject(NotificationService);
    userService = TestBed.inject(UserService);
    demandePriseEnChargeService = TestBed.inject(DemandePriseEnChargeService);
    tacheService = TestBed.inject(TacheService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call User query and add missing value', () => {
      const notification: INotification = { id: 16244 };
      const utilisateur: IUser = { id: 3944 };
      notification.utilisateur = utilisateur;

      const userCollection: IUser[] = [{ id: 3944 }];
      vi.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [utilisateur];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      vi.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ notification });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.usersSharedCollection()).toEqual(expectedCollection);
    });

    it('should call DemandePriseEnCharge query and add missing value', () => {
      const notification: INotification = { id: 16244 };
      const demande: IDemandePriseEnCharge = { id: 17525 };
      notification.demande = demande;

      const demandePriseEnChargeCollection: IDemandePriseEnCharge[] = [{ id: 17525 }];
      vi.spyOn(demandePriseEnChargeService, 'query').mockReturnValue(of(new HttpResponse({ body: demandePriseEnChargeCollection })));
      const additionalDemandePriseEnCharges = [demande];
      const expectedCollection: IDemandePriseEnCharge[] = [...additionalDemandePriseEnCharges, ...demandePriseEnChargeCollection];
      vi.spyOn(demandePriseEnChargeService, 'addDemandePriseEnChargeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ notification });
      comp.ngOnInit();

      expect(demandePriseEnChargeService.query).toHaveBeenCalled();
      expect(demandePriseEnChargeService.addDemandePriseEnChargeToCollectionIfMissing).toHaveBeenCalledWith(
        demandePriseEnChargeCollection,
        ...additionalDemandePriseEnCharges.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.demandePriseEnChargesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Tache query and add missing value', () => {
      const notification: INotification = { id: 16244 };
      const tache: ITache = { id: 18006 };
      notification.tache = tache;

      const tacheCollection: ITache[] = [{ id: 18006 }];
      vi.spyOn(tacheService, 'query').mockReturnValue(of(new HttpResponse({ body: tacheCollection })));
      const additionalTaches = [tache];
      const expectedCollection: ITache[] = [...additionalTaches, ...tacheCollection];
      vi.spyOn(tacheService, 'addTacheToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ notification });
      comp.ngOnInit();

      expect(tacheService.query).toHaveBeenCalled();
      expect(tacheService.addTacheToCollectionIfMissing).toHaveBeenCalledWith(
        tacheCollection,
        ...additionalTaches.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.tachesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const notification: INotification = { id: 16244 };
      const utilisateur: IUser = { id: 3944 };
      notification.utilisateur = utilisateur;
      const demande: IDemandePriseEnCharge = { id: 17525 };
      notification.demande = demande;
      const tache: ITache = { id: 18006 };
      notification.tache = tache;

      activatedRoute.data = of({ notification });
      comp.ngOnInit();

      expect(comp.usersSharedCollection()).toContainEqual(utilisateur);
      expect(comp.demandePriseEnChargesSharedCollection()).toContainEqual(demande);
      expect(comp.tachesSharedCollection()).toContainEqual(tache);
      expect(comp.notification).toEqual(notification);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<INotification>();
      const notification = { id: 16124 };
      vi.spyOn(notificationFormService, 'getNotification').mockReturnValue(notification);
      vi.spyOn(notificationService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ notification });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(notification);
      saveSubject.complete();

      // THEN
      expect(notificationFormService.getNotification).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(notificationService.update).toHaveBeenCalledWith(expect.objectContaining(notification));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<INotification>();
      const notification = { id: 16124 };
      vi.spyOn(notificationFormService, 'getNotification').mockReturnValue({ id: null });
      vi.spyOn(notificationService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ notification: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(notification);
      saveSubject.complete();

      // THEN
      expect(notificationFormService.getNotification).toHaveBeenCalled();
      expect(notificationService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<INotification>();
      const notification = { id: 16124 };
      vi.spyOn(notificationService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ notification });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(notificationService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('should forward to userService', () => {
        const entity = { id: 3944 };
        const entity2 = { id: 6275 };
        vi.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareDemandePriseEnCharge', () => {
      it('should forward to demandePriseEnChargeService', () => {
        const entity = { id: 17525 };
        const entity2 = { id: 19327 };
        vi.spyOn(demandePriseEnChargeService, 'compareDemandePriseEnCharge');
        comp.compareDemandePriseEnCharge(entity, entity2);
        expect(demandePriseEnChargeService.compareDemandePriseEnCharge).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareTache', () => {
      it('should forward to tacheService', () => {
        const entity = { id: 18006 };
        const entity2 = { id: 26209 };
        vi.spyOn(tacheService, 'compareTache');
        comp.compareTache(entity, entity2);
        expect(tacheService.compareTache).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
