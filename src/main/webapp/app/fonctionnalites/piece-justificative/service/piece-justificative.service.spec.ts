import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IPieceJustificative } from '../piece-justificative.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../piece-justificative.test-samples';

import { PieceJustificativeService, RestPieceJustificative } from './piece-justificative.service';

const requireRestSample: RestPieceJustificative = {
  ...sampleWithRequiredData,
  dateAjout: sampleWithRequiredData.dateAjout?.toJSON(),
};

describe('PieceJustificative Service', () => {
  let service: PieceJustificativeService;
  let httpMock: HttpTestingController;
  let expectedResult: IPieceJustificative | IPieceJustificative[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(PieceJustificativeService);
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

    it('should create a PieceJustificative', () => {
      const pieceJustificative = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(pieceJustificative).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a PieceJustificative', () => {
      const pieceJustificative = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(pieceJustificative).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a PieceJustificative', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of PieceJustificative', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a PieceJustificative', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addPieceJustificativeToCollectionIfMissing', () => {
      it('should add a PieceJustificative to an empty array', () => {
        const pieceJustificative: IPieceJustificative = sampleWithRequiredData;
        expectedResult = service.addPieceJustificativeToCollectionIfMissing([], pieceJustificative);
        expect(expectedResult).toEqual([pieceJustificative]);
      });

      it('should not add a PieceJustificative to an array that contains it', () => {
        const pieceJustificative: IPieceJustificative = sampleWithRequiredData;
        const pieceJustificativeCollection: IPieceJustificative[] = [
          {
            ...pieceJustificative,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPieceJustificativeToCollectionIfMissing(pieceJustificativeCollection, pieceJustificative);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a PieceJustificative to an array that doesn't contain it", () => {
        const pieceJustificative: IPieceJustificative = sampleWithRequiredData;
        const pieceJustificativeCollection: IPieceJustificative[] = [sampleWithPartialData];
        expectedResult = service.addPieceJustificativeToCollectionIfMissing(pieceJustificativeCollection, pieceJustificative);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(pieceJustificative);
      });

      it('should add only unique PieceJustificative to an array', () => {
        const pieceJustificativeArray: IPieceJustificative[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const pieceJustificativeCollection: IPieceJustificative[] = [sampleWithRequiredData];
        expectedResult = service.addPieceJustificativeToCollectionIfMissing(pieceJustificativeCollection, ...pieceJustificativeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const pieceJustificative: IPieceJustificative = sampleWithRequiredData;
        const pieceJustificative2: IPieceJustificative = sampleWithPartialData;
        expectedResult = service.addPieceJustificativeToCollectionIfMissing([], pieceJustificative, pieceJustificative2);
        expect(expectedResult).toEqual([pieceJustificative, pieceJustificative2]);
      });

      it('should accept null and undefined values', () => {
        const pieceJustificative: IPieceJustificative = sampleWithRequiredData;
        expectedResult = service.addPieceJustificativeToCollectionIfMissing([], null, pieceJustificative, undefined);
        expect(expectedResult).toEqual([pieceJustificative]);
      });

      it('should return initial array if no PieceJustificative is added', () => {
        const pieceJustificativeCollection: IPieceJustificative[] = [sampleWithRequiredData];
        expectedResult = service.addPieceJustificativeToCollectionIfMissing(pieceJustificativeCollection, undefined, null);
        expect(expectedResult).toEqual(pieceJustificativeCollection);
      });
    });

    describe('comparePieceJustificative', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePieceJustificative(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 17302 };
        const entity2 = null;

        const compareResult1 = service.comparePieceJustificative(entity1, entity2);
        const compareResult2 = service.comparePieceJustificative(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 17302 };
        const entity2 = { id: 20717 };

        const compareResult1 = service.comparePieceJustificative(entity1, entity2);
        const compareResult2 = service.comparePieceJustificative(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 17302 };
        const entity2 = { id: 17302 };

        const compareResult1 = service.comparePieceJustificative(entity1, entity2);
        const compareResult2 = service.comparePieceJustificative(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
