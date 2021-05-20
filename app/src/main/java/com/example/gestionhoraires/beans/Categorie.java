package com.example.gestionhoraires.beans;

/**
 * Modélise une catégorie de fiche horaire
 */
public class Categorie {

    /** Identifiant servant pour le get dans le DAO */
    private String id;

    /** Nom de la catégorie */
    private String nom;

    /** La lococalisation de la catégorie */
    private String idLocalisation;

    /**
     * Constructeur sans argument
     */
    public Categorie() {
    }

    /**
     * Constructeur avec argument
     * @param nom le nom de la catégorie
     * @param idLocalisation la catégorie de la localisation
     */
    public Categorie(String nom, String idLocalisation) {
        this.nom = nom;
        this.idLocalisation = idLocalisation;
    }

    /**
     * Accesseur sur le nom
     * @return le nom de la catégorie
     */
    public String getNom() {
        return this.nom;
    }

    public String getIdLocalisation() {
        return this.idLocalisation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setIdLocalisation(String idLocalisation) {
        this.idLocalisation = idLocalisation;
    }
}
