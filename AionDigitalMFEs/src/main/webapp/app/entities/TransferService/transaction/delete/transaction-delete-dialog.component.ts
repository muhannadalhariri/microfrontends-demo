import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITransaction } from '../transaction.model';
import { TransactionService } from '../service/transaction.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './transaction-delete-dialog.component.html',
})
export class TransactionDeleteDialogComponent {
  transaction?: ITransaction;

  constructor(protected transactionService: TransactionService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.transactionService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
