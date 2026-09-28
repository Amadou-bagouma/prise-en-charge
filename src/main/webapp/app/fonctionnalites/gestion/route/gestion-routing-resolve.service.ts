import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IGestion } from '../gestion.model';
import { GestionService } from '../service/gestion.service';

const gestionResolve = (route: ActivatedRouteSnapshot): Observable<null | IGestion> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(GestionService);
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

export default gestionResolve;
