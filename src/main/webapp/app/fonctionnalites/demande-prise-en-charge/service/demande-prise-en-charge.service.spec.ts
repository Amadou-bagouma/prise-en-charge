import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IDemandePriseEnCharge } from '../demande-prise-en-charge.model';
import {
  sampleWithFullData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithRequiredData,
} from '../demande-prise-en-charge.test-samples';

import { DemandePriseEnChargeService, RestDemandePriseEnCharge } from './demande-prise-en-charge.service';

const requireRestSample: RestDemandePriseEnCharge = {
  ...sampleWithRequiredData,
  dateCreation: sampleWithRequiredData.dateCreation?.toJSON(),
  dateModification: sampleWithRequiredData.dateModification?.toJSON(),
  dateAssignation: sampleWithRequiredData.dateAssignation?.toJSON(),
  dateEcheance: sampleWithRequiredData.dateEcheance?.toJSON(),
};

describe('DemandePriseEnCharge Service', () => {
  let service: DemandePriseEnChargeService;
  let httpMock: HttpTestingController;
  let expectedResult: IDemandePriseEnCharge | IDemandePriseEnCharge[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(DemandePriseEnChargeService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a DemandePriseEnCharge', () => {
      const demandePriseEnCharge = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(demandePriseEnCharge).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a DemandePriseEnCharge', () => {
      const demandePriseEnCharge = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(demandePriseEnCharge).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a DemandePriseEnCharge', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of DemandePriseEnCharge', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a DemandePriseEnCharge', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addDemandePriseEnChargeToCollectionIfMissing', () => {
      it('should add a DemandePriseEnCharge to an empty array', () => {
        const demandePriseEnCharge: IDemandePriseEnCharge = sampleWithRequiredData;
        expectedResult = service.addDemandePriseEnChargeToCollectionIfMissing([], demandePriseEnCharge);
        expect(expectedResult).toEqual([demandePriseEnCharge]);
      });

      it('should not add a DemandePriseEnCharge to an array that contains it', () => {
        const demandePriseEnCharge: IDemandePriseEnCharge = sampleWithRequiredData;
        const demandePriseEnChargeCollection: IDemandePriseEnCharge[] = [
          {
            ...demandePriseEnCharge,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addDemandePriseEnChargeToCollectionIfMissing(demandePriseEnChargeCollection, demandePriseEnCharge);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a DemandePriseEnCharge to an array that doesn't contain it", () => {
        const demandePriseEnCharge: IDemandePriseEnCharge = sampleWithRequiredData;
        const demandePriseEnChargeCollection: IDemandePriseEnCharge[] = [sampleWithPartialData];
        expectedResult = service.addDemandePriseEnChargeToCollectionIfMissing(demandePriseEnChargeCollection, demandePriseEnCharge);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(demandePriseEnCharge);
      });

      it('should add only unique DemandePriseEnCharge to an array', () => {
        const demandePriseEnChargeArray: IDemandePriseEnCharge[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const demandePriseEnChargeCollection: IDemandePriseEnCharge[] = [sampleWithRequiredData];
        expectedResult = service.addDemandePriseEnChargeToCollectionIfMissing(demandePriseEnChargeCollection, ...demandePriseEnChargeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const demandePriseEnCharge: IDemandePriseEnCharge = sampleWithRequiredData;
        const demandePriseEnCharge2: IDemandePriseEnCharge = sampleWithPartialData;
        expectedResult = service.addDemandePriseEnChargeToCollectionIfMissing([], demandePriseEnCharge, demandePriseEnCharge2);
        expect(expectedResult).toEqual([demandePriseEnCharge, demandePriseEnCharge2]);
      });

      it('should accept null and undefined values', () => {
        const demandePriseEnCharge: IDemandePriseEnCharge = sampleWithRequiredData;
        expectedResult = service.addDemandePriseEnChargeToCollectionIfMissing([], null, demandePriseEnCharge, undefined);
        expect(expectedResult).toEqual([demandePriseEnCharge]);
      });

      it('should return initial array if no DemandePriseEnCharge is added', () => {
        const demandePriseEnChargeCollection: IDemandePriseEnCharge[] = [sampleWithRequiredData];
        expectedResult = service.addDemandePriseEnChargeToCollectionIfMissing(demandePriseEnChargeCollection, undefined, null);
        expect(expectedResult).toEqual(demandePriseEnChargeCollection);
      });
    });

    describe('compareDemandePriseEnCharge', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareDemandePriseEnCharge(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 17525 };
        const entity2 = null;

        const compareResult1 = service.compareDemandePriseEnCharge(entity1, entity2);
        const compareResult2 = service.compareDemandePriseEnCharge(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 17525 };
        const entity2 = { id: 19327 };

        const compareResult1 = service.compareDemandePriseEnCharge(entity1, entity2);
        const compareResult2 = service.compareDemandePriseEnCharge(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 17525 };
        const entity2 = { id: 17525 };

        const compareResult1 = service.compareDemandePriseEnCharge(entity1, entity2);
        const compareResult2 = service.compareDemandePriseEnCharge(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
