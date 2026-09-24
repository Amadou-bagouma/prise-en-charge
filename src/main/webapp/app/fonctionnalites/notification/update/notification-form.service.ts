import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config';
import { INotification, NewNotification } from '../notification.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts INotification for edit and NewNotificationFormGroupInput for create.
 */
type NotificationFormGroupInput = INotification | PartialWithRequiredKeyOf<NewNotification>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends INotification | NewNotification> = Omit<T, 'dateCreation' | 'dateLecture'> & {
  dateCreation?: string | null;
  dateLecture?: string | null;
};

type NotificationFormRawValue = FormValueOf<INotification>;

type NewNotificationFormRawValue = FormValueOf<NewNotification>;

type NotificationFormDefaults = Pick<NewNotification, 'id' | 'dateCreation' | 'dateLecture' | 'lu'>;

type NotificationFormGroupContent = {
  id: FormControl<NotificationFormRawValue['id'] | NewNotification['id']>;
  titre: FormControl<NotificationFormRawValue['titre']>;
  message: FormControl<NotificationFormRawValue['message']>;
  dateCreation: FormControl<NotificationFormRawValue['dateCreation']>;
  dateLecture: FormControl<NotificationFormRawValue['dateLecture']>;
  lu: FormControl<NotificationFormRawValue['lu']>;
  type: FormControl<NotificationFormRawValue['type']>;
  utilisateur: FormControl<NotificationFormRawValue['utilisateur']>;
  demande: FormControl<NotificationFormRawValue['demande']>;
  tache: FormControl<NotificationFormRawValue['tache']>;
};

export type NotificationFormGroup = FormGroup<NotificationFormGroupContent>;

@Service()
export class NotificationFormService {
  createNotificationFormGroup(notification?: NotificationFormGroupInput): NotificationFormGroup {
    const notificationRawValue = this.convertNotificationToNotificationRawValue({
      ...this.getFormDefaults(),
      ...(notification ?? { id: null }),
    });

    return new FormGroup<NotificationFormGroupContent>({
      id: new FormControl(
        { value: notificationRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      titre: new FormControl(notificationRawValue.titre, {
        validators: [Validators.required],
      }),
      message: new FormControl(notificationRawValue.message, {
        validators: [Validators.required],
      }),
      dateCreation: new FormControl(notificationRawValue.dateCreation, {
        validators: [Validators.required],
      }),
      dateLecture: new FormControl(notificationRawValue.dateLecture),
      lu: new FormControl(notificationRawValue.lu, {
        validators: [Validators.required],
      }),
      type: new FormControl(notificationRawValue.type, {
        validators: [Validators.required],
      }),
      utilisateur: new FormControl(notificationRawValue.utilisateur, {
        validators: [Validators.required],
      }),
      demande: new FormControl(notificationRawValue.demande),
      tache: new FormControl(notificationRawValue.tache),
    });
  }

  getNotification(form: NotificationFormGroup): INotification | NewNotification {
    return this.convertNotificationRawValueToNotification(form.getRawValue());
  }

  resetForm(form: NotificationFormGroup, notification: NotificationFormGroupInput): void {
    const notificationRawValue = this.convertNotificationToNotificationRawValue({ ...this.getFormDefaults(), ...notification });
    form.reset({
      ...notificationRawValue,
      id: { value: notificationRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): NotificationFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateCreation: currentTime,
      dateLecture: currentTime,
      lu: false,
    };
  }

  private convertNotificationRawValueToNotification(
    rawNotification: NotificationFormRawValue | NewNotificationFormRawValue,
  ): INotification | NewNotification {
    return {
      ...rawNotification,
      dateCreation: dayjs(rawNotification.dateCreation, DATE_TIME_FORMAT),
      dateLecture: dayjs(rawNotification.dateLecture, DATE_TIME_FORMAT),
    };
  }

  private convertNotificationToNotificationRawValue(
    notification: INotification | (Partial<NewNotification> & NotificationFormDefaults),
  ): NotificationFormRawValue | PartialWithRequiredKeyOf<NewNotificationFormRawValue> {
    return {
      ...notification,
      dateCreation: notification.dateCreation ? notification.dateCreation.format(DATE_TIME_FORMAT) : undefined,
      dateLecture: notification.dateLecture ? notification.dateLecture.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
