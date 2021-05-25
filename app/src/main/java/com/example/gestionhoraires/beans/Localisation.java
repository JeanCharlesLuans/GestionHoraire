package com.example.gestionhoraires.beans;

import com.example.gestionhoraires.HelperBDHoraire;
import com.example.gestionhoraires.HoraireDAO;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Modélisation d'un localisation
 */
public class Localisation {

    /** Identifiant servant pour le get dans le DAO */
    private String id;

    /** Nom de la localisation */
    private String nom;

    /** L'identifiant qui permet de déterminer s'il s'agit de la localisation par défaut */
    private int isDefault;

    /**
     * Constructeur sans argument
     */
    public Localisation() {
        this.isDefault = 0;
    }

    /**
     * Constructeur avec argument
     * @param nom le nom de la localisation
     */
    public Localisation(String nom) {
        this.isDefault = 0;
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

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    /**
     * @return l'objet json corespondant a l'instance de l'objet catégorie
     */
    public JSONObject getJson() throws JSONException {
        JSONObject jsonLocalisation = new JSONObject();
        jsonLocalisation.put(HelperBDHoraire.LOCALISATION_NOM, this.nom);
        //jsonLocalisation.put(HelperBDHoraire.LOCALISATION_IS_DEFAULT, this.);
        return jsonLocalisation;
    }
}

