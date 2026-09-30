package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.enumeration.TypeBeneficiaire;
import com.mycompany.myapp.service.dto.CarteBeneficiaireDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.CarteBeneficiaire}.
 */
public interface CarteBeneficiaireService {
    /**
     * Etablit une carte pour un beneficiaire, numero et periode de validite compris.
     *
     * <p>Rien n'est demande a l'appelant que le beneficiaire : le numero est pose par le
     * serveur, la periode court de ce jour, et la date d'emission est celle du jour. Laisser
     * saisir tout cela ouvrait la porte a deux cartes du meme numero et a des periodes
     * antidatees, que rien dans l'application n'aurait relevees.
     *
     * @param typeBeneficiaire {@code AGENT} ou {@code AYANT_DROIT}.
     * @param beneficiaireId l'agent ou l'ayant droit concerne.
     * @return la carte etablie.
     */
    CarteBeneficiaireDTO generer(TypeBeneficiaire typeBeneficiaire, Long beneficiaireId);

    /**
     * Etablit le duplicata d'une carte perdue ou volee.
     *
     * <p>La carte d'origine cesse d'etre valable le jour meme, et le duplicata prend sa suite
     * jusqu'au terme initialement prevu. La periode n'est pas repartie a neuf : un duplicata
     * remplace, il ne proroge pas - sans quoi perdre sa carte deviendrait un moyen d'en obtenir
     * une plus longue.
     *
     * @param carteId la carte perdue.
     * @param motif ce qui est arrive, conserve au journal.
     * @return le duplicata.
     */
    CarteBeneficiaireDTO dupliquer(Long carteId, String motif);

    /**
     * Save a carteBeneficiaire.
     *
     * @param carteBeneficiaireDTO the entity to save.
     * @return the persisted entity.
     */
    CarteBeneficiaireDTO save(CarteBeneficiaireDTO carteBeneficiaireDTO);

    /**
     * Updates a carteBeneficiaire.
     *
     * @param carteBeneficiaireDTO the entity to update.
     * @return the persisted entity.
     */
    CarteBeneficiaireDTO update(CarteBeneficiaireDTO carteBeneficiaireDTO);

    /**
     * Partially updates a carteBeneficiaire.
     *
     * @param carteBeneficiaireDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CarteBeneficiaireDTO> partialUpdate(CarteBeneficiaireDTO carteBeneficiaireDTO);

    /**
     * Get all the carteBeneficiaires with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CarteBeneficiaireDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" carteBeneficiaire.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CarteBeneficiaireDTO> findOne(Long id);

    /**
     * Delete the "id" carteBeneficiaire.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
