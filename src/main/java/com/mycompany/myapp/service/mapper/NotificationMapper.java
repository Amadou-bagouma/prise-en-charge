package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.DemandePriseEnCharge;
import com.mycompany.myapp.domain.Notification;
import com.mycompany.myapp.domain.Tache;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.dto.DemandePriseEnChargeDTO;
import com.mycompany.myapp.service.dto.NotificationDTO;
import com.mycompany.myapp.service.dto.TacheDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Notification} and its DTO {@link NotificationDTO}.
 */
@Mapper(componentModel = "spring")
public interface NotificationMapper extends EntityMapper<NotificationDTO, Notification> {
    @Mapping(target = "utilisateur", source = "utilisateur", qualifiedByName = "userLogin")
    @Mapping(target = "demande", source = "demande", qualifiedByName = "demandePriseEnChargeReference")
    @Mapping(target = "tache", source = "tache", qualifiedByName = "tacheTitre")
    NotificationDTO toDto(Notification s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("demandePriseEnChargeReference")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "reference", source = "reference")
    DemandePriseEnChargeDTO toDtoDemandePriseEnChargeReference(DemandePriseEnCharge demandePriseEnCharge);

    @Named("tacheTitre")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "titre", source = "titre")
    TacheDTO toDtoTacheTitre(Tache tache);
}
