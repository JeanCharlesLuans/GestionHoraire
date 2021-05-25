package com.example.gestionhoraires.beans;

import com.example.gestionhoraires.HelperBDHoraire;
import com.example.gestionhoraires.HoraireDAO;

import org.json.JSONArray;
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

    /**
     * Formate un JSON avec la localisation -> Categorie -> Fiche -> Ensemble -> Horaire
     * @param horaireDAO acces a la BD
     * @return le SON formater
     * @throws JSONException
     */
    public JSONObject getJson(HoraireDAO horaireDAO) throws JSONException {

        JSONObject general = new JSONObject();

        // Objet a retourner
        JSONObject jsonLocalisation = new JSONObject();
        JSONObject jsonCategorie = new JSONObject();
        JSONObject jsonFiche = new JSONObject();
        JSONArray jsonHoraire = new JSONArray();

        // Initialisation de l'objet horaires
        Categorie categorie = horaireDAO.getCategorieById(this.idCategorie);
        jsonCategorie = categorie.getJson();

        // Initialisation de l'objet json Localisation
        Localisation localisation = horaireDAO.getLocalisationById(categorie.getIdLocalisation());
        jsonLocalisation = localisation.getJson();

        // Initialisation de l'objets
        ArrayList<EnsemblePlageHoraire> listeEnsemble = horaireDAO.getEnsembleHorraireOfFiche(this.id);
        for (int i = 0; i < listeEnsemble.size(); i++) {
            jsonHoraire.put(listeEnsemble.get(i).getJSON(horaireDAO));
        }

        // Initialisation de l'objet HoraireDAO
        jsonFiche.put(HelperBDHoraire.FICHE_PLAGE_HORAIRE_NOM, this.nom);
        jsonFiche.put(HelperBDHoraire.FICHE_PLAGE_HORAIRE_CLE_CATEGORIE, this.idCategorie);
        jsonFiche.put(HelperBDHoraire.FICHE_PLAGE_HORAIRE_INFORMATION, this.information);
        jsonFiche.put(HelperBDHoraire.FICHE_PLAGE_HORAIRE_CHEMIN_IMAGE, this.cheminPhoto);


        general.put("LOCALISATION",jsonLocalisation);
        general.put("FICHE",jsonFiche);
        general.put("CATEGORIE",jsonCategorie);
        general.put("HORAIRE",jsonHoraire);

        return general;
    }
}
