import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IFinanceRequest } from '../finance-request.model';
import { FinanceRequestService } from '../service/finance-request.service';

@Injectable({ providedIn: 'root' })
export class FinanceRequestRoutingResolveService implements Resolve<IFinanceRequest | null> {
  constructor(protected service: FinanceRequestService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IFinanceRequest | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((financeRequest: HttpResponse<IFinanceRequest>) => {
          if (financeRequest.body) {
            return of(financeRequest.body);
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
