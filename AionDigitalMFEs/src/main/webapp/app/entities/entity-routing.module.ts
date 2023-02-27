import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'transaction',
        data: { pageTitle: 'aionDigitalMfEsApp.transferServiceTransaction.home.title' },
        loadChildren: () => import('./TransferService/transaction/transaction.module').then(m => m.TransferServiceTransactionModule),
      },
      {
        path: 'city',
        data: { pageTitle: 'aionDigitalMfEsApp.lookupsServiceCity.home.title' },
        loadChildren: () => import('./LookupsService/city/city.module').then(m => m.LookupsServiceCityModule),
      },
      {
        path: 'finance-request',
        data: { pageTitle: 'aionDigitalMfEsApp.financeServiceFinanceRequest.home.title' },
        loadChildren: () =>
          import('./FinanceService/finance-request/finance-request.module').then(m => m.FinanceServiceFinanceRequestModule),
      },
      {
        path: 'card',
        data: { pageTitle: 'aionDigitalMfEsApp.lookupsServiceCard.home.title' },
        loadChildren: () => import('./LookupsService/card/card.module').then(m => m.LookupsServiceCardModule),
      },
      {
        path: 'transaction-details',
        data: { pageTitle: 'aionDigitalMfEsApp.transferServiceTransactionDetails.home.title' },
        loadChildren: () =>
          import('./TransferService/transaction-details/transaction-details.module').then(m => m.TransferServiceTransactionDetailsModule),
      },
      {
        path: 'country',
        data: { pageTitle: 'aionDigitalMfEsApp.lookupsServiceCountry.home.title' },
        loadChildren: () => import('./LookupsService/country/country.module').then(m => m.LookupsServiceCountryModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
