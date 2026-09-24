# Inventaire — état des lieux avant adaptation au design system

Produit selon l'étape 0 de `ADAPTATION.md`. Aucune modification n'a été faite au projet pour
produire ce document. À valider avant l'étape 1 (les jetons).

## Framework, version, stratégie de style

- **Angular 22** (standalone components, signals, `httpResource`), généré par **JHipster 9.3**,
  backend Spring Boot séparé (non concerné par ce design system).
- **Bootstrap 5.3** + **ng-bootstrap 21** (modales, dropdowns, pagination, tabs) : la quasi-totalité
  des classes utilitaires et composants interactifs (`.btn`, `.form-control`, `.table`, `.alert`,
  `ngbDropdown`, `ngbPagination`, `ngbCollapse`) viennent de Bootstrap, pas de composants maison.
- **FontAwesome 5 (`@fortawesome/angular-fontawesome`)** pour toutes les icônes (`<fa-icon icon="...">`),
  contrairement au système fourni qui dessine ses icônes en SVG inline 16px/1.5px trait.
- Style en **SCSS global**, pas de CSS Modules ni de `ViewEncapsulation` particulière observée sur
  les composants métier. Trois fichiers pilotent tout :
  - `content/scss/global.scss` (677 lignes) — le gros du style custom, décrit ci-dessous ;
  - `content/scss/vendor.scss` — imports Bootstrap + overrides de variables Bootstrap ;
  - `content/scss/_bootstrap-variables.scss` — variables Sass Bootstrap (couleurs de thème Bootstrap,
    pas les couleurs métier).
  - `app/layouts/navbar/navbar.scss` — sidebar/topbar, encapsulé au composant (seul endroit qui utilise
    l'encapsulation Angular par défaut plutôt que le global).
- Import Sass en `@import` (déprécié par Dart Sass, à migrer un jour, indépendant de ce chantier).

## Où sont définies les couleurs et tailles aujourd'hui

**Aucun système de tokens.** Trois strates coexistent, aucune ne se réclame de l'autre :

1. **Couleurs Bootstrap** (`_bootstrap-variables.scss`) : palette de thème générique
   (`$primary`, `$danger`…), utilisée par les classes `.btn-primary`, `.alert-danger`, etc.
2. **Variables `--jhi-*` maison** (`global.scss:255-267`) : 12 custom properties CSS
   (`--jhi-accent: #61b577`, `--jhi-danger: #ef1d25`, `--jhi-warning: #e08a00`, `--jhi-info: #3d7fc4`,
   `--jhi-muted: #6c757d`, chacune avec sa variante `-soft`, plus `--jhi-card-radius: 12px` et
   `--jhi-card-shadow`). C'est la vraie base du style des écrans d'entités (panneaux, tableaux,
   formulaires, badges). Un seul thème : **aucun mode sombre nulle part** dans le projet.
3. **Valeurs en dur, partout** : la page de login (`global.scss:213-370`) n'utilise aucune variable
   (`#f5f5f5`, `#70c486`, `#ef1d25`… en clair), les titres de page sont en Georgia serif codé en dur
   (`font-family: Georgia, 'Times New Roman', serif`, `global.scss:271`), et les badges de statut/priorité
   sont ~25 règles `.jhi-badge-statut-XXX { background: var(--jhi-warning-soft); color: var(--jhi-warning); }`
   une par valeur d'enum métier (voir plus bas).

Recherche rapide de valeurs en dur dans les fichiers de style :
`grep -nE '#[0-9a-fA-F]{3,8}' src/main/webapp/content/scss/global.scss` → **plus de 40 occurrences**,
essentiellement dans le bloc "Login page". C'est la checklist finale de `CLAUDE.md` appliquée à
l'état actuel : elle échoue largement, ce qui est normal avant l'étape 1.

## Écrans existants, et le plus proche d'une liste de dossiers

17 modules d'entités sous `app/fonctionnalites/` (CRUD générés par JHipster : liste, détail, formulaire,
suppression), plus tableau de bord (`home`), authentification (`login`, `account/*`), et écrans admin
JHipster standard (santé, métriques, logs, utilisateurs).

**`demande-prise-en-charge`** est l'écran le plus proche d'`EcranListeDemandes` : c'est la liste des
dossiers de prise en charge, avec tri, pagination, et une action « Imprimer » qui télécharge un rapport
PDF. Colonnes actuelles : `reference · agent/ayantDroit · établissement de santé · statut · priorité ·
date de création · date d'échéance · assigné à · actions`. Le design system attend
`Dossier · Assuré · Nature · Structure · Statut · Dépôt · Ancienneté · Plafond · actions` — proche dans
l'esprit, mais pas aligné (voir écarts).

Second écran candidat pour l'étape 3 (détail) : `demande-prise-en-charge/detail`, avec ses boutons de
workflow (Valider / Rejeter / Resoumettre) — à comparer à `EcranDetailDossier` et `EtapesDossier`.

## Table de correspondance

| Fiche du design system | Équivalent dans le projet                                                                                  | Écart                                                                                                                                                                                   |
| ---------------------- | ---------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `Bouton`               | `.btn`, `.btn-accent`, `.btn-secondary` (Bootstrap + `--jhi-accent`)                                       | Pas de variante `discret`. Couleur codée par variable maison, pas par jeton `--brand-600`.                                                                                              |
| `Champ`                | `.form-control`, `.form-select` (Bootstrap) + `.jhi-field` (wrapper maison)                                | Pas de mode `mono` (chasse fixe) — aucune police mono n'est chargée dans tout le projet.                                                                                                |
| `BadgeStatut`          | `.jhi-badge` + ~25 classes `.jhi-badge-statut-XXX` une par valeur d'enum                                   | Écart majeur, voir "Statuts" ci-dessous : vocabulaire et cardinalité différents.                                                                                                        |
| `Panneau`              | `.jhi-panel`, `.jhi-form-panel`, `.jhi-table-panel` (`@extend`)                                            | Ombre systématique (`--jhi-card-shadow`) là où le système distingue panneau (bordure) et couche flottante (ombre).                                                                      |
| `Alerte`               | `jhi-alert`, `jhi-alert-error` (composants maison) + `.alert` Bootstrap                                    | Existe déjà comme composant Angular dédié — bon point d'ancrage pour l'étape 3.                                                                                                         |
| `TableauDossiers`      | `.jhi-table` + balisage `<table>` répété dans chaque `list/*.html`                                         | Pas de composant réutilisable : chaque entité a sa propre table HTML. Colonne actions déjà en `position: sticky`, ce que le système ne demande pas explicitement mais ne contredit pas. |
| `Pagination`           | `<ngb-pagination>` + `<jhi-item-count>`                                                                    | ng-bootstrap gère déjà l'essentiel ; à envelopper plutôt qu'à remplacer.                                                                                                                |
| `ListeDefinitions`     | `.row-md.jh-entity-details` (grille `<dt>/<dd>`) sur chaque écran `detail/*.html`                          | Structurellement proche, juste à retoucher visuellement.                                                                                                                                |
| `Onglets`              | **Absent**                                                                                                 | Aucun écran n'utilise d'onglets aujourd'hui.                                                                                                                                            |
| `EtapesDossier`        | **Absent** (le statut de la demande est un badge unique, pas une frise)                                    | À construire : c'est l'écran `demande-prise-en-charge/detail` qui en a le plus besoin (workflow DRH → infirmerie).                                                                      |
| `CarteAssure`          | **Absent**                                                                                                 | L'écran détail demande liste l'agent/ayant droit en `<dt>/<dd>` classiques, pas en carte dédiée.                                                                                        |
| `TuileIndicateur`      | **Absent**                                                                                                 | `home` (tableau de bord) est un écran quasi vide aujourd'hui — à construire de zéro si on veut un vrai tableau de bord.                                                                 |
| `BarreFiltres`         | `<jhi-filter>` (composant maison, `app/shared/filter`)                                                     | Existe déjà, à vérifier fiche par fiche à l'étape 3.                                                                                                                                    |
| `CadreApplication`     | `navbar.html`/`navbar.ts` (sidebar 216px déjà proche — actuellement `260px` en grid + logo CNSS déjà posé) | Bonne base : structure similaire, dimensions à réaligner (`260px` → `216px`, en-tête `56px` → `52px`).                                                                                  |

## Ce qui empêche aujourd'hui les règles non négociables de `CLAUDE.md`

1. **Couleurs/tailles/rayons en dur** — massivement, voir ci-dessus. Non bloquant en soi : l'étape 1
   (jetons + alias `--couleur-primaire: var(--brand-600)`) est faite pour ça.
2. **Les six statuts fermés (`deposee/instruction/piece-manquante/accordee/rejetee/cloturee`)** —
   **écart réel, pas seulement cosmétique.** Le workflow métier de ce projet est différent :
   `StatutDemande` (backend) vaut `EN_ATTENTE_VALIDATION_DRH`, `EN_ATTENTE_VALIDATION_INFIRMERIE`,
   `VALIDEE`, `RETOURNEE` — un circuit de double validation hiérarchique (DRH puis infirmerie), pas un
   cycle guichet → instruction → pièce manquante → décision. Il n'y a ni « pièce manquante » ni
   « clôturée » dans le modèle actuel (pas de `PieceJustificative` bloquant le statut, pas d'état de
   fin de vie après validation). **Je ne peux pas mapper les deux vocabulaires sans trancher une
   question métier** : soit on **renomme l'affichage** (garder les 6 libellés du système, en acceptant
   qu'ils ne collent qu'approximativement au workflow réel), soit on **documente un écart dans
   `ECARTS.md`** et on garde le vocabulaire métier existant avec les jetons de couleur/forme du système
   (`BadgeStatut` sait déjà accepter un `libelle` de remplacement). Je recommande la seconde option et
   l'ai notée pour votre arbitrage plutôt que de trancher seul, comme demandé par `CLAUDE.md`.
3. **Chasse fixe pour matricule/numéro/montant/date/code** — aucune police mono n'est chargée dans le
   projet aujourd'hui ; `IBM Plex Mono` est à ajouter à l'étape 1.
4. **Montants en francs CFA, entiers, alignés à droite** — **écart de modèle de données** :
   `DemandePriseEnCharge` (backend) n'a **aucun champ monétaire** (pas de montant, pas de plafond). Le
   système suppose un usage de remboursement/plafond ; ce projet est une **autorisation de soins**
   (prise en charge médicale), pas un flux financier. La colonne « Plafond » d'`EcranListeDemandes` n'a
   pas d'équivalent possible sans ajouter un champ au domaine — hors périmètre d'une adaptation
   purement visuelle. À documenter dans `ECARTS.md` : cette colonne sera omise, pas approximée.
5. **Anneau de focus jamais supprimé** — pas de `outline: none` trouvé dans `global.scss` ; Bootstrap
   gère le focus par défaut. À vérifier composant par composant à l'étape 3, mais pas de blocage connu.
6. **Le logo est employé tel quel, jamais sous 32px** — déjà fait : le logo CNSS est posé dans la
   sidebar à 40×40px (`navbar.scss`, ajouté hors de ce chantier). Bon point de départ, rien à corriger.
7. **Le français est administratif et direct** — globalement déjà le cas dans les libellés d'action
   (« Imprimer », « Valider », « Rejeter »), mais `CLAUDE.md` demande des libellés nommant l'action
   précise (« Accorder la prise en charge » plutôt que « Valider ») : à revoir écran par écran à
   l'étape 4, pas un blocage pour démarrer.

## Ce qui n'est PAS un blocage

- `ng-bootstrap` et `FontAwesome` n'ont pas besoin d'être retirés : `ADAPTATION.md` dit explicitement
  de garder une bibliothèque tierce et de la rethémer par-dessus. Les icônes FontAwesome peuvent
  cohabiter avec les nouveaux SVG du système (les remplacer un par un serait un chantier séparé, non
  demandé).
- `jhi-alert`/`jhi-alert-error`/`jhi-filter` sont déjà des composants Angular dédiés : bons points
  d'ancrage pour les fiches `Alerte` et `BarreFiltres` à l'étape 3, pas à réécrire de zéro.

---

**Deux décisions à valider avant l'étape 1** (au-delà du feu vert général) :

1. Statuts : vocabulaire du design system imposé tel quel, ou vocabulaire métier existant conservé
   avec les jetons visuels du système + `ECARTS.md` ?
2. Colonne « Plafond » : omise (recommandé, pas de champ monétaire dans le modèle), ou faut-il
   envisager d'ajouter un champ montant à `DemandePriseEnCharge` — ce qui sortirait du cadre d'une
   adaptation visuelle et redeviendrait un chantier fonctionnel comme Profil ?
