import { ICountry } from 'app/entities/LookupsService/country/country.model';

export interface ICity {
  id: string;
  nameAr?: string | null;
  nameEn?: string | null;
  country?: Pick<ICountry, 'id'> | null;
}

export type NewCity = Omit<ICity, 'id'> & { id: null };
