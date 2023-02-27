export interface ICard {
  id: string;
  nameAr?: string | null;
  nameEn?: string | null;
  cardTypeId?: number | null;
  cardReference?: string | null;
}

export type NewCard = Omit<ICard, 'id'> & { id: null };
