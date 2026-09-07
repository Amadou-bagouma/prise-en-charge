import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ITypeSoin } from '../type-soin.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../type-soin.test-samples';

import { TypeSoinService } from './type-soin.service';

const requireRestSample: ITypeSoin = {
  ...sampleWithRequiredData,
};

describe('TypeSoin Service', () => {
  let service: TypeSoinService;
  let httpMock: HttpTestingController;
  let expectedResult: ITypeSoin | ITypeSoin[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(TypeSoinService);
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

    it('should create a TypeSoin', () => {
      const typeSoin = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(typeSoin).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TypeSoin', () => {
      const typeSoin = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(typeSoin).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TypeSoin', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TypeSoin', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TypeSoin', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addTypeSoinToCollectionIfMissing', () => {
      it('should add a TypeSoin to an empty array', () => {
        const typeSoin: ITypeSoin = sampleWithRequiredData;
        expectedResult = service.addTypeSoinToCollectionIfMissing([], typeSoin);
        expect(expectedResult).toEqual([typeSoin]);
      });

      it('should not add a TypeSoin to an array that contains it', () => {
        const typeSoin: ITypeSoin = sampleWithRequiredData;
        const typeSoinCollection: ITypeSoin[] = [
          {
            ...typeSoin,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTypeSoinToCollectionIfMissing(typeSoinCollection, typeSoin);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TypeSoin to an array that doesn't contain it", () => {
        const typeSoin: ITypeSoin = sampleWithRequiredData;
        const typeSoinCollection: ITypeSoin[] = [sampleWithPartialData];
        expectedResult = service.addTypeSoinToCollectionIfMissing(typeSoinCollection, typeSoin);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(typeSoin);
      });

      it('should add only unique TypeSoin to an array', () => {
        const typeSoinArray: ITypeSoin[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const typeSoinCollection: ITypeSoin[] = [sampleWithRequiredData];
        expectedResult = service.addTypeSoinToCollectionIfMissing(typeSoinCollection, ...typeSoinArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const typeSoin: ITypeSoin = sampleWithRequiredData;
        const typeSoin2: ITypeSoin = sampleWithPartialData;
        expectedResult = service.addTypeSoinToCollectionIfMissing([], typeSoin, typeSoin2);
        expect(expectedResult).toEqual([typeSoin, typeSoin2]);
      });

      it('should accept null and undefined values', () => {
        const typeSoin: ITypeSoin = sampleWithRequiredData;
        expectedResult = service.addTypeSoinToCollectionIfMissing([], null, typeSoin, undefined);
        expect(expectedResult).toEqual([typeSoin]);
      });

      it('should return initial array if no TypeSoin is added', () => {
        const typeSoinCollection: ITypeSoin[] = [sampleWithRequiredData];
        expectedResult = service.addTypeSoinToCollectionIfMissing(typeSoinCollection, undefined, null);
        expect(expectedResult).toEqual(typeSoinCollection);
      });
    });

    describe('compareTypeSoin', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTypeSoin(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 20111 };
        const entity2 = null;

        const compareResult1 = service.compareTypeSoin(entity1, entity2);
        const compareResult2 = service.compareTypeSoin(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 20111 };
        const entity2 = { id: 12086 };

        const compareResult1 = service.compareTypeSoin(entity1, entity2);
        const compareResult2 = service.compareTypeSoin(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 20111 };
        const entity2 = { id: 20111 };

        const compareResult1 = service.compareTypeSoin(entity1, entity2);
        const compareResult2 = service.compareTypeSoin(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
