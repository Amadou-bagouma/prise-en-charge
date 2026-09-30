import { HttpClient } from '@angular/common/http';
import { Service, inject } from '@angular/core';

import { Observable } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { IParametre } from '../parametre.model';

/**
 * Les réglages de l'application.
 *
 * Ni création ni suppression : le référentiel est posé au démarrage, et le programme interroge
 * des codes qu'il connaît. En ajouter un à la main donnerait un réglage que personne ne lit ;
 * en retirer un ferait retomber le code sur une valeur que personne n'a choisie.
 */
@Service()
export class ParametreService {
  protected readonly resourceUrl = `${serverApiUrl}api/parametres`;
  private readonly http = inject(HttpClient);

  query(): Observable<IParametre[]> {
    return this.http.get<IParametre[]>(this.resourceUrl);
  }

  update(parametre: IParametre): Observable<IParametre> {
    return this.http.put<IParametre>(`${this.resourceUrl}/${parametre.id}`, parametre);
  }

  /**
   * L'adresse de l'image d'un réglage.
   *
   * Servie à part et non dans la liste : une signature pèse quelques dizaines de kilo-octets, et
   * les rapatrier toutes pour afficher un tableau n'aurait pas de sens.
   */
  urlImage(code: string): string {
    return `${this.resourceUrl}/${encodeURIComponent(code)}/image`;
  }
}
