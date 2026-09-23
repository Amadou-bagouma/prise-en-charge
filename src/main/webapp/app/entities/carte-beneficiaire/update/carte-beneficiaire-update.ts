import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbInputDatepicker } from '@ng-bootstrap/ng-bootstrap/datepicker';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { IAgent } from 'app/entities/agent/agent.model';
import { AgentService } from 'app/entities/agent/service/agent.service';
import { IAyantDroit } from 'app/entities/ayant-droit/ayant-droit.model';
import { AyantDroitService } from 'app/entities/ayant-droit/service/ayant-droit.service';
import { TypeBeneficiaire } from 'app/entities/enumerations/type-beneficiaire.model';
import { AlertError } from 'app/shared/alert';
import { TranslateDirective } from 'app/shared/language';
import { ICarteBeneficiaire } from '../carte-beneficiaire.model';
import { CarteBeneficiaireService } from '../service/carte-beneficiaire.service';

import { CarteBeneficiaireFormGroup, CarteBeneficiaireFormService } from './carte-beneficiaire-form.service';

@Component({
  selector: 'jhi-carte-beneficiaire-update',
  templateUrl: './carte-beneficiaire-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule, NgbInputDatepicker],
})
export class CarteBeneficiaireUpdate implements OnInit {
  readonly isSaving = signal(false);
  carteBeneficiaire: ICarteBeneficiaire | null = null;
  typeBeneficiaireValues = Object.keys(TypeBeneficiaire);

  agentsSharedCollection = signal<IAgent[]>([]);
  ayantDroitsSharedCollection = signal<IAyantDroit[]>([]);

  protected carteBeneficiaireService = inject(CarteBeneficiaireService);
  protected carteBeneficiaireFormService = inject(CarteBeneficiaireFormService);
  protected agentService = inject(AgentService);
  protected ayantDroitService = inject(AyantDroitService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CarteBeneficiaireFormGroup = this.carteBeneficiaireFormService.createCarteBeneficiaireFormGroup();

  compareAgent = (o1: IAgent | null, o2: IAgent | null): boolean => this.agentService.compareAgent(o1, o2);

  compareAyantDroit = (o1: IAyantDroit | null, o2: IAyantDroit | null): boolean => this.ayantDroitService.compareAyantDroit(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ carteBeneficiaire }) => {
      this.carteBeneficiaire = carteBeneficiaire;
      if (carteBeneficiaire) {
        this.updateForm(carteBeneficiaire);
      }

      this.loadRelationshipsOptions();
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
      .query()
      .pipe(map((res: HttpResponse<IAgent[]>) => res.body ?? []))
      .pipe(map((agents: IAgent[]) => this.agentService.addAgentToCollectionIfMissing<IAgent>(agents, this.carteBeneficiaire?.agent)))
      .subscribe((agents: IAgent[]) => this.agentsSharedCollection.set(agents));

    this.ayantDroitService
      .query()
      .pipe(map((res: HttpResponse<IAyantDroit[]>) => res.body ?? []))
      .pipe(
        map((ayantDroits: IAyantDroit[]) =>
          this.ayantDroitService.addAyantDroitToCollectionIfMissing<IAyantDroit>(ayantDroits, this.carteBeneficiaire?.ayantDroit),
        ),
      )
      .subscribe((ayantDroits: IAyantDroit[]) => this.ayantDroitsSharedCollection.set(ayantDroits));
  }
}
