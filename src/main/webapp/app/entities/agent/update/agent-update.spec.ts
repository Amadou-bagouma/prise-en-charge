import { beforeEach, describe, expect, it, vi } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IDirection } from 'app/entities/direction/direction.model';
import { DirectionService } from 'app/entities/direction/service/direction.service';
import { IGestion } from 'app/entities/gestion/gestion.model';
import { GestionService } from 'app/entities/gestion/service/gestion.service';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { IAgent } from '../agent.model';
import { AgentService } from '../service/agent.service';

import { AgentFormService } from './agent-form.service';
import { AgentUpdate } from './agent-update';

describe('Agent Management Update Component', () => {
  let comp: AgentUpdate;
  let fixture: ComponentFixture<AgentUpdate>;
  let activatedRoute: ActivatedRoute;
  let agentFormService: AgentFormService;
  let agentService: AgentService;
  let directionService: DirectionService;
  let gestionService: GestionService;
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

    fixture = TestBed.createComponent(AgentUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    agentFormService = TestBed.inject(AgentFormService);
    agentService = TestBed.inject(AgentService);
    directionService = TestBed.inject(DirectionService);
    gestionService = TestBed.inject(GestionService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Direction query and add missing value', () => {
      const agent: IAgent = { id: 18913 };
      const direction: IDirection = { id: 3524 };
      agent.direction = direction;

      const directionCollection: IDirection[] = [{ id: 3524 }];
      vi.spyOn(directionService, 'query').mockReturnValue(of(new HttpResponse({ body: directionCollection })));
      const additionalDirections = [direction];
      const expectedCollection: IDirection[] = [...additionalDirections, ...directionCollection];
      vi.spyOn(directionService, 'addDirectionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ agent });
      comp.ngOnInit();

      expect(directionService.query).toHaveBeenCalled();
      expect(directionService.addDirectionToCollectionIfMissing).toHaveBeenCalledWith(
        directionCollection,
        ...additionalDirections.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.directionsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Gestion query and add missing value', () => {
      const agent: IAgent = { id: 18913 };
      const gestion: IGestion = { id: 26677 };
      agent.gestion = gestion;

      const gestionCollection: IGestion[] = [{ id: 26677 }];
      vi.spyOn(gestionService, 'query').mockReturnValue(of(new HttpResponse({ body: gestionCollection })));
      const additionalGestions = [gestion];
      const expectedCollection: IGestion[] = [...additionalGestions, ...gestionCollection];
      vi.spyOn(gestionService, 'addGestionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ agent });
      comp.ngOnInit();

      expect(gestionService.query).toHaveBeenCalled();
      expect(gestionService.addGestionToCollectionIfMissing).toHaveBeenCalledWith(
        gestionCollection,
        ...additionalGestions.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.gestionsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call User query and add missing value', () => {
      const agent: IAgent = { id: 18913 };
      const user: IUser = { id: 3944 };
      agent.user = user;

      const userCollection: IUser[] = [{ id: 3944 }];
      vi.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      vi.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ agent });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.usersSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const agent: IAgent = { id: 18913 };
      const direction: IDirection = { id: 3524 };
      agent.direction = direction;
      const gestion: IGestion = { id: 26677 };
      agent.gestion = gestion;
      const user: IUser = { id: 3944 };
      agent.user = user;

      activatedRoute.data = of({ agent });
      comp.ngOnInit();

      expect(comp.directionsSharedCollection()).toContainEqual(direction);
      expect(comp.gestionsSharedCollection()).toContainEqual(gestion);
      expect(comp.usersSharedCollection()).toContainEqual(user);
      expect(comp.agent).toEqual(agent);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IAgent>();
      const agent = { id: 25235 };
      vi.spyOn(agentFormService, 'getAgent').mockReturnValue(agent);
      vi.spyOn(agentService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ agent });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(agent);
      saveSubject.complete();

      // THEN
      expect(agentFormService.getAgent).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(agentService.update).toHaveBeenCalledWith(expect.objectContaining(agent));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IAgent>();
      const agent = { id: 25235 };
      vi.spyOn(agentFormService, 'getAgent').mockReturnValue({ id: null });
      vi.spyOn(agentService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ agent: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(agent);
      saveSubject.complete();

      // THEN
      expect(agentFormService.getAgent).toHaveBeenCalled();
      expect(agentService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IAgent>();
      const agent = { id: 25235 };
      vi.spyOn(agentService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ agent });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(agentService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareDirection', () => {
      it('should forward to directionService', () => {
        const entity = { id: 3524 };
        const entity2 = { id: 10870 };
        vi.spyOn(directionService, 'compareDirection');
        comp.compareDirection(entity, entity2);
        expect(directionService.compareDirection).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareGestion', () => {
      it('should forward to gestionService', () => {
        const entity = { id: 26677 };
        const entity2 = { id: 13557 };
        vi.spyOn(gestionService, 'compareGestion');
        comp.compareGestion(entity, entity2);
        expect(gestionService.compareGestion).toHaveBeenCalledWith(entity, entity2);
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
