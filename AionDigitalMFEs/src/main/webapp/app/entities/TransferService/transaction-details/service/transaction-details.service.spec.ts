import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITransactionDetails } from '../transaction-details.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../transaction-details.test-samples';

import { TransactionDetailsService } from './transaction-details.service';

const requireRestSample: ITransactionDetails = {
  ...sampleWithRequiredData,
};

describe('TransactionDetails Service', () => {
  let service: TransactionDetailsService;
  let httpMock: HttpTestingController;
  let expectedResult: ITransactionDetails | ITransactionDetails[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TransactionDetailsService);
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

    it('should create a TransactionDetails', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const transactionDetails = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(transactionDetails).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TransactionDetails', () => {
      const transactionDetails = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(transactionDetails).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TransactionDetails', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TransactionDetails', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TransactionDetails', () => {
      const expected = true;

      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTransactionDetailsToCollectionIfMissing', () => {
      it('should add a TransactionDetails to an empty array', () => {
        const transactionDetails: ITransactionDetails = sampleWithRequiredData;
        expectedResult = service.addTransactionDetailsToCollectionIfMissing([], transactionDetails);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(transactionDetails);
      });

      it('should not add a TransactionDetails to an array that contains it', () => {
        const transactionDetails: ITransactionDetails = sampleWithRequiredData;
        const transactionDetailsCollection: ITransactionDetails[] = [
          {
            ...transactionDetails,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTransactionDetailsToCollectionIfMissing(transactionDetailsCollection, transactionDetails);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TransactionDetails to an array that doesn't contain it", () => {
        const transactionDetails: ITransactionDetails = sampleWithRequiredData;
        const transactionDetailsCollection: ITransactionDetails[] = [sampleWithPartialData];
        expectedResult = service.addTransactionDetailsToCollectionIfMissing(transactionDetailsCollection, transactionDetails);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(transactionDetails);
      });

      it('should add only unique TransactionDetails to an array', () => {
        const transactionDetailsArray: ITransactionDetails[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const transactionDetailsCollection: ITransactionDetails[] = [sampleWithRequiredData];
        expectedResult = service.addTransactionDetailsToCollectionIfMissing(transactionDetailsCollection, ...transactionDetailsArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const transactionDetails: ITransactionDetails = sampleWithRequiredData;
        const transactionDetails2: ITransactionDetails = sampleWithPartialData;
        expectedResult = service.addTransactionDetailsToCollectionIfMissing([], transactionDetails, transactionDetails2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(transactionDetails);
        expect(expectedResult).toContain(transactionDetails2);
      });

      it('should accept null and undefined values', () => {
        const transactionDetails: ITransactionDetails = sampleWithRequiredData;
        expectedResult = service.addTransactionDetailsToCollectionIfMissing([], null, transactionDetails, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(transactionDetails);
      });

      it('should return initial array if no TransactionDetails is added', () => {
        const transactionDetailsCollection: ITransactionDetails[] = [sampleWithRequiredData];
        expectedResult = service.addTransactionDetailsToCollectionIfMissing(transactionDetailsCollection, undefined, null);
        expect(expectedResult).toEqual(transactionDetailsCollection);
      });
    });

    describe('compareTransactionDetails', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTransactionDetails(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = null;

        const compareResult1 = service.compareTransactionDetails(entity1, entity2);
        const compareResult2 = service.compareTransactionDetails(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = { id: 'CBA' };

        const compareResult1 = service.compareTransactionDetails(entity1, entity2);
        const compareResult2 = service.compareTransactionDetails(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = { id: 'ABC' };

        const compareResult1 = service.compareTransactionDetails(entity1, entity2);
        const compareResult2 = service.compareTransactionDetails(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
