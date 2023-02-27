import { ICountry, NewCountry } from './country.model';

export const sampleWithRequiredData: ICountry = {
  id: '0ddd0fdb-a1f3-42a8-b66c-b2136483a227',
  nameAr: 'Ringgit',
  nameEn: 'Program panel',
  code: 'Op',
  currencyCode: 'INR',
};

export const sampleWithPartialData: ICountry = {
  id: '5979a218-b2f1-455e-a5b1-b44bc20e8759',
  nameAr: 'streamline',
  nameEn: 'Pizza invoice green',
  code: 'Cr',
  currencyCode: 'HKD',
};

export const sampleWithFullData: ICountry = {
  id: '3bbeb23c-2c89-4871-96d7-ba029d8b0955',
  nameAr: 'AGP withdrawal',
  nameEn: 'payment Tasty ivory',
  code: 'pa',
  currencyCode: 'XOF',
};

export const sampleWithNewData: NewCountry = {
  nameAr: 'transmit',
  nameEn: 'Integrated azure',
  code: 'gr',
  currencyCode: 'MOP',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
