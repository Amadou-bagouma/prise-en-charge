import { HttpHeaders } from '@angular/common/http';
import { Component, computed, effect, inject, signal, untracked } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Data, ParamMap, Router, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { NgbDropdown, NgbDropdownItem, NgbDropdownMenu, NgbDropdownToggle } from '@ng-bootstrap/ng-bootstrap/dropdown';
import { NgbPagination } from '@ng-bootstrap/ng-bootstrap/pagination';
import { combineLatest, filter, map, tap } from 'rxjs';

import { DEFAULT_SORT_DATA, ITEMS_PER_PAGE, ITEM_DELETED_EVENT, PAGE_HEADER, SORT, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config';
import { Alert, AlertError } from 'app/shared/alert';
import { TranslateDirective } from 'app/shared/language';
import { ItemCount } from 'app/shared/pagination';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { AuthorityService } from 'app/fonctionnalites/admin/authority/service/authority.service';
import { ProfilDeleteDialog } from '../delete/profil-delete-dialog';
import { IProfil } from '../profil.model';
import { ProfilService } from '../service/profil.service';

/**
 * Le nom d'un droit, en clair et en court.
 *
 * La description complète dit ce que le droit autorise, en une phrase : trois phrases sur une
 * ligne de tableau la rendent illisible. Le tableau porte donc l'intitulé court, et la phrase
 * est mise en infobulle — elle reste à portée sans occuper la place.
 */
function intituleCourt(droit: string): string {
  const mots = droit
    .replace(/^ROLE_/, '')
    .toLowerCase()
    .split('_')
    .join(' ');
  return mots.charAt(0).toUpperCase() + mots.slice(1);
}

@Component({
  selector: 'jhi-profil',
  templateUrl: './profil.html',
  imports: [
    RouterLink,
    FontAwesomeModule,
    AlertError,
    Alert,
    SortDirective,
    SortByDirective,
    TranslateDirective,
    NgbDropdown,
    NgbDropdownItem,
    NgbDropdownMenu,
    NgbDropdownToggle,
    NgbPagination,
    ItemCount,
  ],
})
export class Profil {
  readonly profils = signal<IProfil[]>([]);

  sortState = sortStateSignal({});

  /**
   * Le nom cherché.
   *
   * Envoyé au serveur, pas appliqué aux lignes déjà reçues : chercher dans la page affichée
   * annoncerait « aucun résultat » pour un profil qui se trouve à la page suivante.
   */
  readonly searchTerm = signal('');

  readonly itemsPerPage = signal(ITEMS_PER_PAGE);
  readonly totalItems = signal(0);
  readonly page = signal(1);

  readonly router = inject(Router);
  protected readonly profilService = inject(ProfilService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.profilService.profilsResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly activatedRouteState = toSignal(
    combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data]).pipe(
      map(([queryParamMap, data]) => ({ queryParamMap, data })),
    ),
    { initialValue: { queryParamMap: this.activatedRoute.snapshot.queryParamMap, data: this.activatedRoute.snapshot.data } },
  );
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);
  protected readonly authorityService = inject(AuthorityService);

  /**
   * Les droits tels qu'ils se disent, indexés par leur nom technique.
   *
   * `ROLE_VALIDATEUR_DRH` n'apprend rien à qui vérifie une habilitation : c'est la description
   * qui est affichée, et le nom ne sert plus que de clé.
   */
  private readonly descriptions = computed(() => {
    const table = new Map<string, string>();
    for (const droit of this.authorityService.authorities()) {
      table.set(droit.name, droit.description ?? droit.name);
    }
    return table;
  });

  constructor() {
    // Les libellés des droits, chargés une fois : sans eux la colonne n'afficherait que des
    // noms techniques.
    this.authorityService.authoritiesParams.set({});
    effect(() => {
      // Le nom cherché repart au serveur, en revenant à la première page : rester à la page
      // trois d'une recherche qui n'en compte qu'une afficherait un tableau vide.
      this.searchTerm();
      untracked(() => {
        this.page.set(1);
        this.queryBackend();
      });
    });
    effect(() => {
      const headers = this.profilService.profilsResource.headers();
      if (headers) {
        this.fillComponentAttributesFromResponseHeader(headers);
      }
    });
    effect(() => {
      this.profils.set(this.fillComponentAttributesFromResponseBody([...this.profilService.profils()]));
    });
    effect(() => {
      const activatedRouteState = this.activatedRouteState();
      untracked(() => {
        // Only watch for route changes. Other signals should be ignored.
        this.fillComponentAttributeFromRoute(activatedRouteState.queryParamMap, activatedRouteState.data);
        this.load();
      });
    });
  }

  /**
   * Les droits d'un profil, dits en clair, tronqués à trois.
   *
   * Dix badges sur une ligne la rendraient illisible : les trois premiers situent le profil, le
   * reste se compte et se lit sur la fiche.
   */
  droitsResumes(profil: IProfil): { visibles: { libelle: string; description: string }[]; reste: number } | null {
    const droits = profil.authorities ?? [];
    if (droits.length === 0) {
      return null;
    }
    const table = this.descriptions();
    const lisibles = droits.map(droit => ({ libelle: intituleCourt(droit), description: table.get(droit) ?? droit }));
    return { visibles: lisibles.slice(0, 3), reste: Math.max(0, lisibles.length - 3) };
  }

  trackId = (item: IProfil): number => this.profilService.getProfilIdentifier(item);

  delete(profil: IProfil): void {
    const modalRef = this.modalService.open(ProfilDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.profil = profil;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => this.load()),
      )
      .subscribe();
  }

  load(): void {
    this.queryBackend();
  }

  navigateToWithComponentValues(event: SortState): void {
    this.handleNavigation(this.page(), event);
  }

  navigateToPage(page: number): void {
    this.handleNavigation(page, this.sortState());
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    const page = params.get(PAGE_HEADER);
    this.page.set(+(page ?? 1));
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
  }

  protected fillComponentAttributesFromResponseBody(data: IProfil[]): IProfil[] {
    return data;
  }

  protected fillComponentAttributesFromResponseHeader(headers: HttpHeaders): void {
    this.totalItems.set(Number(headers.get(TOTAL_COUNT_RESPONSE_HEADER)));
  }

  protected queryBackend(): void {
    const pageToLoad: number = this.page();
    const queryObject: any = {
      page: pageToLoad - 1,
      size: this.itemsPerPage(),
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    const nom = this.searchTerm().trim();
    if (nom) {
      queryObject.nom = nom;
    }
    this.profilService.profilsParams.set(queryObject);
  }

  protected handleNavigation(page: number, sortState: SortState): void {
    const queryParamsObj = {
      page,
      size: this.itemsPerPage(),
      sort: this.sortService.buildSortParam(sortState),
    };

    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute,
      queryParams: queryParamsObj,
    });
  }
}
