import { Service, inject } from '@angular/core';

import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { Observable, filter, map } from 'rxjs';

import { CONFIRMED_EVENT, ConfirmDialog, ConfirmTon } from './confirm-dialog';

export interface ConfirmOptions {
  /** Ce qui va se passer, sans point d'exclamation. */
  titre: string;
  /** Ce que l'agent doit savoir avant de trancher, y compris ce qui est irréversible. */
  message: string;
  /** Libellé du bouton de confirmation : il nomme l'action. */
  libelleConfirmer: string;
  /** `danger` pour ce qui détruit ou refuse, `primaire` sinon. */
  ton?: ConfirmTon;
}

/**
 * Ouvre un dialogue de confirmation devant une opération qui engage.
 *
 * L'observable n'émet que si l'agent confirme ; un abandon le complète sans rien émettre,
 * de sorte que l'appelant se contente de `confirmService.confirmer(...).subscribe(() => …)`.
 */
@Service()
export class ConfirmService {
  private readonly modalService = inject(NgbModal);

  confirmer(options: ConfirmOptions): Observable<void> {
    const modalRef = this.modalService.open(ConfirmDialog, { size: 'md', backdrop: 'static' });
    Object.assign(modalRef.componentInstance, { ton: 'primaire', ...options });
    return modalRef.closed.pipe(
      filter(reason => reason === CONFIRMED_EVENT),
      map(() => undefined),
    );
  }

  /** Confirmation d'un enregistrement de formulaire : création ou modification d'une fiche. */
  confirmerEnregistrement(estUneCreation: boolean, quoi: string): Observable<void> {
    return this.confirmer(
      estUneCreation
        ? {
            titre: `Enregistrer ${quoi}`,
            message: 'Les informations saisies vont être enregistrées.',
            libelleConfirmer: 'Enregistrer',
          }
        : {
            titre: 'Enregistrer les modifications',
            message: `Les modifications apportées à ${quoi} vont remplacer les informations actuelles.`,
            libelleConfirmer: 'Enregistrer les modifications',
          },
    );
  }
}
