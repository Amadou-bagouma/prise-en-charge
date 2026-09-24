import { TestBed } from '@angular/core/testing';

import { Observable, of } from 'rxjs';

import { ConfirmOptions, ConfirmService } from './confirm.service';

/**
 * Dans les tests, la confirmation répond « oui » tout de suite.
 *
 * Les composants ouvrent un vrai dialogue devant chaque enregistrement ou suppression ; sans
 * cela, `comp.save()` ne ferait rien tant que personne ne clique, et les specs générés — qui
 * appellent `save()` puis vérifient l'appel au service — échoueraient sur un détail d'interface
 * plutôt que sur ce qu'ils testent.
 *
 * Le dialogue lui-même est vérifié par ses propres specs et par les captures d'écran.
 */
export class ConfirmServiceAutoTest {
  /** Les options reçues, pour les specs qui veulent vérifier ce qui a été demandé à l'agent. */
  readonly demandes: ConfirmOptions[] = [];

  confirmer(options: ConfirmOptions): Observable<void> {
    this.demandes.push(options);
    return of(undefined);
  }

  confirmerEnregistrement(estUneCreation: boolean, quoi: string): Observable<void> {
    return this.confirmer({
      titre: estUneCreation ? `Enregistrer ${quoi}` : 'Enregistrer les modifications',
      message: '',
      libelleConfirmer: 'Enregistrer',
    });
  }
}

/** Remplace le service de confirmation par sa version qui confirme d'office. */
export function autoConfirmer(): void {
  TestBed.overrideProvider(ConfirmService, { useValue: new ConfirmServiceAutoTest() });
}
