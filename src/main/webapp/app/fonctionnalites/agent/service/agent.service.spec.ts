import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { DATE_FORMAT } from 'app/config';
import { IAgent } from '../agent.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../agent.test-samples';

import { AgentService, RestAgent } from './agent.service';

const requireRestSample: RestAgent = {
  ...sampleWithRequiredData,
  dateNaissance: sampleWithRequiredData.dateNaissance?.format(DATE_FORMAT),
};

describe('Agent Service', () => {
  let service: AgentService;
  let httpMock: HttpTestingController;
  let expectedResult: IAgent | IAgent[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(AgentService);
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

    it('should create a Agent', () => {
      const agent = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(agent).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Agent', () => {
      const agent = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(agent).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Agent', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Agent', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Agent', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addAgentToCollectionIfMissing', () => {
      it('should add a Agent to an empty array', () => {
        const agent: IAgent = sampleWithRequiredData;
        expectedResult = service.addAgentToCollectionIfMissing([], agent);
        expect(expectedResult).toEqual([agent]);
      });

      it('should not add a Agent to an array that contains it', () => {
        const agent: IAgent = sampleWithRequiredData;
        const agentCollection: IAgent[] = [
          {
            ...agent,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addAgentToCollectionIfMissing(agentCollection, agent);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Agent to an array that doesn't contain it", () => {
        const agent: IAgent = sampleWithRequiredData;
        const agentCollection: IAgent[] = [sampleWithPartialData];
        expectedResult = service.addAgentToCollectionIfMissing(agentCollection, agent);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(agent);
      });

      it('should add only unique Agent to an array', () => {
        const agentArray: IAgent[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const agentCollection: IAgent[] = [sampleWithRequiredData];
        expectedResult = service.addAgentToCollectionIfMissing(agentCollection, ...agentArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const agent: IAgent = sampleWithRequiredData;
        const agent2: IAgent = sampleWithPartialData;
        expectedResult = service.addAgentToCollectionIfMissing([], agent, agent2);
        expect(expectedResult).toEqual([agent, agent2]);
      });

      it('should accept null and undefined values', () => {
        const agent: IAgent = sampleWithRequiredData;
        expectedResult = service.addAgentToCollectionIfMissing([], null, agent, undefined);
        expect(expectedResult).toEqual([agent]);
      });

      it('should return initial array if no Agent is added', () => {
        const agentCollection: IAgent[] = [sampleWithRequiredData];
        expectedResult = service.addAgentToCollectionIfMissing(agentCollection, undefined, null);
        expect(expectedResult).toEqual(agentCollection);
      });
    });

    describe('compareAgent', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareAgent(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 25235 };
        const entity2 = null;

        const compareResult1 = service.compareAgent(entity1, entity2);
        const compareResult2 = service.compareAgent(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 25235 };
        const entity2 = { id: 18913 };

        const compareResult1 = service.compareAgent(entity1, entity2);
        const compareResult2 = service.compareAgent(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 25235 };
        const entity2 = { id: 25235 };

        const compareResult1 = service.compareAgent(entity1, entity2);
        const compareResult2 = service.compareAgent(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
