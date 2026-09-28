import { HttpClient, HttpResponse } from '@angular/common/http';
import { Service, inject } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { ITache } from 'app/fonctionnalites/tache/tache.model';
import { IBoiteReception } from '../boite-reception.model';

type RestBoiteReception = Omit<IBoiteReception, 'dateCreation' | 'dateDerniereLecture'> & {
  dateCreation?: string | null;
  dateDerniereLecture?: string | null;
};

type RestTache = Omit<ITache, 'dateCreation' | 'dateAssignation' | 'dateEcheance' | 'dateTerminaison'> & {
  dateCreation?: string | null;
  dateAssignation?: string | null;
  dateEcheance?: string | null;
  dateTerminaison?: string | null;
};

/**
 * Accès à la boîte de réception.
 *
 * Aucune méthode de création, de modification ni de suppression : une boîte existe parce qu'un
 * profil existe, et le serveur n'expose aucune route pour en fabriquer une.
 *
 * Aucun identifiant de boîte n'est jamais envoyé : le serveur résout lui-même le profil du compte
 * appelant. C'est ce qui garantit qu'un utilisateur ne peut pas lire la boîte d'un autre rôle en
 * changeant un numéro dans l'URL.
 */
@Service()
export class BoiteReceptionService {
  protected readonly resourceUrl = `${serverApiUrl}api/boite-receptions`;

  private readonly http = inject(HttpClient);

  /** La boîte du profil de l'utilisateur courant, compteurs recalculés. */
  maBoite(): Observable<IBoiteReception> {
    return this.http.get<RestBoiteReception>(`${this.resourceUrl}/mienne`).pipe(map(boite => this.convertirBoite(boite)));
  }

  /** Les tâches que les droits de l'utilisateur lui donnent à traiter. */
  mesTaches(req?: any): Observable<HttpResponse<ITache[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTache[]>(`${this.resourceUrl}/mienne/taches`, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: (res.body ?? []).map(tache => this.convertirTache(tache)) })));
  }

  /** Enregistre que la boîte vient d'être ouverte. */
  marquerConsultee(): Observable<IBoiteReception> {
    return this.http.put<RestBoiteReception>(`${this.resourceUrl}/mienne/consultee`, {}).pipe(map(boite => this.convertirBoite(boite)));
  }

  /** S'attribuer une tâche de sa boîte. */
  prendreEnCharge(id: number): Observable<ITache> {
    return this.http
      .put<RestTache>(`${this.resourceUrl}/mienne/taches/${encodeURIComponent(id)}/prendre`, {})
      .pipe(map(tache => this.convertirTache(tache)));
  }

  /** Rendre une tâche à la file, si l'on ne peut finalement pas la traiter. */
  relacher(id: number): Observable<ITache> {
    return this.http
      .put<RestTache>(`${this.resourceUrl}/mienne/taches/${encodeURIComponent(id)}/relacher`, {})
      .pipe(map(tache => this.convertirTache(tache)));
  }

  /** Marque une tâche de sa propre boîte comme lue. */
  marquerTacheLue(id: number): Observable<ITache> {
    return this.http
      .put<RestTache>(`${this.resourceUrl}/mienne/taches/${encodeURIComponent(id)}/lue`, {})
      .pipe(map(tache => this.convertirTache(tache)));
  }

  private convertirBoite(boite: RestBoiteReception): IBoiteReception {
    return {
      ...boite,
      dateCreation: boite.dateCreation ? dayjs(boite.dateCreation) : undefined,
      dateDerniereLecture: boite.dateDerniereLecture ? dayjs(boite.dateDerniereLecture) : undefined,
    };
  }

  private convertirTache(tache: RestTache): ITache {
    return {
      ...tache,
      dateCreation: tache.dateCreation ? dayjs(tache.dateCreation) : undefined,
      dateAssignation: tache.dateAssignation ? dayjs(tache.dateAssignation) : undefined,
      dateEcheance: tache.dateEcheance ? dayjs(tache.dateEcheance) : undefined,
      dateTerminaison: tache.dateTerminaison ? dayjs(tache.dateTerminaison) : undefined,
    };
  }
}
