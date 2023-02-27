import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { ITransactionDetails, NewTransactionDetails } from '../transaction-details.model';

export type PartialUpdateTransactionDetails = Partial<ITransactionDetails> & Pick<ITransactionDetails, 'id'>;

export type EntityResponseType = HttpResponse<ITransactionDetails>;
export type EntityArrayResponseType = HttpResponse<ITransactionDetails[]>;

@Injectable({ providedIn: 'root' })
export class TransactionDetailsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/transaction-details', 'transferservice');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/transaction-details', 'transferservice');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(transactionDetails: NewTransactionDetails): Observable<EntityResponseType> {
    return this.http.post<ITransactionDetails>(this.resourceUrl, transactionDetails, { observe: 'response' });
  }

  update(transactionDetails: ITransactionDetails): Observable<EntityResponseType> {
    return this.http.put<ITransactionDetails>(
      `${this.resourceUrl}/${this.getTransactionDetailsIdentifier(transactionDetails)}`,
      transactionDetails,
      { observe: 'response' }
    );
  }

  partialUpdate(transactionDetails: PartialUpdateTransactionDetails): Observable<EntityResponseType> {
    return this.http.patch<ITransactionDetails>(
      `${this.resourceUrl}/${this.getTransactionDetailsIdentifier(transactionDetails)}`,
      transactionDetails,
      { observe: 'response' }
    );
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<ITransactionDetails>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITransactionDetails[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITransactionDetails[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getTransactionDetailsIdentifier(transactionDetails: Pick<ITransactionDetails, 'id'>): string {
    return transactionDetails.id;
  }

  compareTransactionDetails(o1: Pick<ITransactionDetails, 'id'> | null, o2: Pick<ITransactionDetails, 'id'> | null): boolean {
    return o1 && o2 ? this.getTransactionDetailsIdentifier(o1) === this.getTransactionDetailsIdentifier(o2) : o1 === o2;
  }

  addTransactionDetailsToCollectionIfMissing<Type extends Pick<ITransactionDetails, 'id'>>(
    transactionDetailsCollection: Type[],
    ...transactionDetailsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const transactionDetails: Type[] = transactionDetailsToCheck.filter(isPresent);
    if (transactionDetails.length > 0) {
      const transactionDetailsCollectionIdentifiers = transactionDetailsCollection.map(
        transactionDetailsItem => this.getTransactionDetailsIdentifier(transactionDetailsItem)!
      );
      const transactionDetailsToAdd = transactionDetails.filter(transactionDetailsItem => {
        const transactionDetailsIdentifier = this.getTransactionDetailsIdentifier(transactionDetailsItem);
        if (transactionDetailsCollectionIdentifiers.includes(transactionDetailsIdentifier)) {
          return false;
        }
        transactionDetailsCollectionIdentifiers.push(transactionDetailsIdentifier);
        return true;
      });
      return [...transactionDetailsToAdd, ...transactionDetailsCollection];
    }
    return transactionDetailsCollection;
  }
}
