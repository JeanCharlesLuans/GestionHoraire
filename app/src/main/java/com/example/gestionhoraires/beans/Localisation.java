package com.example.gestionhoraires.beans;

/**
 * Modélisation d'un localisation
 */
public class Localisation {

    /** Identifiant servant pour le get dans le DAO */
    private String id;

    /** Nom de la localisation */
    private String nom;

    /**
     * Constructeur sans argument
     */
    public Localisation() {
    }

    /**
     * Constructeur avec argument
     * @param nom le nom de la localisation
     */
    public Localisation(String nom) {
        this.nom = nom;
    }

    /**
     * Accesseur sur le nom de la localisation
     * @return le nom de la localisation
     */
    public String getNom() {
        return this.nom;
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

}

