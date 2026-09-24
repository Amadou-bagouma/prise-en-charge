import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { INotification } from '../notification.model';
import { NotificationService } from '../service/notification.service';

const notificationResolve = (route: ActivatedRouteSnapshot): Observable<null | INotification> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(NotificationService);
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

export default notificationResolve;
