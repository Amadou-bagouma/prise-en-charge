import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config';
import { IBoiteReception, NewBoiteReception } from '../boite-reception.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBoiteReception for edit and NewBoiteReceptionFormGroupInput for create.
 */
type BoiteReceptionFormGroupInput = IBoiteReception | PartialWithRequiredKeyOf<NewBoiteReception>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IBoiteReception | NewBoiteReception> = Omit<T, 'dateCreation' | 'dateDerniereLecture'> & {
  dateCreation?: string | null;
  dateDerniereLecture?: string | null;
};

type BoiteReceptionFormRawValue = FormValueOf<IBoiteReception>;

type NewBoiteReceptionFormRawValue = FormValueOf<NewBoiteReception>;

type BoiteReceptionFormDefaults = Pick<NewBoiteReception, 'id' | 'dateCreation' | 'dateDerniereLecture' | 'actif'>;

type BoiteReceptionFormGroupContent = {
  id: FormControl<BoiteReceptionFormRawValue['id'] | NewBoiteReception['id']>;
  dateCreation: FormControl<BoiteReceptionFormRawValue['dateCreation']>;
  dateDerniereLecture: FormControl<BoiteReceptionFormRawValue['dateDerniereLecture']>;
  nombreNonLus: FormControl<BoiteReceptionFormRawValue['nombreNonLus']>;
  actif: FormControl<BoiteReceptionFormRawValue['actif']>;
  utilisateur: FormControl<BoiteReceptionFormRawValue['utilisateur']>;
};

export type BoiteReceptionFormGroup = FormGroup<BoiteReceptionFormGroupContent>;

@Service()
export class BoiteReceptionFormService {
  createBoiteReceptionFormGroup(boiteReception?: BoiteReceptionFormGroupInput): BoiteReceptionFormGroup {
    const boiteReceptionRawValue = this.convertBoiteReceptionToBoiteReceptionRawValue({
      ...this.getFormDefaults(),
      ...(boiteReception ?? { id: null }),
    });

    return new FormGroup<BoiteReceptionFormGroupContent>({
      id: new FormControl(
        { value: boiteReceptionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      dateCreation: new FormControl(boiteReceptionRawValue.dateCreation, {
        validators: [Validators.required],
      }),
      dateDerniereLecture: new FormControl(boiteReceptionRawValue.dateDerniereLecture),
      nombreNonLus: new FormControl(boiteReceptionRawValue.nombreNonLus, {
        validators: [Validators.required],
      }),
      actif: new FormControl(boiteReceptionRawValue.actif, {
        validators: [Validators.required],
      }),
      utilisateur: new FormControl(boiteReceptionRawValue.utilisateur, {
        validators: [Validators.required],
      }),
    });
  }

  getBoiteReception(form: BoiteReceptionFormGroup): IBoiteReception | NewBoiteReception {
    return this.convertBoiteReceptionRawValueToBoiteReception(form.getRawValue());
  }

  resetForm(form: BoiteReceptionFormGroup, boiteReception: BoiteReceptionFormGroupInput): void {
    const boiteReceptionRawValue = this.convertBoiteReceptionToBoiteReceptionRawValue({ ...this.getFormDefaults(), ...boiteReception });
    form.reset({
      ...boiteReceptionRawValue,
      id: { value: boiteReceptionRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): BoiteReceptionFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateCreation: currentTime,
      dateDerniereLecture: currentTime,
      actif: false,
    };
  }

  private convertBoiteReceptionRawValueToBoiteReception(
    rawBoiteReception: BoiteReceptionFormRawValue | NewBoiteReceptionFormRawValue,
  ): IBoiteReception | NewBoiteReception {
    return {
      ...rawBoiteReception,
      dateCreation: dayjs(rawBoiteReception.dateCreation, DATE_TIME_FORMAT),
      dateDerniereLecture: dayjs(rawBoiteReception.dateDerniereLecture, DATE_TIME_FORMAT),
    };
  }

  private convertBoiteReceptionToBoiteReceptionRawValue(
    boiteReception: IBoiteReception | (Partial<NewBoiteReception> & BoiteReceptionFormDefaults),
  ): BoiteReceptionFormRawValue | PartialWithRequiredKeyOf<NewBoiteReceptionFormRawValue> {
    return {
      ...boiteReception,
      dateCreation: boiteReception.dateCreation ? boiteReception.dateCreation.format(DATE_TIME_FORMAT) : undefined,
      dateDerniereLecture: boiteReception.dateDerniereLecture ? boiteReception.dateDerniereLecture.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
