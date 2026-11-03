package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Fonction;
import com.mycompany.myapp.service.dto.FonctionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Fonction} and its DTO {@link FonctionDTO}.
 */
@Mapper(componentModel = "spring")
public interface FonctionMapper extends EntityMapper<FonctionDTO, Fonction> {}
