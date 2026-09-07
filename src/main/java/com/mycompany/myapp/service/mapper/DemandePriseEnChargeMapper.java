package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Agent;
import com.mycompany.myapp.domain.AyantDroit;
import com.mycompany.myapp.domain.DemandePriseEnCharge;
import com.mycompany.myapp.domain.EtablissementSante;
import com.mycompany.myapp.domain.TypeSoin;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.dto.AgentDTO;
import com.mycompany.myapp.service.dto.AyantDroitDTO;
import com.mycompany.myapp.service.dto.DemandePriseEnChargeDTO;
import com.mycompany.myapp.service.dto.EtablissementSanteDTO;
import com.mycompany.myapp.service.dto.TypeSoinDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DemandePriseEnCharge} and its DTO {@link DemandePriseEnChargeDTO}.
 */
@Mapper(componentModel = "spring")
public interface DemandePriseEnChargeMapper extends EntityMapper<DemandePriseEnChargeDTO, DemandePriseEnCharge> {
    @Mapping(target = "agent", source = "agent", qualifiedByName = "agentId")
    @Mapping(target = "ayantDroit", source = "ayantDroit", qualifiedByName = "ayantDroitId")
    @Mapping(target = "typeSoin", source = "typeSoin", qualifiedByName = "typeSoinId")
    @Mapping(target = "etablissementSante", source = "etablissementSante", qualifiedByName = "etablissementSanteId")
    @Mapping(target = "gestionnaireCreateur", source = "gestionnaireCreateur", qualifiedByName = "userLogin")
    @Mapping(target = "assigneA", source = "assigneA", qualifiedByName = "userLogin")
    DemandePriseEnChargeDTO toDto(DemandePriseEnCharge s);

    @Named("agentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AgentDTO toDtoAgentId(Agent agent);

    @Named("ayantDroitId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AyantDroitDTO toDtoAyantDroitId(AyantDroit ayantDroit);

    @Named("typeSoinId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TypeSoinDTO toDtoTypeSoinId(TypeSoin typeSoin);

    @Named("etablissementSanteId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EtablissementSanteDTO toDtoEtablissementSanteId(EtablissementSante etablissementSante);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
