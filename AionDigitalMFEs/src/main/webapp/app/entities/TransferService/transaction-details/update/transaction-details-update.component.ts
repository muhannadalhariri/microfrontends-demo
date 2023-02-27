import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { TransactionDetailsFormService, TransactionDetailsFormGroup } from './transaction-details-form.service';
import { ITransactionDetails } from '../transaction-details.model';
import { TransactionDetailsService } from '../service/transaction-details.service';
import { ITransaction } from 'app/entities/TransferService/transaction/transaction.model';
import { TransactionService } from 'app/entities/TransferService/transaction/service/transaction.service';

@Component({
  selector: 'jhi-transaction-details-update',
  templateUrl: './transaction-details-update.component.html',
})
export class TransactionDetailsUpdateComponent implements OnInit {
  isSaving = false;
  transactionDetails: ITransactionDetails | null = null;

  transactionsSharedCollection: ITransaction[] = [];

  editForm: TransactionDetailsFormGroup = this.transactionDetailsFormService.createTransactionDetailsFormGroup();

  constructor(
    protected transactionDetailsService: TransactionDetailsService,
    protected transactionDetailsFormService: TransactionDetailsFormService,
    protected transactionService: TransactionService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareTransaction = (o1: ITransaction | null, o2: ITransaction | null): boolean => this.transactionService.compareTransaction(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ transactionDetails }) => {
      this.transactionDetails = transactionDetails;
      if (transactionDetails) {
        this.updateForm(transactionDetails);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const transactionDetails = this.transactionDetailsFormService.getTransactionDetails(this.editForm);
    if (transactionDetails.id !== null) {
      this.subscribeToSaveResponse(this.transactionDetailsService.update(transactionDetails));
    } else {
      this.subscribeToSaveResponse(this.transactionDetailsService.create(transactionDetails));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITransactionDetails>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(transactionDetails: ITransactionDetails): void {
    this.transactionDetails = transactionDetails;
    this.transactionDetailsFormService.resetForm(this.editForm, transactionDetails);

    this.transactionsSharedCollection = this.transactionService.addTransactionToCollectionIfMissing<ITransaction>(
      this.transactionsSharedCollection,
      transactionDetails.transaction
    );
  }

  protected loadRelationshipsOptions(): void {
    this.transactionService
      .query()
      .pipe(map((res: HttpResponse<ITransaction[]>) => res.body ?? []))
      .pipe(
        map((transactions: ITransaction[]) =>
          this.transactionService.addTransactionToCollectionIfMissing<ITransaction>(transactions, this.transactionDetails?.transaction)
        )
      )
      .subscribe((transactions: ITransaction[]) => (this.transactionsSharedCollection = transactions));
  }
}
