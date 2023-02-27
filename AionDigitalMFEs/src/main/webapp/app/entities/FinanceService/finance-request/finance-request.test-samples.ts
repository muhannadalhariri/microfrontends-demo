import { IFinanceRequest, NewFinanceRequest } from './finance-request.model';

export const sampleWithRequiredData: IFinanceRequest = {
  id: '2dbbf910-5da1-4b2f-8b13-57d88b559056',
  userId: 'incentivize',
  totalAmount: 92891,
  installmentAmount: 63960,
  installmentPeriod: 59021,
};

export const sampleWithPartialData: IFinanceRequest = {
  id: '45f2d27a-a3bb-4c1f-86af-604043feccb1',
  userId: 'Paradigm',
  totalAmount: 21026,
  installmentAmount: 35632,
  installmentPeriod: 14267,
};

export const sampleWithFullData: IFinanceRequest = {
  id: '5231ae99-0c86-4e29-9de6-eb34a328000a',
  userId: 'Future-proofed methodology invoice',
  totalAmount: 5125,
  installmentAmount: 88245,
  installmentPeriod: 33730,
};

export const sampleWithNewData: NewFinanceRequest = {
  userId: 'Principal orchid Loan',
  totalAmount: 62575,
  installmentAmount: 16333,
  installmentPeriod: 11319,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
