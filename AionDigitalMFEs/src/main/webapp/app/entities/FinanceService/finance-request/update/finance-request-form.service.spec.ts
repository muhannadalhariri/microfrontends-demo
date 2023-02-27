import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../finance-request.test-samples';

import { FinanceRequestFormService } from './finance-request-form.service';

describe('FinanceRequest Form Service', () => {
  let service: FinanceRequestFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FinanceRequestFormService);
  });

  describe('Service methods', () => {
    describe('createFinanceRequestFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createFinanceRequestFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            userId: expect.any(Object),
            totalAmount: expect.any(Object),
            installmentAmount: expect.any(Object),
            installmentPeriod: expect.any(Object),
          })
        );
      });

      it('passing IFinanceRequest should create a new form with FormGroup', () => {
        const formGroup = service.createFinanceRequestFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            userId: expect.any(Object),
            totalAmount: expect.any(Object),
            installmentAmount: expect.any(Object),
            installmentPeriod: expect.any(Object),
          })
        );
      });
    });

    describe('getFinanceRequest', () => {
      it('should return NewFinanceRequest for default FinanceRequest initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createFinanceRequestFormGroup(sampleWithNewData);

        const financeRequest = service.getFinanceRequest(formGroup) as any;

        expect(financeRequest).toMatchObject(sampleWithNewData);
      });

      it('should return NewFinanceRequest for empty FinanceRequest initial value', () => {
        const formGroup = service.createFinanceRequestFormGroup();

        const financeRequest = service.getFinanceRequest(formGroup) as any;

        expect(financeRequest).toMatchObject({});
      });

      it('should return IFinanceRequest', () => {
        const formGroup = service.createFinanceRequestFormGroup(sampleWithRequiredData);

        const financeRequest = service.getFinanceRequest(formGroup) as any;

        expect(financeRequest).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IFinanceRequest should not enable id FormControl', () => {
        const formGroup = service.createFinanceRequestFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewFinanceRequest should disable id FormControl', () => {
        const formGroup = service.createFinanceRequestFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
