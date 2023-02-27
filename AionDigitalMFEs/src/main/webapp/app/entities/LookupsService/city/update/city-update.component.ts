import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { CityFormService, CityFormGroup } from './city-form.service';
import { ICity } from '../city.model';
import { CityService } from '../service/city.service';
import { ICountry } from 'app/entities/LookupsService/country/country.model';
import { CountryService } from 'app/entities/LookupsService/country/service/country.service';

@Component({
  selector: 'jhi-city-update',
  templateUrl: './city-update.component.html',
})
export class CityUpdateComponent implements OnInit {
  isSaving = false;
  city: ICity | null = null;

  countriesSharedCollection: ICountry[] = [];

  editForm: CityFormGroup = this.cityFormService.createCityFormGroup();

  constructor(
    protected cityService: CityService,
    protected cityFormService: CityFormService,
    protected countryService: CountryService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareCountry = (o1: ICountry | null, o2: ICountry | null): boolean => this.countryService.compareCountry(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ city }) => {
      this.city = city;
      if (city) {
        this.updateForm(city);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const city = this.cityFormService.getCity(this.editForm);
    if (city.id !== null) {
      this.subscribeToSaveResponse(this.cityService.update(city));
    } else {
      this.subscribeToSaveResponse(this.cityService.create(city));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICity>>): void {
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

  protected updateForm(city: ICity): void {
    this.city = city;
    this.cityFormService.resetForm(this.editForm, city);

    this.countriesSharedCollection = this.countryService.addCountryToCollectionIfMissing<ICountry>(
      this.countriesSharedCollection,
      city.country
    );
  }

  protected loadRelationshipsOptions(): void {
    this.countryService
      .query()
      .pipe(map((res: HttpResponse<ICountry[]>) => res.body ?? []))
      .pipe(map((countries: ICountry[]) => this.countryService.addCountryToCollectionIfMissing<ICountry>(countries, this.city?.country)))
      .subscribe((countries: ICountry[]) => (this.countriesSharedCollection = countries));
  }
}
