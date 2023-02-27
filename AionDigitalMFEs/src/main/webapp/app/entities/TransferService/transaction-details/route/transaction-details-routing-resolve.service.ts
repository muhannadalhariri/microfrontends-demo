import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITransactionDetails } from '../transaction-details.model';
import { TransactionDetailsService } from '../service/transaction-details.service';

@Injectable({ providedIn: 'root' })
export class TransactionDetailsRoutingResolveService implements Resolve<ITransactionDetails | null> {
  constructor(protected service: TransactionDetailsService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITransactionDetails | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((transactionDetails: HttpResponse<ITransactionDetails>) => {
          if (transactionDetails.body) {
            return of(transactionDetails.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
