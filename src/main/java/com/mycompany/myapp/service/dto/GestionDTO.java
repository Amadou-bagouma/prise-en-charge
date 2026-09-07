package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.enumeration.TypeGestion;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Gestion} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class GestionDTO implements Serializable {

    private Long id;

    @NotNull
    private String nom;

    @NotNull
    private TypeGestion type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public TypeGestion getType() {
        return type;
    }

    public void setType(TypeGestion type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GestionDTO)) {
            return false;
        }

        GestionDTO gestionDTO = (GestionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, gestionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GestionDTO{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
}
