import { Service, signal } from '@angular/core';

const CLE_RAIL = 'sidebarRail';

/**
 * État de la coquille partagé entre la navigation et la page.
 *
 * En mode « rail », la navigation se réduit aux icônes (72 px) plutôt que de disparaître :
 * l'agent garde ses repères de position, conformément à la fiche CadreApplication. Le choix
 * est conservé d'une session à l'autre.
 */
@Service()
export class LayoutService {
  readonly rail = signal(this.lireChoix());

  basculerRail(): void {
    this.rail.update(actif => {
      const suivant = !actif;
      try {
        localStorage.setItem(CLE_RAIL, String(suivant));
      } catch {
        // Stockage indisponible (navigation privée, cookies bloqués) : le choix ne survit
        // pas à la session, ce n'est pas bloquant.
      }
      return suivant;
    });
  }

  private lireChoix(): boolean {
    try {
      return localStorage.getItem(CLE_RAIL) === 'true';
    } catch {
      return false;
    }
  }
}
