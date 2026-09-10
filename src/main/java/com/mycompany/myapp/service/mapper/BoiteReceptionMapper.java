package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.BoiteReception;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.dto.BoiteReceptionDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link BoiteReception} and its DTO {@link BoiteReceptionDTO}.
 */
@Mapper(componentModel = "spring")
public interface BoiteReceptionMapper extends EntityMapper<BoiteReceptionDTO, BoiteReception> {
    @Mapping(target = "utilisateur", source = "utilisateur", qualifiedByName = "userLogin")
    BoiteReceptionDTO toDto(BoiteReception s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
