package com.example.gestionhoraires.beans;

import java.util.ArrayList;

/**
 * Modélise une horaire ponctuelle
 */
public class HorairePonctuelle {

    /** Identifiant servant pour le get dans le DAO */
    private String id;

    /** Heure de début, format : HH:mm */
    private String horaireDebut;

    /** Facultatif */
    private String horaireFin;

    /** Date de l'horaire ponctuelle */
    private String idJour;

    /** La fiche horaire correspondante */
    private String idFicheHorairePonctuelle;

    /**
     * Constructeur sans argument
     */
    public HorairePonctuelle() {
    }

    /**
     * Constructeur avec arguments
     * @param horaireDebut heure de début
     * @param idJour le jour de l'horaire
     * @param idFicheHorairePonctuelle l'id de la fiche horaire ponctuelle correspondante
     */
    public HorairePonctuelle(String horaireDebut, String idJour, String idFicheHorairePonctuelle) {
        this.horaireDebut = horaireDebut;
        this.idJour = idJour;
        this.idFicheHorairePonctuelle = idFicheHorairePonctuelle;
    }

    /**
     * Constructeur avec arguments
     * @param horaireDebut heure de début
     * @param horaireFin heure de fin
     * @param idJour jour de l'horaire ponctuelle
     */
    public HorairePonctuelle(String horaireDebut, String horaireFin, String idJour, String idFicheHorairePonctuelle) {
        this.horaireDebut = horaireDebut;
        this.horaireFin = horaireFin;
        this.idJour = idJour;
        this.idFicheHorairePonctuelle = idFicheHorairePonctuelle;
    }

    public String getHoraireDebut() {
        return horaireDebut;
    }

    public void setHoraireDebut(String horaireDebut) {
        this.horaireDebut = horaireDebut;
    }

    public String getHoraireFin() {
        return horaireFin;
    }

    public void setHoraireFin(String horaireFin) {
        this.horaireFin = horaireFin;
    }

    /**
     * Accesseur sur le jour de l'horaire
     * @return le jour de l'horaire
     */
    public String getIdJour() {
        return this.idJour;
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

    public String getIdFicheHorairePonctuelle() {
        return idFicheHorairePonctuelle;
    }

    public void setIdFicheHorairePonctuelle(String idFicheHorairePonctuelle) {
        this.idFicheHorairePonctuelle = idFicheHorairePonctuelle;
    }


}

