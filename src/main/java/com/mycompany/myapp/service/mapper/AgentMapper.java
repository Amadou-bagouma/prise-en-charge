package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Agent;
import com.mycompany.myapp.domain.Direction;
import com.mycompany.myapp.domain.Fonction;
import com.mycompany.myapp.domain.Gestion;
import com.mycompany.myapp.service.dto.AgentDTO;
import com.mycompany.myapp.service.dto.DirectionDTO;
import com.mycompany.myapp.service.dto.FonctionDTO;
import com.mycompany.myapp.service.dto.GestionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Agent} and its DTO {@link AgentDTO}.
 */
@Mapper(componentModel = "spring")
public interface AgentMapper extends EntityMapper<AgentDTO, Agent> {
    @Mapping(target = "direction", source = "direction", qualifiedByName = "directionNom")
    @Mapping(target = "gestion", source = "gestion", qualifiedByName = "gestionNom")
    @Mapping(target = "fonction", source = "fonction", qualifiedByName = "fonctionLibelle")
    AgentDTO toDto(Agent s);

    @Named("directionNom")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nom", source = "nom")
    DirectionDTO toDtoDirectionNom(Direction direction);

    @Named("gestionNom")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nom", source = "nom")
    GestionDTO toDtoGestionNom(Gestion gestion);

    @Named("fonctionLibelle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "libelle", source = "libelle")
    FonctionDTO toDtoFonctionLibelle(Fonction fonction);
}
