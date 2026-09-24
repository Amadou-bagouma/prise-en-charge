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
import java.util.Set;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DemandePriseEnCharge} and its DTO {@link DemandePriseEnChargeDTO}.
 */
@Mapper(componentModel = "spring")
public interface DemandePriseEnChargeMapper extends EntityMapper<DemandePriseEnChargeDTO, DemandePriseEnCharge> {
    @Mapping(target = "agent", source = "agent", qualifiedByName = "agentMatricule")
    @Mapping(target = "ayantDroit", source = "ayantDroit", qualifiedByName = "ayantDroitNom")
    @Mapping(target = "etablissementSante", source = "etablissementSante", qualifiedByName = "etablissementSanteNom")
    @Mapping(target = "gestionnaireCreateur", source = "gestionnaireCreateur", qualifiedByName = "userLogin")
    @Mapping(target = "assigneA", source = "assigneA", qualifiedByName = "userLogin")
    DemandePriseEnChargeDTO toDto(DemandePriseEnCharge s);

    /**
     * Un type de soin remonte sans les dossiers qui le portent : la relation inverse ferait
     * revenir la moitie de la base derriere chaque case a cocher.
     */
    @Named("typeSoinSansDemandes")
    @Mapping(target = "demandes", ignore = true)
    TypeSoin toEntityTypeSoin(TypeSoinDTO typeSoinDTO);

    @IterableMapping(qualifiedByName = "typeSoinSansDemandes")
    Set<TypeSoin> toEntityTypeSoins(Set<TypeSoinDTO> typeSoinDTOs);

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
