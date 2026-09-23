import { Component, OnInit, computed, inject, input, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { IAgent } from 'app/entities/agent/agent.model';
import { AgentService } from 'app/entities/agent/service/agent.service';
import { IAyantDroit } from 'app/entities/ayant-droit/ayant-droit.model';
import { AyantDroitService } from 'app/entities/ayant-droit/service/ayant-droit.service';
import { FormatMediumDatePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { ICarteBeneficiaire } from '../carte-beneficiaire.model';

@Component({
  selector: 'jhi-carte-beneficiaire-print',
  templateUrl: './carte-beneficiaire-print.html',
  styleUrl: './carte-beneficiaire-print.scss',
  imports: [FontAwesomeModule, TranslateDirective, RouterLink, FormatMediumDatePipe],
})
export class CarteBeneficiairePrint implements OnInit {
  readonly carteBeneficiaire = input<ICarteBeneficiaire | null>(null);

  readonly agent = signal<IAgent | null>(null);
  readonly ayantDroit = signal<IAyantDroit | null>(null);

  readonly titulaireNom = computed(() => this.agent()?.nom ?? this.ayantDroit()?.nom ?? '');
  readonly titulairePrenom = computed(() => this.agent()?.prenom ?? this.ayantDroit()?.prenom ?? '');
  readonly matricule = computed(() => this.agent()?.matricule ?? this.ayantDroit()?.agent?.matricule ?? '');
  readonly photo = computed(() => this.agent()?.photo ?? this.ayantDroit()?.photo ?? null);
  readonly photoContentType = computed(() => this.agent()?.photoContentType ?? this.ayantDroit()?.photoContentType ?? null);

  private readonly agentService = inject(AgentService);
  private readonly ayantDroitService = inject(AyantDroitService);

  ngOnInit(): void {
    const carte = this.carteBeneficiaire();
    const agentId = carte?.agent?.id;
    const ayantDroitId = carte?.ayantDroit?.id;

    if (agentId != null) {
      this.agentService.find(agentId).subscribe(agent => this.agent.set(agent));
    }
    if (ayantDroitId != null) {
      this.ayantDroitService.find(ayantDroitId).subscribe(ayantDroit => this.ayantDroit.set(ayantDroit));
    }
  }

  print(): void {
    globalThis.print();
  }

  previousState(): void {
    globalThis.history.back();
  }
}
