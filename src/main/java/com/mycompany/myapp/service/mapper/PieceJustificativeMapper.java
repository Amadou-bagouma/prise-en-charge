package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.DemandePriseEnCharge;
import com.mycompany.myapp.domain.PieceJustificative;
import com.mycompany.myapp.service.dto.DemandePriseEnChargeDTO;
import com.mycompany.myapp.service.dto.PieceJustificativeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PieceJustificative} and its DTO {@link PieceJustificativeDTO}.
 */
@Mapper(componentModel = "spring")
public interface PieceJustificativeMapper extends EntityMapper<PieceJustificativeDTO, PieceJustificative> {
    @Mapping(target = "demande", source = "demande", qualifiedByName = "demandePriseEnChargeReference")
    PieceJustificativeDTO toDto(PieceJustificative s);

    @Named("demandePriseEnChargeReference")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "reference", source = "reference")
    DemandePriseEnChargeDTO toDtoDemandePriseEnChargeReference(DemandePriseEnCharge demandePriseEnCharge);
}
