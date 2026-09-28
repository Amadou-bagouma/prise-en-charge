package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Authority;
import com.mycompany.myapp.domain.Profil;
import com.mycompany.myapp.security.AuthoritiesConstants;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Regle d'adressage des taches aux boites de reception.
 *
 * <p>Une tache porte l'habilitation qui en donne la charge ({@code Tache.droitRequis}) ; la boite
 * d'un profil montre les taches dont cette habilitation figure parmi ses droits. C'est la regle
 * telle qu'elle se formule : « chacun ne voit que les taches que ses droits lui donnent ».
 *
 * <p>La version precedente deduisait le destinataire de l'etape ou se trouvait la demande. Cela
 * faisait une seconde regle de routage, parallele a celle qui creait les taches, et donc deux
 * verites appelees a diverger : une etape ajoutee au circuit d'un cote et pas de l'autre, et des
 * taches n'arrivant nulle part. Le droit porte par la tache est desormais la seule source.
 */
@Service
public class RoutageBoiteReception {

    /**
     * Ce qu'une boite laisse voir.
     *
     * <p>Trois cas, et l'appelant doit les traiter tous les trois - d'ou un type qui les nomme
     * plutot qu'un ensemble dont le vide serait ambigu : « aucune tache » et « toutes les
     * taches » s'ecriraient de la meme facon, et la confusion ouvrirait la boite de tout le monde
     * a un profil sans droits.
     *
     * @param tout vrai pour un administrateur : la boite n'est bornee par aucun droit.
     * @param droits les habilitations visibles, quand {@code tout} est faux. Vide = rien a voir.
     */
    public record Portee(boolean tout, Set<String> droits) {
        public boolean rienAVoir() {
            return !tout && droits.isEmpty();
        }
    }

    /**
     * La portee de la boite d'un profil.
     *
     * <p>L'administrateur est traite a part : il repond des circuits bloques, et doit donc voir
     * toutes les files, y compris celles dont il ne porte pas l'habilitation.
     */
    public Portee porteeDe(Profil profil) {
        if (profil == null) {
            return new Portee(false, Set.of());
        }
        Set<String> droits = profil.getAuthorities().stream().map(Authority::getName).collect(Collectors.toSet());
        if (droits.contains(AuthoritiesConstants.ADMIN)) {
            return new Portee(true, droits);
        }
        return new Portee(false, droits);
    }
}
