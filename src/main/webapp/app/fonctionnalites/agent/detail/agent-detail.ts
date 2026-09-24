import { Component, computed, effect, inject, input, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import dayjs from 'dayjs/esm';
import { catchError, filter, of, tap } from 'rxjs';

import { ITEM_DELETED_EVENT } from 'app/config';
import { DataUtils } from 'app/core/util/data-util.service';
import { AyantDroitDeleteDialog } from 'app/fonctionnalites/ayant-droit/delete/ayant-droit-delete-dialog';
import { IAyantDroit } from 'app/fonctionnalites/ayant-droit/ayant-droit.model';
import { AyantDroitService } from 'app/fonctionnalites/ayant-droit/service/ayant-droit.service';
import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { IAgent } from '../agent.model';

/**
 * Nombre d'années révolues depuis une date, ou `null` si la date manque.
 *
 * Une date future rendrait un nombre négatif, qui ne veut rien dire sur un dossier : on
 * préfère ne rien afficher plutôt qu'afficher une absurdité.
 */
function anneesRevolues(date?: dayjs.Dayjs | null): number | null {
  if (!date) {
    return null;
  }
  const annees = dayjs().diff(date, 'year');
  return annees >= 0 ? annees : null;
}

@Component({
  selector: 'jhi-agent-detail',
  templateUrl: './agent-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, RouterLink, FormatMediumDatePipe],
})
export class AgentDetail {
  readonly agent = input<IAgent | null>(null);

  /**
   * Les ayants droit de l'agent, à sa fiche.
   *
   * Un agent et ses ayants droit se consultent ensemble : c'est de là que part une demande de
   * prise en charge, et c'est là qu'on vérifie qui est couvert. Les faire chercher dans une
   * liste générale, filtre à la main, revient à faire deux fois le même travail.
   */
  readonly ayantsDroit = signal<IAyantDroit[]>([]);
  readonly chargementAyantsDroit = signal(false);

  readonly identite = computed(() => {
    const agent = this.agent();
    if (!agent) {
      return '';
    }
    return [agent.prenom, agent.nom].filter(Boolean).join(' ') || (agent.matricule ?? '');
  });

  /** Initiales pour la pastille d'identité, quand l'agent n'a pas de photo. */
  readonly initiales = computed(() => {
    const agent = this.agent();
    if (!agent) {
      return '';
    }
    const prenom = agent.prenom?.trim().charAt(0) ?? '';
    const nom = agent.nom?.trim().charAt(0) ?? '';
    return (prenom + nom || agent.matricule?.slice(0, 2) || '').toUpperCase();
  });

  /**
   * Âge de l'agent, en années révolues.
   *
   * Calculé plutôt qu'affiché brut : sur un dossier de protection sociale, c'est l'âge qui
   * décide, pas la date. La laisser seule oblige l'agent à compter de tête.
   */
  readonly age = computed(() => anneesRevolues(this.agent()?.dateNaissance));

  /** Ancienneté dans l'institution, même raisonnement que pour l'âge. */
  readonly anciennete = computed(() => anneesRevolues(this.agent()?.dateEmbauche));

  protected dataUtils = inject(DataUtils);
  protected readonly ayantDroitService = inject(AyantDroitService);
  protected readonly modalService = inject(NgbModal);
  protected readonly router = inject(Router);

  constructor() {
    effect(() => this.chargerAyantsDroit(this.agent()?.id));
  }

  previousState(): void {
    globalThis.history.back();
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  /** Ouvre la saisie d'un ayant droit avec l'agent déjà rattaché. */
  ajouterAyantDroit(): void {
    const agentId = this.agent()?.id;
    if (agentId) {
      this.router.navigate(['/ayant-droit', 'new'], { queryParams: { agent: agentId } });
    }
  }

  supprimerAyantDroit(ayantDroit: IAyantDroit): void {
    const modalRef = this.modalService.open(AyantDroitDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.ayantDroit = ayantDroit;
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => this.chargerAyantsDroit(this.agent()?.id)),
      )
      .subscribe();
  }

  /** Le rattachement se lit sur le serveur : `agentId.equals` existe déjà sur les critères. */
  private chargerAyantsDroit(agentId?: number): void {
    if (!agentId) {
      this.ayantsDroit.set([]);
      return;
    }
    this.chargementAyantsDroit.set(true);
    this.ayantDroitService
      .query({ 'agentId.equals': agentId, size: 50, sort: ['nom,asc', 'prenom,asc'] })
      .pipe(catchError(() => of(null)))
      .subscribe(reponse => {
        this.ayantsDroit.set(reponse?.body ?? []);
        this.chargementAyantsDroit.set(false);
      });
  }
}
