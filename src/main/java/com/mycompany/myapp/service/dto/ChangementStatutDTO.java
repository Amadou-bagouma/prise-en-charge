package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Corps d'une demande de changement de situation, pour un agent comme pour un ayant droit.
 *
 * <p>Le statut voyage en texte et non en enumeration typee : les deux enumerations sont
 * distinctes, et c'est le service concerne qui sait laquelle attendre. Un libelle inconnu est
 * refuse la, avec la liste des valeurs admises.
 *
 * @param statut le nouveau statut, tel qu'il est nomme dans l'enumeration.
 * @param motif la raison du changement. Obligatoire des que l'on quitte ACTIF : une radiation
 *        sans raison ecrite est inexplicable six mois plus tard, et incontestable au guichet.
 */
public record ChangementStatutDTO(@NotNull @Size(max = 50) String statut, @Size(max = 500) String motif) {}
