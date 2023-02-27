import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ITransactionDetails, NewTransactionDetails } from '../transaction-details.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITransactionDetails for edit and NewTransactionDetailsFormGroupInput for create.
 */
type TransactionDetailsFormGroupInput = ITransactionDetails | PartialWithRequiredKeyOf<NewTransactionDetails>;

type TransactionDetailsFormDefaults = Pick<NewTransactionDetails, 'id'>;

type TransactionDetailsFormGroupContent = {
  id: FormControl<ITransactionDetails['id'] | NewTransactionDetails['id']>;
  key: FormControl<ITransactionDetails['key']>;
  value: FormControl<ITransactionDetails['value']>;
  transaction: FormControl<ITransactionDetails['transaction']>;
};

export type TransactionDetailsFormGroup = FormGroup<TransactionDetailsFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TransactionDetailsFormService {
  createTransactionDetailsFormGroup(transactionDetails: TransactionDetailsFormGroupInput = { id: null }): TransactionDetailsFormGroup {
    const transactionDetailsRawValue = {
      ...this.getFormDefaults(),
      ...transactionDetails,
    };
    return new FormGroup<TransactionDetailsFormGroupContent>({
      id: new FormControl(
        { value: transactionDetailsRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      key: new FormControl(transactionDetailsRawValue.key),
      value: new FormControl(transactionDetailsRawValue.value),
      transaction: new FormControl(transactionDetailsRawValue.transaction),
    });
  }

  getTransactionDetails(form: TransactionDetailsFormGroup): ITransactionDetails | NewTransactionDetails {
    return form.getRawValue() as ITransactionDetails | NewTransactionDetails;
  }

  resetForm(form: TransactionDetailsFormGroup, transactionDetails: TransactionDetailsFormGroupInput): void {
    const transactionDetailsRawValue = { ...this.getFormDefaults(), ...transactionDetails };
    form.reset(
      {
        ...transactionDetailsRawValue,
        id: { value: transactionDetailsRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): TransactionDetailsFormDefaults {
    return {
      id: null,
    };
  }
}
