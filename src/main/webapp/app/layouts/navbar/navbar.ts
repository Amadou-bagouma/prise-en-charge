import { Component, OnInit, computed, effect, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { NavigationEnd, Router, RouterLink, RouterLinkActive } from '@angular/router';
import { filter, map, startWith } from 'rxjs';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbCollapse } from '@ng-bootstrap/ng-bootstrap/collapse';
import { NgbDropdown, NgbDropdownMenu, NgbDropdownToggle } from '@ng-bootstrap/ng-bootstrap/dropdown';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { environment } from 'environments/environment';

import { LANGUAGES } from 'app/config';
import { AccountService, StateStorageService } from 'app/core/auth';
import { ChargeTravailService } from 'app/layouts/charge-travail.service';
import { LayoutService } from 'app/layouts/layout.service';
import { ProfileService } from 'app/layouts/profiles/profile.service';
import { LoginService } from 'app/login/login.service';
import { HasAnyAuthorityDirective } from 'app/shared/auth';
import { FindLanguageFromKeyPipe, TranslateDirective } from 'app/shared/language';

import ActiveMenuDirective from './active-menu.directive';

/** Les écrans du référentiel, dans l'ordre du menu. */
const SEGMENTS_REFERENTIEL = ['agent', 'ayant-droit', 'etablissement-sante', 'type-soin', 'direction', 'gestion', 'region', 'profil'];

@Component({
  selector: 'jhi-navbar',
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss',
  imports: [
    RouterLink,
    RouterLinkActive,
    FontAwesomeModule,
    NgbCollapse,
    NgbDropdown,
    NgbDropdownMenu,
    NgbDropdownToggle,
    HasAnyAuthorityDirective,
    ActiveMenuDirective,
    FindLanguageFromKeyPipe,
    TranslateDirective,
    TranslatePipe,
  ],
})
export default class Navbar implements OnInit {
  readonly inProduction = signal(true);
  readonly isNavbarCollapsed = signal(true);
  readonly entitiesCollapsed = signal(true);
  readonly adminCollapsed = signal(true);
  readonly referentielCollapsed = signal(true);
  readonly languages = LANGUAGES;
  readonly openAPIEnabled = signal(false);
  readonly version: string;
  readonly account = inject(AccountService).account;

  readonly layoutService = inject(LayoutService);
  readonly chargeTravail = inject(ChargeTravailService);

  private readonly loginService = inject(LoginService);
  private readonly translateService = inject(TranslateService);
  private readonly stateStorageService = inject(StateStorageService);
  private readonly profileService = inject(ProfileService);
  private readonly router = inject(Router);

  /** Premier segment de l'URL : ce qui identifie l'écran courant dans le fil d'Ariane. */
  private readonly segmentCourant = toSignal(
    this.router.events.pipe(
      filter(evenement => evenement instanceof NavigationEnd),
      map(() => this.router.url),
      startWith(this.router.url),
      map(url => url.split('?')[0].split('/').filter(Boolean)[0] ?? ''),
    ),
    { initialValue: '' },
  );

  /**
   * Fil d'Ariane : il se termine par ce qu'on regarde. La clé de traduction est celle du menu,
   * pour que le fil et la navigation disent toujours le même mot.
   */
  readonly filCleTraduction = computed(() => {
    const segment = this.segmentCourant();
    if (!segment) {
      return 'global.menu.home';
    }
    const CLES: Record<string, string> = {
      profil: 'global.menu.entities.profil',
      gestion: 'global.menu.entities.gestion',
      region: 'global.menu.entities.region',
      direction: 'global.menu.entities.direction',
      agent: 'global.menu.entities.agent',
      'ayant-droit': 'global.menu.entities.ayantDroit',
      'etablissement-sante': 'global.menu.entities.etablissementSante',
      'demande-prise-en-charge': 'global.menu.entities.demandePriseEnCharge',
      'type-soin': 'global.menu.entities.typeSoin',
      'piece-justificative': 'global.menu.entities.pieceJustificative',
      tache: 'global.menu.entities.tache',
      'boite-reception': 'global.menu.entities.boiteReception',
      notification: 'global.menu.entities.notification',
      'historique-action': 'global.menu.entities.historiqueAction',
      'user-management': 'userManagement.home.title',
      authority: 'global.menu.entities.adminAuthority',
      account: 'global.menu.account.main',
      admin: 'global.menu.admin.main',
    };
    return CLES[segment] ?? 'global.menu.home';
  });

  /** Initiales de l'agent connecté, pour la pastille de l'en-tête. */
  readonly initiales = computed(() => {
    const compte = this.account();
    if (!compte) {
      return '';
    }
    const prenom = compte.firstName?.trim().charAt(0) ?? '';
    const nom = compte.lastName?.trim().charAt(0) ?? '';
    return (prenom + nom || compte.login.slice(0, 2)).toUpperCase();
  });

  constructor() {
    // Les compteurs suivent l'écran : on les relit à chaque navigation, et dès que l'agent
    // est connu. Une valeur périmée vaudrait moins que pas de valeur du tout.
    effect(() => {
      this.account();
      this.segmentCourant();
      this.chargeTravail.rafraichir();
    });

    // Arriver sur un écran du référentiel sans voir où l'on se trouve dans la navigation
    // désoriente : le groupe s'ouvre de lui-même. Il reste refermable à la main.
    effect(() => {
      if (SEGMENTS_REFERENTIEL.includes(this.segmentCourant())) {
        this.referentielCollapsed.set(false);
      }
    });

    const { VERSION } = environment;
    if (VERSION) {
      this.version = VERSION.toLowerCase().startsWith('v') ? VERSION : `v${VERSION}`;
    } else {
      this.version = '';
    }
  }

  ngOnInit(): void {
    this.profileService.getProfileInfo().subscribe(profileInfo => {
      this.inProduction.set(profileInfo.inProduction ?? true);
      this.openAPIEnabled.set(profileInfo.openAPIEnabled ?? false);
    });
  }

  changeLanguage(languageKey: string): void {
    this.stateStorageService.storeLocale(languageKey);
    this.translateService.use(languageKey);
  }

  collapseNavbar(): void {
    this.isNavbarCollapsed.set(true);
  }

  /** Réduit la navigation à ses icônes, ou la redéploie. */
  basculerRail(): void {
    this.layoutService.basculerRail();
  }

  toggleEntities(): void {
    this.entitiesCollapsed.update(collapsed => !collapsed);
  }

  toggleAdmin(): void {
    this.adminCollapsed.update(collapsed => !collapsed);
  }

  toggleReferentiel(): void {
    this.referentielCollapsed.update(collapsed => !collapsed);
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  logout(): void {
    this.collapseNavbar();
    this.loginService.logout();
    this.router.navigate(['/login']);
  }
}
