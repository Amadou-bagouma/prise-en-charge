import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ITypeSoin, NewTypeSoin } from '../type-soin.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITypeSoin for edit and NewTypeSoinFormGroupInput for create.
 */
type TypeSoinFormGroupInput = ITypeSoin | PartialWithRequiredKeyOf<NewTypeSoin>;

type TypeSoinFormDefaults = Pick<NewTypeSoin, 'id' | 'actif'>;

type TypeSoinFormGroupContent = {
  id: FormControl<ITypeSoin['id'] | NewTypeSoin['id']>;
  libelle: FormControl<ITypeSoin['libelle']>;
  description: FormControl<ITypeSoin['description']>;
  actif: FormControl<ITypeSoin['actif']>;
};

export type TypeSoinFormGroup = FormGroup<TypeSoinFormGroupContent>;

@Service()
export class TypeSoinFormService {
  createTypeSoinFormGroup(typeSoin?: TypeSoinFormGroupInput): TypeSoinFormGroup {
    const typeSoinRawValue = {
      ...this.getFormDefaults(),
      ...(typeSoin ?? { id: null }),
    };

    return new FormGroup<TypeSoinFormGroupContent>({
      id: new FormControl(
        { value: typeSoinRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      libelle: new FormControl(typeSoinRawValue.libelle, {
        validators: [Validators.required],
      }),
      description: new FormControl(typeSoinRawValue.description),
      actif: new FormControl(typeSoinRawValue.actif, {
        validators: [Validators.required],
      }),
    });
  }

  getTypeSoin(form: TypeSoinFormGroup): ITypeSoin | NewTypeSoin {
    return form.getRawValue();
  }

  resetForm(form: TypeSoinFormGroup, typeSoin: TypeSoinFormGroupInput): void {
    const typeSoinRawValue = { ...this.getFormDefaults(), ...typeSoin };
    form.reset({
      ...typeSoinRawValue,
      id: { value: typeSoinRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): TypeSoinFormDefaults {
    return {
      id: null,
      actif: false,
    };
  }
}
