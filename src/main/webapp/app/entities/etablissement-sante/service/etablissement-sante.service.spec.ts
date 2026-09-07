import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IEtablissementSante } from '../etablissement-sante.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../etablissement-sante.test-samples';

import { EtablissementSanteService } from './etablissement-sante.service';

const requireRestSample: IEtablissementSante = {
  ...sampleWithRequiredData,
};

describe('EtablissementSante Service', () => {
  let service: EtablissementSanteService;
  let httpMock: HttpTestingController;
  let expectedResult: IEtablissementSante | IEtablissementSante[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(EtablissementSanteService);
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

    it('should create a EtablissementSante', () => {
      const etablissementSante = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(etablissementSante).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a EtablissementSante', () => {
      const etablissementSante = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(etablissementSante).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a EtablissementSante', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of EtablissementSante', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a EtablissementSante', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addEtablissementSanteToCollectionIfMissing', () => {
      it('should add a EtablissementSante to an empty array', () => {
        const etablissementSante: IEtablissementSante = sampleWithRequiredData;
        expectedResult = service.addEtablissementSanteToCollectionIfMissing([], etablissementSante);
        expect(expectedResult).toEqual([etablissementSante]);
      });

      it('should not add a EtablissementSante to an array that contains it', () => {
        const etablissementSante: IEtablissementSante = sampleWithRequiredData;
        const etablissementSanteCollection: IEtablissementSante[] = [
          {
            ...etablissementSante,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addEtablissementSanteToCollectionIfMissing(etablissementSanteCollection, etablissementSante);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a EtablissementSante to an array that doesn't contain it", () => {
        const etablissementSante: IEtablissementSante = sampleWithRequiredData;
        const etablissementSanteCollection: IEtablissementSante[] = [sampleWithPartialData];
        expectedResult = service.addEtablissementSanteToCollectionIfMissing(etablissementSanteCollection, etablissementSante);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(etablissementSante);
      });

      it('should add only unique EtablissementSante to an array', () => {
        const etablissementSanteArray: IEtablissementSante[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const etablissementSanteCollection: IEtablissementSante[] = [sampleWithRequiredData];
        expectedResult = service.addEtablissementSanteToCollectionIfMissing(etablissementSanteCollection, ...etablissementSanteArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const etablissementSante: IEtablissementSante = sampleWithRequiredData;
        const etablissementSante2: IEtablissementSante = sampleWithPartialData;
        expectedResult = service.addEtablissementSanteToCollectionIfMissing([], etablissementSante, etablissementSante2);
        expect(expectedResult).toEqual([etablissementSante, etablissementSante2]);
      });

      it('should accept null and undefined values', () => {
        const etablissementSante: IEtablissementSante = sampleWithRequiredData;
        expectedResult = service.addEtablissementSanteToCollectionIfMissing([], null, etablissementSante, undefined);
        expect(expectedResult).toEqual([etablissementSante]);
      });

      it('should return initial array if no EtablissementSante is added', () => {
        const etablissementSanteCollection: IEtablissementSante[] = [sampleWithRequiredData];
        expectedResult = service.addEtablissementSanteToCollectionIfMissing(etablissementSanteCollection, undefined, null);
        expect(expectedResult).toEqual(etablissementSanteCollection);
      });
    });

    describe('compareEtablissementSante', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareEtablissementSante(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 2850 };
        const entity2 = null;

        const compareResult1 = service.compareEtablissementSante(entity1, entity2);
        const compareResult2 = service.compareEtablissementSante(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 2850 };
        const entity2 = { id: 20730 };

        const compareResult1 = service.compareEtablissementSante(entity1, entity2);
        const compareResult2 = service.compareEtablissementSante(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 2850 };
        const entity2 = { id: 2850 };

        const compareResult1 = service.compareEtablissementSante(entity1, entity2);
        const compareResult2 = service.compareEtablissementSante(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
