import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IPieceJustificative } from '../piece-justificative.model';
import { PieceJustificativeService } from '../service/piece-justificative.service';

const pieceJustificativeResolve = (route: ActivatedRouteSnapshot): Observable<null | IPieceJustificative> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(PieceJustificativeService);
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

export default pieceJustificativeResolve;
