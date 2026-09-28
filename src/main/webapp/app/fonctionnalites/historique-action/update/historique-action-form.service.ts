import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config';
import { IHistoriqueAction, NewHistoriqueAction } from '../historique-action.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IHistoriqueAction for edit and NewHistoriqueActionFormGroupInput for create.
 */
type HistoriqueActionFormGroupInput = IHistoriqueAction | PartialWithRequiredKeyOf<NewHistoriqueAction>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IHistoriqueAction | NewHistoriqueAction> = Omit<T, 'dateAction'> & {
  dateAction?: string | null;
};

type HistoriqueActionFormRawValue = FormValueOf<IHistoriqueAction>;

type NewHistoriqueActionFormRawValue = FormValueOf<NewHistoriqueAction>;

type HistoriqueActionFormDefaults = Pick<NewHistoriqueAction, 'id' | 'dateAction'>;

type HistoriqueActionFormGroupContent = {
  id: FormControl<HistoriqueActionFormRawValue['id'] | NewHistoriqueAction['id']>;
  action: FormControl<HistoriqueActionFormRawValue['action']>;
  description: FormControl<HistoriqueActionFormRawValue['description']>;
  dateAction: FormControl<HistoriqueActionFormRawValue['dateAction']>;
  demande: FormControl<HistoriqueActionFormRawValue['demande']>;
  utilisateur: FormControl<HistoriqueActionFormRawValue['utilisateur']>;
};

export type HistoriqueActionFormGroup = FormGroup<HistoriqueActionFormGroupContent>;

@Service()
export class HistoriqueActionFormService {
  createHistoriqueActionFormGroup(historiqueAction?: HistoriqueActionFormGroupInput): HistoriqueActionFormGroup {
    const historiqueActionRawValue = this.convertHistoriqueActionToHistoriqueActionRawValue({
      ...this.getFormDefaults(),
      ...(historiqueAction ?? { id: null }),
    });

    return new FormGroup<HistoriqueActionFormGroupContent>({
      id: new FormControl(
        { value: historiqueActionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      action: new FormControl(historiqueActionRawValue.action, {
        validators: [Validators.required],
      }),
      description: new FormControl(historiqueActionRawValue.description),
      dateAction: new FormControl(historiqueActionRawValue.dateAction, {
        validators: [Validators.required],
      }),
      demande: new FormControl(historiqueActionRawValue.demande, {
        validators: [Validators.required],
      }),
      utilisateur: new FormControl(historiqueActionRawValue.utilisateur, {
        validators: [Validators.required],
      }),
    });
  }

  getHistoriqueAction(form: HistoriqueActionFormGroup): IHistoriqueAction | NewHistoriqueAction {
    return this.convertHistoriqueActionRawValueToHistoriqueAction(form.getRawValue());
  }

  resetForm(form: HistoriqueActionFormGroup, historiqueAction: HistoriqueActionFormGroupInput): void {
    const historiqueActionRawValue = this.convertHistoriqueActionToHistoriqueActionRawValue({
      ...this.getFormDefaults(),
      ...historiqueAction,
    });
    form.reset({
      ...historiqueActionRawValue,
      id: { value: historiqueActionRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): HistoriqueActionFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateAction: currentTime,
    };
  }

  private convertHistoriqueActionRawValueToHistoriqueAction(
    rawHistoriqueAction: HistoriqueActionFormRawValue | NewHistoriqueActionFormRawValue,
  ): IHistoriqueAction | NewHistoriqueAction {
    return {
      ...rawHistoriqueAction,
      dateAction: dayjs(rawHistoriqueAction.dateAction, DATE_TIME_FORMAT),
    };
  }

  private convertHistoriqueActionToHistoriqueActionRawValue(
    historiqueAction: IHistoriqueAction | (Partial<NewHistoriqueAction> & HistoriqueActionFormDefaults),
  ): HistoriqueActionFormRawValue | PartialWithRequiredKeyOf<NewHistoriqueActionFormRawValue> {
    return {
      ...historiqueAction,
      dateAction: historiqueAction.dateAction ? historiqueAction.dateAction.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
