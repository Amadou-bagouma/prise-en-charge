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
    @Mapping(target = "agent", source = "agent", qualifiedByName = "agentMatricule")
    AyantDroitDTO toDto(AyantDroit s);

    @Named("agentMatricule")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "matricule", source = "matricule")
    AgentDTO toDtoAgentMatricule(Agent agent);
}
