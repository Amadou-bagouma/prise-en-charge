import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { ICarteBeneficiaire } from '../carte-beneficiaire.model';
import { CarteBeneficiaireService } from '../service/carte-beneficiaire.service';

const carteBeneficiaireResolve = (route: ActivatedRouteSnapshot): Observable<null | ICarteBeneficiaire> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(CarteBeneficiaireService);
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

export default carteBeneficiaireResolve;
