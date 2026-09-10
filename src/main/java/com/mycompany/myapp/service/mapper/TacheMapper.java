package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.BoiteReception;
import com.mycompany.myapp.domain.DemandePriseEnCharge;
import com.mycompany.myapp.domain.Tache;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.dto.BoiteReceptionDTO;
import com.mycompany.myapp.service.dto.DemandePriseEnChargeDTO;
import com.mycompany.myapp.service.dto.TacheDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Tache} and its DTO {@link TacheDTO}.
 */
@Mapper(componentModel = "spring")
public interface TacheMapper extends EntityMapper<TacheDTO, Tache> {
    @Mapping(target = "demande", source = "demande", qualifiedByName = "demandePriseEnChargeReference")
    @Mapping(target = "utilisateur", source = "utilisateur", qualifiedByName = "userLogin")
    @Mapping(target = "boiteReception", source = "boiteReception", qualifiedByName = "boiteReceptionId")
    TacheDTO toDto(Tache s);

    @Named("demandePriseEnChargeReference")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "reference", source = "reference")
    DemandePriseEnChargeDTO toDtoDemandePriseEnChargeReference(DemandePriseEnCharge demandePriseEnCharge);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("boiteReceptionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BoiteReceptionDTO toDtoBoiteReceptionId(BoiteReception boiteReception);
}
