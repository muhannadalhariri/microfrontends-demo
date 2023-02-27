import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IFinanceRequest, NewFinanceRequest } from '../finance-request.model';

export type PartialUpdateFinanceRequest = Partial<IFinanceRequest> & Pick<IFinanceRequest, 'id'>;

export type EntityResponseType = HttpResponse<IFinanceRequest>;
export type EntityArrayResponseType = HttpResponse<IFinanceRequest[]>;

@Injectable({ providedIn: 'root' })
export class FinanceRequestService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/finance-requests', 'financeservice');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/finance-requests', 'financeservice');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(financeRequest: NewFinanceRequest): Observable<EntityResponseType> {
    return this.http.post<IFinanceRequest>(this.resourceUrl, financeRequest, { observe: 'response' });
  }

  update(financeRequest: IFinanceRequest): Observable<EntityResponseType> {
    return this.http.put<IFinanceRequest>(`${this.resourceUrl}/${this.getFinanceRequestIdentifier(financeRequest)}`, financeRequest, {
      observe: 'response',
    });
  }

  partialUpdate(financeRequest: PartialUpdateFinanceRequest): Observable<EntityResponseType> {
    return this.http.patch<IFinanceRequest>(`${this.resourceUrl}/${this.getFinanceRequestIdentifier(financeRequest)}`, financeRequest, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IFinanceRequest>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IFinanceRequest[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IFinanceRequest[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getFinanceRequestIdentifier(financeRequest: Pick<IFinanceRequest, 'id'>): string {
    return financeRequest.id;
  }

  compareFinanceRequest(o1: Pick<IFinanceRequest, 'id'> | null, o2: Pick<IFinanceRequest, 'id'> | null): boolean {
    return o1 && o2 ? this.getFinanceRequestIdentifier(o1) === this.getFinanceRequestIdentifier(o2) : o1 === o2;
  }

  addFinanceRequestToCollectionIfMissing<Type extends Pick<IFinanceRequest, 'id'>>(
    financeRequestCollection: Type[],
    ...financeRequestsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const financeRequests: Type[] = financeRequestsToCheck.filter(isPresent);
    if (financeRequests.length > 0) {
      const financeRequestCollectionIdentifiers = financeRequestCollection.map(
        financeRequestItem => this.getFinanceRequestIdentifier(financeRequestItem)!
      );
      const financeRequestsToAdd = financeRequests.filter(financeRequestItem => {
        const financeRequestIdentifier = this.getFinanceRequestIdentifier(financeRequestItem);
        if (financeRequestCollectionIdentifiers.includes(financeRequestIdentifier)) {
          return false;
        }
        financeRequestCollectionIdentifiers.push(financeRequestIdentifier);
        return true;
      });
      return [...financeRequestsToAdd, ...financeRequestCollection];
    }
    return financeRequestCollection;
  }
}
