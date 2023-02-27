import { ITransaction } from 'app/entities/TransferService/transaction/transaction.model';

export interface ITransactionDetails {
  id: string;
  key?: string | null;
  value?: string | null;
  transaction?: Pick<ITransaction, 'id'> | null;
}

export type NewTransactionDetails = Omit<ITransactionDetails, 'id'> & { id: null };
