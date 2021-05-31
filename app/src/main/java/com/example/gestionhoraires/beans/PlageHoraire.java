package com.example.gestionhoraires.beans;

import android.content.pm.PermissionGroupInfo;

import com.example.gestionhoraires.HelperBDHoraire;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Modélise une plage horaire
 */
public class PlageHoraire {

    /** Identifiant servant pour le get dans le DAO */
    private String id;

    /** Horaire d'ouverture, format : HH:mm HH:mm */
    private String horaireOuverture;

    /** Horaire de fermeture, format : HH:mm HH:mm*/
    private String horaireFermeture;

    /** Etat de l'horaire d'ouverture : 0 si pas renseigné 1 si renseigné */
    private int etatOuverture;

    /** Etat de l'horaire d'ouverture : 0 si pas renseigné 1 si renseigné */
    private int etatFermeture;

    /** Indicateur pour savoir si l'établissement est fermé (1) ou ouvert (0) */
    private int estFerme;

    /**
     * Constructeur sans argument
     */
    public PlageHoraire() {
    }

    /**
     * Constructeur avec arguments
     * @param horaireOuverture l'horaire d'ouverture
     * @param estFerme indicateur si l'établissement est fermé
     */
    public PlageHoraire(String horaireOuverture, int estFerme) {
        this.horaireOuverture = horaireOuverture;
        this.estFerme = estFerme;
    }

    /**
     * Constructeur avec arguments
     * @param horaireOuverture l'horaire d'ouverture
     * @param etatOuverture l'état de l'horaire d'ouverture
     * @param estFerme indicateur si l'établissement est fermé
     */
    public PlageHoraire(String horaireOuverture, int etatOuverture, int estFerme) {
        this.horaireOuverture = horaireOuverture;
        this.etatOuverture = etatOuverture;
        this.estFerme = estFerme;
    }

    /**
     * Constructeur avec arguments
     * @param horaireOuverture l'horaire d'ouverture
     * @param horaireFermeture l'horaire de fermeture
     */
    public PlageHoraire(String horaireOuverture, String horaireFermeture, int estFerme) {
        this.horaireOuverture = horaireOuverture;
        this.horaireFermeture = horaireFermeture;
        this.estFerme = estFerme;
    }

    /**
     * Constructeur avec arguments
     * @param horaireOuverture l'horaire d'ouverture
     * @param etatOuverture l'état de l'horaire d'ouverture
     * @param horaireFermeture l'horaire de fermeture
     * @param etatFermeture l'état de l'horaire de fermeture
     */
    public PlageHoraire(String horaireOuverture, int etatOuverture, String horaireFermeture, int etatFermeture, int estFerme) {
        this.horaireOuverture = horaireOuverture;
        this.horaireFermeture = horaireFermeture;
        this.etatOuverture = etatOuverture;
        this.etatFermeture = etatFermeture;
        this.estFerme = estFerme;
    }

    public String getHoraireOuverture() {
        return horaireOuverture;
    }

    public void setHoraireOuverture(String horaireOuverture) {
        this.horaireOuverture = horaireOuverture;
    }

    public String getHoraireFermeture() {
        return horaireFermeture;
    }

    public void setHoraireFermeture(String horaireFermeture) {
        this.horaireFermeture = horaireFermeture;
    }

    public int getEtatOuverture() {
        return etatOuverture;
    }

    public void setEtatOuverture(int etatOuverture) {
        this.etatOuverture = etatOuverture;
    }

    public int getEtatFermeture() {
        return etatFermeture;
    }

    public void setEtatFermeture(int etatFermeture) {
        this.etatFermeture = etatFermeture;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getEstFerme() {
        return estFerme;
    }

    public void setEstFerme(int estFerme) {
        this.estFerme = estFerme;
    }
    
    public JSONObject getJSON() throws JSONException {
        JSONObject jsonPlage = new JSONObject();
        jsonPlage.put(HelperBDHoraire.PLAGE_HORAIRE_CLE, id);
        jsonPlage.put(HelperBDHoraire.PLAGE_HORAIRE_OUVERTURE, this.horaireOuverture);
        jsonPlage.put(HelperBDHoraire.PLAGE_HORAIRE_FERMETURE, this.horaireFermeture);
        jsonPlage.put(HelperBDHoraire.PLAGE_HORAIRE_ETAT_OUVERTURE, this.etatOuverture);
        jsonPlage.put(HelperBDHoraire.PLAGE_HORAIRE_ETAT_FERMETURE, this.etatFermeture);

        return jsonPlage;
    }
}
