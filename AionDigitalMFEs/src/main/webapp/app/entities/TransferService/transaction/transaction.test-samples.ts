import { ITransaction, NewTransaction } from './transaction.model';

export const sampleWithRequiredData: ITransaction = {
  id: 'b2cbc86d-b488-4107-be94-b13ad4e4d04a',
  referenceId: 'Bike',
  userId: 'circuit',
};

export const sampleWithPartialData: ITransaction = {
  id: 'd68c95c1-3db0-4ebe-aa13-b5216b964d78',
  referenceId: 'action-items SMS XML',
  userId: 'Savings Tennessee Shirt',
};

export const sampleWithFullData: ITransaction = {
  id: '9a1e4888-b37a-4ad4-9f6f-f4c050c91d9c',
  referenceId: 'invoice connecting',
  userId: 'Frozen Keyboard',
};

export const sampleWithNewData: NewTransaction = {
  referenceId: 'Industrial',
  userId: 'deposit utilize Shore',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
