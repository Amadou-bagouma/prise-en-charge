import { NgClass } from '@angular/common';
import { HttpHeaders } from '@angular/common/http';
import { Component, effect, inject, signal, untracked } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Data, ParamMap, Router, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbDropdown, NgbDropdownItem, NgbDropdownMenu, NgbDropdownToggle } from '@ng-bootstrap/ng-bootstrap/dropdown';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { NgbPagination } from '@ng-bootstrap/ng-bootstrap/pagination';
import { combineLatest, filter, map, tap } from 'rxjs';

import { DEFAULT_SORT_DATA, ITEMS_PER_PAGE, ITEM_DELETED_EVENT, PAGE_HEADER, SORT, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config';
import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatePipe } from 'app/shared/date';
import { Filter, FilterOption, FilterOptions, IFilterOption, IFilterOptions } from 'app/shared/filter';
import { TranslateDirective } from 'app/shared/language';
import { ItemCount } from 'app/shared/pagination';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { IAyantDroit } from '../ayant-droit.model';
import { AyantDroitDeleteDialog } from '../delete/ayant-droit-delete-dialog';
import { AyantDroitService } from '../service/ayant-droit.service';

@Component({
  selector: 'jhi-ayant-droit',
  templateUrl: './ayant-droit.html',
  imports: [
    NgClass,
    RouterLink,
    FontAwesomeModule,
    AlertError,
    Alert,
    SortDirective,
    SortByDirective,
    TranslateDirective,
    FormatMediumDatePipe,
    Filter,
    NgbDropdown,
    NgbDropdownItem,
    NgbDropdownMenu,
    NgbDropdownToggle,
    NgbPagination,
    ItemCount,
  ],
})
export class AyantDroit {
  readonly ayantDroits = signal<IAyantDroit[]>([]);

  sortState = sortStateSignal({});
  filters: IFilterOptions = new FilterOptions();
  readonly searchTerm = signal('');
  private readonly searchFilterName = 'nom.contains';

  readonly itemsPerPage = signal(ITEMS_PER_PAGE);
  readonly totalItems = signal(0);
  readonly page = signal(1);

  readonly router = inject(Router);
  protected readonly ayantDroitService = inject(AyantDroitService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.ayantDroitService.ayantDroitsResource.isLoading;
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

  constructor() {
    effect(() => {
      const headers = this.ayantDroitService.ayantDroitsResource.headers();
      if (headers) {
        this.fillComponentAttributesFromResponseHeader(headers);
      }
    });
    effect(() => {
      this.ayantDroits.set(this.fillComponentAttributesFromResponseBody([...this.ayantDroitService.ayantDroits()]));
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

  trackId = (item: IAyantDroit): number => this.ayantDroitService.getAyantDroitIdentifier(item);

  delete(ayantDroit: IAyantDroit): void {
    const modalRef = this.modalService.open(AyantDroitDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.ayantDroit = ayantDroit;
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

  protected fillComponentAttributesFromResponseBody(data: IAyantDroit[]): IAyantDroit[] {
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
    this.ayantDroitService.ayantDroitsParams.set(queryObject);
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
