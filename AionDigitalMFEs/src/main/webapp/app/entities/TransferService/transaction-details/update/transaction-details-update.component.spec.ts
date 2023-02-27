import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TransactionDetailsFormService } from './transaction-details-form.service';
import { TransactionDetailsService } from '../service/transaction-details.service';
import { ITransactionDetails } from '../transaction-details.model';
import { ITransaction } from 'app/entities/TransferService/transaction/transaction.model';
import { TransactionService } from 'app/entities/TransferService/transaction/service/transaction.service';

import { TransactionDetailsUpdateComponent } from './transaction-details-update.component';

describe('TransactionDetails Management Update Component', () => {
  let comp: TransactionDetailsUpdateComponent;
  let fixture: ComponentFixture<TransactionDetailsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let transactionDetailsFormService: TransactionDetailsFormService;
  let transactionDetailsService: TransactionDetailsService;
  let transactionService: TransactionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TransactionDetailsUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(TransactionDetailsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TransactionDetailsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    transactionDetailsFormService = TestBed.inject(TransactionDetailsFormService);
    transactionDetailsService = TestBed.inject(TransactionDetailsService);
    transactionService = TestBed.inject(TransactionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Transaction query and add missing value', () => {
      const transactionDetails: ITransactionDetails = { id: 'CBA' };
      const transaction: ITransaction = { id: '6d13ff4c-e98a-4b0f-9827-b967df144068' };
      transactionDetails.transaction = transaction;

      const transactionCollection: ITransaction[] = [{ id: '3ccbc3c5-b97a-4ab1-b3e8-23228e4777f1' }];
      jest.spyOn(transactionService, 'query').mockReturnValue(of(new HttpResponse({ body: transactionCollection })));
      const additionalTransactions = [transaction];
      const expectedCollection: ITransaction[] = [...additionalTransactions, ...transactionCollection];
      jest.spyOn(transactionService, 'addTransactionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ transactionDetails });
      comp.ngOnInit();

      expect(transactionService.query).toHaveBeenCalled();
      expect(transactionService.addTransactionToCollectionIfMissing).toHaveBeenCalledWith(
        transactionCollection,
        ...additionalTransactions.map(expect.objectContaining)
      );
      expect(comp.transactionsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const transactionDetails: ITransactionDetails = { id: 'CBA' };
      const transaction: ITransaction = { id: 'f7c499c1-4c20-4967-9021-fe977ca5fba0' };
      transactionDetails.transaction = transaction;

      activatedRoute.data = of({ transactionDetails });
      comp.ngOnInit();

      expect(comp.transactionsSharedCollection).toContain(transaction);
      expect(comp.transactionDetails).toEqual(transactionDetails);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITransactionDetails>>();
      const transactionDetails = { id: 'ABC' };
      jest.spyOn(transactionDetailsFormService, 'getTransactionDetails').mockReturnValue(transactionDetails);
      jest.spyOn(transactionDetailsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ transactionDetails });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: transactionDetails }));
      saveSubject.complete();

      // THEN
      expect(transactionDetailsFormService.getTransactionDetails).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(transactionDetailsService.update).toHaveBeenCalledWith(expect.objectContaining(transactionDetails));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITransactionDetails>>();
      const transactionDetails = { id: 'ABC' };
      jest.spyOn(transactionDetailsFormService, 'getTransactionDetails').mockReturnValue({ id: null });
      jest.spyOn(transactionDetailsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ transactionDetails: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: transactionDetails }));
      saveSubject.complete();

      // THEN
      expect(transactionDetailsFormService.getTransactionDetails).toHaveBeenCalled();
      expect(transactionDetailsService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITransactionDetails>>();
      const transactionDetails = { id: 'ABC' };
      jest.spyOn(transactionDetailsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ transactionDetails });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(transactionDetailsService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTransaction', () => {
      it('Should forward to transactionService', () => {
        const entity = { id: 'ABC' };
        const entity2 = { id: 'CBA' };
        jest.spyOn(transactionService, 'compareTransaction');
        comp.compareTransaction(entity, entity2);
        expect(transactionService.compareTransaction).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
