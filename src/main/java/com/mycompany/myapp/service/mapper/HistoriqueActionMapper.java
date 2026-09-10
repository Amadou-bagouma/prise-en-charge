package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.DemandePriseEnCharge;
import com.mycompany.myapp.domain.HistoriqueAction;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.dto.DemandePriseEnChargeDTO;
import com.mycompany.myapp.service.dto.HistoriqueActionDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link HistoriqueAction} and its DTO {@link HistoriqueActionDTO}.
 */
@Mapper(componentModel = "spring")
public interface HistoriqueActionMapper extends EntityMapper<HistoriqueActionDTO, HistoriqueAction> {
    @Mapping(target = "demande", source = "demande", qualifiedByName = "demandePriseEnChargeReference")
    @Mapping(target = "utilisateur", source = "utilisateur", qualifiedByName = "userLogin")
    HistoriqueActionDTO toDto(HistoriqueAction s);

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
}
