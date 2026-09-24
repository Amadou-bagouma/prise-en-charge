import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { IRegion } from 'app/entities/region/region.model';
import { RegionService } from 'app/entities/region/service/region.service';
import { AlertError } from 'app/shared/alert';
import { ConfirmService } from 'app/shared/confirm';
import { TranslateDirective } from 'app/shared/language';
import { IDirection } from '../direction.model';
import { DirectionService } from '../service/direction.service';

import { DirectionFormGroup, DirectionFormService } from './direction-form.service';

@Component({
  selector: 'jhi-direction-update',
  templateUrl: './direction-update.html',
  imports: [TranslateDirective, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class DirectionUpdate implements OnInit {
  readonly isSaving = signal(false);
  direction: IDirection | null = null;

  regionsSharedCollection = signal<IRegion[]>([]);

  protected directionService = inject(DirectionService);
  protected directionFormService = inject(DirectionFormService);
  protected regionService = inject(RegionService);
  protected activatedRoute = inject(ActivatedRoute);
  protected readonly confirmService = inject(ConfirmService);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: DirectionFormGroup = this.directionFormService.createDirectionFormGroup();

  compareRegion = (o1: IRegion | null, o2: IRegion | null): boolean => this.regionService.compareRegion(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ direction }) => {
      this.direction = direction;
      if (direction) {
        this.updateForm(direction);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    const direction = this.directionFormService.getDirection(this.editForm);
    this.confirmService
      .confirmerEnregistrement(direction.id === null, direction.id === null ? 'une direction' : 'cette direction')
      .subscribe(() => {
        this.isSaving.set(true);
        if (direction.id === null) {
          this.subscribeToSaveResponse(this.directionService.create(direction));
        } else {
          this.subscribeToSaveResponse(this.directionService.update(direction));
        }
      });
  }

  protected subscribeToSaveResponse(result: Observable<IDirection | null>): void {
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

  protected updateForm(direction: IDirection): void {
    this.direction = direction;
    this.directionFormService.resetForm(this.editForm, direction);

    this.regionsSharedCollection.update(regions => this.regionService.addRegionToCollectionIfMissing<IRegion>(regions, direction.region));
  }

  protected loadRelationshipsOptions(): void {
    this.regionService
      .query()
      .pipe(map((res: HttpResponse<IRegion[]>) => res.body ?? []))
      .pipe(map((regions: IRegion[]) => this.regionService.addRegionToCollectionIfMissing<IRegion>(regions, this.direction?.region)))
      .subscribe((regions: IRegion[]) => this.regionsSharedCollection.set(regions));
  }
}
