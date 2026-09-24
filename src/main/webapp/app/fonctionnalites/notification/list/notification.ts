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
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { Filter, FilterOptions, IFilterOption, IFilterOptions } from 'app/shared/filter';
import { ConfirmService } from 'app/shared/confirm';
import { TranslateDirective } from 'app/shared/language';
import { ItemCount } from 'app/shared/pagination';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { NotificationDeleteDialog } from '../delete/notification-delete-dialog';
import { INotification } from '../notification.model';
import { NotificationService } from '../service/notification.service';

@Component({
  selector: 'jhi-notification',
  templateUrl: './notification.html',
  imports: [
    NgClass,
    RouterLink,
    FontAwesomeModule,
    AlertError,
    Alert,
    SortDirective,
    SortByDirective,
    TranslateDirective,
    FormatMediumDatetimePipe,
    Filter,
    NgbDropdown,
    NgbDropdownItem,
    NgbDropdownMenu,
    NgbDropdownToggle,
    NgbPagination,
    ItemCount,
  ],
})
export class Notification {
  readonly notifications = signal<INotification[]>([]);

  sortState = sortStateSignal({});
  filters: IFilterOptions = new FilterOptions();

  readonly itemsPerPage = signal(ITEMS_PER_PAGE);
  readonly totalItems = signal(0);
  readonly page = signal(1);

  readonly router = inject(Router);
  protected readonly notificationService = inject(NotificationService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.notificationService.notificationsResource.isLoading;
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
  protected readonly confirmService = inject(ConfirmService);

  constructor() {
    effect(() => {
      const headers = this.notificationService.notificationsResource.headers();
      if (headers) {
        this.fillComponentAttributesFromResponseHeader(headers);
      }
    });
    effect(() => {
      this.notifications.set(this.fillComponentAttributesFromResponseBody([...this.notificationService.notifications()]));
    });
    effect(() => {
      const activatedRouteState = this.activatedRouteState();
      untracked(() => {
        // Only watch for route changes. Other signals should be ignored.
        this.fillComponentAttributeFromRoute(activatedRouteState.queryParamMap, activatedRouteState.data);
        this.load();
      });
    });

    this.chargerEffectifs();

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

  trackId = (item: INotification): number => this.notificationService.getNotificationIdentifier(item);

  /**
   * Les deux vues de la boîte.
   *
   * « Non lues » d'abord, parce que c'est la question du jour : ce qui reste à voir. « Toutes »
   * sert à retrouver quelque chose, pas à travailler.
   */
  readonly puces: { cle: 'nonLues' | 'toutes'; libelle: string; filtre: Record<string, unknown> }[] = [
    { cle: 'nonLues', libelle: 'Non lues', filtre: { 'lu.equals': 'false' } },
    { cle: 'toutes', libelle: 'Toutes', filtre: {} },
  ];

  /**
   * La boîte est personnelle, y compris pour un administrateur.
   *
   * Sans ce drapeau, un administrateur y verrait les notifications de tous les agents alors
   * que « tout marquer comme lu » ne solde que les siennes : l'écran se contredirait.
   */
  private readonly PERIMETRE_PERSONNEL = { mesNotifications: true };

  readonly puceActive = signal<'nonLues' | 'toutes'>('nonLues');
  readonly effectifs = signal<Record<string, number>>({});
  readonly isSoldant = signal(false);

  /** Le ton du badge de type : ce qui appelle une action se distingue de ce qui informe. */
  typeTon(type?: string | null): string {
    switch (type) {
      case 'DEMANDE_VALIDEE':
        return 'ok';
      case 'DEMANDE_REJETEE':
        return 'danger';
      case 'ECHEANCE_PROCHE':
        return 'warn';
      case 'INFORMATION':
        return 'neutre';
      default:
        return 'info';
    }
  }

  /**
   * L'écran vers lequel la notification renvoie, ou `null` si elle n'en désigne aucun.
   *
   * Une notification qui ne mène nulle part oblige l'agent à retrouver le dossier lui-même :
   * quand elle en cite un, le lien y va directement.
   */
  lienCible(notification: INotification): unknown[] | null {
    if (notification.demande) {
      return ['/demande-prise-en-charge', notification.demande.id, 'view'];
    }
    if (notification.tache) {
      return ['/tache', notification.tache.id, 'view'];
    }
    return null;
  }

  choisirPuce(cle: 'nonLues' | 'toutes'): void {
    this.puceActive.set(cle);
    this.page.set(1);
    this.queryBackend();
  }

  marquerLue(notification: INotification): void {
    if (notification.lu) {
      return;
    }
    this.notificationService.marquerLue(notification.id).subscribe(() => {
      this.load();
      this.chargerEffectifs();
    });
  }

  /**
   * Solder la boîte engage : on le fait confirmer, comme toute action qui touche à tout.
   *
   * Le chiffre annoncé est exact : la boîte étant personnelle (voir {@link PERIMETRE_PERSONNEL}),
   * l'effectif affiché et ce que l'action va marquer portent sur le même périmètre.
   */
  marquerToutLu(): void {
    const nonLues = this.effectifs().nonLues ?? 0;
    if (nonLues === 0) {
      return;
    }
    this.confirmService
      .confirmer({
        titre: 'Marquer vos notifications comme lues',
        message: `Vos ${nonLues} notifications non lues seront marquées comme lues. Elles restent consultables dans « Toutes ».`,
        libelleConfirmer: 'Tout marquer comme lu',
      })
      .subscribe(() => {
        this.isSoldant.set(true);
        this.notificationService.marquerToutLu().subscribe({
          next: () => {
            this.isSoldant.set(false);
            this.load();
            this.chargerEffectifs();
          },
          error: () => this.isSoldant.set(false),
        });
      });
  }

  /** Effectif de chaque vue, affiché sur sa puce. */
  protected chargerEffectifs(): void {
    for (const puce of this.puces) {
      this.notificationService.count({ ...this.PERIMETRE_PERSONNEL, ...puce.filtre }).subscribe(nombre => {
        this.effectifs.update(effectifs => ({ ...effectifs, [puce.cle]: nombre }));
      });
    }
  }

  delete(notification: INotification): void {
    const modalRef = this.modalService.open(NotificationDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.notification = notification;
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

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    const page = params.get(PAGE_HEADER);
    this.page.set(+(page ?? 1));
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
    this.filters.initializeFromParams(params);
  }

  protected fillComponentAttributesFromResponseBody(data: INotification[]): INotification[] {
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
    Object.assign(queryObject, this.PERIMETRE_PERSONNEL, this.puces.find(puce => puce.cle === this.puceActive())?.filtre ?? {});
    this.notificationService.notificationsParams.set(queryObject);
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
