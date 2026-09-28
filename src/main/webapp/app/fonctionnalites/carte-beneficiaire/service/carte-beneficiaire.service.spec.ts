import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { DATE_FORMAT } from 'app/config';
import { ICarteBeneficiaire } from '../carte-beneficiaire.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../carte-beneficiaire.test-samples';

import { CarteBeneficiaireService, RestCarteBeneficiaire } from './carte-beneficiaire.service';

const requireRestSample: RestCarteBeneficiaire = {
  ...sampleWithRequiredData,
  dateDebutValidite: sampleWithRequiredData.dateDebutValidite?.format(DATE_FORMAT),
  dateFinValidite: sampleWithRequiredData.dateFinValidite?.format(DATE_FORMAT),
  dateEmission: sampleWithRequiredData.dateEmission?.format(DATE_FORMAT),
};

describe('CarteBeneficiaire Service', () => {
  let service: CarteBeneficiaireService;
  let httpMock: HttpTestingController;
  let expectedResult: ICarteBeneficiaire | ICarteBeneficiaire[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(CarteBeneficiaireService);
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

    it('should create a CarteBeneficiaire', () => {
      const carteBeneficiaire = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(carteBeneficiaire).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a CarteBeneficiaire', () => {
      const carteBeneficiaire = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(carteBeneficiaire).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a CarteBeneficiaire', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of CarteBeneficiaire', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a CarteBeneficiaire', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addCarteBeneficiaireToCollectionIfMissing', () => {
      it('should add a CarteBeneficiaire to an empty array', () => {
        const carteBeneficiaire: ICarteBeneficiaire = sampleWithRequiredData;
        expectedResult = service.addCarteBeneficiaireToCollectionIfMissing([], carteBeneficiaire);
        expect(expectedResult).toEqual([carteBeneficiaire]);
      });

      it('should not add a CarteBeneficiaire to an array that contains it', () => {
        const carteBeneficiaire: ICarteBeneficiaire = sampleWithRequiredData;
        const carteBeneficiaireCollection: ICarteBeneficiaire[] = [
          {
            ...carteBeneficiaire,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCarteBeneficiaireToCollectionIfMissing(carteBeneficiaireCollection, carteBeneficiaire);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a CarteBeneficiaire to an array that doesn't contain it", () => {
        const carteBeneficiaire: ICarteBeneficiaire = sampleWithRequiredData;
        const carteBeneficiaireCollection: ICarteBeneficiaire[] = [sampleWithPartialData];
        expectedResult = service.addCarteBeneficiaireToCollectionIfMissing(carteBeneficiaireCollection, carteBeneficiaire);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(carteBeneficiaire);
      });

      it('should add only unique CarteBeneficiaire to an array', () => {
        const carteBeneficiaireArray: ICarteBeneficiaire[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const carteBeneficiaireCollection: ICarteBeneficiaire[] = [sampleWithRequiredData];
        expectedResult = service.addCarteBeneficiaireToCollectionIfMissing(carteBeneficiaireCollection, ...carteBeneficiaireArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const carteBeneficiaire: ICarteBeneficiaire = sampleWithRequiredData;
        const carteBeneficiaire2: ICarteBeneficiaire = sampleWithPartialData;
        expectedResult = service.addCarteBeneficiaireToCollectionIfMissing([], carteBeneficiaire, carteBeneficiaire2);
        expect(expectedResult).toEqual([carteBeneficiaire, carteBeneficiaire2]);
      });

      it('should accept null and undefined values', () => {
        const carteBeneficiaire: ICarteBeneficiaire = sampleWithRequiredData;
        expectedResult = service.addCarteBeneficiaireToCollectionIfMissing([], null, carteBeneficiaire, undefined);
        expect(expectedResult).toEqual([carteBeneficiaire]);
      });

      it('should return initial array if no CarteBeneficiaire is added', () => {
        const carteBeneficiaireCollection: ICarteBeneficiaire[] = [sampleWithRequiredData];
        expectedResult = service.addCarteBeneficiaireToCollectionIfMissing(carteBeneficiaireCollection, undefined, null);
        expect(expectedResult).toEqual(carteBeneficiaireCollection);
      });
    });

    describe('compareCarteBeneficiaire', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCarteBeneficiaire(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 194 };
        const entity2 = null;

        const compareResult1 = service.compareCarteBeneficiaire(entity1, entity2);
        const compareResult2 = service.compareCarteBeneficiaire(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 194 };
        const entity2 = { id: 29825 };

        const compareResult1 = service.compareCarteBeneficiaire(entity1, entity2);
        const compareResult2 = service.compareCarteBeneficiaire(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 194 };
        const entity2 = { id: 194 };

        const compareResult1 = service.compareCarteBeneficiaire(entity1, entity2);
        const compareResult2 = service.compareCarteBeneficiaire(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
