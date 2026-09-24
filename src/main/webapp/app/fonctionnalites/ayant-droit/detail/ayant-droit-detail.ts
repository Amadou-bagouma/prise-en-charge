import { Component, computed, inject, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import dayjs from 'dayjs/esm';

import { DataUtils } from 'app/core/util/data-util.service';
import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { IAyantDroit } from '../ayant-droit.model';

@Component({
  selector: 'jhi-ayant-droit-detail',
  templateUrl: './ayant-droit-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, RouterLink, FormatMediumDatePipe],
})
export class AyantDroitDetail {
  readonly ayantDroit = input<IAyantDroit | null>(null);

  readonly identite = computed(() => {
    const ayantDroit = this.ayantDroit();
    if (!ayantDroit) {
      return '';
    }
    return [ayantDroit.prenom, ayantDroit.nom].filter(Boolean).join(' ') || (ayantDroit.codeAyantDroit ?? '');
  });

  /** Initiales pour la pastille d'identité, quand l'ayant droit n'a pas de photo. */
  readonly initiales = computed(() => {
    const ayantDroit = this.ayantDroit();
    if (!ayantDroit) {
      return '';
    }
    const prenom = ayantDroit.prenom?.trim().charAt(0) ?? '';
    const nom = ayantDroit.nom?.trim().charAt(0) ?? '';
    return (prenom + nom || ayantDroit.codeAyantDroit?.slice(0, 2) || '').toUpperCase();
  });

  /**
   * Âge en années révolues.
   *
   * C'est l'information qui décide de la couverture d'un enfant : l'afficher évite de compter
   * de tête à partir d'une date de naissance.
   */
  readonly age = computed(() => {
    const date = this.ayantDroit()?.dateNaissance;
    if (!date) {
      return null;
    }
    const annees = dayjs().diff(date, 'year');
    return annees >= 0 ? annees : null;
  });

  protected dataUtils = inject(DataUtils);

  previousState(): void {
    globalThis.history.back();
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }
}
