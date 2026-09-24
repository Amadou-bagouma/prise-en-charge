import { HttpHeaders } from '@angular/common/http';
import { Component, effect, inject, signal, untracked } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Data, ParamMap, Router, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { NgbPagination } from '@ng-bootstrap/ng-bootstrap/pagination';
import { TranslateService } from '@ngx-translate/core';
import dayjs from 'dayjs/esm';
import { combineLatest, filter, map, tap } from 'rxjs';

import { DEFAULT_SORT_DATA, ITEMS_PER_PAGE, ITEM_DELETED_EVENT, PAGE_HEADER, SORT, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config';
import { Alert, AlertError } from 'app/shared/alert';
import { HasAnyAuthorityDirective } from 'app/shared/auth';
import { Filter, FilterOption, FilterOptions, IFilterOption, IFilterOptions } from 'app/shared/filter';
import { Authority } from 'app/shared/jhipster/constants';
import { TranslateDirective } from 'app/shared/language';
import { ItemCount } from 'app/shared/pagination';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { DemandePriseEnChargeDeleteDialog } from '../delete/demande-prise-en-charge-delete-dialog';
import { IDemandePriseEnCharge } from '../demande-prise-en-charge.model';
import { DemandePriseEnChargeService } from '../service/demande-prise-en-charge.service';

@Component({
  selector: 'jhi-demande-prise-en-charge',
  templateUrl: './demande-prise-en-charge.html',
  imports: [
    RouterLink,
    FontAwesomeModule,
    AlertError,
    Alert,
    SortDirective,
    SortByDirective,
    TranslateDirective,
    Filter,
    NgbPagination,
    ItemCount,
    HasAnyAuthorityDirective,
  ],
})
export class DemandePriseEnCharge {
  protected readonly Authority = Authority;

  readonly demandePriseEnCharges = signal<IDemandePriseEnCharge[]>([]);

  sortState = sortStateSignal({});
  filters: IFilterOptions = new FilterOptions();
  readonly searchTerm = signal('');
  private readonly searchFilterName = 'reference.contains';
  private readonly statutFilterName = 'statut.in';

  /**
   * Puces de la barre de filtres : elles suivent la journée de travail de l'agent, pas l'ordre de
   * l'enum. Quatre au maximum (fiche BarreFiltres), chacune avec son effectif.
   */
  readonly puces: { cle: string; libelle: string; statuts: string[] }[] = [
    { cle: 'toutes', libelle: 'Toutes', statuts: [] },
    { cle: 'drh', libelle: 'À valider DRH', statuts: ['EN_ATTENTE_VALIDATION_DRH'] },
    { cle: 'infirmerie', libelle: 'À valider infirmerie', statuts: ['EN_ATTENTE_VALIDATION_INFIRMERIE'] },
    { cle: 'retournees', libelle: 'Retournées', statuts: ['RETOURNEE'] },
  ];
  /** Effectif par puce ; indéfini tant que le compte n'est pas revenu du serveur. */
  readonly effectifs = signal<Record<string, number | undefined>>({});

  readonly itemsPerPage = signal(ITEMS_PER_PAGE);
  readonly totalItems = signal(0);
  readonly page = signal(1);
  readonly downloadingRapportId = signal<number | null>(null);

  readonly router = inject(Router);
  protected readonly demandePriseEnChargeService = inject(DemandePriseEnChargeService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.demandePriseEnChargeService.demandePriseEnChargesResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly activatedRouteState = toSignal(
    combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data]).pipe(
      map(([queryParamMap, data]) => ({ queryParamMap, data })),
    ),
    { initialValue: { queryParamMap: this.activatedRoute.snapshot.queryParamMap, data: this.activatedRoute.snapshot.data } },
  );
  protected readonly sortService = inject(SortService);
  protected readonly filterOptions = toSignal(this.filters.filterChanges);
  protected modalService = inject(NgbModal);
  protected readonly translateService = inject(TranslateService);

  constructor() {
    effect(() => {
      const headers = this.demandePriseEnChargeService.demandePriseEnChargesResource.headers();
      if (headers) {
        this.fillComponentAttributesFromResponseHeader(headers);
      }
    });
    effect(() => {
      this.demandePriseEnCharges.set(
        this.fillComponentAttributesFromResponseBody([...this.demandePriseEnChargeService.demandePriseEnCharges()]),
      );
    });
    effect(() => {
      const activatedRouteState = this.activatedRouteState();
      untracked(() => {
        // Only watch for route changes. Other signals should be ignored.
        this.fillComponentAttributeFromRoute(activatedRouteState.queryParamMap, activatedRouteState.data);
        this.load();
      });
    });

    effect(() => {
      const filterOptions = this.filterOptions();
      if (filterOptions) {
        untracked(() => {
          // Only watch for filter changes. Other signals should be ignored.
          this.handleNavigation(1, this.sortState(), filterOptions);
        });
      }
    });
  }

  trackId = (item: IDemandePriseEnCharge): number => this.demandePriseEnChargeService.getDemandePriseEnChargeIdentifier(item);

  /** Puce active, déduite des filtres de l'URL pour survivre au retour depuis un dossier. */
  puceActive(): string {
    const statuts = this.filters.filterOptions.find(option => option.name === this.statutFilterName)?.values ?? [];
    if (statuts.length === 0) {
      return 'toutes';
    }
    return this.puces.find(puce => puce.statuts.length === statuts.length && puce.statuts.every(s => statuts.includes(s)))?.cle ?? '';
  }

  choisirPuce(cle: string): void {
    const puce = this.puces.find(p => p.cle === cle);
    const autres = this.filters.filterOptions.filter(option => option.name !== this.statutFilterName);
    const filterOptions =
      puce && puce.statuts.length > 0 ? [...autres, new FilterOption(this.statutFilterName, [...puce.statuts])] : autres;
    this.handleNavigation(1, this.sortState(), filterOptions);
  }

  /** Jours écoulés depuis le dépôt de la demande. */
  ancienneteJours(demandePriseEnCharge: IDemandePriseEnCharge): number | null {
    const depot = demandePriseEnCharge.dateCreation;
    return depot ? dayjs().startOf('day').diff(depot.startOf('day'), 'day') : null;
  }

  /** Date en colonne : JJ/MM/AAAA, en chasse fixe côté gabarit. */
  formatDateCourte(date?: dayjs.Dayjs | null): string {
    return date ? date.format('DD/MM/YYYY') : '';
  }

  /**
   * Ton du badge de statut. Le vocabulaire métier est conservé (voir ECARTS.md) ; seuls les tons
   * du design system sont repris.
   */
  statutTon(statut?: string | null): string {
    switch (statut) {
      case 'VALIDEE':
        return 'ok';
      case 'RETOURNEE':
      case 'REJETEE':
        return 'danger';
      case 'EN_ATTENTE_VALIDATION_DRH':
      case 'EN_ATTENTE_VALIDATION_INFIRMERIE':
        return 'warn';
      case 'ANNULEE':
      case 'CLOTUREE':
        return 'neutre';
      default:
        return 'info';
    }
  }

  private chargerEffectifs(): void {
    const base: any = {};
    for (const filterOption of this.filters.filterOptions) {
      if (filterOption.name !== this.statutFilterName) {
        base[filterOption.name] = filterOption.values;
      }
    }
    for (const puce of this.puces) {
      const req = { ...base };
      if (puce.statuts.length > 0) {
        req[this.statutFilterName] = puce.statuts;
      }
      this.demandePriseEnChargeService.count(req).subscribe({
        next: nombre => this.effectifs.update(effectifs => ({ ...effectifs, [puce.cle]: nombre })),
        error: () => this.effectifs.update(effectifs => ({ ...effectifs, [puce.cle]: 0 })),
      });
    }
  }

  delete(demandePriseEnCharge: IDemandePriseEnCharge): void {
    const modalRef = this.modalService.open(DemandePriseEnChargeDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.demandePriseEnCharge = demandePriseEnCharge;
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
    this.chargerEffectifs();
  }

  /** Les libellés viennent du référentiel : ils ne sont plus traduits, ils sont administrés. */
  typeSoinsLabel(demandePriseEnCharge: IDemandePriseEnCharge): string {
    return (demandePriseEnCharge.typeSoins ?? []).map(typeSoin => typeSoin.libelle).join(', ');
  }

  telechargerRapport(demandePriseEnCharge: IDemandePriseEnCharge): void {
    this.downloadingRapportId.set(demandePriseEnCharge.id);
    this.demandePriseEnChargeService.telechargerRapport(demandePriseEnCharge.id).subscribe({
      next: blob => {
        this.downloadingRapportId.set(null);
        const objectUrl = URL.createObjectURL(blob);
        const anchor = document.createElement('a');
        anchor.href = objectUrl;
        anchor.download = `rapport-${demandePriseEnCharge.reference ?? demandePriseEnCharge.id}.pdf`;
        anchor.click();
        URL.revokeObjectURL(objectUrl);
      },
      error: () => this.downloadingRapportId.set(null),
    });
  }

  navigateToWithComponentValues(event: SortState): void {
    this.handleNavigation(this.page(), event, this.filters.filterOptions);
  }

  navigateToPage(page: number): void {
    this.handleNavigation(page, this.sortState(), this.filters.filterOptions);
  }

  search(): void {
    const term = this.searchTerm().trim();
    const otherFilters = this.filters.filterOptions.filter(option => option.name !== this.searchFilterName);
    const filterOptions = term ? [...otherFilters, new FilterOption(this.searchFilterName, [term])] : otherFilters;
    this.handleNavigation(1, this.sortState(), filterOptions);
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    const page = params.get(PAGE_HEADER);
    this.page.set(+(page ?? 1));
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
    this.filters.initializeFromParams(params);
    this.searchTerm.set(this.filters.filterOptions.find(option => option.name === this.searchFilterName)?.values[0] ?? '');
  }

  protected fillComponentAttributesFromResponseBody(data: IDemandePriseEnCharge[]): IDemandePriseEnCharge[] {
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
      eagerload: true,
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    for (const filterOption of this.filters.filterOptions) {
      queryObject[filterOption.name] = filterOption.values;
    }
    this.demandePriseEnChargeService.demandePriseEnChargesParams.set(queryObject);
  }

  protected handleNavigation(page: number, sortState: SortState, filterOptions?: IFilterOption[]): void {
    const queryParamsObj: any = {
      page,
      size: this.itemsPerPage(),
      sort: this.sortService.buildSortParam(sortState),
    };

    if (filterOptions) {
      for (const filterOption of filterOptions) {
        queryParamsObj[filterOption.nameAsQueryParam()] = filterOption.values;
      }
    }

    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute,
      queryParams: queryParamsObj,
    });
  }
}
