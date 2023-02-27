import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IFinanceRequest } from '../finance-request.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../finance-request.test-samples';

import { FinanceRequestService } from './finance-request.service';

const requireRestSample: IFinanceRequest = {
  ...sampleWithRequiredData,
};

describe('FinanceRequest Service', () => {
  let service: FinanceRequestService;
  let httpMock: HttpTestingController;
  let expectedResult: IFinanceRequest | IFinanceRequest[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(FinanceRequestService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find('ABC').subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a FinanceRequest', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const financeRequest = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(financeRequest).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a FinanceRequest', () => {
      const financeRequest = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(financeRequest).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a FinanceRequest', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of FinanceRequest', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a FinanceRequest', () => {
      const expected = true;

      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addFinanceRequestToCollectionIfMissing', () => {
      it('should add a FinanceRequest to an empty array', () => {
        const financeRequest: IFinanceRequest = sampleWithRequiredData;
        expectedResult = service.addFinanceRequestToCollectionIfMissing([], financeRequest);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(financeRequest);
      });

      it('should not add a FinanceRequest to an array that contains it', () => {
        const financeRequest: IFinanceRequest = sampleWithRequiredData;
        const financeRequestCollection: IFinanceRequest[] = [
          {
            ...financeRequest,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addFinanceRequestToCollectionIfMissing(financeRequestCollection, financeRequest);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a FinanceRequest to an array that doesn't contain it", () => {
        const financeRequest: IFinanceRequest = sampleWithRequiredData;
        const financeRequestCollection: IFinanceRequest[] = [sampleWithPartialData];
        expectedResult = service.addFinanceRequestToCollectionIfMissing(financeRequestCollection, financeRequest);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(financeRequest);
      });

      it('should add only unique FinanceRequest to an array', () => {
        const financeRequestArray: IFinanceRequest[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const financeRequestCollection: IFinanceRequest[] = [sampleWithRequiredData];
        expectedResult = service.addFinanceRequestToCollectionIfMissing(financeRequestCollection, ...financeRequestArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const financeRequest: IFinanceRequest = sampleWithRequiredData;
        const financeRequest2: IFinanceRequest = sampleWithPartialData;
        expectedResult = service.addFinanceRequestToCollectionIfMissing([], financeRequest, financeRequest2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(financeRequest);
        expect(expectedResult).toContain(financeRequest2);
      });

      it('should accept null and undefined values', () => {
        const financeRequest: IFinanceRequest = sampleWithRequiredData;
        expectedResult = service.addFinanceRequestToCollectionIfMissing([], null, financeRequest, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(financeRequest);
      });

      it('should return initial array if no FinanceRequest is added', () => {
        const financeRequestCollection: IFinanceRequest[] = [sampleWithRequiredData];
        expectedResult = service.addFinanceRequestToCollectionIfMissing(financeRequestCollection, undefined, null);
        expect(expectedResult).toEqual(financeRequestCollection);
      });
    });

    describe('compareFinanceRequest', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareFinanceRequest(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = null;

        const compareResult1 = service.compareFinanceRequest(entity1, entity2);
        const compareResult2 = service.compareFinanceRequest(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = { id: 'CBA' };

        const compareResult1 = service.compareFinanceRequest(entity1, entity2);
        const compareResult2 = service.compareFinanceRequest(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = { id: 'ABC' };

        const compareResult1 = service.compareFinanceRequest(entity1, entity2);
        const compareResult2 = service.compareFinanceRequest(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
