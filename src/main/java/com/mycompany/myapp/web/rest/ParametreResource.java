package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Parametre;
import com.mycompany.myapp.domain.enumeration.TypeParametre;
import com.mycompany.myapp.repository.ParametreRepository;
import com.mycompany.myapp.security.ActionsConstants;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.Parametres;
import com.mycompany.myapp.service.dto.ParametreDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;

/**
 * Les reglages de l'application.
 *
 * <p>Pas de creation ni de suppression : le referentiel est pose au demarrage, et le programme
 * interroge des codes qu'il connait. En ajouter un a la main donnerait un reglage que personne
 * ne lit ; en retirer un ferait retomber le code sur une valeur par defaut que personne n'a
 * choisie. Seule la valeur se modifie - c'est tout l'objet du referentiel.
 *
 * <p>Les valeurs sont lisibles par quiconque consulte les referentiels : le nom de l'institution
 * et la duree de validite d'un dossier s'affichent sur des ecrans ordinaires. Les modifier reste
 * reserve a qui tient les referentiels.
 */
@RestController
@RequestMapping("/api/parametres")
@Transactional
public class ParametreResource {

    private static final Logger LOG = LoggerFactory.getLogger(ParametreResource.class);

    private static final String ENTITY_NAME = "parametre";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ParametreRepository parametreRepository;

    public ParametreResource(ParametreRepository parametreRepository) {
        this.parametreRepository = parametreRepository;
    }

    /**
     * {@code GET /parametres} : tous les reglages, dans l'ordre de leur libelle.
     *
     * <p>Pas de pagination : ils se comptent sur les doigts, et une liste de reglages se
     * parcourt d'un regard. Paginer ferait chercher a la page suivante un reglage dont on
     * ignore le nom exact.
     */
    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('" + ActionsConstants.REFERENTIEL_CONSULTER + "', '" + AuthoritiesConstants.ADMIN + "')")
    public List<ParametreDTO> getAllParametres() {
        LOG.debug("REST request to get all Parametres");
        return parametreRepository
            .findAll()
            .stream()
            .sorted((a, b) -> a.getLibelle().compareToIgnoreCase(b.getLibelle()))
            .map(this::toDto)
            .toList();
    }

    /**
     * {@code GET /parametres/:code/image} : l'image d'un reglage, telle qu'elle s'imprime.
     *
     * <p>Servie a part et non dans la liste : une signature pese quelques dizaines de
     * kilo-octets, et les rapatrier toutes pour afficher un tableau de reglages n'aurait pas de
     * sens. Accessible a la simple consultation - elle figure sur les cartes imprimees.
     */
    @GetMapping("/{code}/image")
    @PreAuthorize("hasAnyAuthority('" + ActionsConstants.REFERENTIEL_CONSULTER + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<byte[]> getImage(@PathVariable("code") String code) {
        LOG.debug("REST request to get image of Parametre {}", code);
        return parametreRepository
            .findOneByCode(code)
            .filter(parametre -> parametre.getValeurBinaire() != null)
            .map(parametre ->
                ResponseEntity.ok()
                    .contentType(
                        parametre.getValeurBinaireContentType() == null
                            ? MediaType.APPLICATION_OCTET_STREAM
                            : MediaType.parseMediaType(parametre.getValeurBinaireContentType())
                    )
                    .body(parametre.getValeurBinaire())
            )
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * {@code PUT /parametres/:id} : modifie la valeur d'un reglage.
     *
     * <p>Le code, le libelle et le type ne bougent pas : le code applicatif interroge le premier,
     * et changer le troisieme ferait lire un entier la ou une image est attendue.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + ActionsConstants.REFERENTIEL_MODIFIER + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<ParametreDTO> updateParametre(@PathVariable("id") Long id, @Valid @RequestBody ParametreDTO parametreDTO) {
        LOG.debug("REST request to update Parametre : {}", parametreDTO);
        Parametre parametre = parametreRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Ce parametre n'existe pas", ENTITY_NAME, "idnotfound"));

        exigerValeurLisible(parametre.getType(), parametreDTO.getValeur());
        parametre.setValeur(parametreDTO.getValeur());
        parametre.setDescription(parametreDTO.getDescription());
        if (parametre.getType() == TypeParametre.IMAGE) {
            parametre.setValeurBinaire(parametreDTO.getValeurBinaire());
            parametre.setValeurBinaireContentType(parametreDTO.getValeurBinaireContentType());
        }
        parametre = parametreRepository.save(parametre);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, parametre.getCode()))
            .body(toDto(parametre));
    }

    /**
     * Refuse une valeur que son type ne permet pas de lire.
     *
     * <p>Le refus est immediat : un delai saisi « quinze » plutot que « 15 » doit etre repris au
     * moment ou on l'ecrit, et non decouvert trois semaines plus tard au milieu d'un calcul
     * d'echeance, sur un dossier qui n'y est pour rien.
     */
    private void exigerValeurLisible(TypeParametre type, String valeur) {
        if (type != TypeParametre.ENTIER || valeur == null || valeur.isBlank()) {
            return;
        }
        try {
            int lu = Integer.parseInt(valeur.trim());
            if (lu <= 0) {
                throw new BadRequestAlertException("Ce reglage attend un nombre positif", ENTITY_NAME, "parametre.positif");
            }
        } catch (NumberFormatException e) {
            throw new BadRequestAlertException("Ce reglage attend un nombre entier", ENTITY_NAME, "parametre.entier");
        }
    }

    private ParametreDTO toDto(Parametre parametre) {
        ParametreDTO dto = new ParametreDTO();
        dto.setId(parametre.getId());
        dto.setCode(parametre.getCode());
        dto.setLibelle(parametre.getLibelle());
        dto.setDescription(parametre.getDescription());
        dto.setValeur(parametre.getValeur());
        dto.setType(parametre.getType());
        dto.setSocle(parametre.isSocle());
        // L'image n'est pas rendue dans la liste : elle se recupere par son point d'entree.
        dto.setValeurBinaireContentType(parametre.getValeurBinaireContentType());
        return dto;
    }

    /** Rendu public pour que les ecrans sachent quels codes existent sans les recopier. */
    @GetMapping("/codes")
    @PreAuthorize("hasAnyAuthority('" + ActionsConstants.REFERENTIEL_CONSULTER + "', '" + AuthoritiesConstants.ADMIN + "')")
    public List<String> getCodes() {
        return List.of(
            Parametres.VALIDITE_PRISE_EN_CHARGE_JOURS,
            Parametres.VALIDITE_CARTE_ANNEES,
            Parametres.ALERTE_CARTE_JOURS,
            Parametres.TAILLE_MAX_PIECE_MO,
            Parametres.NOM_DIRECTEUR_GENERAL,
            Parametres.SIGNATURE_DIRECTEUR_GENERAL,
            Parametres.NOM_INSTITUTION,
            Parametres.PAYS_INSTITUTION
        );
    }

    /** Le reglage demande, pour un ecran qui n'en consulte qu'un. */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + ActionsConstants.REFERENTIEL_CONSULTER + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<ParametreDTO> getParametre(@PathVariable("id") Long id) {
        Optional<ParametreDTO> parametre = parametreRepository.findById(id).map(this::toDto);
        return parametre.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
