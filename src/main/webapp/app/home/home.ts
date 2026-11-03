import { Component, computed, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { AccountService } from 'app/core/auth';
import { voitReferentiel } from 'app/shared/auth';

@Component({
  selector: 'jhi-home',
  templateUrl: './home.html',
  styleUrl: './home.scss',
  imports: [RouterLink, FontAwesomeModule],
})
export default class Home {
  private readonly accountService = inject(AccountService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  public readonly account = this.accountService.account;
  /** Le raccourci vers les établissements est un écran du référentiel. */
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly voitReferentiel = computed(() => voitReferentiel(this.accountService));

  private readonly router = inject(Router);

  login(): void {
    this.router.navigate(['/login']);
  }
}
