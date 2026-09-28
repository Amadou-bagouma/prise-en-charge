import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config';
import { ITache, NewTache } from '../tache.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITache for edit and NewTacheFormGroupInput for create.
 */
type TacheFormGroupInput = ITache | PartialWithRequiredKeyOf<NewTache>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITache | NewTache> = Omit<T, 'dateCreation' | 'dateAssignation' | 'dateEcheance' | 'dateTerminaison'> & {
  dateCreation?: string | null;
  dateAssignation?: string | null;
  dateEcheance?: string | null;
  dateTerminaison?: string | null;
};

type TacheFormRawValue = FormValueOf<ITache>;

type NewTacheFormRawValue = FormValueOf<NewTache>;

type TacheFormDefaults = Pick<NewTache, 'id' | 'dateCreation' | 'dateAssignation' | 'dateEcheance' | 'dateTerminaison' | 'lu'>;

type TacheFormGroupContent = {
  id: FormControl<TacheFormRawValue['id'] | NewTache['id']>;
  titre: FormControl<TacheFormRawValue['titre']>;
  description: FormControl<TacheFormRawValue['description']>;
  dateCreation: FormControl<TacheFormRawValue['dateCreation']>;
  dateAssignation: FormControl<TacheFormRawValue['dateAssignation']>;
  dateEcheance: FormControl<TacheFormRawValue['dateEcheance']>;
  dateTerminaison: FormControl<TacheFormRawValue['dateTerminaison']>;
  statut: FormControl<TacheFormRawValue['statut']>;
  priorite: FormControl<TacheFormRawValue['priorite']>;
  lu: FormControl<TacheFormRawValue['lu']>;
  commentaire: FormControl<TacheFormRawValue['commentaire']>;
  demande: FormControl<TacheFormRawValue['demande']>;
  utilisateur: FormControl<TacheFormRawValue['utilisateur']>;
  boiteReception: FormControl<TacheFormRawValue['boiteReception']>;
};

export type TacheFormGroup = FormGroup<TacheFormGroupContent>;

@Service()
export class TacheFormService {
  createTacheFormGroup(tache?: TacheFormGroupInput): TacheFormGroup {
    const tacheRawValue = this.convertTacheToTacheRawValue({
      ...this.getFormDefaults(),
      ...(tache ?? { id: null }),
    });

    return new FormGroup<TacheFormGroupContent>({
      id: new FormControl(
        { value: tacheRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      titre: new FormControl(tacheRawValue.titre, {
        validators: [Validators.required],
      }),
      description: new FormControl(tacheRawValue.description),
      dateCreation: new FormControl(tacheRawValue.dateCreation, {
        validators: [Validators.required],
      }),
      dateAssignation: new FormControl(tacheRawValue.dateAssignation),
      dateEcheance: new FormControl(tacheRawValue.dateEcheance),
      dateTerminaison: new FormControl(tacheRawValue.dateTerminaison),
      statut: new FormControl(tacheRawValue.statut, {
        validators: [Validators.required],
      }),
      priorite: new FormControl(tacheRawValue.priorite, {
        validators: [Validators.required],
      }),
      lu: new FormControl(tacheRawValue.lu, {
        validators: [Validators.required],
      }),
      commentaire: new FormControl(tacheRawValue.commentaire),
      demande: new FormControl(tacheRawValue.demande, {
        validators: [Validators.required],
      }),
      utilisateur: new FormControl(tacheRawValue.utilisateur, {
        validators: [Validators.required],
      }),
      boiteReception: new FormControl(tacheRawValue.boiteReception),
    });
  }

  getTache(form: TacheFormGroup): ITache | NewTache {
    return this.convertTacheRawValueToTache(form.getRawValue());
  }

  resetForm(form: TacheFormGroup, tache: TacheFormGroupInput): void {
    const tacheRawValue = this.convertTacheToTacheRawValue({ ...this.getFormDefaults(), ...tache });
    form.reset({
      ...tacheRawValue,
      id: { value: tacheRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): TacheFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateCreation: currentTime,
      dateAssignation: currentTime,
      dateEcheance: currentTime,
      dateTerminaison: currentTime,
      lu: false,
    };
  }

  private convertTacheRawValueToTache(rawTache: TacheFormRawValue | NewTacheFormRawValue): ITache | NewTache {
    return {
      ...rawTache,
      dateCreation: dayjs(rawTache.dateCreation, DATE_TIME_FORMAT),
      dateAssignation: dayjs(rawTache.dateAssignation, DATE_TIME_FORMAT),
      dateEcheance: dayjs(rawTache.dateEcheance, DATE_TIME_FORMAT),
      dateTerminaison: dayjs(rawTache.dateTerminaison, DATE_TIME_FORMAT),
    };
  }

  private convertTacheToTacheRawValue(
    tache: ITache | (Partial<NewTache> & TacheFormDefaults),
  ): TacheFormRawValue | PartialWithRequiredKeyOf<NewTacheFormRawValue> {
    return {
      ...tache,
      dateCreation: tache.dateCreation ? tache.dateCreation.format(DATE_TIME_FORMAT) : undefined,
      dateAssignation: tache.dateAssignation ? tache.dateAssignation.format(DATE_TIME_FORMAT) : undefined,
      dateEcheance: tache.dateEcheance ? tache.dateEcheance.format(DATE_TIME_FORMAT) : undefined,
      dateTerminaison: tache.dateTerminaison ? tache.dateTerminaison.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
