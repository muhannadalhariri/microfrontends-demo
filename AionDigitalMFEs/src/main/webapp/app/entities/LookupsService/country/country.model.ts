export interface ICountry {
  id: string;
  nameAr?: string | null;
  nameEn?: string | null;
  code?: string | null;
  currencyCode?: string | null;
}

export type NewCountry = Omit<ICountry, 'id'> & { id: null };
