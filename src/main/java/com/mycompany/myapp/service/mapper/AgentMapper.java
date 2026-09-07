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
    @Mapping(target = "direction", source = "direction", qualifiedByName = "directionId")
    @Mapping(target = "gestion", source = "gestion", qualifiedByName = "gestionId")
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    AgentDTO toDto(Agent s);

    @Named("directionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DirectionDTO toDtoDirectionId(Direction direction);

    @Named("gestionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    GestionDTO toDtoGestionId(Gestion gestion);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
