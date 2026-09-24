import { MockInstance, afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faCheck, faEye, faPencilAlt, faPlus, faSort, faSortDown, faSortUp, faSync, faTimes } from '@fortawesome/free-solid-svg-icons';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { provideTranslateService } from '@ngx-translate/core';
import { Subject, of } from 'rxjs';

import { sampleWithRequiredData } from '../notification.test-samples';
import { NotificationService } from '../service/notification.service';

import { Notification } from './notification';

vi.useFakeTimers();

describe('Notification Management Component', () => {
  let httpMock: HttpTestingController;
  let comp: Notification;
  let fixture: ComponentFixture<Notification>;
  let service: NotificationService;
  let routerNavigateSpy: MockInstance;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideTranslateService(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
                'filter[someId.in]': 'dc4279ea-cfb9-11ec-9d64-0242ac120002',
              }),
            ),
            snapshot: {
              queryParams: {},
              queryParamMap: convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
                'filter[someId.in]': 'dc4279ea-cfb9-11ec-9d64-0242ac120002',
              }),
            },
          },
        },
      ],
    });

    fixture = TestBed.createComponent(Notification);
    comp = fixture.componentInstance;
    service = TestBed.inject(NotificationService);
    routerNavigateSpy = vi.spyOn(comp.router, 'navigate');

    const library = TestBed.inject(FaIconLibrary);
    // La boite de reception porte ses propres actions : « marquer comme lue » a son icone.
    library.addIcons(faCheck, faEye, faPencilAlt, faPlus, faSort, faSortDown, faSortUp, faSync, faTimes);
    httpMock = TestBed.inject(HttpTestingController);
  });

  /**
   * L'ecran charge la liste et, en meme temps, l'effectif de chaque vue. Les specs ne portent
   * que sur la liste : ce raccourci ecarte les appels de comptage, qui partent vers `/count`.
   */
  const requeteListe = () => httpMock.expectOne(req => req.method === 'GET' && !req.url.endsWith('/count'));

  /** Solde les comptages en attente, pour que `verify()` ne bute pas dessus. */
  const soldeLesEffectifs = () => httpMock.match(req => req.url.endsWith('/count')).forEach(req => req.flush(0));

  afterEach(() => {
    TestBed.resetTestingModule();
    soldeLesEffectifs();
    httpMock.verify();
  });

  it('should call load all on init', async () => {
    // WHEN
    TestBed.tick();
    const req = requeteListe();
    req.flush([{ id: 16124 }], { headers: { link: '<http://localhost/api/foo?page=1&size=20>; rel="next"' } });
    await vi.runAllTimersAsync();

    // THEN
    expect(comp.isLoading()).toEqual(false);
    expect(comp.notifications()[0]).toEqual(expect.objectContaining({ id: 16124 }));
  });

  it('should cancel previous requests when loading a new page', async () => {
    // WHEN
    TestBed.tick();
    const req = requeteListe();
    await vi.runAllTimersAsync();

    comp.page.set(3);
    comp.load();
    await vi.runAllTimersAsync();
    const req2 = requeteListe();
    req2.flush([{ id: 16124 }], { headers: { link: '<http://localhost/api/foo?page=1&size=20>; rel="next"' } });
    await vi.runAllTimersAsync();

    // THEN
    expect(req.cancelled).toBeTruthy();
    expect(comp.isLoading()).toEqual(false);
    expect(comp.notifications()[0]).toEqual(expect.objectContaining({ id: 16124 }));
  });

  it('should not fail on resource error state', async () => {
    // GIVEN - first load triggers an HTTP error
    TestBed.tick();
    const errorReq = requeteListe();
    errorReq.flush('error', { status: 500, statusText: 'Server Error' });
    await vi.runAllTimersAsync();

    // THEN - loading state was reset and list is empty
    expect(comp.isLoading()).toBe(false);
    expect(comp.notifications()).toEqual([]);

    // WHEN - second load should still work
    comp.load();
    TestBed.tick();
    const successReq = requeteListe();
    successReq.flush([{ id: 16124 }], { headers: { link: '<http://localhost/api/foo?page=1&size=20>; rel="next"' } });
    await vi.runAllTimersAsync();

    // THEN - subscription is still alive and second load succeeds
    expect(comp.notifications()[0]).toEqual(expect.objectContaining({ id: 16124 }));
  });

  describe('trackId', () => {
    it('should forward to notificationService', () => {
      const entity = { id: 16124 };
      vi.spyOn(service, 'getNotificationIdentifier');
      const id = comp.trackId(entity);
      expect(service.getNotificationIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });

  it('should calculate the sort attribute for a non-id attribute', () => {
    // WHEN
    comp.navigateToWithComponentValues({ predicate: 'non-existing-column', order: 'asc' });

    // THEN
    expect(routerNavigateSpy).toHaveBeenLastCalledWith(
      expect.anything(),
      expect.objectContaining({
        queryParams: expect.objectContaining({
          sort: ['non-existing-column,asc'],
        }),
      }),
    );
  });

  it('should load a page', () => {
    // WHEN
    comp.navigateToPage(1);

    // THEN
    expect(routerNavigateSpy).toHaveBeenCalled();
  });

  it('should calculate the sort attribute for an id', () => {
    // WHEN
    TestBed.tick();
    requeteListe();

    // THEN
    expect(service.notificationsParams()).toMatchObject({ sort: ['id,desc'] });
  });

  it('should calculate the filter attribute', () => {
    // WHEN
    TestBed.tick();
    requeteListe();

    // THEN
    expect(service.notificationsParams()).toMatchObject({ 'someId.in': ['dc4279ea-cfb9-11ec-9d64-0242ac120002'] });
  });

  describe('delete', () => {
    let ngbModal: NgbModal;
    let deleteModalMock: any;

    beforeEach(() => {
      deleteModalMock = { componentInstance: {}, closed: new Subject() };
      // NgbModal is not a singleton using TestBed.inject.
      // ngbModal = TestBed.inject(NgbModal);
      ngbModal = (comp as unknown as { modalService: NgbModal }).modalService;
      vi.spyOn(ngbModal, 'open').mockReturnValue(deleteModalMock);
    });

    it('on confirm should call load', () => {
      // GIVEN
      vi.spyOn(comp, 'load');

      // WHEN
      comp.delete(sampleWithRequiredData);
      deleteModalMock.closed.next('deleted');

      // THEN
      expect(ngbModal.open).toHaveBeenCalled();
      expect(comp.load).toHaveBeenCalled();
    });

    it('on dismiss should call load', () => {
      // GIVEN
      vi.spyOn(comp, 'load');

      // WHEN
      comp.delete(sampleWithRequiredData);
      deleteModalMock.closed.next();

      // THEN
      expect(ngbModal.open).toHaveBeenCalled();
      expect(comp.load).not.toHaveBeenCalled();
    });
  });
});
