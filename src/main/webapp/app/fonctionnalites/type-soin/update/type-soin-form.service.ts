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

type TypeSoinFormDefaults = Pick<NewTypeSoin, 'id' | 'ordre' | 'actif'>;

type TypeSoinFormGroupContent = {
  id: FormControl<ITypeSoin['id'] | NewTypeSoin['id']>;
  code: FormControl<ITypeSoin['code']>;
  libelle: FormControl<ITypeSoin['libelle']>;
  description: FormControl<ITypeSoin['description']>;
  ordre: FormControl<ITypeSoin['ordre']>;
  actif: FormControl<ITypeSoin['actif']>;
};

export type TypeSoinFormGroup = FormGroup<TypeSoinFormGroupContent>;

/** Le code est la clé du référentiel : même règle qu'en base, pour refuser avant l'aller-retour. */
const MOTIF_CODE = /^[A-Z][A-Z0-9_]*$/;

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
      code: new FormControl(typeSoinRawValue.code, {
        validators: [Validators.required, Validators.maxLength(50), Validators.pattern(MOTIF_CODE)],
      }),
      libelle: new FormControl(typeSoinRawValue.libelle, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      description: new FormControl(typeSoinRawValue.description, {
        validators: [Validators.maxLength(500)],
      }),
      ordre: new FormControl(typeSoinRawValue.ordre, {
        validators: [Validators.required, Validators.min(0)],
      }),
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
      // Une nouvelle categorie arrive en service et en fin de liste : elle ne s'intercale pas
      // d'elle-meme dans l'ordre de l'imprime officiel.
      ordre: 99,
      actif: true,
    };
  }
}
