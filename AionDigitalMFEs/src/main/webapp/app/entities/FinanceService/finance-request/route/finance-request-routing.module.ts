import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { FinanceRequestComponent } from '../list/finance-request.component';
import { FinanceRequestDetailComponent } from '../detail/finance-request-detail.component';
import { FinanceRequestUpdateComponent } from '../update/finance-request-update.component';
import { FinanceRequestRoutingResolveService } from './finance-request-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const financeRequestRoute: Routes = [
  {
    path: '',
    component: FinanceRequestComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: FinanceRequestDetailComponent,
    resolve: {
      financeRequest: FinanceRequestRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: FinanceRequestUpdateComponent,
    resolve: {
      financeRequest: FinanceRequestRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: FinanceRequestUpdateComponent,
    resolve: {
      financeRequest: FinanceRequestRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(financeRequestRoute)],
  exports: [RouterModule],
})
export class FinanceRequestRoutingModule {}
