import { ICity, NewCity } from './city.model';

export const sampleWithRequiredData: ICity = {
  id: 'c8d08c97-401b-48da-9e89-291ff58e5207',
  nameAr: 'Fresh Course embrace',
  nameEn: 'Associate',
};

export const sampleWithPartialData: ICity = {
  id: '38ba101b-1b32-40eb-8db1-1e977f56ac0a',
  nameAr: 'yellow Health',
  nameEn: 'Re-engineered Rupiah compressing',
};

export const sampleWithFullData: ICity = {
  id: '1cf3fe49-76c3-4b04-8d9a-9c54950b0cfb',
  nameAr: 'navigate',
  nameEn: 'Gloves back-end',
};

export const sampleWithNewData: NewCity = {
  nameAr: 'Plains',
  nameEn: 'hack Tuna XML',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
