import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IFonction, NewFonction } from '../fonction.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IFonction for edit and NewFonctionFormGroupInput for create.
 */
type FonctionFormGroupInput = IFonction | PartialWithRequiredKeyOf<NewFonction>;

type FonctionFormDefaults = Pick<NewFonction, 'id'>;

type FonctionFormGroupContent = {
  id: FormControl<IFonction['id'] | NewFonction['id']>;
  libelle: FormControl<IFonction['libelle']>;
};

export type FonctionFormGroup = FormGroup<FonctionFormGroupContent>;

@Service()
export class FonctionFormService {
  createFonctionFormGroup(fonction?: FonctionFormGroupInput): FonctionFormGroup {
    const fonctionRawValue = {
      ...this.getFormDefaults(),
      ...(fonction ?? { id: null }),
    };

    return new FormGroup<FonctionFormGroupContent>({
      id: new FormControl(
        { value: fonctionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      libelle: new FormControl(fonctionRawValue.libelle, {
        validators: [Validators.required, Validators.maxLength(255)],
      }),
    });
  }

  getFonction(form: FonctionFormGroup): IFonction | NewFonction {
    return form.getRawValue();
  }

  resetForm(form: FonctionFormGroup, fonction: FonctionFormGroupInput): void {
    const fonctionRawValue = { ...this.getFormDefaults(), ...fonction };
    form.reset({
      ...fonctionRawValue,
      id: { value: fonctionRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): FonctionFormDefaults {
    return {
      id: null,
    };
  }
}
