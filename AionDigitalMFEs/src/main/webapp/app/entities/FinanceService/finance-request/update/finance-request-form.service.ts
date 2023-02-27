import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IFinanceRequest, NewFinanceRequest } from '../finance-request.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IFinanceRequest for edit and NewFinanceRequestFormGroupInput for create.
 */
type FinanceRequestFormGroupInput = IFinanceRequest | PartialWithRequiredKeyOf<NewFinanceRequest>;

type FinanceRequestFormDefaults = Pick<NewFinanceRequest, 'id'>;

type FinanceRequestFormGroupContent = {
  id: FormControl<IFinanceRequest['id'] | NewFinanceRequest['id']>;
  userId: FormControl<IFinanceRequest['userId']>;
  totalAmount: FormControl<IFinanceRequest['totalAmount']>;
  installmentAmount: FormControl<IFinanceRequest['installmentAmount']>;
  installmentPeriod: FormControl<IFinanceRequest['installmentPeriod']>;
};

export type FinanceRequestFormGroup = FormGroup<FinanceRequestFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class FinanceRequestFormService {
  createFinanceRequestFormGroup(financeRequest: FinanceRequestFormGroupInput = { id: null }): FinanceRequestFormGroup {
    const financeRequestRawValue = {
      ...this.getFormDefaults(),
      ...financeRequest,
    };
    return new FormGroup<FinanceRequestFormGroupContent>({
      id: new FormControl(
        { value: financeRequestRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      userId: new FormControl(financeRequestRawValue.userId, {
        validators: [Validators.required],
      }),
      totalAmount: new FormControl(financeRequestRawValue.totalAmount, {
        validators: [Validators.required],
      }),
      installmentAmount: new FormControl(financeRequestRawValue.installmentAmount, {
        validators: [Validators.required],
      }),
      installmentPeriod: new FormControl(financeRequestRawValue.installmentPeriod, {
        validators: [Validators.required],
      }),
    });
  }

  getFinanceRequest(form: FinanceRequestFormGroup): IFinanceRequest | NewFinanceRequest {
    return form.getRawValue() as IFinanceRequest | NewFinanceRequest;
  }

  resetForm(form: FinanceRequestFormGroup, financeRequest: FinanceRequestFormGroupInput): void {
    const financeRequestRawValue = { ...this.getFormDefaults(), ...financeRequest };
    form.reset(
      {
        ...financeRequestRawValue,
        id: { value: financeRequestRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): FinanceRequestFormDefaults {
    return {
      id: null,
    };
  }
}
