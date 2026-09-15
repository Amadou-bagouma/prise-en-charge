import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config';
import { StatutDemande } from 'app/entities/enumerations/statut-demande.model';
import { IDemandePriseEnCharge, NewDemandePriseEnCharge } from '../demande-prise-en-charge.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDemandePriseEnCharge for edit and NewDemandePriseEnChargeFormGroupInput for create.
 */
type DemandePriseEnChargeFormGroupInput = IDemandePriseEnCharge | PartialWithRequiredKeyOf<NewDemandePriseEnCharge>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IDemandePriseEnCharge | NewDemandePriseEnCharge> = Omit<
  T,
  'dateCreation' | 'dateModification' | 'dateAssignation' | 'dateEcheance'
> & {
  dateCreation?: string | null;
  dateModification?: string | null;
  dateAssignation?: string | null;
  dateEcheance?: string | null;
};

type DemandePriseEnChargeFormRawValue = FormValueOf<IDemandePriseEnCharge>;

type NewDemandePriseEnChargeFormRawValue = FormValueOf<NewDemandePriseEnCharge>;

type DemandePriseEnChargeFormDefaults = Pick<
  NewDemandePriseEnCharge,
  'id' | 'dateCreation' | 'dateModification' | 'dateAssignation' | 'dateEcheance' | 'statut'
>;

type DemandePriseEnChargeFormGroupContent = {
  id: FormControl<DemandePriseEnChargeFormRawValue['id'] | NewDemandePriseEnCharge['id']>;
  reference: FormControl<DemandePriseEnChargeFormRawValue['reference']>;
  dateCreation: FormControl<DemandePriseEnChargeFormRawValue['dateCreation']>;
  dateModification: FormControl<DemandePriseEnChargeFormRawValue['dateModification']>;
  typeBeneficiaire: FormControl<DemandePriseEnChargeFormRawValue['typeBeneficiaire']>;
  description: FormControl<DemandePriseEnChargeFormRawValue['description']>;
  statut: FormControl<DemandePriseEnChargeFormRawValue['statut']>;
  priorite: FormControl<DemandePriseEnChargeFormRawValue['priorite']>;
  dateAssignation: FormControl<DemandePriseEnChargeFormRawValue['dateAssignation']>;
  dateEcheance: FormControl<DemandePriseEnChargeFormRawValue['dateEcheance']>;
  motifRejet: FormControl<DemandePriseEnChargeFormRawValue['motifRejet']>;
  observation: FormControl<DemandePriseEnChargeFormRawValue['observation']>;
  agent: FormControl<DemandePriseEnChargeFormRawValue['agent']>;
  ayantDroit: FormControl<DemandePriseEnChargeFormRawValue['ayantDroit']>;
  typeSoins: FormControl<DemandePriseEnChargeFormRawValue['typeSoins']>;
  etablissementSante: FormControl<DemandePriseEnChargeFormRawValue['etablissementSante']>;
  gestionnaireCreateur: FormControl<DemandePriseEnChargeFormRawValue['gestionnaireCreateur']>;
  assigneA: FormControl<DemandePriseEnChargeFormRawValue['assigneA']>;
};

export type DemandePriseEnChargeFormGroup = FormGroup<DemandePriseEnChargeFormGroupContent>;

@Service()
export class DemandePriseEnChargeFormService {
  createDemandePriseEnChargeFormGroup(demandePriseEnCharge?: DemandePriseEnChargeFormGroupInput): DemandePriseEnChargeFormGroup {
    const demandePriseEnChargeRawValue = this.convertDemandePriseEnChargeToDemandePriseEnChargeRawValue({
      ...this.getFormDefaults(),
      ...(demandePriseEnCharge ?? { id: null }),
    });

    return new FormGroup<DemandePriseEnChargeFormGroupContent>({
      id: new FormControl(
        { value: demandePriseEnChargeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      reference: new FormControl({ value: demandePriseEnChargeRawValue.reference, disabled: true }),
      dateCreation: new FormControl(demandePriseEnChargeRawValue.dateCreation, {
        validators: [Validators.required],
      }),
      dateModification: new FormControl(demandePriseEnChargeRawValue.dateModification),
      typeBeneficiaire: new FormControl(demandePriseEnChargeRawValue.typeBeneficiaire, {
        validators: [Validators.required],
      }),
      description: new FormControl(demandePriseEnChargeRawValue.description),
      statut: new FormControl(
        { value: demandePriseEnChargeRawValue.statut, disabled: true },
        {
          validators: [Validators.required],
        },
      ),
      priorite: new FormControl(demandePriseEnChargeRawValue.priorite, {
        validators: [Validators.required],
      }),
      dateAssignation: new FormControl(demandePriseEnChargeRawValue.dateAssignation),
      dateEcheance: new FormControl(demandePriseEnChargeRawValue.dateEcheance),
      motifRejet: new FormControl(demandePriseEnChargeRawValue.motifRejet),
      observation: new FormControl(demandePriseEnChargeRawValue.observation),
      agent: new FormControl(demandePriseEnChargeRawValue.agent),
      ayantDroit: new FormControl(demandePriseEnChargeRawValue.ayantDroit),
      typeSoins: new FormControl(demandePriseEnChargeRawValue.typeSoins ?? [], {
        validators: [Validators.required],
      }),
      etablissementSante: new FormControl(demandePriseEnChargeRawValue.etablissementSante),
      gestionnaireCreateur: new FormControl(
        { value: demandePriseEnChargeRawValue.gestionnaireCreateur, disabled: true },
        {
          validators: [Validators.required],
        },
      ),
      assigneA: new FormControl(demandePriseEnChargeRawValue.assigneA),
    });
  }

  getDemandePriseEnCharge(form: DemandePriseEnChargeFormGroup): IDemandePriseEnCharge | NewDemandePriseEnCharge {
    return this.convertDemandePriseEnChargeRawValueToDemandePriseEnCharge(form.getRawValue());
  }

  resetForm(form: DemandePriseEnChargeFormGroup, demandePriseEnCharge: DemandePriseEnChargeFormGroupInput): void {
    const demandePriseEnChargeRawValue = this.convertDemandePriseEnChargeToDemandePriseEnChargeRawValue({
      ...this.getFormDefaults(),
      ...demandePriseEnCharge,
    });
    form.reset({
      ...demandePriseEnChargeRawValue,
      id: { value: demandePriseEnChargeRawValue.id, disabled: true },
      reference: { value: demandePriseEnChargeRawValue.reference, disabled: true },
      statut: { value: demandePriseEnChargeRawValue.statut, disabled: true },
      gestionnaireCreateur: { value: demandePriseEnChargeRawValue.gestionnaireCreateur, disabled: true },
    });
  }

  private getFormDefaults(): DemandePriseEnChargeFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateCreation: currentTime,
      dateModification: currentTime,
      dateAssignation: currentTime,
      dateEcheance: currentTime,
      statut: StatutDemande.NOUVELLE,
    };
  }

  private convertDemandePriseEnChargeRawValueToDemandePriseEnCharge(
    rawDemandePriseEnCharge: DemandePriseEnChargeFormRawValue | NewDemandePriseEnChargeFormRawValue,
  ): IDemandePriseEnCharge | NewDemandePriseEnCharge {
    return {
      ...rawDemandePriseEnCharge,
      dateCreation: dayjs(rawDemandePriseEnCharge.dateCreation, DATE_TIME_FORMAT),
      dateModification: dayjs(rawDemandePriseEnCharge.dateModification, DATE_TIME_FORMAT),
      dateAssignation: dayjs(rawDemandePriseEnCharge.dateAssignation, DATE_TIME_FORMAT),
      dateEcheance: dayjs(rawDemandePriseEnCharge.dateEcheance, DATE_TIME_FORMAT),
    };
  }

  private convertDemandePriseEnChargeToDemandePriseEnChargeRawValue(
    demandePriseEnCharge: IDemandePriseEnCharge | (Partial<NewDemandePriseEnCharge> & DemandePriseEnChargeFormDefaults),
  ): DemandePriseEnChargeFormRawValue | PartialWithRequiredKeyOf<NewDemandePriseEnChargeFormRawValue> {
    return {
      ...demandePriseEnCharge,
      dateCreation: demandePriseEnCharge.dateCreation ? demandePriseEnCharge.dateCreation.format(DATE_TIME_FORMAT) : undefined,
      dateModification: demandePriseEnCharge.dateModification ? demandePriseEnCharge.dateModification.format(DATE_TIME_FORMAT) : undefined,
      dateAssignation: demandePriseEnCharge.dateAssignation ? demandePriseEnCharge.dateAssignation.format(DATE_TIME_FORMAT) : undefined,
      dateEcheance: demandePriseEnCharge.dateEcheance ? demandePriseEnCharge.dateEcheance.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
