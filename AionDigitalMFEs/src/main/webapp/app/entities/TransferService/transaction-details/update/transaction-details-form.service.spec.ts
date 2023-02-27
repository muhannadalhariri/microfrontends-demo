import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../transaction-details.test-samples';

import { TransactionDetailsFormService } from './transaction-details-form.service';

describe('TransactionDetails Form Service', () => {
  let service: TransactionDetailsFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TransactionDetailsFormService);
  });

  describe('Service methods', () => {
    describe('createTransactionDetailsFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTransactionDetailsFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            key: expect.any(Object),
            value: expect.any(Object),
            transaction: expect.any(Object),
          })
        );
      });

      it('passing ITransactionDetails should create a new form with FormGroup', () => {
        const formGroup = service.createTransactionDetailsFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            key: expect.any(Object),
            value: expect.any(Object),
            transaction: expect.any(Object),
          })
        );
      });
    });

    describe('getTransactionDetails', () => {
      it('should return NewTransactionDetails for default TransactionDetails initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createTransactionDetailsFormGroup(sampleWithNewData);

        const transactionDetails = service.getTransactionDetails(formGroup) as any;

        expect(transactionDetails).toMatchObject(sampleWithNewData);
      });

      it('should return NewTransactionDetails for empty TransactionDetails initial value', () => {
        const formGroup = service.createTransactionDetailsFormGroup();

        const transactionDetails = service.getTransactionDetails(formGroup) as any;

        expect(transactionDetails).toMatchObject({});
      });

      it('should return ITransactionDetails', () => {
        const formGroup = service.createTransactionDetailsFormGroup(sampleWithRequiredData);

        const transactionDetails = service.getTransactionDetails(formGroup) as any;

        expect(transactionDetails).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITransactionDetails should not enable id FormControl', () => {
        const formGroup = service.createTransactionDetailsFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTransactionDetails should disable id FormControl', () => {
        const formGroup = service.createTransactionDetailsFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
