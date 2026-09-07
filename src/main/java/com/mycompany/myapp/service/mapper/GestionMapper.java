package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Gestion;
import com.mycompany.myapp.service.dto.GestionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Gestion} and its DTO {@link GestionDTO}.
 */
@Mapper(componentModel = "spring")
public interface GestionMapper extends EntityMapper<GestionDTO, Gestion> {}
