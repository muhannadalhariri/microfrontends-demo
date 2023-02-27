import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ICountry, NewCountry } from '../country.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICountry for edit and NewCountryFormGroupInput for create.
 */
type CountryFormGroupInput = ICountry | PartialWithRequiredKeyOf<NewCountry>;

type CountryFormDefaults = Pick<NewCountry, 'id'>;

type CountryFormGroupContent = {
  id: FormControl<ICountry['id'] | NewCountry['id']>;
  nameAr: FormControl<ICountry['nameAr']>;
  nameEn: FormControl<ICountry['nameEn']>;
  code: FormControl<ICountry['code']>;
  currencyCode: FormControl<ICountry['currencyCode']>;
};

export type CountryFormGroup = FormGroup<CountryFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CountryFormService {
  createCountryFormGroup(country: CountryFormGroupInput = { id: null }): CountryFormGroup {
    const countryRawValue = {
      ...this.getFormDefaults(),
      ...country,
    };
    return new FormGroup<CountryFormGroupContent>({
      id: new FormControl(
        { value: countryRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      nameAr: new FormControl(countryRawValue.nameAr, {
        validators: [Validators.required],
      }),
      nameEn: new FormControl(countryRawValue.nameEn, {
        validators: [Validators.required],
      }),
      code: new FormControl(countryRawValue.code, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(2)],
      }),
      currencyCode: new FormControl(countryRawValue.currencyCode, {
        validators: [Validators.required, Validators.minLength(3), Validators.maxLength(3)],
      }),
    });
  }

  getCountry(form: CountryFormGroup): ICountry | NewCountry {
    return form.getRawValue() as ICountry | NewCountry;
  }

  resetForm(form: CountryFormGroup, country: CountryFormGroupInput): void {
    const countryRawValue = { ...this.getFormDefaults(), ...country };
    form.reset(
      {
        ...countryRawValue,
        id: { value: countryRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): CountryFormDefaults {
    return {
      id: null,
    };
  }
}
