import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IProfil, NewProfil } from '../profil.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProfil for edit and NewProfilFormGroupInput for create.
 */
type ProfilFormGroupInput = IProfil | PartialWithRequiredKeyOf<NewProfil>;

type ProfilFormDefaults = Pick<NewProfil, 'id' | 'authorities'>;

type ProfilFormGroupContent = {
  id: FormControl<IProfil['id'] | NewProfil['id']>;
  nom: FormControl<IProfil['nom']>;
  description: FormControl<IProfil['description']>;
  authorities: FormControl<IProfil['authorities']>;
};

export type ProfilFormGroup = FormGroup<ProfilFormGroupContent>;

@Service()
export class ProfilFormService {
  createProfilFormGroup(profil?: ProfilFormGroupInput): ProfilFormGroup {
    const profilRawValue = {
      ...this.getFormDefaults(),
      ...(profil ?? { id: null }),
    };

    return new FormGroup<ProfilFormGroupContent>({
      id: new FormControl(
        { value: profilRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nom: new FormControl(profilRawValue.nom, {
        validators: [Validators.required],
      }),
      description: new FormControl(profilRawValue.description),
      authorities: new FormControl(profilRawValue.authorities, {
        validators: [Validators.required, Validators.minLength(1)],
      }),
    });
  }

  getProfil(form: ProfilFormGroup): IProfil | NewProfil {
    return form.getRawValue();
  }

  resetForm(form: ProfilFormGroup, profil: ProfilFormGroupInput): void {
    const profilRawValue = { ...this.getFormDefaults(), ...profil };
    form.reset({
      ...profilRawValue,
      id: { value: profilRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ProfilFormDefaults {
    return {
      id: null,
      authorities: [],
    };
  }
}
