import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IGestion, NewGestion } from '../gestion.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IGestion for edit and NewGestionFormGroupInput for create.
 */
type GestionFormGroupInput = IGestion | PartialWithRequiredKeyOf<NewGestion>;

type GestionFormDefaults = Pick<NewGestion, 'id'>;

type GestionFormGroupContent = {
  id: FormControl<IGestion['id'] | NewGestion['id']>;
  nom: FormControl<IGestion['nom']>;
  type: FormControl<IGestion['type']>;
};

export type GestionFormGroup = FormGroup<GestionFormGroupContent>;

@Service()
export class GestionFormService {
  createGestionFormGroup(gestion?: GestionFormGroupInput): GestionFormGroup {
    const gestionRawValue = {
      ...this.getFormDefaults(),
      ...(gestion ?? { id: null }),
    };

    return new FormGroup<GestionFormGroupContent>({
      id: new FormControl(
        { value: gestionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nom: new FormControl(gestionRawValue.nom, {
        validators: [Validators.required],
      }),
      type: new FormControl(gestionRawValue.type, {
        validators: [Validators.required],
      }),
    });
  }

  getGestion(form: GestionFormGroup): IGestion | NewGestion {
    return form.getRawValue();
  }

  resetForm(form: GestionFormGroup, gestion: GestionFormGroupInput): void {
    const gestionRawValue = { ...this.getFormDefaults(), ...gestion };
    form.reset({
      ...gestionRawValue,
      id: { value: gestionRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): GestionFormDefaults {
    return {
      id: null,
    };
  }
}
