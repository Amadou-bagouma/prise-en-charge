package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.BoiteReceptionDTO;
import com.mycompany.myapp.service.dto.TacheDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.BoiteReception}.
 *
 * <p>Aucune methode de creation ni de suppression : une boite existe parce qu'un profil existe.
 * {@link #synchroniser()} est le seul point qui en cree, et il est idempotent.
 */
public interface BoiteReceptionService {
    /**
     * Cree les boites manquantes et recalcule les compteurs de toutes les boites.
     *
     * <p>Appele au demarrage et disponible aux administrateurs. Idempotent : le relancer ne cree
     * pas de doublon.
     *
     * @return le nombre de boites creees.
     */
    int synchroniser();

    /**
     * La boite du profil de l'utilisateur courant, compteurs recalcules.
     *
     * <p>Cree la boite a la volee si le profil n'en a pas encore : un utilisateur ne doit jamais
     * tomber sur une boite absente parce qu'un profil a ete ajoute entre deux synchronisations.
     */
    BoiteReceptionDTO maBoite();

    /**
     * Les taches visibles dans la boite de l'utilisateur courant : uniquement celles dont ses
     * droits lui donnent la charge.
     *
     * @param ouvertesSeulement ecarte les taches terminees et annulees. Le filtre est applique
     *        avant la pagination, faute de quoi le compteur de pages ne correspondrait pas aux
     *        lignes affichees.
     */
    Page<TacheDTO> mesTaches(Pageable pageable, boolean ouvertesSeulement);

    /** Enregistre que la boite vient d'etre consultee. */
    BoiteReceptionDTO marquerConsultee();

    /**
     * Marque une tache de la boite comme lue.
     *
     * @throws org.springframework.security.access.AccessDeniedException si la tache n'est pas
     *         adressee a la boite de l'utilisateur courant.
     * @return la tache mise a jour, vide si elle n'existe pas.
     */
    Optional<TacheDTO> marquerTacheLue(Long tacheId);

    /**
     * Attribue une tache de sa boite a l'utilisateur courant.
     *
     * <p>C'est le geste qui distingue une file partagee d'une file confuse : tant que personne ne
     * l'a prise, la tache attend ; des qu'elle est prise, les autres voient par qui.
     *
     * @return la tache mise a jour, vide si elle n'existe pas.
     */
    Optional<TacheDTO> prendreEnCharge(Long tacheId);

    /** Rend une tache a la file, si l'on ne peut finalement pas la traiter. */
    Optional<TacheDTO> relacher(Long tacheId);

    /** Toutes les boites, pour la console d'administration. */
    Page<BoiteReceptionDTO> findAll(Pageable pageable);

    /** Toutes les boites avec leur profil charge. */
    Page<BoiteReceptionDTO> findAllWithEagerRelationships(Pageable pageable);

    /** Une boite par son identifiant, pour la console d'administration. */
    Optional<BoiteReceptionDTO> findOne(Long id);
}
