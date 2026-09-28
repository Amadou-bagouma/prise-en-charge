import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { ITypeSoin } from '../type-soin.model';
import { TypeSoinService } from '../service/type-soin.service';

const typeSoinResolve = (route: ActivatedRouteSnapshot): Observable<null | ITypeSoin> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(TypeSoinService);
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

export default typeSoinResolve;
