import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IBoiteReception } from '../boite-reception.model';
import { BoiteReceptionService } from '../service/boite-reception.service';

const boiteReceptionResolve = (route: ActivatedRouteSnapshot): Observable<null | IBoiteReception> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(BoiteReceptionService);
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

export default boiteReceptionResolve;
