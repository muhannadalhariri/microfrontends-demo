import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITransactionDetails } from '../transaction-details.model';
import { TransactionDetailsService } from '../service/transaction-details.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './transaction-details-delete-dialog.component.html',
})
export class TransactionDetailsDeleteDialogComponent {
  transactionDetails?: ITransactionDetails;

  constructor(protected transactionDetailsService: TransactionDetailsService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.transactionDetailsService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
