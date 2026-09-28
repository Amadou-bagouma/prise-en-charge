import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IBoiteReception } from '../boite-reception.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../boite-reception.test-samples';

import { BoiteReceptionService, RestBoiteReception } from './boite-reception.service';

const requireRestSample: RestBoiteReception = {
  ...sampleWithRequiredData,
  dateCreation: sampleWithRequiredData.dateCreation?.toJSON(),
  dateDerniereLecture: sampleWithRequiredData.dateDerniereLecture?.toJSON(),
};

describe('BoiteReception Service', () => {
  let service: BoiteReceptionService;
  let httpMock: HttpTestingController;
  let expectedResult: IBoiteReception | IBoiteReception[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(BoiteReceptionService);
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

    it('should create a BoiteReception', () => {
      const boiteReception = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(boiteReception).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a BoiteReception', () => {
      const boiteReception = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(boiteReception).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a BoiteReception', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of BoiteReception', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a BoiteReception', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addBoiteReceptionToCollectionIfMissing', () => {
      it('should add a BoiteReception to an empty array', () => {
        const boiteReception: IBoiteReception = sampleWithRequiredData;
        expectedResult = service.addBoiteReceptionToCollectionIfMissing([], boiteReception);
        expect(expectedResult).toEqual([boiteReception]);
      });

      it('should not add a BoiteReception to an array that contains it', () => {
        const boiteReception: IBoiteReception = sampleWithRequiredData;
        const boiteReceptionCollection: IBoiteReception[] = [
          {
            ...boiteReception,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addBoiteReceptionToCollectionIfMissing(boiteReceptionCollection, boiteReception);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a BoiteReception to an array that doesn't contain it", () => {
        const boiteReception: IBoiteReception = sampleWithRequiredData;
        const boiteReceptionCollection: IBoiteReception[] = [sampleWithPartialData];
        expectedResult = service.addBoiteReceptionToCollectionIfMissing(boiteReceptionCollection, boiteReception);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(boiteReception);
      });

      it('should add only unique BoiteReception to an array', () => {
        const boiteReceptionArray: IBoiteReception[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const boiteReceptionCollection: IBoiteReception[] = [sampleWithRequiredData];
        expectedResult = service.addBoiteReceptionToCollectionIfMissing(boiteReceptionCollection, ...boiteReceptionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const boiteReception: IBoiteReception = sampleWithRequiredData;
        const boiteReception2: IBoiteReception = sampleWithPartialData;
        expectedResult = service.addBoiteReceptionToCollectionIfMissing([], boiteReception, boiteReception2);
        expect(expectedResult).toEqual([boiteReception, boiteReception2]);
      });

      it('should accept null and undefined values', () => {
        const boiteReception: IBoiteReception = sampleWithRequiredData;
        expectedResult = service.addBoiteReceptionToCollectionIfMissing([], null, boiteReception, undefined);
        expect(expectedResult).toEqual([boiteReception]);
      });

      it('should return initial array if no BoiteReception is added', () => {
        const boiteReceptionCollection: IBoiteReception[] = [sampleWithRequiredData];
        expectedResult = service.addBoiteReceptionToCollectionIfMissing(boiteReceptionCollection, undefined, null);
        expect(expectedResult).toEqual(boiteReceptionCollection);
      });
    });

    describe('compareBoiteReception', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareBoiteReception(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 19151 };
        const entity2 = null;

        const compareResult1 = service.compareBoiteReception(entity1, entity2);
        const compareResult2 = service.compareBoiteReception(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 19151 };
        const entity2 = { id: 25149 };

        const compareResult1 = service.compareBoiteReception(entity1, entity2);
        const compareResult2 = service.compareBoiteReception(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 19151 };
        const entity2 = { id: 19151 };

        const compareResult1 = service.compareBoiteReception(entity1, entity2);
        const compareResult2 = service.compareBoiteReception(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
