import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IHistoriqueAction } from '../historique-action.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../historique-action.test-samples';

import { HistoriqueActionService, RestHistoriqueAction } from './historique-action.service';

const requireRestSample: RestHistoriqueAction = {
  ...sampleWithRequiredData,
  dateAction: sampleWithRequiredData.dateAction?.toJSON(),
};

describe('HistoriqueAction Service', () => {
  let service: HistoriqueActionService;
  let httpMock: HttpTestingController;
  let expectedResult: IHistoriqueAction | IHistoriqueAction[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(HistoriqueActionService);
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

    it('should create a HistoriqueAction', () => {
      const historiqueAction = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(historiqueAction).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a HistoriqueAction', () => {
      const historiqueAction = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(historiqueAction).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a HistoriqueAction', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of HistoriqueAction', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a HistoriqueAction', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addHistoriqueActionToCollectionIfMissing', () => {
      it('should add a HistoriqueAction to an empty array', () => {
        const historiqueAction: IHistoriqueAction = sampleWithRequiredData;
        expectedResult = service.addHistoriqueActionToCollectionIfMissing([], historiqueAction);
        expect(expectedResult).toEqual([historiqueAction]);
      });

      it('should not add a HistoriqueAction to an array that contains it', () => {
        const historiqueAction: IHistoriqueAction = sampleWithRequiredData;
        const historiqueActionCollection: IHistoriqueAction[] = [
          {
            ...historiqueAction,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addHistoriqueActionToCollectionIfMissing(historiqueActionCollection, historiqueAction);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a HistoriqueAction to an array that doesn't contain it", () => {
        const historiqueAction: IHistoriqueAction = sampleWithRequiredData;
        const historiqueActionCollection: IHistoriqueAction[] = [sampleWithPartialData];
        expectedResult = service.addHistoriqueActionToCollectionIfMissing(historiqueActionCollection, historiqueAction);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(historiqueAction);
      });

      it('should add only unique HistoriqueAction to an array', () => {
        const historiqueActionArray: IHistoriqueAction[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const historiqueActionCollection: IHistoriqueAction[] = [sampleWithRequiredData];
        expectedResult = service.addHistoriqueActionToCollectionIfMissing(historiqueActionCollection, ...historiqueActionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const historiqueAction: IHistoriqueAction = sampleWithRequiredData;
        const historiqueAction2: IHistoriqueAction = sampleWithPartialData;
        expectedResult = service.addHistoriqueActionToCollectionIfMissing([], historiqueAction, historiqueAction2);
        expect(expectedResult).toEqual([historiqueAction, historiqueAction2]);
      });

      it('should accept null and undefined values', () => {
        const historiqueAction: IHistoriqueAction = sampleWithRequiredData;
        expectedResult = service.addHistoriqueActionToCollectionIfMissing([], null, historiqueAction, undefined);
        expect(expectedResult).toEqual([historiqueAction]);
      });

      it('should return initial array if no HistoriqueAction is added', () => {
        const historiqueActionCollection: IHistoriqueAction[] = [sampleWithRequiredData];
        expectedResult = service.addHistoriqueActionToCollectionIfMissing(historiqueActionCollection, undefined, null);
        expect(expectedResult).toEqual(historiqueActionCollection);
      });
    });

    describe('compareHistoriqueAction', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareHistoriqueAction(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 7263 };
        const entity2 = null;

        const compareResult1 = service.compareHistoriqueAction(entity1, entity2);
        const compareResult2 = service.compareHistoriqueAction(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 7263 };
        const entity2 = { id: 10985 };

        const compareResult1 = service.compareHistoriqueAction(entity1, entity2);
        const compareResult2 = service.compareHistoriqueAction(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 7263 };
        const entity2 = { id: 7263 };

        const compareResult1 = service.compareHistoriqueAction(entity1, entity2);
        const compareResult2 = service.compareHistoriqueAction(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
