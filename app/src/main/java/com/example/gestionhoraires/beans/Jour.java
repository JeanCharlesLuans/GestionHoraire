package com.example.gestionhoraires.beans;

/**
 * Modélise un jour
 */
public class Jour {

    /** Identifiant du jour */
    private String id;

    /** Le jour */
    private String jour;

    /**
     * Constructeur sans argument
     * (jamais utilisé)
     */
    public Jour() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJour() {
        return jour;
    }

    public void setJour(String jour) {
        this.jour = jour;
    }
}
