import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IAyantDroit, NewAyantDroit } from '../ayant-droit.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAyantDroit for edit and NewAyantDroitFormGroupInput for create.
 */
type AyantDroitFormGroupInput = IAyantDroit | PartialWithRequiredKeyOf<NewAyantDroit>;

type AyantDroitFormDefaults = Pick<NewAyantDroit, 'id'>;

type AyantDroitFormGroupContent = {
  id: FormControl<IAyantDroit['id'] | NewAyantDroit['id']>;
  nom: FormControl<IAyantDroit['nom']>;
  prenom: FormControl<IAyantDroit['prenom']>;
  dateNaissance: FormControl<IAyantDroit['dateNaissance']>;
  lien: FormControl<IAyantDroit['lien']>;
  agent: FormControl<IAyantDroit['agent']>;
};

export type AyantDroitFormGroup = FormGroup<AyantDroitFormGroupContent>;

@Service()
export class AyantDroitFormService {
  createAyantDroitFormGroup(ayantDroit?: AyantDroitFormGroupInput): AyantDroitFormGroup {
    const ayantDroitRawValue = {
      ...this.getFormDefaults(),
      ...(ayantDroit ?? { id: null }),
    };

    return new FormGroup<AyantDroitFormGroupContent>({
      id: new FormControl(
        { value: ayantDroitRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nom: new FormControl(ayantDroitRawValue.nom, {
        validators: [Validators.required],
      }),
      prenom: new FormControl(ayantDroitRawValue.prenom, {
        validators: [Validators.required],
      }),
      dateNaissance: new FormControl(ayantDroitRawValue.dateNaissance),
      lien: new FormControl(ayantDroitRawValue.lien, {
        validators: [Validators.required],
      }),
      agent: new FormControl(ayantDroitRawValue.agent),
    });
  }

  getAyantDroit(form: AyantDroitFormGroup): IAyantDroit | NewAyantDroit {
    return form.getRawValue();
  }

  resetForm(form: AyantDroitFormGroup, ayantDroit: AyantDroitFormGroupInput): void {
    const ayantDroitRawValue = { ...this.getFormDefaults(), ...ayantDroit };
    form.reset({
      ...ayantDroitRawValue,
      id: { value: ayantDroitRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): AyantDroitFormDefaults {
    return {
      id: null,
    };
  }
}
