package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Agent;
import com.mycompany.myapp.domain.AyantDroit;
import com.mycompany.myapp.service.dto.AgentDTO;
import com.mycompany.myapp.service.dto.AyantDroitDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AyantDroit} and its DTO {@link AyantDroitDTO}.
 */
@Mapper(componentModel = "spring")
public interface AyantDroitMapper extends EntityMapper<AyantDroitDTO, AyantDroit> {
    @Mapping(target = "agent", source = "agent", qualifiedByName = "agentId")
    AyantDroitDTO toDto(AyantDroit s);

    @Named("agentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AgentDTO toDtoAgentId(Agent agent);
}
