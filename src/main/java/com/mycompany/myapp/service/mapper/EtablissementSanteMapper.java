package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.EtablissementSante;
import com.mycompany.myapp.service.dto.EtablissementSanteDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link EtablissementSante} and its DTO {@link EtablissementSanteDTO}.
 */
@Mapper(componentModel = "spring")
public interface EtablissementSanteMapper extends EntityMapper<EtablissementSanteDTO, EtablissementSante> {}
