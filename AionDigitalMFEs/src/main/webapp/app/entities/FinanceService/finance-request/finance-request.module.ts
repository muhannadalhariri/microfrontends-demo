import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { FinanceRequestComponent } from './list/finance-request.component';
import { FinanceRequestDetailComponent } from './detail/finance-request-detail.component';
import { FinanceRequestUpdateComponent } from './update/finance-request-update.component';
import { FinanceRequestDeleteDialogComponent } from './delete/finance-request-delete-dialog.component';
import { FinanceRequestRoutingModule } from './route/finance-request-routing.module';

@NgModule({
  imports: [SharedModule, FinanceRequestRoutingModule],
  declarations: [
    FinanceRequestComponent,
    FinanceRequestDetailComponent,
    FinanceRequestUpdateComponent,
    FinanceRequestDeleteDialogComponent,
  ],
})
export class FinanceServiceFinanceRequestModule {}
