package com.example.gestionhoraires.beans;

import com.example.gestionhoraires.HelperBDHoraire;

import org.json.JSONException;
import org.json.JSONObject;

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

    /** L'identifiant qui détermine si c'est la catégorie par défaut */
    private int isDefault;

    /**
     * Constructeur sans argument
     */
    public Categorie() {
        this.isDefault = 0;
    }

    /**
     * Constructeur avec argument
     * @param nom le nom de la catégorie
     * @param idLocalisation la catégorie de la localisation
     */
    public Categorie(String nom, String idLocalisation) {
        this.isDefault = 0;
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

    /**
     * @return l'objet json corespondant a l'instance de l'objet catégorie
     */
    public JSONObject getJson() throws JSONException {
        JSONObject jsonCategorie = new JSONObject();
        jsonCategorie.put(HelperBDHoraire.CATEGORIE_CLE_LOCALISATION, this.idLocalisation);
        jsonCategorie.put(HelperBDHoraire.CATEGORIE_NOM, this.nom);
        jsonCategorie.put(HelperBDHoraire.CATEGORIE_IS_DEFAULT, this.isDefault);


        return jsonCategorie;
    }
    
    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }
}
