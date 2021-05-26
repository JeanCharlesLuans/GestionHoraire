package com.example.gestionhoraires.beans;

import java.util.ArrayList;

/**
 * Modélise une fiche horaire ponctuelle
 */
public class FicheHorairePonctuelle {

    /** Identifiant servant pour le get dans le DAO */
    private String id;

    /** Nom de l'horaire ponctuelle */
    private String nom;

    /** Un texte d'information, facultatif */
    private String information;

    /** Le chemin de la photo, facultatif */
    private String cheminPhoto;

    /** L'identifiant de la catégorie de la fiche */
    private String idCategorie;

    /**
     * Constructeur sans argument
     */
    public FicheHorairePonctuelle() {
    }

    /**
     * Constructeur avec arguments
     * @param nom
     * @param idHorairesPonctuelles les horaires de la fiches horaires ponctuelles
     * @param information information, facultatif
     * @param cheminPhoto chemin physque de la photo
     */
    public FicheHorairePonctuelle(String nom,
                                  ArrayList<String> idHorairesPonctuelles,
                                  String information,
                                  String cheminPhoto) {
        this.nom = nom;
        this.information = information;
        this.cheminPhoto = cheminPhoto;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
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

    public String getIdCategorie() {
        return idCategorie;
    }

    public void setIdCategorie(String idCategorie) {
        this.idCategorie = idCategorie;
    }
}
