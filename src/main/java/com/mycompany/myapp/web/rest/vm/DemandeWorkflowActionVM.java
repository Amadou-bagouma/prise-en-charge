package com.mycompany.myapp.web.rest.vm;

/**
 * View model used by the DemandePriseEnCharge validation workflow endpoints
 * ({@code /valider} and {@code /rejeter}).
 */
public class DemandeWorkflowActionVM {

    private String commentaire;

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
}
