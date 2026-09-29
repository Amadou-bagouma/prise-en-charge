import { Service, computed, effect, inject, signal } from '@angular/core';

import { Client, IMessage } from '@stomp/stompjs';

import { AccountService } from 'app/core/auth';
import { StateStorageService } from 'app/core/auth';

/** Ce que le serveur remet sur le canal : de quoi afficher un avis, pas d'instruire un dossier. */
export interface AvisDiffuse {
  id: number;
  titre?: string | null;
  message?: string | null;
  type?: string | null;
  demandeId?: number | null;
  reference?: string | null;
  dateCreation?: string | null;
}

/** La file personnelle, telle que la nomme `WebsocketConfiguration`. */
const FILE = '/user/queue/avis';

/** Le point de rendez-vous, sous `/api` comme le reste. */
const POINT_ENTREE = 'api/avis';

/**
 * L'adresse du canal, deduite de celle de la page.
 *
 * WebSocket natif plutôt que SockJS : la bibliothèque SockJS s'attend à la variable `global` de
 * Node, absente du navigateur, et son seul chargement faisait tomber l'application entière —
 * page blanche, sans message. Le repli qu'elle apporte ne vaut pas ce risque.
 */
function adresseCanal(jeton: string): string {
  const protocole = location.protocol === 'https:' ? 'wss:' : 'ws:';
  return `${protocole}//${location.host}/${POINT_ENTREE}?access_token=${encodeURIComponent(jeton)}`;
}

/**
 * Reçoit les avis pendant qu'on travaille, au lieu du prochain changement d'écran.
 *
 * Un dossier retourné pour correction attendait jusqu'ici que son auteur pense à regarder sa
 * boîte — et le délai de validité de quatorze jours courait pendant ce temps.
 *
 * Le canal ne fait que prévenir plus tôt. Un avis manqué — connexion coupée, onglet fermé — ne
 * se perd pas : il reste en base, et la cloche le montre au prochain chargement. Rien de ce qui
 * arrive ici n'est donc traité comme la source de vérité.
 */
@Service()
export class AvisTempsReelService {
  /** Le dernier avis reçu, que la cloche observe pour se rafraîchir et alerter. */
  readonly dernier = signal<AvisDiffuse | null>(null);

  readonly connecte = signal(false);

  private readonly accountService = inject(AccountService);
  private readonly stateStorageService = inject(StateStorageService);

  private client: Client | null = null;

  /** Vrai quand un compte est ouvert : c'est ce qui décide d'ouvrir ou de fermer le canal. */
  private readonly authentifie = computed(() => !!this.accountService.account());

  constructor() {
    effect(() => {
      if (this.authentifie()) {
        this.connecter();
      } else {
        this.deconnecter();
      }
    });
  }

  private connecter(): void {
    if (this.client) {
      return;
    }
    const jeton = this.jeton();
    if (!jeton) {
      // Sans jeton la poignée de main serait anonyme, et le serveur n'aurait aucun
      // destinataire à qui remettre l'avis : il partirait dans le vide, sans erreur.
      return;
    }

    const client = new Client({
      brokerURL: adresseCanal(jeton),
      // Une reconnexion silencieuse : la coupure d'un instant ne doit pas exiger un rechargement.
      reconnectDelay: 5000,
      // Le canal n'a pas a etre bavard dans la console de qui utilise l'application.
      debug: () => undefined,
    });

    client.onConnect = () => {
      this.connecte.set(true);
      client.subscribe(FILE, (message: IMessage) => this.recevoir(message));
    };
    client.onWebSocketClose = () => this.connecte.set(false);
    client.onStompError = () => this.connecte.set(false);

    client.activate();
    this.client = client;
  }

  private deconnecter(): void {
    this.connecte.set(false);
    const client = this.client;
    this.client = null;
    if (client) {
      void client.deactivate();
    }
  }

  private recevoir(message: IMessage): void {
    try {
      this.dernier.set(JSON.parse(message.body) as AvisDiffuse);
    } catch {
      // Un message illisible ne doit pas rompre l'abonnement : les suivants arriveront.
    }
  }

  /** Le jeton de la session, là où l'intercepteur HTTP le lit lui aussi. */
  private jeton(): string | null {
    return this.stateStorageService.getAuthenticationToken() ?? null;
  }
}
