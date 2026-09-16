import { HttpResponse } from '@angular/common/http';
import { ChangeDetectorRef, Component, ElementRef, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbInputDatepicker } from '@ng-bootstrap/ng-bootstrap/datepicker';
import { Observable, finalize, map } from 'rxjs';

import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { AlertService } from 'app/core/util/alert.service';
import { IDirection } from 'app/entities/direction/direction.model';
import { DirectionService } from 'app/entities/direction/service/direction.service';
import { IGestion } from 'app/entities/gestion/gestion.model';
import { GestionService } from 'app/entities/gestion/service/gestion.service';
import { AlertError } from 'app/shared/alert';
import { TranslateDirective } from 'app/shared/language';
import { IAgent } from '../agent.model';
import { AgentService } from '../service/agent.service';

import { AgentFormGroup, AgentFormService } from './agent-form.service';

@Component({
  selector: 'jhi-agent-update',
  templateUrl: './agent-update.html',
  imports: [TranslateDirective, FontAwesomeModule, AlertError, ReactiveFormsModule, NgbInputDatepicker],
})
export class AgentUpdate implements OnInit {
  readonly isSaving = signal(false);
  agent: IAgent | null = null;

  directionsSharedCollection = signal<IDirection[]>([]);
  gestionsSharedCollection = signal<IGestion[]>([]);

  protected dataUtils = inject(DataUtils);
  protected alertService = inject(AlertService);
  protected agentService = inject(AgentService);
  protected agentFormService = inject(AgentFormService);
  protected directionService = inject(DirectionService);
  protected gestionService = inject(GestionService);
  protected activatedRoute = inject(ActivatedRoute);
  protected elementRef = inject(ElementRef);
  protected cdr = inject(ChangeDetectorRef);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: AgentFormGroup = this.agentFormService.createAgentFormGroup();

  compareDirection = (o1: IDirection | null, o2: IDirection | null): boolean => this.directionService.compareDirection(o1, o2);

  compareGestion = (o1: IGestion | null, o2: IGestion | null): boolean => this.gestionService.compareGestion(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ agent }) => {
      this.agent = agent;
      if (agent) {
        this.updateForm(agent);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  byteSize(base64String: string | null | undefined): string {
    return base64String ? this.dataUtils.byteSize(base64String) : '';
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      next: () => this.cdr.markForCheck(),
      error: (err: FileLoadError) =>
        this.alertService.addAlert({
          type: 'danger',
          translationKey: `error.file.${err.key}`,
          translationParams: err.params,
        }),
    });
  }

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    const inputElement = this.elementRef.nativeElement.querySelector(`#${idInput}`);
    if (inputElement) {
      inputElement.value = null;
    }
  }

  save(): void {
    this.isSaving.set(true);
    const agent = this.agentFormService.getAgent(this.editForm);
    if (agent.id === null) {
      this.subscribeToSaveResponse(this.agentService.create(agent));
    } else {
      this.subscribeToSaveResponse(this.agentService.update(agent));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IAgent | null>): void {
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

  protected updateForm(agent: IAgent): void {
    this.agent = agent;
    this.agentFormService.resetForm(this.editForm, agent);

    this.directionsSharedCollection.update(directions =>
      this.directionService.addDirectionToCollectionIfMissing<IDirection>(directions, agent.direction),
    );
    this.gestionsSharedCollection.update(gestions =>
      this.gestionService.addGestionToCollectionIfMissing<IGestion>(gestions, agent.gestion),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.directionService
      .query()
      .pipe(map((res: HttpResponse<IDirection[]>) => res.body ?? []))
      .pipe(
        map((directions: IDirection[]) =>
          this.directionService.addDirectionToCollectionIfMissing<IDirection>(directions, this.agent?.direction),
        ),
      )
      .subscribe((directions: IDirection[]) => this.directionsSharedCollection.set(directions));

    this.gestionService
      .query()
      .pipe(map((res: HttpResponse<IGestion[]>) => res.body ?? []))
      .pipe(map((gestions: IGestion[]) => this.gestionService.addGestionToCollectionIfMissing<IGestion>(gestions, this.agent?.gestion)))
      .subscribe((gestions: IGestion[]) => this.gestionsSharedCollection.set(gestions));
  }
}
