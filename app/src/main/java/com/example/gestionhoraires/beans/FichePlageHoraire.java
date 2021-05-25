package com.example.gestionhoraires.beans;

import com.example.gestionhoraires.HelperBDHoraire;
import com.example.gestionhoraires.HoraireDAO;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    public JSONObject getJson(HoraireDAO horaireDAO) throws JSONException {

        // Objet a retourner
        JSONObject jsonLocalisation = new JSONObject();
        JSONObject jsonCategorie = new JSONObject();
        JSONObject jsonFiche = new JSONObject();
        JSONObject jsonHoraire = new JSONObject();

        // Recherche de la localisation et des horaires
        Categorie categorie = horaireDAO.getCategorieById(this.idCategorie);
        Localisation localisation = horaireDAO.getLocalisationById(categorie.getIdLocalisation());

        // Initialisation de l'objet HoraireDAO
        jsonFiche.put(HelperBDHoraire.FICHE_PLAGE_HORAIRE_NOM, this.nom);
        jsonFiche.put(HelperBDHoraire.FICHE_PLAGE_HORAIRE_CLE_CATEGORIE, this.idCategorie);
        jsonFiche.put(HelperBDHoraire.FICHE_PLAGE_HORAIRE_INFORMATION, this.information);
        jsonFiche.put(HelperBDHoraire.FICHE_PLAGE_HORAIRE_CHEMIN_IMAGE, this.cheminPhoto);

        return jsonFiche;
    }
}
