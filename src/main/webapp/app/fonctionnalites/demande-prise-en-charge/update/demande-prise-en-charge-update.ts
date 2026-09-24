import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { IAgent } from 'app/entities/agent/agent.model';
import { AgentService } from 'app/entities/agent/service/agent.service';
import { IAyantDroit } from 'app/entities/ayant-droit/ayant-droit.model';
import { AyantDroitService } from 'app/entities/ayant-droit/service/ayant-droit.service';
import { AccountService } from 'app/core/auth';
import { PrioriteDemande } from 'app/entities/enumerations/priorite-demande.model';
import { TypeBeneficiaire } from 'app/entities/enumerations/type-beneficiaire.model';
import { ITypeSoin } from 'app/fonctionnalites/type-soin/type-soin.model';
import { TypeSoinService } from 'app/fonctionnalites/type-soin/service/type-soin.service';
import { IEtablissementSante } from 'app/entities/etablissement-sante/etablissement-sante.model';
import { EtablissementSanteService } from 'app/entities/etablissement-sante/service/etablissement-sante.service';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { AlertError } from 'app/shared/alert';
import { ConfirmService } from 'app/shared/confirm';
import { TranslateDirective } from 'app/shared/language';
import { IDemandePriseEnCharge } from '../demande-prise-en-charge.model';
import { DemandePriseEnChargeService } from '../service/demande-prise-en-charge.service';

import { DemandePriseEnChargeFormGroup, DemandePriseEnChargeFormService } from './demande-prise-en-charge-form.service';

@Component({
  selector: 'jhi-demande-prise-en-charge-update',
  templateUrl: './demande-prise-en-charge-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class DemandePriseEnChargeUpdate implements OnInit {
  readonly isSaving = signal(false);
  demandePriseEnCharge: IDemandePriseEnCharge | null = null;
  typeBeneficiaireValues = Object.keys(TypeBeneficiaire);
  prioriteDemandeValues = Object.keys(PrioriteDemande);
  /**
   * Les types de soin viennent du referentiel, plus d'une enumeration figee dans le code :
   * l'administration peut en ajouter sans livraison. Seuls les types en service sont proposes.
   */
  readonly typeSoinValues = signal<ITypeSoin[]>([]);

  agentsSharedCollection = signal<IAgent[]>([]);
  ayantDroitsSharedCollection = signal<IAyantDroit[]>([]);
  etablissementSantesSharedCollection = signal<IEtablissementSante[]>([]);
  usersSharedCollection = signal<IUser[]>([]);

  protected demandePriseEnChargeService = inject(DemandePriseEnChargeService);
  protected demandePriseEnChargeFormService = inject(DemandePriseEnChargeFormService);
  protected agentService = inject(AgentService);
  protected ayantDroitService = inject(AyantDroitService);
  protected etablissementSanteService = inject(EtablissementSanteService);
  protected readonly typeSoinService = inject(TypeSoinService);
  protected userService = inject(UserService);
  protected accountService = inject(AccountService);
  protected activatedRoute = inject(ActivatedRoute);
  protected readonly confirmService = inject(ConfirmService);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: DemandePriseEnChargeFormGroup = this.demandePriseEnChargeFormService.createDemandePriseEnChargeFormGroup();

  compareAgent = (o1: IAgent | null, o2: IAgent | null): boolean => this.agentService.compareAgent(o1, o2);

  compareAyantDroit = (o1: IAyantDroit | null, o2: IAyantDroit | null): boolean => this.ayantDroitService.compareAyantDroit(o1, o2);

  compareEtablissementSante = (o1: IEtablissementSante | null, o2: IEtablissementSante | null): boolean =>
    this.etablissementSanteService.compareEtablissementSante(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ demandePriseEnCharge }) => {
      this.demandePriseEnCharge = demandePriseEnCharge;
      if (demandePriseEnCharge) {
        this.updateForm(demandePriseEnCharge);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    const demandePriseEnCharge = this.demandePriseEnChargeFormService.getDemandePriseEnCharge(this.editForm);
    this.confirmService
      .confirmerEnregistrement(
        demandePriseEnCharge.id === null,
        demandePriseEnCharge.id === null ? 'une demande de prise en charge' : 'cette demande',
      )
      .subscribe(() => {
        this.isSaving.set(true);
        if (demandePriseEnCharge.id === null) {
          this.subscribeToSaveResponse(this.demandePriseEnChargeService.create(demandePriseEnCharge));
        } else {
          this.subscribeToSaveResponse(this.demandePriseEnChargeService.update(demandePriseEnCharge));
        }
      });
  }

  protected subscribeToSaveResponse(result: Observable<IDemandePriseEnCharge | null>): void {
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

  protected updateForm(demandePriseEnCharge: IDemandePriseEnCharge): void {
    this.demandePriseEnCharge = demandePriseEnCharge;
    this.demandePriseEnChargeFormService.resetForm(this.editForm, demandePriseEnCharge);

    this.agentsSharedCollection.update(agents =>
      this.agentService.addAgentToCollectionIfMissing<IAgent>(agents, demandePriseEnCharge.agent),
    );
    this.ayantDroitsSharedCollection.update(ayantDroits =>
      this.sortAyantDroitsDescending(
        this.ayantDroitService.addAyantDroitToCollectionIfMissing<IAyantDroit>(ayantDroits, demandePriseEnCharge.ayantDroit),
      ),
    );
    this.etablissementSantesSharedCollection.update(etablissementSantes =>
      this.etablissementSanteService.addEtablissementSanteToCollectionIfMissing<IEtablissementSante>(
        etablissementSantes,
        demandePriseEnCharge.etablissementSante,
      ),
    );
    this.usersSharedCollection.update(users =>
      this.userService.addUserToCollectionIfMissing<IUser>(users, demandePriseEnCharge.gestionnaireCreateur, demandePriseEnCharge.assigneA),
    );
  }

  protected loadRelationshipsOptions(): void {
    // Les types deja portes par la demande sont conserves meme s'ils ont ete retires du
    // service depuis : on n'efface pas en silence ce qu'un dossier a enregistre.
    this.typeSoinService.actifs().subscribe(actifs => {
      const dejaChoisis = this.demandePriseEnCharge?.typeSoins ?? [];
      const manquants = dejaChoisis.filter(choisi => !actifs.some(actif => actif.id === choisi.id));
      this.typeSoinValues.set([...actifs, ...manquants]);
    });

    this.agentService
      .query()
      .pipe(map((res: HttpResponse<IAgent[]>) => res.body ?? []))
      .pipe(map((agents: IAgent[]) => this.agentService.addAgentToCollectionIfMissing<IAgent>(agents, this.demandePriseEnCharge?.agent)))
      .subscribe((agents: IAgent[]) => this.agentsSharedCollection.set(agents));

    this.ayantDroitService
      .query()
      .pipe(map((res: HttpResponse<IAyantDroit[]>) => res.body ?? []))
      .pipe(
        map((ayantDroits: IAyantDroit[]) =>
          this.sortAyantDroitsDescending(
            this.ayantDroitService.addAyantDroitToCollectionIfMissing<IAyantDroit>(ayantDroits, this.demandePriseEnCharge?.ayantDroit),
          ),
        ),
      )
      .subscribe((ayantDroits: IAyantDroit[]) => this.ayantDroitsSharedCollection.set(ayantDroits));

    this.etablissementSanteService
      .query()
      .pipe(map((res: HttpResponse<IEtablissementSante[]>) => res.body ?? []))
      .pipe(
        map((etablissementSantes: IEtablissementSante[]) =>
          this.etablissementSanteService.addEtablissementSanteToCollectionIfMissing<IEtablissementSante>(
            etablissementSantes,
            this.demandePriseEnCharge?.etablissementSante,
          ),
        ),
      )
      .subscribe((etablissementSantes: IEtablissementSante[]) => this.etablissementSantesSharedCollection.set(etablissementSantes));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) =>
          this.userService.addUserToCollectionIfMissing<IUser>(
            users,
            this.demandePriseEnCharge?.gestionnaireCreateur,
            this.demandePriseEnCharge?.assigneA,
          ),
        ),
      )
      .subscribe((users: IUser[]) => {
        this.usersSharedCollection.set(users);
        this.setGestionnaireCreateurFromCurrentUser(users);
      });
  }

  protected setGestionnaireCreateurFromCurrentUser(users: IUser[]): void {
    if (this.demandePriseEnCharge) {
      return;
    }
    const currentLogin = this.accountService.account()?.login;
    const currentUser = users.find(user => user.login === currentLogin);
    if (currentUser) {
      this.editForm.patchValue({ gestionnaireCreateur: currentUser });
    }
  }

  protected sortAyantDroitsDescending(ayantDroits: IAyantDroit[]): IAyantDroit[] {
    return [...ayantDroits].sort((a, b) => `${b.nom ?? ''} ${b.prenom ?? ''}`.localeCompare(`${a.nom ?? ''} ${a.prenom ?? ''}`));
  }

  isTypeSoinChecked(typeSoin: ITypeSoin): boolean {
    return (this.editForm.controls.typeSoins.value ?? []).some(choisi => choisi.id === typeSoin.id);
  }

  toggleTypeSoin(typeSoin: ITypeSoin, checked: boolean): void {
    const current = this.editForm.controls.typeSoins.value ?? [];
    const next = checked ? [...current, typeSoin] : current.filter(choisi => choisi.id !== typeSoin.id);
    this.editForm.controls.typeSoins.setValue(next);
    this.editForm.controls.typeSoins.markAsDirty();
  }
}
