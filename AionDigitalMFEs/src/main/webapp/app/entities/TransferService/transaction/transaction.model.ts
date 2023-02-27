export interface ITransaction {
  id: string;
  referenceId?: string | null;
  userId?: string | null;
}

export type NewTransaction = Omit<ITransaction, 'id'> & { id: null };
