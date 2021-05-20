package com.example.gestionhoraires.beans;

import java.util.ArrayList;

/**
 * Modélise une fiche de plages horaires
 */
public class FichePlageHoraire {

    /** Identifiant servant pour le get dans le DAO */
    private String id;

    /** Nom de la fiche horaire */
    private String nom;

    /** Catégorie de la fiche horaire */
    private String idCategorie;

    /** Information de la fiche horaire */
    private String information;

    /** Chemin de la photo */
    private String cheminPhoto;

    /**
     * Constructeur sans argument
     */
    public FichePlageHoraire() {
    }

    /**
     * Constructeur avec arguments
     * @param nom nom de la fiche
     * @param idCategorie categorie de la fiche
     * @param information informations liées à la fiche
     */
    public FichePlageHoraire(String nom,
                             String idCategorie,
                             String information,
                             String cheminPhoto) {
        this.nom = nom;
        this.idCategorie = idCategorie;
        this.information = information;
        this.cheminPhoto = cheminPhoto;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getIdCategorie() {
        return idCategorie;
    }

    public void setIdCategorie(String idCategorie) {
        this.idCategorie = idCategorie;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getCheminPhoto() {
        return cheminPhoto;
    }

    public void setCheminPhoto(String cheminPhoto) {
        this.cheminPhoto = cheminPhoto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
