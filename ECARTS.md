# Écarts au design system CNSS

Ce fichier documente, comme demandé par `CLAUDE.md`, les cas où une contrainte du projet existant
empêche d'appliquer une règle non négociable du design system telle quelle. Ce ne sont pas des
oublis : ce sont des décisions prises avec le développeur (voir `INVENTAIRE.md` pour l'analyse
complète) plutôt que tranchées seul.

## 1. Vocabulaire des statuts

**Règle du système (CLAUDE.md #2)** : six statuts fermés — `deposee`, `instruction`,
`piece-manquante`, `accordee`, `rejetee`, `cloturee` — et rien d'autre.

**Écart** : le workflow réel de l'application (`StatutDemande`, backend) est une double validation
hiérarchique — `EN_ATTENTE_VALIDATION_DRH` → `EN_ATTENTE_VALIDATION_INFIRMERIE` → `VALIDEE`, ou
`RETOURNEE` en cas de rejet — sans notion de « pièce manquante » bloquante ni d'état de fin de vie
« clôturée » après validation.

**Décision** : on garde le vocabulaire métier existant (4 statuts) plutôt que de forcer les 6
libellés du système sur un cycle de vie qui ne leur correspond pas. Les jetons visuels du système
restent la référence : chaque statut métier se voit assigner un ton (`ok` / `warn` / `danger` /
`info` / `neutral`) et s'affiche via l'équivalent local de `BadgeStatut`, avec son propre libellé
(le composant du système accepte déjà une prop `libelle` de remplacement pour ce cas). Mapping
retenu, à appliquer à l'étape 3 :

| Statut métier                      | Ton      |
| ---------------------------------- | -------- |
| `EN_ATTENTE_VALIDATION_DRH`        | `warn`   |
| `EN_ATTENTE_VALIDATION_INFIRMERIE` | `warn`   |
| `VALIDEE`                          | `ok`     |
| `RETOURNEE`                        | `danger` |

Un rejet (`RETOURNEE`) continue de toujours s'afficher avec son motif dans la même vue (déjà le cas
aujourd'hui via `motifRejet`), conformément à la règle du système sur ce point précis.

## 2. Colonne « Plafond » (montant)

**Règle du système** : `EcranListeDemandes` affiche une colonne « Plafond », montant en francs CFA
aligné à droite en chasse fixe.

**Écart** : `DemandePriseEnCharge` (backend) n'a aucun champ monétaire. Cette application gère des
autorisations de soins (une « prise en charge médicale » au sens propre), pas un remboursement
plafonné : il n'existe ni montant ni plafond dans le modèle de données.

**Décision** : la colonne est omise, pas approximée. Ajouter un champ montant serait un changement
fonctionnel (nouveau champ métier, migration, formulaire, règles de validation) hors du périmètre
d'une adaptation visuelle — comparable en ampleur à la fonctionnalité Profil, pas à un réglage de
jetons. Si un tel besoin existe réellement côté métier, il doit être exprimé comme une demande de
fonctionnalité à part entière, pas glissé dans ce chantier.
