import { ICard, NewCard } from './card.model';

export const sampleWithRequiredData: ICard = {
  id: '98eccc21-db3e-4bf8-8299-551a6a819701',
};

export const sampleWithPartialData: ICard = {
  id: '01a5deea-343b-43fd-834d-1be493a74c7c',
  nameAr: 'backing Refined grow',
  nameEn: 'holistic Loop Grocery',
  cardTypeId: 67464,
  cardReference: 'Chair iterate',
};

export const sampleWithFullData: ICard = {
  id: '0a3161b1-1465-4599-8d9e-d7adb5042022',
  nameAr: 'Licensed',
  nameEn: 'frame auxiliary Up-sized',
  cardTypeId: 40728,
  cardReference: 'withdrawal Gorgeous brand',
};

export const sampleWithNewData: NewCard = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
