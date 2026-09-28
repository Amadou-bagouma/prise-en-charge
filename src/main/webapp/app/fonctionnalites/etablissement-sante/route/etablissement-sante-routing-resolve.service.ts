import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IEtablissementSante } from '../etablissement-sante.model';
import { EtablissementSanteService } from '../service/etablissement-sante.service';

const etablissementSanteResolve = (route: ActivatedRouteSnapshot): Observable<null | IEtablissementSante> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(EtablissementSanteService);
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

export default etablissementSanteResolve;
