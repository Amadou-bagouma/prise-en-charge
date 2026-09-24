import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { IDemandePriseEnCharge } from 'app/entities/demande-prise-en-charge/demande-prise-en-charge.model';
import { DemandePriseEnChargeService } from 'app/entities/demande-prise-en-charge/service/demande-prise-en-charge.service';
import { AlertError } from 'app/shared/alert';
import { ConfirmService } from 'app/shared/confirm';
import { TranslateDirective } from 'app/shared/language';
import { IPieceJustificative } from '../piece-justificative.model';
import { PieceJustificativeService } from '../service/piece-justificative.service';

import { PieceJustificativeFormGroup, PieceJustificativeFormService } from './piece-justificative-form.service';

@Component({
  selector: 'jhi-piece-justificative-update',
  templateUrl: './piece-justificative-update.html',
  imports: [TranslateDirective, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class PieceJustificativeUpdate implements OnInit {
  readonly isSaving = signal(false);
  pieceJustificative: IPieceJustificative | null = null;

  demandePriseEnChargesSharedCollection = signal<IDemandePriseEnCharge[]>([]);

  protected pieceJustificativeService = inject(PieceJustificativeService);
  protected pieceJustificativeFormService = inject(PieceJustificativeFormService);
  protected demandePriseEnChargeService = inject(DemandePriseEnChargeService);
  protected activatedRoute = inject(ActivatedRoute);
  protected readonly confirmService = inject(ConfirmService);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PieceJustificativeFormGroup = this.pieceJustificativeFormService.createPieceJustificativeFormGroup();

  compareDemandePriseEnCharge = (o1: IDemandePriseEnCharge | null, o2: IDemandePriseEnCharge | null): boolean =>
    this.demandePriseEnChargeService.compareDemandePriseEnCharge(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ pieceJustificative }) => {
      this.pieceJustificative = pieceJustificative;
      if (pieceJustificative) {
        this.updateForm(pieceJustificative);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    const pieceJustificative = this.pieceJustificativeFormService.getPieceJustificative(this.editForm);
    this.confirmService
      .confirmerEnregistrement(pieceJustificative.id === null, pieceJustificative.id === null ? 'une pièce justificative' : 'cette pièce')
      .subscribe(() => {
        this.isSaving.set(true);
        if (pieceJustificative.id === null) {
          this.subscribeToSaveResponse(this.pieceJustificativeService.create(pieceJustificative));
        } else {
          this.subscribeToSaveResponse(this.pieceJustificativeService.update(pieceJustificative));
        }
      });
  }

  protected subscribeToSaveResponse(result: Observable<IPieceJustificative | null>): void {
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

  protected updateForm(pieceJustificative: IPieceJustificative): void {
    this.pieceJustificative = pieceJustificative;
    this.pieceJustificativeFormService.resetForm(this.editForm, pieceJustificative);

    this.demandePriseEnChargesSharedCollection.update(demandePriseEnCharges =>
      this.demandePriseEnChargeService.addDemandePriseEnChargeToCollectionIfMissing<IDemandePriseEnCharge>(
        demandePriseEnCharges,
        pieceJustificative.demande,
      ),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.demandePriseEnChargeService
      .query()
      .pipe(map((res: HttpResponse<IDemandePriseEnCharge[]>) => res.body ?? []))
      .pipe(
        map((demandePriseEnCharges: IDemandePriseEnCharge[]) =>
          this.demandePriseEnChargeService.addDemandePriseEnChargeToCollectionIfMissing<IDemandePriseEnCharge>(
            demandePriseEnCharges,
            this.pieceJustificative?.demande,
          ),
        ),
      )
      .subscribe((demandePriseEnCharges: IDemandePriseEnCharge[]) => {
        this.demandePriseEnChargesSharedCollection.set(demandePriseEnCharges);
        this.preselectionnerDemande();
      });
  }

  /**
   * Saisie lancée depuis un dossier : la demande est déjà connue, on ne la redemande pas.
   *
   * La demande est relue sur le serveur plutôt que cherchée dans la liste déroulante : celle-ci
   * ne contient que la première page, et le dossier d'où l'on vient peut très bien ne pas y
   * être. Le rattachement reste modifiable, mais n'est posé que sur une création, pour ne
   * jamais réécrire en silence celui d'une pièce existante.
   */
  private preselectionnerDemande(): void {
    const demandeDemandee = this.activatedRoute.snapshot.queryParamMap.get('demande');
    if (this.pieceJustificative?.id || !demandeDemandee) {
      return;
    }
    this.demandePriseEnChargeService.find(Number(demandeDemandee)).subscribe(demande => {
      if (!demande) {
        return;
      }
      this.demandePriseEnChargesSharedCollection.update(demandes =>
        this.demandePriseEnChargeService.addDemandePriseEnChargeToCollectionIfMissing<IDemandePriseEnCharge>(demandes, demande),
      );
      this.editForm.patchValue({ demande });
    });
  }
}
