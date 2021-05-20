package com.example.gestionhoraires;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gestionhoraires.beans.Categorie;
import com.example.gestionhoraires.beans.EnsemblePlageHoraire;
import com.example.gestionhoraires.beans.FicheHorairePonctuelle;
import com.example.gestionhoraires.beans.FichePlageHoraire;
import com.example.gestionhoraires.beans.HorairePonctuelle;
import com.example.gestionhoraires.beans.Localisation;
import com.example.gestionhoraires.beans.PlageHoraire;

import java.util.ArrayList;

/**
 * Classe permettant de gérer tous les accès à la base de données
 */
public class HoraireDAO {

    /** Numéro de version de la base données */
    public static final int VERSION = 1;

    /** Nom de la base données qui contiendra les horaires */
    public static final String NOM_BD = "horaire.db";

    //// Localisation ////
    /** Numéro de la colonne de la clé */
    public static final int LOCALISATION_NUM_COLONNE_CLE = 0;

    /** Numéro de la colonne contenant le nom */
    public static final int LOCALISATION_NUM_COLONNE_NOM = 1;

    /** Numéro de la colonne contenant l'information de localisation par défaut */
    public static final int LOCALISATION_NUM_COLONNE_DEFAUT = 2;

    //// Categorie ////
    /** Numéro de la colonne de la clé */
    public static final int CATEGORIE_NUM_COLONNE_CLE = 0;

    /** Numéro de la colonne contenant la clé étrangère */
    public static final int CATEGORIE_NUM_COLONNE_CLE_LOCALISATION = 1;

    /** Numéro de la colonne contenant le nom */
    public static final int CATEGORIE_NUM_COLONNE_NOM = 2;

    /** Numéro de la contenant l'information de catégorie par défaut */
    public static final int CATEGORIE_NUM_COLONNE_DEFAUT = 3;

    //// Fiche Plage Horaire ////
    /** Numéro de la colonne de la clé */
    public static final int FICHE_PLAGE_HORAIRE_NUM_COLONNE_CLE = 0;

    /** Numéro de la colonnne contenant le nom */
    public static final int FICHE_PLAGE_HORAIRE_NUM_COLONNE_NOM = 1;

    /** Numéro de la colonne contenant l'information */
    public static final int FICHE_PLAGE_HORAIRE_NUM_COLONNE_INFORMATION = 2;

    /** Numéro de la colonne contenant le chemin de l'image */
    public static final int FICHE_PLAGE_HORAIRE_NUM_COLONNE_CHEMIN_IMAGE = 3;

    /** Numéro de la colonne contenant la clé étrangère de la catégorie */
    public static final int FICHE_PLAGE_HORAIRE_NUM_COLONNE_CLE_CATEGORIE = 4;

    //// Ensemble Plage Horaire ////
    /** Numéro de la colonne de la clé */
    public static final int ENSEMBLE_PLAGE_HORAIRE_NUM_COLONNE_CLE = 0;

    /** Numéro de la colonne de la clé étrangère de l'horaire du matin */
    public static final int ENSEMBLE_PLAGE_HORAIRE_NUM_COLONNE_CLE_PLAGE_HORAIRE_MATIN = 1;

    /** Numéro de la colonne de la clé étrangère de l'horaire du soir */
    public static final int ENSEMBLE_PLAGE_HORAIRE_NUM_COLONNE_CLE_PLAGE_HORAIRE_SOIR = 2;

    /** Numéro de la colonne de la clé étrangère du jour */
    public static final int ENSEMBLE_PLAGE_HORAIRE_NUM_COLONNE_JOUR = 3;

    /** Numéro de la colonne de la clé étrangère de la fiche plage horaire */
    public static final int ENSEMBLE_PLAGE_HORAIRE_NUM_COLONNE_CLE_FICHE_PLAGE_HORAIRE = 4;

    //// Plage Horaire ////
    /** Numéro de la colonne de la clé */
    public static final int PLAGE_HORAIRE_NUM_COLONNE_CLE = 0;

    /** Numéro de la colonne de l'horaire d'ouverture */
    public static final int PLAGE_HORAIRE_NUM_COLONNE_HORAIRE_OUVERTURE = 1;

    /** Numéro de la colonne de l'horaire de fermeture */
    public static final int PLAGE_HORAIRE_NUM_COLONNE_HORAIRE_FERMETURE = 2;

    /** Numéro de la colonne de l'état de l'ouverture */
    public static final int PLAGE_HORAIRE_NUM_COLONNE_ETAT_OUVERTURE = 3;

    /** Numéro de la colonne de l'état de la fermeture */
    public static final int PLAGE_HORAIRE_NUM_COLONNE_ETAT_FERMETURE = 3;

    //// Horaire Ponctuelle ////
    /** Numéro de la colonne de la clé */
    public static final int HORAIRE_PONCTUELLE_NUM_COLONNE_CLE = 0;

    /** Numéro de la colonne de l'horaire d'ouverture */
    public static final int HORAIRE_PONCTUELLE_NUM_COLONNE_HORAIRE_OUVERTURE = 1;

    /** Numéro de la colonne de l'horaire de de fermeture */
    public static final int HORAIRE_PONCTUELLE_NUM_COLONNE_HORAIRE_FERMETURE = 2;

    /** Numéro de la colonne contenant la clé étrangère du jour */
    public static final int HORAIRE_PONCTUELLE_NUM_COLONNE_CLE_JOUR = 3;

    /** Numéro de la colonne la clé de la fiche d'horaire ponctuelle */
    public static final int HORAIRE_PONCTUELLE_NUM_COLONNE_CLE_FICHE_HORAIRE_PONCTUELLE = 4;

    //// Fiche Horaire Ponctuelle ////
    /** Numéro de la colonne de la clé */
    public static final int FICHE_HORAIRE_PONCTUELLE_NUM_COLONNE_CLE = 0;

    /** Numéro de la colonne contenant le nom */
    public static final int FICHE_HORAIRE_PONCTUELLE_NUM_COLONNE_NOM = 1;

    /** Numéro de la colonne contenant l'information */
    public static final int FICHE_HORAIRE_PONCTUELLE_NUM_COLONNE_INFORMATION = 2;

    /** Numéro de la colonne contenant le chemin de l'image */
    public static final int FICHE_HORAIRE_PONCTUELLE_NUM_COLONNE_CHEMIN_IMAGE = 4;

    //// Jour ////
    /** Numéro de la colonne de la clé */
    public static final int JOUR_NUM_COLONNE_CLE = 0;

    /** Numéro de la colonne du libelle */
    public static final int JOUR_NUM_COLONNE_LIBELLE = 1;

    /** Gestionnaire permettant de créer la base de données */
    private HelperBDHoraire gestionnaireBase;

    /** Base de données contenant la liste des horaires */
    private SQLiteDatabase baseHoraire;

    /** Requête pour sélectionner toutes les localisations */
    public static final String REQUETE_TOUT_SELECTIONNER_LOCALISATION =
            "SELECT * FROM " + HelperBDHoraire.NOM_TABLE_LOCALISATION + " ORDER BY " + HelperBDHoraire.LOCALISATION_NOM;

    /** Requête pour sélectionner toutes les catégories */
    public static final String REQUETE_TOUT_SELECTIONNER_CATEGORIE =
            "SELECT * FROM " + HelperBDHoraire.NOM_TABLE_CATEGORIE + " ORDER BY " + HelperBDHoraire.CATEGORIE_NOM;

    /** Requête pour sélectionner toutes fiches plage horaire */
    public static final String REQUETE_TOUT_SELECTIONNER_FICHE_PLAGE_HORAIRE =
            "SELECT * FROM " + HelperBDHoraire.NOM_TABLE_FICHE_PLAGE_HORAIRE + " ORDER BY " + HelperBDHoraire.FICHE_PLAGE_HORAIRE_NOM;

    /** Requête pour sélectionner toutes les fiches horaires ponctuelles */
    public static final String REQUETE_TOUT_SELECTIONNER_FICHE_HORAIRE_PONCTUELLE =
            "SELECT * FROM " + HelperBDHoraire.NOM_TABLE_FICHE_HORAIRE_PONCTUELLE + " ORDER BY " + HelperBDHoraire.FICHE_HORAIRE_PONCTUELLE_NOM;

    /**
     * Constructeur avec argument
     * @param context le contexte de l'application
     */
    public HoraireDAO(Context context) {
        gestionnaireBase = new HelperBDHoraire(context, NOM_BD, null, VERSION);
    }

    /**
     * Ouverture de la base de données
     */
    public void open() {
        baseHoraire = gestionnaireBase.getWritableDatabase();
    }

    /**
     * Fermeture de la base de données
     */
    public void close() {
        gestionnaireBase.close();
        baseHoraire.close();
    }

    /**
     * Retourne un curseur sur toutes les localisations
     * @return le curseur
     */
    public Cursor getCursorAllLocalisation() {
        return baseHoraire.rawQuery(REQUETE_TOUT_SELECTIONNER_LOCALISATION, null);
    }

    /**
     * Retourne un curseur sur toutes les catégories
     * @return le curseur
     */
    public Cursor getCursorAllCategorie() {
        return baseHoraire.rawQuery(REQUETE_TOUT_SELECTIONNER_CATEGORIE, null);
    }

    /**
     * Retourne un curseur sur toutes les fiches plage horaire
     * @return le curseur
     */
    public Cursor getCursorAllFichePlageHoraire() {
        return baseHoraire.rawQuery(REQUETE_TOUT_SELECTIONNER_FICHE_PLAGE_HORAIRE, null);
    }

    /**
     * Retourne un curseur sur toutes les fiches horaire ponctuelle
     * @return le curseur
     */
    public Cursor getCursorAllFicheHorairePonctuelle() {
        return baseHoraire.rawQuery(REQUETE_TOUT_SELECTIONNER_FICHE_HORAIRE_PONCTUELLE, null);
    }

    public FichePlageHoraire getFichePlageHoraireById(String id) {
        FichePlageHoraire fichePlageHoraire = new FichePlageHoraire();
        String requete =
                "SELECT * FROM " + HelperBDHoraire.NOM_TABLE_FICHE_PLAGE_HORAIRE + " WHERE "
                + HelperBDHoraire.FICHE_PLAGE_HORAIRE_CLE + " = " + id;
        Cursor cursor = baseHoraire.rawQuery(requete, null);
        cursor.moveToFirst();
        fichePlageHoraire.setId(cursor.getString(0));
        fichePlageHoraire.setNom(cursor.getString(1));
        fichePlageHoraire.setInformation(cursor.getString(2));
        fichePlageHoraire.setInformation(cursor.getString(3));
        fichePlageHoraire.setIdCategorie(cursor.getString(4));
        return fichePlageHoraire;
    }

    public FicheHorairePonctuelle getFicheHorairePonctuelleById(String id) {
        FicheHorairePonctuelle ficheHorairePonctuelle = new FicheHorairePonctuelle();
        String requete =
                "SELECT * FROM " + HelperBDHoraire.NOM_TABLE_FICHE_HORAIRE_PONCTUELLE + " WHERE "
                        + HelperBDHoraire.FICHE_HORAIRE_PONCTUELLE_CLE + " = " + id;
        Cursor cursor = baseHoraire.rawQuery(requete, null);
        cursor.moveToFirst();
        ficheHorairePonctuelle.setId(cursor.getString(0));
        ficheHorairePonctuelle.setNom(cursor.getString(1));
        ficheHorairePonctuelle.setInformation(cursor.getString(2));
        ficheHorairePonctuelle.setCheminPhoto(cursor.getString(3));
        return ficheHorairePonctuelle;
    }

    /**
     * Permet d'ajouter une localisation
     * @param localisation la localisation à ajouter
     */
    public void addLocalisation(Localisation localisation) {
        ContentValues ajoutLocalisation = new ContentValues();
        ajoutLocalisation.put(HelperBDHoraire.LOCALISATION_NOM, localisation.getNom());
        ajoutLocalisation.put(HelperBDHoraire.LOCALISATION_IS_DEFAULT, 0);
        baseHoraire.insert(HelperBDHoraire.NOM_TABLE_LOCALISATION, HelperBDHoraire.LOCALISATION_NOM, ajoutLocalisation);
    }

    /**
     * Permet d'ajouter une catégorie
     * @param categorie la catégorie à ajouter
     */
    public void addCategorie(Categorie categorie) {
        ContentValues ajoutCategorie = new ContentValues();
        ajoutCategorie.put(HelperBDHoraire.CATEGORIE_NOM, categorie.getNom());
        ajoutCategorie.put(HelperBDHoraire.CATEGORIE_CLE_LOCALISATION, categorie.getIdLocalisation());
        ajoutCategorie.put(HelperBDHoraire.CATEGORIE_IS_DEFAULT, 0);
        baseHoraire.insert(HelperBDHoraire.NOM_TABLE_CATEGORIE, HelperBDHoraire.LOCALISATION_NOM, ajoutCategorie);
    }

    /**
     * Ajout d'une fiche plage horaire
     * @param fichePlageHoraire la fiche plage horaire à ajouter
     */
    public void addFichePlageHoraire(FichePlageHoraire fichePlageHoraire) {
        ContentValues ajoutFichePlageHoraire = new ContentValues();
        ajoutFichePlageHoraire.put(HelperBDHoraire.FICHE_PLAGE_HORAIRE_NOM, fichePlageHoraire.getNom());
        ajoutFichePlageHoraire.put(HelperBDHoraire.FICHE_PLAGE_HORAIRE_INFORMATION, fichePlageHoraire.getInformation());
        ajoutFichePlageHoraire.put(HelperBDHoraire.FICHE_PLAGE_HORAIRE_CHEMIN_IMAGE, fichePlageHoraire.getCheminPhoto());
        ajoutFichePlageHoraire.put(HelperBDHoraire.FICHE_PLAGE_HORAIRE_CLE_CATEGORIE, fichePlageHoraire.getIdCategorie());
        baseHoraire.insert(HelperBDHoraire.NOM_TABLE_FICHE_PLAGE_HORAIRE, HelperBDHoraire.FICHE_PLAGE_HORAIRE_NOM, ajoutFichePlageHoraire);
    }

    /**
     * Ajout d'un ensemble de plage horaire
     * @param ensemblePlageHoraire l'ensemble de plage horaire
     */
    public void addEnsemblePlageHoraire(EnsemblePlageHoraire ensemblePlageHoraire) {
        ContentValues ajoutEnsemblePlageHoraire = new ContentValues();
        ajoutEnsemblePlageHoraire.put(HelperBDHoraire.ENSEMBLE_PLAGE_HORAIRE_CLE_HORAIRE_MATIN, ensemblePlageHoraire.getIdPlageHoraireMatin());
        ajoutEnsemblePlageHoraire.put(HelperBDHoraire.ENSEMBLE_PLAGE_HORAIRE_CLE_HORAIRE_SOIR, ensemblePlageHoraire.getIdPlageHoraireSoir());
        ajoutEnsemblePlageHoraire.put(HelperBDHoraire.ENSEMBLE_PLAGE_HORAIRE_CLE_HORAIRE_SOIR, ensemblePlageHoraire.getIdPlageHoraireSoir());
        ajoutEnsemblePlageHoraire.put(HelperBDHoraire.ENSEMBLE_PLAGE_HORAIRE_CLE_JOUR, ensemblePlageHoraire.getIdJour());
        ajoutEnsemblePlageHoraire.put(HelperBDHoraire.ENSEMBLE_PLAGE_HORAIRE_CLE_FICHE, ensemblePlageHoraire.getIdFichePlageHoraire());
        baseHoraire.insert(HelperBDHoraire.NOM_TABLE_ENSEMBLE_PLAGE_HORAIRE, HelperBDHoraire.ENSEMBLE_PLAGE_HORAIRE_CLE, ajoutEnsemblePlageHoraire);
    }

    /**
     * Ajout d'une plage horaire
     * @param plageHoraire la plage horaire
     */
    public void addPlageHoraire(PlageHoraire plageHoraire) {
        ContentValues ajoutPlageHoraire = new ContentValues();
        ajoutPlageHoraire.put(HelperBDHoraire.PLAGE_HORAIRE_OUVERTURE, plageHoraire.getHoraireOuverture());
        ajoutPlageHoraire.put(HelperBDHoraire.PLAGE_HORAIRE_FERMETURE, plageHoraire.getHoraireFermeture());
        ajoutPlageHoraire.put(HelperBDHoraire.PLAGE_HORAIRE_ETAT_OUVERTURE, plageHoraire.getEtatOuverture());
        ajoutPlageHoraire.put(HelperBDHoraire.PLAGE_HORAIRE_ETAT_FERMETURE, plageHoraire.getEtatFermeture());
        baseHoraire.insert(HelperBDHoraire.NOM_TABLE_PLAGE_HORAIRE, HelperBDHoraire.PLAGE_HORAIRE_CLE, ajoutPlageHoraire);
    }

    /**
     * Ajout d'une horaire ponctuelle
     * @param horairePonctuelle l'horaire ponctuelle à ajouter
     */
    public void addHorairePonctuelle(HorairePonctuelle horairePonctuelle) {
        ContentValues ajoutHorairePonctuelle = new ContentValues();
        ajoutHorairePonctuelle.put(HelperBDHoraire.HORAIRE_PONCTUELLE_OUVERTURE, horairePonctuelle.getHoraireDebut());
        ajoutHorairePonctuelle.put(HelperBDHoraire.HORAIRE_PONCTUELLE_FERMETURE, horairePonctuelle.getHoraireFin());
        ajoutHorairePonctuelle.put(HelperBDHoraire.HORAIRE_PONCTUELLE_CLE_JOUR, horairePonctuelle.getIdJour());
        ajoutHorairePonctuelle.put(HelperBDHoraire.HORAIRE_PONCTUELLE_CLE_FICHE, horairePonctuelle.getIdFicheHorairePonctuelle());
        baseHoraire.insert(HelperBDHoraire.NOM_TABLE_HORAIRE_PONCTUELLE, HelperBDHoraire.HORAIRE_PONCTUELLE_CLE, ajoutHorairePonctuelle);
    }

    /**
     * Ajout d'une fiche horaire ponctuelle
     * @param ficheHorairePonctuelle la nouvelle fiche horaire ponctuelle
     */
    public void addFicheHorairePonctuelle(FicheHorairePonctuelle ficheHorairePonctuelle) {
        ContentValues ajoutFicheHorairePonctuelle = new ContentValues();
        ajoutFicheHorairePonctuelle.put(HelperBDHoraire.FICHE_HORAIRE_PONCTUELLE_NOM, ficheHorairePonctuelle.getNom());
        ajoutFicheHorairePonctuelle.put(HelperBDHoraire.FICHE_HORAIRE_PONCTUELLE_INFORMATION, ficheHorairePonctuelle.getInformation());
        ajoutFicheHorairePonctuelle.put(HelperBDHoraire.FICHE_HORAIRE_PONCTUELLE_IMAGE, ficheHorairePonctuelle.getCheminPhoto());
        baseHoraire.insert(HelperBDHoraire.NOM_TABLE_FICHE_HORAIRE_PONCTUELLE, HelperBDHoraire.FICHE_HORAIRE_PONCTUELLE_NOM, ajoutFicheHorairePonctuelle);
    }

    /**
     * Suppression d'une localisation
     * @param idLocalisation l'id de la localisation à supprimer
     * @return un indicateur sur la suppression
     */
    public int deleteLocalisation(String idLocalisation) {
        return baseHoraire.delete(HelperBDHoraire.NOM_TABLE_LOCALISATION,
                HelperBDHoraire.LOCALISATION_CLE + " = ?",
                new String[] {idLocalisation});
    }

    /**
     * Suppression d'une catégorie
     * @param idCategorie l'id de la catégorie à supprimer
     * @return un indicateur sur la suppression
     */
    public int deleteCategorie(String idCategorie) {
        return baseHoraire.delete(HelperBDHoraire.NOM_TABLE_CATEGORIE,
                HelperBDHoraire.CATEGORIE_CLE + " = ?",
                new String[] {idCategorie});
    }

    /**
     * Suppresion d'une fiche de plage horaire
     * @param idFichePlageHoraire l'id de la fiche plage horaire
     * @return un indicateur sur la suppression
     */
    public int deleteFichePlageHoraire(String idFichePlageHoraire) {
        return baseHoraire.delete(HelperBDHoraire.NOM_TABLE_FICHE_PLAGE_HORAIRE,
                HelperBDHoraire.FICHE_PLAGE_HORAIRE_CLE + " = ?",
                new String[] {idFichePlageHoraire});
    }

    /**
     * Suppression d'une fiche horaire ponctuelle
     * @param idFicheHorairePonctuelle l'id de la fiche horaire ponctuelle
     * @return un indicateur sur la suppression
     */
    public int deleteFicheHorairePonctuelle(String idFicheHorairePonctuelle) {
        return baseHoraire.delete(HelperBDHoraire.NOM_TABLE_FICHE_HORAIRE_PONCTUELLE,
                HelperBDHoraire.FICHE_HORAIRE_PONCTUELLE_CLE + " = ?",
                new String[] {idFicheHorairePonctuelle});
    }

    /**
     * Modifie une localisation
     * @param localisation la localisation à modifier
     * @param identifiant l'identifiant de la catégorie à modifier
     */
    public void updateLocalisation(Localisation localisation, String identifiant) {
        ContentValues nouvelleLocalisation = new ContentValues();
        nouvelleLocalisation.put(HelperBDHoraire.LOCALISATION_NOM, localisation.getNom());
        baseHoraire.update(HelperBDHoraire.NOM_TABLE_LOCALISATION,
                nouvelleLocalisation,
                HelperBDHoraire.LOCALISATION_CLE + " = ?",
                new String[] {identifiant});
    }

    /**
     * Modifie une catégorie
     * @param categorie la catégorie modifiée
     * @param identifiant l'identifiant de la catégorie à modifier
     */
    public void updateCategorie(Categorie categorie, String identifiant) {
        ContentValues nouvelleCategorie = new ContentValues();
        nouvelleCategorie.put(HelperBDHoraire.CATEGORIE_NOM, categorie.getNom());
        nouvelleCategorie.put(HelperBDHoraire.CATEGORIE_CLE_LOCALISATION, categorie.getId());
        baseHoraire.update(HelperBDHoraire.NOM_TABLE_CATEGORIE,
                nouvelleCategorie,
                HelperBDHoraire.CATEGORIE_CLE + " = ?",
                new String[] {identifiant});
    }

    /**
     * Modifie une fiche plage horaire
     * @param fichePlageHoraire la fiche plage horaire modifiée
     * @param identifiant l'identifiant de la fiche plage horaire à modifier
     */
    public void updateFichePlageHoraire(FichePlageHoraire fichePlageHoraire, String identifiant) {
        ContentValues nouvelleFichePlageHoraire = new ContentValues();
        nouvelleFichePlageHoraire.put(HelperBDHoraire.FICHE_PLAGE_HORAIRE_NOM, fichePlageHoraire.getNom());
        nouvelleFichePlageHoraire.put(HelperBDHoraire.FICHE_PLAGE_HORAIRE_INFORMATION, fichePlageHoraire.getInformation());
        nouvelleFichePlageHoraire.put(HelperBDHoraire.FICHE_PLAGE_HORAIRE_CHEMIN_IMAGE, fichePlageHoraire.getCheminPhoto());
        nouvelleFichePlageHoraire.put(HelperBDHoraire.FICHE_PLAGE_HORAIRE_CLE_CATEGORIE, fichePlageHoraire.getIdCategorie());
        baseHoraire.update(HelperBDHoraire.NOM_TABLE_FICHE_PLAGE_HORAIRE,
                nouvelleFichePlageHoraire,
                HelperBDHoraire.FICHE_PLAGE_HORAIRE_CLE + " = ?",
                new String[] {identifiant});
    }

    /**
     * Modifie un ensemble de plage horaire
     * @param ensemblePlageHoraire l'ensemble de fiche plage horaire
     * @param identifiant l'identifiant de l'ensemble plage horaire
     */
    public void updateEnsemblePlageHoraire(EnsemblePlageHoraire ensemblePlageHoraire, String identifiant) {
        ContentValues nouvelEnsemblePlageHoraire = new ContentValues();
        nouvelEnsemblePlageHoraire.put(HelperBDHoraire.ENSEMBLE_PLAGE_HORAIRE_CLE_HORAIRE_MATIN, ensemblePlageHoraire.getIdPlageHoraireMatin());
        nouvelEnsemblePlageHoraire.put(HelperBDHoraire.ENSEMBLE_PLAGE_HORAIRE_CLE_HORAIRE_SOIR, ensemblePlageHoraire.getIdPlageHoraireSoir());
        nouvelEnsemblePlageHoraire.put(HelperBDHoraire.ENSEMBLE_PLAGE_HORAIRE_CLE_JOUR, ensemblePlageHoraire.getIdJour());
        nouvelEnsemblePlageHoraire.put(HelperBDHoraire.ENSEMBLE_PLAGE_HORAIRE_CLE_FICHE, ensemblePlageHoraire.getIdFichePlageHoraire());
        baseHoraire.update(HelperBDHoraire.NOM_TABLE_ENSEMBLE_PLAGE_HORAIRE,
                nouvelEnsemblePlageHoraire,
                HelperBDHoraire.ENSEMBLE_PLAGE_HORAIRE_CLE + " = ?",
                new String[] {identifiant});
    }

    /**
     * Modifie une plage horaire
     * @param plageHoraire la plage horaire modifiée
     * @param identifiant l'identifiant de la plage horaire à modifier
     */
    public void updatePlageHoraire(PlageHoraire plageHoraire, String identifiant) {
        ContentValues nouvellePlageHoraire = new ContentValues();
        nouvellePlageHoraire.put(HelperBDHoraire.PLAGE_HORAIRE_OUVERTURE, plageHoraire.getHoraireOuverture());
        nouvellePlageHoraire.put(HelperBDHoraire.PLAGE_HORAIRE_FERMETURE, plageHoraire.getHoraireFermeture());
        nouvellePlageHoraire.put(HelperBDHoraire.PLAGE_HORAIRE_ETAT_OUVERTURE, plageHoraire.getEtatOuverture());
        nouvellePlageHoraire.put(HelperBDHoraire.PLAGE_HORAIRE_ETAT_FERMETURE, plageHoraire.getEtatFermeture());
        baseHoraire.update(HelperBDHoraire.NOM_TABLE_PLAGE_HORAIRE,
                nouvellePlageHoraire,
                HelperBDHoraire.PLAGE_HORAIRE_CLE + " = ?",
                new String[] {identifiant});
    }

    /**
     * Modifie une horaire ponctuelle
     * @param horairePonctuelle l'horaire ponctuelle modifiée
     * @param identifiant l'identifiant de l'horaire ponctuelle à modifier
     */
    public void updateHorairePonctuelle(HorairePonctuelle horairePonctuelle, String identifiant) {
        ContentValues nouvellleHorairePonctuelle = new ContentValues();
        nouvellleHorairePonctuelle.put(HelperBDHoraire.HORAIRE_PONCTUELLE_OUVERTURE, horairePonctuelle.getHoraireDebut());
        nouvellleHorairePonctuelle.put(HelperBDHoraire.HORAIRE_PONCTUELLE_FERMETURE, horairePonctuelle.getHoraireFin());
        nouvellleHorairePonctuelle.put(HelperBDHoraire.HORAIRE_PONCTUELLE_CLE_JOUR, horairePonctuelle.getIdJour());
        nouvellleHorairePonctuelle.put(HelperBDHoraire.HORAIRE_PONCTUELLE_CLE_FICHE, horairePonctuelle.getIdFicheHorairePonctuelle());
        baseHoraire.update(HelperBDHoraire.NOM_TABLE_HORAIRE_PONCTUELLE,
                nouvellleHorairePonctuelle,
                HelperBDHoraire.HORAIRE_PONCTUELLE_CLE + " = ?",
                new String[] {identifiant});
    }

    /**
     * Modifie une fiche horaire ponctuelle
     * @param ficheHorairePonctuelle la fiche horaire ponctuelle modifiée
     * @param identifiant l'identifiant de la fiche horaire à modifier
     */
    public void updateFicheHorairePonctuelle(FicheHorairePonctuelle ficheHorairePonctuelle, String identifiant) {
        ContentValues nouvelleFicheHorairePonctuelle = new ContentValues();
        nouvelleFicheHorairePonctuelle.put(HelperBDHoraire.FICHE_HORAIRE_PONCTUELLE_NOM, ficheHorairePonctuelle.getNom());
        nouvelleFicheHorairePonctuelle.put(HelperBDHoraire.FICHE_HORAIRE_PONCTUELLE_INFORMATION, ficheHorairePonctuelle.getInformation());
        nouvelleFicheHorairePonctuelle.put(HelperBDHoraire.FICHE_HORAIRE_PONCTUELLE_IMAGE, ficheHorairePonctuelle.getCheminPhoto());
        baseHoraire.update(HelperBDHoraire.NOM_TABLE_FICHE_HORAIRE_PONCTUELLE,
                nouvelleFicheHorairePonctuelle,
                HelperBDHoraire.FICHE_HORAIRE_PONCTUELLE_CLE + " = ?",
                new String[] {identifiant});

    }

    /**
     * Retourne une liste de catégories en fonction d'une localisation
     * @param idLocalisation l'identifiant de la localisation
     * @return la liste des catégories
     */
    public ArrayList<Categorie> getCategoriesByLocalisation(String idLocalisation) {
        ArrayList<Categorie> listeCategories = new ArrayList<>();

        String requete =
                "SELECT * FROM "
                + HelperBDHoraire.NOM_TABLE_CATEGORIE
                + " WHERE " + HelperBDHoraire.CATEGORIE_CLE_LOCALISATION
                + " = " + idLocalisation;
        Cursor cursor = baseHoraire.rawQuery(requete, null);
        cursor.moveToFirst();
        Categorie categorie = new Categorie(cursor.getString(2),
                idLocalisation);
        categorie.setId(cursor.getString(0));
        listeCategories.add(categorie);

        while (cursor.moveToNext()) {
            categorie = new Categorie(cursor.getString(2),
                    idLocalisation);
            categorie.setId(cursor.getString(0));
            listeCategories.add(categorie);
        }

        return listeCategories;
    }

    /**
     * Récupère la localisation par défaut
     * @return la localisation
     */
    public Cursor getCursorDefaultLocalisation() {
        String requete =
                "SELECT * FROM " + HelperBDHoraire.NOM_TABLE_LOCALISATION
                + " WHERE " + HelperBDHoraire.LOCALISATION_IS_DEFAULT + " = 1";
        return baseHoraire.rawQuery(requete, null);
    }

    /**
     * Change la localisation des catégories
     * @param categories la listes des catégories dont on souhaite mettre la localisation par défaut
     */
    public void resetCategories(ArrayList<Categorie> categories, String idLocalisation) {
        for (Categorie categorie : categories) {
            updateCategorie(categorie, idLocalisation);
        }
    }

    /**
     * Récupère la liste des fiches plage horaire en fonction d'une catégorie
     * @param idCategorie l'identifiant de la catégorie dont on souhaite récupérer les fiches
     * @return la liste des fiches plage horaire
     */
    public ArrayList<FichePlageHoraire> getFichePlageHoraireByCategorie(String idCategorie) {
        ArrayList<FichePlageHoraire> listeFichePlageHoraire = new ArrayList<>();

        String requete =
                "SELECT * FROM "
                        + HelperBDHoraire.NOM_TABLE_FICHE_PLAGE_HORAIRE
                        + " WHERE " + HelperBDHoraire.FICHE_PLAGE_HORAIRE_CLE_CATEGORIE
                        + " = " + idCategorie;
        Cursor cursor = baseHoraire.rawQuery(requete, null);
        cursor.moveToFirst();
        FichePlageHoraire fichePlageHoraire = new FichePlageHoraire(cursor.getString(1),
                idCategorie,
                cursor.getString(2),
                cursor.getString(3));
        fichePlageHoraire.setId(cursor.getString(0));
        listeFichePlageHoraire.add(fichePlageHoraire);

        while (cursor.moveToNext()) {
            fichePlageHoraire = new FichePlageHoraire(cursor.getString(1),
                    idCategorie,
                    cursor.getString(2),
                    cursor.getString(3));
            fichePlageHoraire.setId(cursor.getString(0));
            listeFichePlageHoraire.add(fichePlageHoraire);
        }

        return listeFichePlageHoraire;
    }

    /**
     * Récupère la catégorie par défaut
     * @return la catégorie
     */
    public Cursor getCursorDefaultCategorie() {
        String requete =
                "SELECT * FROM " + HelperBDHoraire.NOM_TABLE_CATEGORIE
                        + " WHERE " + HelperBDHoraire.CATEGORIE_IS_DEFAULT + " = 1";
        return baseHoraire.rawQuery(requete, null);
    }

    /**
     * Change la localisation des fiches plage horaire
     * @param fichesPlageHoraire la liste des fiches plage horaire dont on souhaite mettre la localisation par défaut
     */
    public void resetFichePlageHoraire(ArrayList<FichePlageHoraire> fichesPlageHoraire, String idCategorie) {
        for (FichePlageHoraire fichePlageHoraire : fichesPlageHoraire) {
            updateFichePlageHoraire(fichePlageHoraire, idCategorie);
        }
    }
}