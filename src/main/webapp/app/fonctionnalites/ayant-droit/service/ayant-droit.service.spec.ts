import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { DATE_FORMAT } from 'app/config';
import { IAyantDroit } from '../ayant-droit.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../ayant-droit.test-samples';

import { AyantDroitService, RestAyantDroit } from './ayant-droit.service';

const requireRestSample: RestAyantDroit = {
  ...sampleWithRequiredData,
  dateNaissance: sampleWithRequiredData.dateNaissance?.format(DATE_FORMAT),
};

describe('AyantDroit Service', () => {
  let service: AyantDroitService;
  let httpMock: HttpTestingController;
  let expectedResult: IAyantDroit | IAyantDroit[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(AyantDroitService);
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

    it('should create a AyantDroit', () => {
      const ayantDroit = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(ayantDroit).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a AyantDroit', () => {
      const ayantDroit = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(ayantDroit).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a AyantDroit', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of AyantDroit', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a AyantDroit', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addAyantDroitToCollectionIfMissing', () => {
      it('should add a AyantDroit to an empty array', () => {
        const ayantDroit: IAyantDroit = sampleWithRequiredData;
        expectedResult = service.addAyantDroitToCollectionIfMissing([], ayantDroit);
        expect(expectedResult).toEqual([ayantDroit]);
      });

      it('should not add a AyantDroit to an array that contains it', () => {
        const ayantDroit: IAyantDroit = sampleWithRequiredData;
        const ayantDroitCollection: IAyantDroit[] = [
          {
            ...ayantDroit,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addAyantDroitToCollectionIfMissing(ayantDroitCollection, ayantDroit);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a AyantDroit to an array that doesn't contain it", () => {
        const ayantDroit: IAyantDroit = sampleWithRequiredData;
        const ayantDroitCollection: IAyantDroit[] = [sampleWithPartialData];
        expectedResult = service.addAyantDroitToCollectionIfMissing(ayantDroitCollection, ayantDroit);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ayantDroit);
      });

      it('should add only unique AyantDroit to an array', () => {
        const ayantDroitArray: IAyantDroit[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const ayantDroitCollection: IAyantDroit[] = [sampleWithRequiredData];
        expectedResult = service.addAyantDroitToCollectionIfMissing(ayantDroitCollection, ...ayantDroitArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const ayantDroit: IAyantDroit = sampleWithRequiredData;
        const ayantDroit2: IAyantDroit = sampleWithPartialData;
        expectedResult = service.addAyantDroitToCollectionIfMissing([], ayantDroit, ayantDroit2);
        expect(expectedResult).toEqual([ayantDroit, ayantDroit2]);
      });

      it('should accept null and undefined values', () => {
        const ayantDroit: IAyantDroit = sampleWithRequiredData;
        expectedResult = service.addAyantDroitToCollectionIfMissing([], null, ayantDroit, undefined);
        expect(expectedResult).toEqual([ayantDroit]);
      });

      it('should return initial array if no AyantDroit is added', () => {
        const ayantDroitCollection: IAyantDroit[] = [sampleWithRequiredData];
        expectedResult = service.addAyantDroitToCollectionIfMissing(ayantDroitCollection, undefined, null);
        expect(expectedResult).toEqual(ayantDroitCollection);
      });
    });

    describe('compareAyantDroit', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareAyantDroit(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 17970 };
        const entity2 = null;

        const compareResult1 = service.compareAyantDroit(entity1, entity2);
        const compareResult2 = service.compareAyantDroit(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 17970 };
        const entity2 = { id: 27188 };

        const compareResult1 = service.compareAyantDroit(entity1, entity2);
        const compareResult2 = service.compareAyantDroit(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 17970 };
        const entity2 = { id: 17970 };

        const compareResult1 = service.compareAyantDroit(entity1, entity2);
        const compareResult2 = service.compareAyantDroit(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
