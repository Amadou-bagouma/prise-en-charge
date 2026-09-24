package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.TypeSoin;
import com.mycompany.myapp.service.dto.TypeSoinDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TypeSoin} and its DTO {@link TypeSoinDTO}.
 */
@Mapper(componentModel = "spring")
public interface TypeSoinMapper extends EntityMapper<TypeSoinDTO, TypeSoin> {
    /** Le referentiel ne transporte jamais les dossiers qui l'utilisent. */
    @Mapping(target = "demandes", ignore = true)
    @Override
    TypeSoin toEntity(TypeSoinDTO dto);
}
