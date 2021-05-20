package com.example.gestionhoraires.beans;

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

    /**
     * Constructeur sans argument
     */
    public PlageHoraire() {
    }

    /**
     * Constructeur avec arguments
     * @param horaireOuverture l'horaire d'ouverture
     * @param etatOuverture l'état de l'horaire d'ouverture
     */
    public PlageHoraire(String horaireOuverture, int etatOuverture) {
        this.horaireOuverture = horaireOuverture;
        this.etatOuverture = etatOuverture;
    }

    /**
     * Constructeur avec arguments
     * @param horaireOuverture l'horaire d'ouverture
     * @param etatOuverture l'état de l'horaire d'ouverture
     * @param horaireFermeture l'horaire de fermeture
     * @param etatFermeture l'état de l'horaire de fermeture
     */
    public PlageHoraire(String horaireOuverture, int etatOuverture, String horaireFermeture, int etatFermeture) {
        this.horaireOuverture = horaireOuverture;
        this.horaireFermeture = horaireFermeture;
        this.etatOuverture = etatOuverture;
        this.etatFermeture = etatFermeture;
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
}
