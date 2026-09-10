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
    @Mapping(target = "agent", source = "agent", qualifiedByName = "agentMatricule")
    @Mapping(target = "ayantDroit", source = "ayantDroit", qualifiedByName = "ayantDroitNom")
    @Mapping(target = "typeSoin", source = "typeSoin", qualifiedByName = "typeSoinLibelle")
    @Mapping(target = "etablissementSante", source = "etablissementSante", qualifiedByName = "etablissementSanteNom")
    @Mapping(target = "gestionnaireCreateur", source = "gestionnaireCreateur", qualifiedByName = "userLogin")
    @Mapping(target = "assigneA", source = "assigneA", qualifiedByName = "userLogin")
    DemandePriseEnChargeDTO toDto(DemandePriseEnCharge s);

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

    @Named("typeSoinLibelle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "libelle", source = "libelle")
    TypeSoinDTO toDtoTypeSoinLibelle(TypeSoin typeSoin);

    @Named("etablissementSanteNom")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nom", source = "nom")
    EtablissementSanteDTO toDtoEtablissementSanteNom(EtablissementSante etablissementSante);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
