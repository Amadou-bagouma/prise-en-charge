import { Component, OnInit, inject, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { AuthorityService } from 'app/fonctionnalites/admin/authority/service/authority.service';
import { Alert, AlertError } from 'app/shared/alert';
import { TranslateDirective } from 'app/shared/language';
import { IProfil } from '../profil.model';

@Component({
  selector: 'jhi-profil-detail',
  templateUrl: './profil-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, RouterLink],
})
export class ProfilDetail implements OnInit {
  readonly profil = input<IProfil | null>(null);

  private readonly authorityService = inject(AuthorityService);

  ngOnInit(): void {
    // Le profil ne porte que les noms des droits : leur description se lit au référentiel.
    this.authorityService.authoritiesParams.set({});
  }

  /**
   * Ce que le droit autorise, en clair.
   *
   * À défaut — référentiel pas encore chargé, droit retiré du code mais encore attribué — le
   * nom technique est rendu tel quel : mieux vaut un nom brut qu'une ligne vide, qui laisserait
   * croire que le profil n'accorde rien.
   */
  description(nom: string): string {
    return this.authorityService.authorities().find(droit => droit.name === nom)?.description || nom;
  }

  previousState(): void {
    globalThis.history.back();
  }
}
