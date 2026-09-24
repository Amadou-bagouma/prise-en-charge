package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.TypeSoinDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.TypeSoin}.
 */
public interface TypeSoinService {
    TypeSoinDTO save(TypeSoinDTO typeSoinDTO);

    TypeSoinDTO update(TypeSoinDTO typeSoinDTO);

    Optional<TypeSoinDTO> partialUpdate(TypeSoinDTO typeSoinDTO);

    Optional<TypeSoinDTO> findOne(Long id);

    /**
     * Les types proposes a la saisie d'une demande, dans l'ordre de l'imprime officiel.
     *
     * @return les types en service.
     */
    List<TypeSoinDTO> findAllActifs();

    /**
     * Supprime un type de soin.
     *
     * @throws com.mycompany.myapp.web.rest.errors.BadRequestAlertException si le type est
     *     encore porte par un dossier : le retirer effacerait une information d'un dossier
     *     deja instruit. On le desactive alors plutot que de le supprimer.
     */
    void delete(Long id);
}
