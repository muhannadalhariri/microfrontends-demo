import { ITransactionDetails, NewTransactionDetails } from './transaction-details.model';

export const sampleWithRequiredData: ITransactionDetails = {
  id: '1de9ede6-1e3e-4e4b-92b9-d6f15f75ff06',
};

export const sampleWithPartialData: ITransactionDetails = {
  id: 'de0e9519-4f96-4150-a327-6da434fab8ef',
};

export const sampleWithFullData: ITransactionDetails = {
  id: '9fc5e4bd-7562-4620-b742-b70950272b3d',
  key: 'Rupiah Borders Gloves',
  value: 'Sleek',
};

export const sampleWithNewData: NewTransactionDetails = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
