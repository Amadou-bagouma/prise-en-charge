import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IDemandePriseEnCharge } from '../demande-prise-en-charge.model';
import { DemandePriseEnChargeService } from '../service/demande-prise-en-charge.service';

const demandePriseEnChargeResolve = (route: ActivatedRouteSnapshot): Observable<null | IDemandePriseEnCharge> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(DemandePriseEnChargeService);
    return service.find(id).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 404) {
          router.navigate(['404']);
        } else {
          router.navigate(['error']);
        }
        return EMPTY;
      }),
    );
  }

  return of(null);
};

export default demandePriseEnChargeResolve;
