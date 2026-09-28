package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.BoiteReception;
import com.mycompany.myapp.domain.Profil;
import com.mycompany.myapp.service.dto.BoiteReceptionDTO;
import com.mycompany.myapp.service.dto.ProfilDTO;
import java.util.List;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link BoiteReception} and its DTO {@link BoiteReceptionDTO}.
 *
 * <p>Ce mapper ne va que dans un sens. Il n'etend pas {@link EntityMapper} parce qu'une boite
 * n'est jamais construite a partir d'un DTO : elle nait avec son profil, cote serveur, et aucune
 * route n'en accepte en entree.
 */
@Mapper(componentModel = "spring")
public interface BoiteReceptionMapper {
    @Mapping(target = "nombreTaches", ignore = true)
    @Mapping(target = "nombreOuvertes", ignore = true)
    @Mapping(target = "profil", source = "profil", qualifiedByName = "profilNom")
    BoiteReceptionDTO toDto(BoiteReception boiteReception);

    List<BoiteReceptionDTO> toDto(List<BoiteReception> boitesReception);

    /**
     * Le profil est expose par son nom seul : la liste de ses droits n'a rien a faire dans une
     * boite de reception, et la charger imposerait une jointure de plus a chaque ligne.
     */
    @Named("profilNom")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nom", source = "nom")
    ProfilDTO toDtoProfilNom(Profil profil);
}
