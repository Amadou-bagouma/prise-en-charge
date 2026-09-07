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
import { LienParente } from 'app/entities/enumerations/lien-parente.model';
import { AlertError } from 'app/shared/alert';
import { TranslateDirective } from 'app/shared/language';
import { IAyantDroit } from '../ayant-droit.model';
import { AyantDroitService } from '../service/ayant-droit.service';

import { AyantDroitFormGroup, AyantDroitFormService } from './ayant-droit-form.service';

@Component({
  selector: 'jhi-ayant-droit-update',
  templateUrl: './ayant-droit-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule, NgbInputDatepicker],
})
export class AyantDroitUpdate implements OnInit {
  readonly isSaving = signal(false);
  ayantDroit: IAyantDroit | null = null;
  lienParenteValues = Object.keys(LienParente);

  agentsSharedCollection = signal<IAgent[]>([]);

  protected ayantDroitService = inject(AyantDroitService);
  protected ayantDroitFormService = inject(AyantDroitFormService);
  protected agentService = inject(AgentService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: AyantDroitFormGroup = this.ayantDroitFormService.createAyantDroitFormGroup();

  compareAgent = (o1: IAgent | null, o2: IAgent | null): boolean => this.agentService.compareAgent(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ayantDroit }) => {
      this.ayantDroit = ayantDroit;
      if (ayantDroit) {
        this.updateForm(ayantDroit);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const ayantDroit = this.ayantDroitFormService.getAyantDroit(this.editForm);
    if (ayantDroit.id === null) {
      this.subscribeToSaveResponse(this.ayantDroitService.create(ayantDroit));
    } else {
      this.subscribeToSaveResponse(this.ayantDroitService.update(ayantDroit));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IAyantDroit | null>): void {
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

  protected updateForm(ayantDroit: IAyantDroit): void {
    this.ayantDroit = ayantDroit;
    this.ayantDroitFormService.resetForm(this.editForm, ayantDroit);

    this.agentsSharedCollection.update(agents => this.agentService.addAgentToCollectionIfMissing<IAgent>(agents, ayantDroit.agent));
  }

  protected loadRelationshipsOptions(): void {
    this.agentService
      .query()
      .pipe(map((res: HttpResponse<IAgent[]>) => res.body ?? []))
      .pipe(map((agents: IAgent[]) => this.agentService.addAgentToCollectionIfMissing<IAgent>(agents, this.ayantDroit?.agent)))
      .subscribe((agents: IAgent[]) => this.agentsSharedCollection.set(agents));
  }
}
