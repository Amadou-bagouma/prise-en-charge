import { beforeEach, describe, expect, it, vi } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { ConfirmService } from 'app/shared/confirm';

import { IBoiteReception } from '../boite-reception.model';
import { BoiteReceptionService } from '../service/boite-reception.service';

import { BoiteReceptionFormService } from './boite-reception-form.service';
import { BoiteReceptionUpdate } from './boite-reception-update';

describe('BoiteReception Management Update Component', () => {
  let comp: BoiteReceptionUpdate;
  let fixture: ComponentFixture<BoiteReceptionUpdate>;
  let activatedRoute: ActivatedRoute;
  let boiteReceptionFormService: BoiteReceptionFormService;
  let boiteReceptionService: BoiteReceptionService;
  let userService: UserService;

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

    fixture = TestBed.createComponent(BoiteReceptionUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    boiteReceptionFormService = TestBed.inject(BoiteReceptionFormService);
    boiteReceptionService = TestBed.inject(BoiteReceptionService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call User query and add missing value', () => {
      const boiteReception: IBoiteReception = { id: 25149 };
      const utilisateur: IUser = { id: 3944 };
      boiteReception.utilisateur = utilisateur;

      const userCollection: IUser[] = [{ id: 3944 }];
      vi.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [utilisateur];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      vi.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ boiteReception });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.usersSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const boiteReception: IBoiteReception = { id: 25149 };
      const utilisateur: IUser = { id: 3944 };
      boiteReception.utilisateur = utilisateur;

      activatedRoute.data = of({ boiteReception });
      comp.ngOnInit();

      expect(comp.usersSharedCollection()).toContainEqual(utilisateur);
      expect(comp.boiteReception).toEqual(boiteReception);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IBoiteReception>();
      const boiteReception = { id: 19151 };
      vi.spyOn(boiteReceptionFormService, 'getBoiteReception').mockReturnValue(boiteReception);
      vi.spyOn(boiteReceptionService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ boiteReception });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(boiteReception);
      saveSubject.complete();

      // THEN
      expect(boiteReceptionFormService.getBoiteReception).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(boiteReceptionService.update).toHaveBeenCalledWith(expect.objectContaining(boiteReception));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IBoiteReception>();
      const boiteReception = { id: 19151 };
      vi.spyOn(boiteReceptionFormService, 'getBoiteReception').mockReturnValue({ id: null });
      vi.spyOn(boiteReceptionService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ boiteReception: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(boiteReception);
      saveSubject.complete();

      // THEN
      expect(boiteReceptionFormService.getBoiteReception).toHaveBeenCalled();
      expect(boiteReceptionService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IBoiteReception>();
      const boiteReception = { id: 19151 };
      vi.spyOn(boiteReceptionService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ boiteReception });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(boiteReceptionService.update).toHaveBeenCalled();
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
  });
});
