package com.example.gestionhoraires.beans;

import com.example.gestionhoraires.HelperBDHoraire;
import com.example.gestionhoraires.HoraireDAO;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Modélise une plage horaire
 */
public class EnsemblePlageHoraire {

    /** Identifiant servant pour le get dans le DAO */
    private String id;

    /** Plage horaire du matin ou de toute la journée, format : HH:mm HH:mm */
    private String idPlageHoraireMatin;

    /** Deuxième plage horaire, facultative
     * Si null alors la plage du matin concerne toute la journée,
     * Si l'état des horaires sont false alors le soir est non renseigné
     */
    private String idPlageHoraireSoir;

    /** Jour de la plage horaire */
    private String idJour;

    /** Identifiant de la fiche plage horaire correspondante */
    private String idFichePlageHoraire;

    /**
     * Constructeur sans argument
     */
    public EnsemblePlageHoraire() {
    }

    /**
     * Constructeur avec arguments
     * @param idPlageHoraireMatin la plage horaire
     * @param idJour le jour de la plage horaire
     */
    public EnsemblePlageHoraire(String idPlageHoraireMatin, String idJour, String idFichePlageHoraire) {
        this.idPlageHoraireMatin = idPlageHoraireMatin;
        this.idJour = idJour;
        this.idFichePlageHoraire = idFichePlageHoraire;
    }

    /**
     * Constructeur avec arguments
     * @param idPlageHoraireMatin première plage horaire
     * @param idPlageHoraireSoir plage horaore
     * @param idJour le jour de la plage horaire
     * @param idFichePlageHoraire
     */
    public EnsemblePlageHoraire(String idPlageHoraireMatin, String idPlageHoraireSoir, String idJour, String idFichePlageHoraire) {
        this.idPlageHoraireMatin = idPlageHoraireMatin;
        this.idPlageHoraireSoir = idPlageHoraireSoir;
        this.idJour = idJour;
        this.idFichePlageHoraire = idFichePlageHoraire;
    }

    public String getIdPlageHoraireMatin() {
        return idPlageHoraireMatin;
    }

    public void setIdPlageHoraireMatin(String plageHoraireMatin) {
        this.idPlageHoraireMatin = plageHoraireMatin;
    }

    public String getIdPlageHoraireSoir() {
        return this.idPlageHoraireSoir;
    }

    public void setIdPlageHoraireSoir(String idPlageHoraireSoir) {
        this.idPlageHoraireSoir = idPlageHoraireSoir;
    }

    public String getIdJour() {
        return idJour;
    }

    public void setIdJour(String idJour) {
        this.idJour = idJour;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdFichePlageHoraire() {
        return idFichePlageHoraire;
    }

    public void setIdFichePlageHoraire(String idFichePlageHoraire) {
        this.idFichePlageHoraire = idFichePlageHoraire;
    }

    public JSONObject getJSON(HoraireDAO accesBD) throws JSONException {

        PlageHoraire plageHoraireMatin = accesBD.getPlageHoraireById(idPlageHoraireMatin);
        PlageHoraire plageHoraireSoir = accesBD.getPlageHoraireById(idPlageHoraireSoir);

        JSONObject jsonEnsemble = new JSONObject();

        jsonEnsemble.put(HelperBDHoraire.ENSEMBLE_PLAGE_HORAIRE_CLE, this.id);
        jsonEnsemble.put(HelperBDHoraire.ENSEMBLE_PLAGE_HORAIRE_CLE_HORAIRE_MATIN, plageHoraireMatin.getJSON());
        jsonEnsemble.put(HelperBDHoraire.ENSEMBLE_PLAGE_HORAIRE_CLE_HORAIRE_SOIR, plageHoraireSoir.getJSON());
        jsonEnsemble.put(HelperBDHoraire.ENSEMBLE_PLAGE_HORAIRE_CLE_JOUR, this.idJour);
        jsonEnsemble.put(HelperBDHoraire.ENSEMBLE_PLAGE_HORAIRE_CLE_FICHE, this.idFichePlageHoraire);

        return jsonEnsemble;
    }

}
