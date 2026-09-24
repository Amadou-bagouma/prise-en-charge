import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IHistoriqueAction } from '../historique-action.model';
import { HistoriqueActionService } from '../service/historique-action.service';

const historiqueActionResolve = (route: ActivatedRouteSnapshot): Observable<null | IHistoriqueAction> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(HistoriqueActionService);
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

export default historiqueActionResolve;
