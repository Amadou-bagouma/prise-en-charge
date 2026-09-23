package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Agent;
import com.mycompany.myapp.domain.AyantDroit;
import com.mycompany.myapp.domain.CarteBeneficiaire;
import com.mycompany.myapp.service.dto.AgentDTO;
import com.mycompany.myapp.service.dto.AyantDroitDTO;
import com.mycompany.myapp.service.dto.CarteBeneficiaireDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CarteBeneficiaire} and its DTO {@link CarteBeneficiaireDTO}.
 */
@Mapper(componentModel = "spring")
public interface CarteBeneficiaireMapper extends EntityMapper<CarteBeneficiaireDTO, CarteBeneficiaire> {
    @Mapping(target = "agent", source = "agent", qualifiedByName = "agentMatricule")
    @Mapping(target = "ayantDroit", source = "ayantDroit", qualifiedByName = "ayantDroitNom")
    CarteBeneficiaireDTO toDto(CarteBeneficiaire s);

    @Named("agentMatricule")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "matricule", source = "matricule")
    AgentDTO toDtoAgentMatricule(Agent agent);

    @Named("ayantDroitNom")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nom", source = "nom")
    AyantDroitDTO toDtoAyantDroitNom(AyantDroit ayantDroit);
}
