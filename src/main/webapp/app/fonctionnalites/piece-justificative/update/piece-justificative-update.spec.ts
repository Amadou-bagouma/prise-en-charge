import { beforeEach, describe, expect, it, vi } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IDemandePriseEnCharge } from 'app/entities/demande-prise-en-charge/demande-prise-en-charge.model';
import { DemandePriseEnChargeService } from 'app/entities/demande-prise-en-charge/service/demande-prise-en-charge.service';
import { ConfirmService } from 'app/shared/confirm';

import { IPieceJustificative } from '../piece-justificative.model';
import { PieceJustificativeService } from '../service/piece-justificative.service';

import { PieceJustificativeFormService } from './piece-justificative-form.service';
import { PieceJustificativeUpdate } from './piece-justificative-update';

describe('PieceJustificative Management Update Component', () => {
  let comp: PieceJustificativeUpdate;
  let fixture: ComponentFixture<PieceJustificativeUpdate>;
  let activatedRoute: ActivatedRoute;
  let pieceJustificativeFormService: PieceJustificativeFormService;
  let pieceJustificativeService: PieceJustificativeService;
  let demandePriseEnChargeService: DemandePriseEnChargeService;

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

    fixture = TestBed.createComponent(PieceJustificativeUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    pieceJustificativeFormService = TestBed.inject(PieceJustificativeFormService);
    pieceJustificativeService = TestBed.inject(PieceJustificativeService);
    demandePriseEnChargeService = TestBed.inject(DemandePriseEnChargeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call DemandePriseEnCharge query and add missing value', () => {
      const pieceJustificative: IPieceJustificative = { id: 20717 };
      const demande: IDemandePriseEnCharge = { id: 17525 };
      pieceJustificative.demande = demande;

      const demandePriseEnChargeCollection: IDemandePriseEnCharge[] = [{ id: 17525 }];
      vi.spyOn(demandePriseEnChargeService, 'query').mockReturnValue(of(new HttpResponse({ body: demandePriseEnChargeCollection })));
      const additionalDemandePriseEnCharges = [demande];
      const expectedCollection: IDemandePriseEnCharge[] = [...additionalDemandePriseEnCharges, ...demandePriseEnChargeCollection];
      vi.spyOn(demandePriseEnChargeService, 'addDemandePriseEnChargeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ pieceJustificative });
      comp.ngOnInit();

      expect(demandePriseEnChargeService.query).toHaveBeenCalled();
      expect(demandePriseEnChargeService.addDemandePriseEnChargeToCollectionIfMissing).toHaveBeenCalledWith(
        demandePriseEnChargeCollection,
        ...additionalDemandePriseEnCharges.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.demandePriseEnChargesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const pieceJustificative: IPieceJustificative = { id: 20717 };
      const demande: IDemandePriseEnCharge = { id: 17525 };
      pieceJustificative.demande = demande;

      activatedRoute.data = of({ pieceJustificative });
      comp.ngOnInit();

      expect(comp.demandePriseEnChargesSharedCollection()).toContainEqual(demande);
      expect(comp.pieceJustificative).toEqual(pieceJustificative);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPieceJustificative>();
      const pieceJustificative = { id: 17302 };
      vi.spyOn(pieceJustificativeFormService, 'getPieceJustificative').mockReturnValue(pieceJustificative);
      vi.spyOn(pieceJustificativeService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pieceJustificative });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(pieceJustificative);
      saveSubject.complete();

      // THEN
      expect(pieceJustificativeFormService.getPieceJustificative).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(pieceJustificativeService.update).toHaveBeenCalledWith(expect.objectContaining(pieceJustificative));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPieceJustificative>();
      const pieceJustificative = { id: 17302 };
      vi.spyOn(pieceJustificativeFormService, 'getPieceJustificative').mockReturnValue({ id: null });
      vi.spyOn(pieceJustificativeService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pieceJustificative: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(pieceJustificative);
      saveSubject.complete();

      // THEN
      expect(pieceJustificativeFormService.getPieceJustificative).toHaveBeenCalled();
      expect(pieceJustificativeService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IPieceJustificative>();
      const pieceJustificative = { id: 17302 };
      vi.spyOn(pieceJustificativeService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pieceJustificative });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(pieceJustificativeService.update).toHaveBeenCalled();
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
  });
});
