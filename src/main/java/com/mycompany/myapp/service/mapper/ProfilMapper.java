package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Authority;
import com.mycompany.myapp.domain.Profil;
import com.mycompany.myapp.repository.AuthorityRepository;
import com.mycompany.myapp.service.dto.ProfilDTO;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Mapper for the entity {@link Profil} and its DTO {@link ProfilDTO}.
 *
 * <p>Hand-coded, like {@link UserMapper}, because mapping the {@code authorities} field (a
 * {@link Set} of authority names on the DTO) back to managed {@link Authority} entities needs a
 * repository lookup that MapStruct cannot express declaratively.
 */
@Service
public class ProfilMapper {

    private final AuthorityRepository authorityRepository;

    public ProfilMapper(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    public ProfilDTO toDto(Profil profil) {
        if (profil == null) {
            return null;
        }
        ProfilDTO dto = new ProfilDTO();
        dto.setId(profil.getId());
        dto.setNom(profil.getNom());
        dto.setDescription(profil.getDescription());
        dto.setAuthorities(profil.getAuthorities().stream().map(Authority::getName).collect(Collectors.toSet()));
        return dto;
    }

    public List<ProfilDTO> toDto(List<Profil> profils) {
        return profils.stream().filter(Objects::nonNull).map(this::toDto).toList();
    }

    public Profil toEntity(ProfilDTO dto) {
        if (dto == null) {
            return null;
        }
        Profil profil = new Profil();
        profil.setId(dto.getId());
        profil.setNom(dto.getNom());
        profil.setDescription(dto.getDescription());
        profil.setAuthorities(authoritiesFromNames(dto.getAuthorities()));
        return profil;
    }

    public List<Profil> toEntity(List<ProfilDTO> dtos) {
        return dtos.stream().filter(Objects::nonNull).map(this::toEntity).toList();
    }

    public void partialUpdate(Profil profil, ProfilDTO dto) {
        if (dto.getNom() != null) {
            profil.setNom(dto.getNom());
        }
        if (dto.getDescription() != null) {
            profil.setDescription(dto.getDescription());
        }
        if (dto.getAuthorities() != null) {
            profil.setAuthorities(authoritiesFromNames(dto.getAuthorities()));
        }
    }

    private Set<Authority> authoritiesFromNames(Set<String> names) {
        if (names == null) {
            return new HashSet<>();
        }
        return names.stream().map(authorityRepository::findById).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
    }
}
