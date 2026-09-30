import { Component, OnInit, computed, inject, input, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { IAgent } from 'app/fonctionnalites/agent/agent.model';
import { AgentService } from 'app/fonctionnalites/agent/service/agent.service';
import { IAyantDroit } from 'app/fonctionnalites/ayant-droit/ayant-droit.model';
import { AyantDroitService } from 'app/fonctionnalites/ayant-droit/service/ayant-droit.service';
import { IParametre } from 'app/fonctionnalites/parametre/parametre.model';
import { ParametreService } from 'app/fonctionnalites/parametre/service/parametre.service';
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

  /** Le nom du directeur general, imprime sous sa signature : une signature seule ne se lit pas. */
  readonly nomDirecteur = signal('');

  /** L'adresse de la signature, ou vide si aucune n'a ete deposee au referentiel. */
  readonly signature = signal<string | null>(null);

  private readonly agentService = inject(AgentService);
  private readonly parametreService = inject(ParametreService);
  private readonly ayantDroitService = inject(AyantDroitService);

  ngOnInit(): void {
    this.chargerSignature();
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

  /**
   * Va chercher au referentiel le nom du directeur general et sa signature.
   *
   * Une carte sans signature s'imprime quand meme : bloquer l'impression parce qu'une image
   * manque au referentiel laisserait l'agent sans carte pour une raison qui ne le regarde pas.
   * L'emplacement reste vide, et cela se voit.
   */
  private chargerSignature(): void {
    this.parametreService.query().subscribe({
      next: parametres => {
        const nom = parametres.find((p: IParametre) => p.code === 'NOM_DIRECTEUR_GENERAL');
        this.nomDirecteur.set(nom?.valeur ?? '');
        const image = parametres.find((p: IParametre) => p.code === 'SIGNATURE_DIRECTEUR_GENERAL');
        this.signature.set(image?.valeurBinaireContentType ? this.parametreService.urlImage(image.code) : null);
      },
      error: () => {
        this.nomDirecteur.set('');
        this.signature.set(null);
      },
    });
  }

  print(): void {
    globalThis.print();
  }

  previousState(): void {
    globalThis.history.back();
  }
}
