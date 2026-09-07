import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IEtablissementSante, NewEtablissementSante } from '../etablissement-sante.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IEtablissementSante for edit and NewEtablissementSanteFormGroupInput for create.
 */
type EtablissementSanteFormGroupInput = IEtablissementSante | PartialWithRequiredKeyOf<NewEtablissementSante>;

type EtablissementSanteFormDefaults = Pick<NewEtablissementSante, 'id' | 'actif'>;

type EtablissementSanteFormGroupContent = {
  id: FormControl<IEtablissementSante['id'] | NewEtablissementSante['id']>;
  code: FormControl<IEtablissementSante['code']>;
  nom: FormControl<IEtablissementSante['nom']>;
  adresse: FormControl<IEtablissementSante['adresse']>;
  telephone: FormControl<IEtablissementSante['telephone']>;
  actif: FormControl<IEtablissementSante['actif']>;
};

export type EtablissementSanteFormGroup = FormGroup<EtablissementSanteFormGroupContent>;

@Service()
export class EtablissementSanteFormService {
  createEtablissementSanteFormGroup(etablissementSante?: EtablissementSanteFormGroupInput): EtablissementSanteFormGroup {
    const etablissementSanteRawValue = {
      ...this.getFormDefaults(),
      ...(etablissementSante ?? { id: null }),
    };

    return new FormGroup<EtablissementSanteFormGroupContent>({
      id: new FormControl(
        { value: etablissementSanteRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      code: new FormControl(etablissementSanteRawValue.code, {
        validators: [Validators.required],
      }),
      nom: new FormControl(etablissementSanteRawValue.nom, {
        validators: [Validators.required],
      }),
      adresse: new FormControl(etablissementSanteRawValue.adresse),
      telephone: new FormControl(etablissementSanteRawValue.telephone),
      actif: new FormControl(etablissementSanteRawValue.actif, {
        validators: [Validators.required],
      }),
    });
  }

  getEtablissementSante(form: EtablissementSanteFormGroup): IEtablissementSante | NewEtablissementSante {
    return form.getRawValue();
  }

  resetForm(form: EtablissementSanteFormGroup, etablissementSante: EtablissementSanteFormGroupInput): void {
    const etablissementSanteRawValue = { ...this.getFormDefaults(), ...etablissementSante };
    form.reset({
      ...etablissementSanteRawValue,
      id: { value: etablissementSanteRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): EtablissementSanteFormDefaults {
    return {
      id: null,
      actif: false,
    };
  }
}
