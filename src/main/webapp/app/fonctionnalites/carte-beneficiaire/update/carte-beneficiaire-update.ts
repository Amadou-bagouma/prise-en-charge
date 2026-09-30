import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbInputDatepicker } from '@ng-bootstrap/ng-bootstrap/datepicker';
import { Observable, finalize, map } from 'rxjs';

import { IAgent } from 'app/fonctionnalites/agent/agent.model';
import { AgentService } from 'app/fonctionnalites/agent/service/agent.service';
import { IAyantDroit } from 'app/fonctionnalites/ayant-droit/ayant-droit.model';
import { AyantDroitService } from 'app/fonctionnalites/ayant-droit/service/ayant-droit.service';
import { AlertError } from 'app/shared/alert';
import { TranslateDirective } from 'app/shared/language';
import { ICarteBeneficiaire } from '../carte-beneficiaire.model';
import { CarteBeneficiaireService } from '../service/carte-beneficiaire.service';

import { CarteBeneficiaireFormGroup, CarteBeneficiaireFormService } from './carte-beneficiaire-form.service';

/** Une seule page, large : chercher un matricule à la page suivante d'un menu ne se devine pas. */
const OPTIONS_PAR_LISTE = 500;

/**
 * Établir une carte, ou corriger celle qui existe.
 *
 * Deux gestes distincts, et l'écran le dit. Établir ne demande que le bénéficiaire : le numéro,
 * la période de validité et la date d'émission sont posés par le serveur. Les faire saisir
 * ouvrait la porte à deux cartes du même numéro et à des périodes antidatées, que rien dans
 * l'application n'aurait relevées — c'est au guichet qu'on l'aurait découvert.
 *
 * Corriger porte sur ce qui reste discutable : les dates. Le numéro et le titulaire d'une carte
 * déjà remise ne se réécrivent pas ; il faut en établir une autre.
 */
@Component({
  selector: 'jhi-carte-beneficiaire-update',
  templateUrl: './carte-beneficiaire-update.html',
  imports: [TranslateDirective, FontAwesomeModule, AlertError, FormsModule, ReactiveFormsModule, NgbInputDatepicker],
})
export class CarteBeneficiaireUpdate implements OnInit {
  readonly isSaving = signal(false);
  carteBeneficiaire: ICarteBeneficiaire | null = null;

  agentsSharedCollection = signal<IAgent[]>([]);
  ayantDroitsSharedCollection = signal<IAyantDroit[]>([]);

  /** Le bénéficiaire choisi à l'établissement : d'abord l'agent, puis l'un de ses ayants droit. */
  readonly agentChoisi = signal<IAgent | null>(null);
  readonly ayantDroitChoisi = signal<IAyantDroit | null>(null);

  protected carteBeneficiaireService = inject(CarteBeneficiaireService);
  protected carteBeneficiaireFormService = inject(CarteBeneficiaireFormService);
  protected agentService = inject(AgentService);
  protected ayantDroitService = inject(AyantDroitService);
  protected activatedRoute = inject(ActivatedRoute);
  protected routeur = inject(Router);

  /** Vrai tant que la carte n'existe pas : l'écran établit au lieu de corriger. */
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly etablissement = computed(() => this.carteBeneficiaire === null || this.carteBeneficiaire.id === undefined);

  /**
   * Les ayants droit de l'agent choisi, et eux seuls.
   *
   * Proposer tous les ayants droit de l'institution ferait établir une carte à l'enfant d'un
   * autre agent sur une simple homonymie.
   */
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly ayantsDroitDeLAgent = signal<IAyantDroit[]>([]);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CarteBeneficiaireFormGroup = this.carteBeneficiaireFormService.createCarteBeneficiaireFormGroup();

  compareAgent = (o1: IAgent | null, o2: IAgent | null): boolean => this.agentService.compareAgent(o1, o2);

  compareAyantDroit = (o1: IAyantDroit | null, o2: IAyantDroit | null): boolean => this.ayantDroitService.compareAyantDroit(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ carteBeneficiaire }) => {
      this.carteBeneficiaire = carteBeneficiaire ?? null;
      if (carteBeneficiaire) {
        this.updateForm(carteBeneficiaire);
      }
      this.loadRelationshipsOptions();
    });
  }

  /** L'agent choisi commande la liste de ses ayants droit, et efface un choix devenu sans objet. */
  choisirAgent(agent: IAgent | null): void {
    this.agentChoisi.set(agent);
    this.ayantDroitChoisi.set(null);
    this.ayantsDroitDeLAgent.set([]);
    if (!agent?.id) {
      return;
    }
    this.ayantDroitService
      .query({ 'agentId.equals': agent.id, size: OPTIONS_PAR_LISTE, sort: ['nom,asc'] })
      .subscribe(reponse => this.ayantsDroitDeLAgent.set(reponse.body ?? []));
  }

  /**
   * Établit la carte, puis l'ouvre pour impression.
   *
   * L'ayant droit l'emporte sur l'agent quand il est renseigné : c'est lui le titulaire, et
   * l'agent n'est là que pour désigner le rattachement.
   */
  etablir(): void {
    const ayantDroit = this.ayantDroitChoisi();
    const agent = this.agentChoisi();
    if (this.isSaving()) {
      return;
    }
    const cible: ['AGENT' | 'AYANT_DROIT', number] | null = ayantDroit?.id
      ? ['AYANT_DROIT', ayantDroit.id]
      : agent?.id
        ? ['AGENT', agent.id]
        : null;
    if (!cible) {
      return;
    }
    this.isSaving.set(true);
    this.carteBeneficiaireService
      .generer(cible[0], cible[1])
      .pipe(finalize(() => this.isSaving.set(false)))
      .subscribe({
        next: carte => void this.routeur.navigate(['/carte-beneficiaire', carte.id, 'print']),
        error: () => {
          /* le refus — une carte valide existe déjà — est affiché par jhi-alert-error */
        },
      });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const carteBeneficiaire = this.carteBeneficiaireFormService.getCarteBeneficiaire(this.editForm);
    if (carteBeneficiaire.id === null) {
      this.subscribeToSaveResponse(this.carteBeneficiaireService.create(carteBeneficiaire));
    } else {
      this.subscribeToSaveResponse(this.carteBeneficiaireService.update(carteBeneficiaire));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ICarteBeneficiaire | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(carteBeneficiaire: ICarteBeneficiaire): void {
    this.carteBeneficiaire = carteBeneficiaire;
    this.carteBeneficiaireFormService.resetForm(this.editForm, carteBeneficiaire);

    this.agentsSharedCollection.update(agents => this.agentService.addAgentToCollectionIfMissing<IAgent>(agents, carteBeneficiaire.agent));
    this.ayantDroitsSharedCollection.update(ayantDroits =>
      this.ayantDroitService.addAyantDroitToCollectionIfMissing<IAyantDroit>(ayantDroits, carteBeneficiaire.ayantDroit),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.agentService
      .query({ size: OPTIONS_PAR_LISTE, sort: ['matricule,asc'] })
      .pipe(map((res: HttpResponse<IAgent[]>) => res.body ?? []))
      .pipe(map((agents: IAgent[]) => this.agentService.addAgentToCollectionIfMissing<IAgent>(agents, this.carteBeneficiaire?.agent)))
      .subscribe((agents: IAgent[]) => this.agentsSharedCollection.set(agents));
  }
}
