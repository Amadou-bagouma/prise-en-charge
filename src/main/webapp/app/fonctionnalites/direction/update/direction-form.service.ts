import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IDirection, NewDirection } from '../direction.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDirection for edit and NewDirectionFormGroupInput for create.
 */
type DirectionFormGroupInput = IDirection | PartialWithRequiredKeyOf<NewDirection>;

type DirectionFormDefaults = Pick<NewDirection, 'id'>;

type DirectionFormGroupContent = {
  id: FormControl<IDirection['id'] | NewDirection['id']>;
  code: FormControl<IDirection['code']>;
  nom: FormControl<IDirection['nom']>;
  region: FormControl<IDirection['region']>;
};

export type DirectionFormGroup = FormGroup<DirectionFormGroupContent>;

@Service()
export class DirectionFormService {
  createDirectionFormGroup(direction?: DirectionFormGroupInput): DirectionFormGroup {
    const directionRawValue = {
      ...this.getFormDefaults(),
      ...(direction ?? { id: null }),
    };

    return new FormGroup<DirectionFormGroupContent>({
      id: new FormControl(
        { value: directionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      code: new FormControl(directionRawValue.code, {
        validators: [Validators.required],
      }),
      nom: new FormControl(directionRawValue.nom, {
        validators: [Validators.required],
      }),
      region: new FormControl(directionRawValue.region),
    });
  }

  getDirection(form: DirectionFormGroup): IDirection | NewDirection {
    return form.getRawValue();
  }

  resetForm(form: DirectionFormGroup, direction: DirectionFormGroupInput): void {
    const directionRawValue = { ...this.getFormDefaults(), ...direction };
    form.reset({
      ...directionRawValue,
      id: { value: directionRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): DirectionFormDefaults {
    return {
      id: null,
    };
  }
}
