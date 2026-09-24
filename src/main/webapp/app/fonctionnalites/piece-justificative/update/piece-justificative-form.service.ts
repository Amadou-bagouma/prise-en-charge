import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config';
import { IPieceJustificative, NewPieceJustificative } from '../piece-justificative.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPieceJustificative for edit and NewPieceJustificativeFormGroupInput for create.
 */
type PieceJustificativeFormGroupInput = IPieceJustificative | PartialWithRequiredKeyOf<NewPieceJustificative>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IPieceJustificative | NewPieceJustificative> = Omit<T, 'dateAjout'> & {
  dateAjout?: string | null;
};

type PieceJustificativeFormRawValue = FormValueOf<IPieceJustificative>;

type NewPieceJustificativeFormRawValue = FormValueOf<NewPieceJustificative>;

type PieceJustificativeFormDefaults = Pick<NewPieceJustificative, 'id' | 'dateAjout'>;

type PieceJustificativeFormGroupContent = {
  id: FormControl<PieceJustificativeFormRawValue['id'] | NewPieceJustificative['id']>;
  nomFichier: FormControl<PieceJustificativeFormRawValue['nomFichier']>;
  cheminFichier: FormControl<PieceJustificativeFormRawValue['cheminFichier']>;
  dateAjout: FormControl<PieceJustificativeFormRawValue['dateAjout']>;
  demande: FormControl<PieceJustificativeFormRawValue['demande']>;
};

export type PieceJustificativeFormGroup = FormGroup<PieceJustificativeFormGroupContent>;

@Service()
export class PieceJustificativeFormService {
  createPieceJustificativeFormGroup(pieceJustificative?: PieceJustificativeFormGroupInput): PieceJustificativeFormGroup {
    const pieceJustificativeRawValue = this.convertPieceJustificativeToPieceJustificativeRawValue({
      ...this.getFormDefaults(),
      ...(pieceJustificative ?? { id: null }),
    });

    return new FormGroup<PieceJustificativeFormGroupContent>({
      id: new FormControl(
        { value: pieceJustificativeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nomFichier: new FormControl(pieceJustificativeRawValue.nomFichier, {
        validators: [Validators.required],
      }),
      cheminFichier: new FormControl(pieceJustificativeRawValue.cheminFichier, {
        validators: [Validators.required],
      }),
      dateAjout: new FormControl(pieceJustificativeRawValue.dateAjout, {
        validators: [Validators.required],
      }),
      demande: new FormControl(pieceJustificativeRawValue.demande, {
        validators: [Validators.required],
      }),
    });
  }

  getPieceJustificative(form: PieceJustificativeFormGroup): IPieceJustificative | NewPieceJustificative {
    return this.convertPieceJustificativeRawValueToPieceJustificative(form.getRawValue());
  }

  resetForm(form: PieceJustificativeFormGroup, pieceJustificative: PieceJustificativeFormGroupInput): void {
    const pieceJustificativeRawValue = this.convertPieceJustificativeToPieceJustificativeRawValue({
      ...this.getFormDefaults(),
      ...pieceJustificative,
    });
    form.reset({
      ...pieceJustificativeRawValue,
      id: { value: pieceJustificativeRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): PieceJustificativeFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateAjout: currentTime,
    };
  }

  private convertPieceJustificativeRawValueToPieceJustificative(
    rawPieceJustificative: PieceJustificativeFormRawValue | NewPieceJustificativeFormRawValue,
  ): IPieceJustificative | NewPieceJustificative {
    return {
      ...rawPieceJustificative,
      dateAjout: dayjs(rawPieceJustificative.dateAjout, DATE_TIME_FORMAT),
    };
  }

  private convertPieceJustificativeToPieceJustificativeRawValue(
    pieceJustificative: IPieceJustificative | (Partial<NewPieceJustificative> & PieceJustificativeFormDefaults),
  ): PieceJustificativeFormRawValue | PartialWithRequiredKeyOf<NewPieceJustificativeFormRawValue> {
    return {
      ...pieceJustificative,
      dateAjout: pieceJustificative.dateAjout ? pieceJustificative.dateAjout.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
