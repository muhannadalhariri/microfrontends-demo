export interface IFinanceRequest {
  id: string;
  userId?: string | null;
  totalAmount?: number | null;
  installmentAmount?: number | null;
  installmentPeriod?: number | null;
}

export type NewFinanceRequest = Omit<IFinanceRequest, 'id'> & { id: null };
