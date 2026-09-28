import dayjs from 'dayjs/esm';

import { IProfil } from 'app/fonctionnalites/profil/profil.model';

/**
 * La boîte de réception d'un profil, et non d'une personne.
 *
 * Deux agents portant le même profil ouvrent la même boîte : le travail est adressé à un rôle,
 * il reste donc traitable quand l'un d'eux est absent.
 *
 * `nombreNonLus` et `nombreTaches` sont recalculés par le serveur à chaque lecture, à partir des
 * tâches réellement en portée : ils ne sont jamais servis depuis une colonne qui se périmerait.
 */
export interface IBoiteReception {
  id: number;
  dateCreation?: dayjs.Dayjs | null;
  dateDerniereLecture?: dayjs.Dayjs | null;
  nombreNonLus?: number | null;
  nombreTaches?: number | null;
  /** Parmi elles, celles qui restent à traiter : ni terminées, ni annulées. */
  nombreOuvertes?: number | null;
  actif?: boolean | null;
  profil?: Pick<IProfil, 'id' | 'nom'> | null;
}
