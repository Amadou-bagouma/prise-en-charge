import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ICarteBeneficiaire, NewCarteBeneficiaire } from '../carte-beneficiaire.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICarteBeneficiaire for edit and NewCarteBeneficiaireFormGroupInput for create.
 */
type CarteBeneficiaireFormGroupInput = ICarteBeneficiaire | PartialWithRequiredKeyOf<NewCarteBeneficiaire>;

type CarteBeneficiaireFormDefaults = Pick<NewCarteBeneficiaire, 'id'>;

type CarteBeneficiaireFormGroupContent = {
  id: FormControl<ICarteBeneficiaire['id'] | NewCarteBeneficiaire['id']>;
  numeroCarte: FormControl<ICarteBeneficiaire['numeroCarte']>;
  typeBeneficiaire: FormControl<ICarteBeneficiaire['typeBeneficiaire']>;
  dateDebutValidite: FormControl<ICarteBeneficiaire['dateDebutValidite']>;
  dateFinValidite: FormControl<ICarteBeneficiaire['dateFinValidite']>;
  dateEmission: FormControl<ICarteBeneficiaire['dateEmission']>;
  agent: FormControl<ICarteBeneficiaire['agent']>;
  ayantDroit: FormControl<ICarteBeneficiaire['ayantDroit']>;
};

export type CarteBeneficiaireFormGroup = FormGroup<CarteBeneficiaireFormGroupContent>;

@Service()
export class CarteBeneficiaireFormService {
  createCarteBeneficiaireFormGroup(carteBeneficiaire?: CarteBeneficiaireFormGroupInput): CarteBeneficiaireFormGroup {
    const carteBeneficiaireRawValue = {
      ...this.getFormDefaults(),
      ...(carteBeneficiaire ?? { id: null }),
    };

    return new FormGroup<CarteBeneficiaireFormGroupContent>({
      id: new FormControl(
        { value: carteBeneficiaireRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      numeroCarte: new FormControl(carteBeneficiaireRawValue.numeroCarte, {
        validators: [Validators.required],
      }),
      typeBeneficiaire: new FormControl(carteBeneficiaireRawValue.typeBeneficiaire, {
        validators: [Validators.required],
      }),
      dateDebutValidite: new FormControl(carteBeneficiaireRawValue.dateDebutValidite, {
        validators: [Validators.required],
      }),
      dateFinValidite: new FormControl(carteBeneficiaireRawValue.dateFinValidite, {
        validators: [Validators.required],
      }),
      dateEmission: new FormControl(carteBeneficiaireRawValue.dateEmission, {
        validators: [Validators.required],
      }),
      agent: new FormControl(carteBeneficiaireRawValue.agent),
      ayantDroit: new FormControl(carteBeneficiaireRawValue.ayantDroit),
    });
  }

  getCarteBeneficiaire(form: CarteBeneficiaireFormGroup): ICarteBeneficiaire | NewCarteBeneficiaire {
    return form.getRawValue();
  }

  resetForm(form: CarteBeneficiaireFormGroup, carteBeneficiaire: CarteBeneficiaireFormGroupInput): void {
    const carteBeneficiaireRawValue = { ...this.getFormDefaults(), ...carteBeneficiaire };
    form.reset({
      ...carteBeneficiaireRawValue,
      id: { value: carteBeneficiaireRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): CarteBeneficiaireFormDefaults {
    return {
      id: null,
    };
  }
}
