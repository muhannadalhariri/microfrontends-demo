import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IFinanceRequest } from '../finance-request.model';

@Component({
  selector: 'jhi-finance-request-detail',
  templateUrl: './finance-request-detail.component.html',
})
export class FinanceRequestDetailComponent implements OnInit {
  financeRequest: IFinanceRequest | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ financeRequest }) => {
      this.financeRequest = financeRequest;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
