package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Direction;
import com.mycompany.myapp.domain.Region;
import com.mycompany.myapp.service.dto.DirectionDTO;
import com.mycompany.myapp.service.dto.RegionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Direction} and its DTO {@link DirectionDTO}.
 */
@Mapper(componentModel = "spring")
public interface DirectionMapper extends EntityMapper<DirectionDTO, Direction> {
    @Mapping(target = "region", source = "region", qualifiedByName = "regionId")
    DirectionDTO toDto(Direction s);

    @Named("regionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RegionDTO toDtoRegionId(Region region);
}
