import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { FinanceRequestFormService, FinanceRequestFormGroup } from './finance-request-form.service';
import { IFinanceRequest } from '../finance-request.model';
import { FinanceRequestService } from '../service/finance-request.service';

@Component({
  selector: 'jhi-finance-request-update',
  templateUrl: './finance-request-update.component.html',
})
export class FinanceRequestUpdateComponent implements OnInit {
  isSaving = false;
  financeRequest: IFinanceRequest | null = null;

  editForm: FinanceRequestFormGroup = this.financeRequestFormService.createFinanceRequestFormGroup();

  constructor(
    protected financeRequestService: FinanceRequestService,
    protected financeRequestFormService: FinanceRequestFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ financeRequest }) => {
      this.financeRequest = financeRequest;
      if (financeRequest) {
        this.updateForm(financeRequest);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const financeRequest = this.financeRequestFormService.getFinanceRequest(this.editForm);
    if (financeRequest.id !== null) {
      this.subscribeToSaveResponse(this.financeRequestService.update(financeRequest));
    } else {
      this.subscribeToSaveResponse(this.financeRequestService.create(financeRequest));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFinanceRequest>>): void {
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

  protected updateForm(financeRequest: IFinanceRequest): void {
    this.financeRequest = financeRequest;
    this.financeRequestFormService.resetForm(this.editForm, financeRequest);
  }
}
