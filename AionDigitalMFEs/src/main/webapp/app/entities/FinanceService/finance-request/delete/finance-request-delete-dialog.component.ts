import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IFinanceRequest } from '../finance-request.model';
import { FinanceRequestService } from '../service/finance-request.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './finance-request-delete-dialog.component.html',
})
export class FinanceRequestDeleteDialogComponent {
  financeRequest?: IFinanceRequest;

  constructor(protected financeRequestService: FinanceRequestService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.financeRequestService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
