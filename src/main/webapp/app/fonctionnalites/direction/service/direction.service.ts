import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { IDirection, NewDirection } from '../direction.model';

export type PartialUpdateDirection = Partial<IDirection> & Pick<IDirection, 'id'>;

@Service()
export class DirectionsService {
  readonly directionsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly directionsResource = httpResource<IDirection[]>(() => {
    const params = this.directionsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of direction that have been fetched. It is updated when the directionsResource emits a new value.
   * In case of error while fetching the directions, the signal is set to an empty array.
   */
  readonly directions = computed(() => (this.directionsResource.hasValue() ? this.directionsResource.value() : []));
  protected readonly resourceUrl = `${serverApiUrl}api/directions`;
}

@Service()
export class DirectionService extends DirectionsService {
  protected readonly http = inject(HttpClient);

  create(direction: NewDirection): Observable<IDirection> {
    return this.http.post<IDirection>(this.resourceUrl, direction);
  }

  update(direction: IDirection): Observable<IDirection> {
    return this.http.put<IDirection>(`${this.resourceUrl}/${encodeURIComponent(this.getDirectionIdentifier(direction))}`, direction);
  }

  partialUpdate(direction: PartialUpdateDirection): Observable<IDirection> {
    return this.http.patch<IDirection>(`${this.resourceUrl}/${encodeURIComponent(this.getDirectionIdentifier(direction))}`, direction);
  }

  find(id: number): Observable<IDirection> {
    return this.http.get<IDirection>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IDirection[]>> {
    const options = createRequestOption(req);
    return this.http.get<IDirection[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getDirectionIdentifier(direction: Pick<IDirection, 'id'>): number {
    return direction.id;
  }

  compareDirection(o1: Pick<IDirection, 'id'> | null, o2: Pick<IDirection, 'id'> | null): boolean {
    return o1 && o2 ? this.getDirectionIdentifier(o1) === this.getDirectionIdentifier(o2) : o1 === o2;
  }

  addDirectionToCollectionIfMissing<Type extends Pick<IDirection, 'id'>>(
    directionCollection: Type[],
    ...directionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const directions: Type[] = directionsToCheck.filter(directionItem => directionItem !== null && directionItem !== undefined);
    if (directions.length > 0) {
      const directionCollectionIdentifiers = directionCollection.map(directionItem => this.getDirectionIdentifier(directionItem));
      const directionsToAdd = directions.filter(directionItem => {
        const directionIdentifier = this.getDirectionIdentifier(directionItem);
        if (directionCollectionIdentifiers.includes(directionIdentifier)) {
          return false;
        }
        directionCollectionIdentifiers.push(directionIdentifier);
        return true;
      });
      return [...directionsToAdd, ...directionCollection];
    }
    return directionCollection;
  }
}
