package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Agent;
import com.mycompany.myapp.domain.Direction;
import com.mycompany.myapp.domain.Gestion;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.dto.AgentDTO;
import com.mycompany.myapp.service.dto.DirectionDTO;
import com.mycompany.myapp.service.dto.GestionDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Agent} and its DTO {@link AgentDTO}.
 */
@Mapper(componentModel = "spring")
public interface AgentMapper extends EntityMapper<AgentDTO, Agent> {
    @Mapping(target = "direction", source = "direction", qualifiedByName = "directionNom")
    @Mapping(target = "gestion", source = "gestion", qualifiedByName = "gestionNom")
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
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

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
