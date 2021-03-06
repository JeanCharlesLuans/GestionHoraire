package com.example.gestionhoraires;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorJoiner;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.sax.EndElementListener;
import android.support.v4.app.INotificationSideChannel;
import android.telephony.emergency.EmergencyNumber;

import com.example.gestionhoraires.beans.Categorie;
import com.example.gestionhoraires.beans.EnsemblePlageHoraire;
import com.example.gestionhoraires.beans.FicheHorairePonctuelle;
import com.example.gestionhoraires.beans.FichePlageHoraire;
import com.example.gestionhoraires.beans.HorairePonctuelle;
import com.example.gestionhoraires.beans.Jour;
import com.example.gestionhoraires.beans.Localisation;
import com.example.gestionhoraires.beans.PlageHoraire;
import com.google.android.material.animation.ChildrenAlphaProperty;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

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

    /** Numéro de la colonne contenant l'information de catégorie par défaut */
    public static final int CATEGORIE_NUM_COLONNE_DEFAUT = 3;

    /** Numéro de la colonne contenant l'indicateur de plage horaire ou horaire ponctuel */
    public static final int CATEGORIE_NUM_COLONNE_HORAIRE_PONCTUELLE = 4;

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

    /** Numéro de la colonne de l'indicateur de fermeture */
    public static final int PLAGE_HORAIRE_NUM_COLONNE_EST_FERME = 3;

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
    public static final int FICHE_HORAIRE_PONCTUELLE_NUM_COLONNE_CHEMIN_IMAGE = 3;

    /** Numéro de la colonne contenant l'id de la catégorie */
    public static final int FICHE_HORAIRE_PONCTUELLE_NUM_COLONNE_CLE_CATEGORIE = 4;

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
            "SELECT * FROM " + HelperBDHoraire.NOM_TABLE_LOCALISATION + " ORDER BY " + HelperBDHoraire.LOCALISATION_CLE;

    /** Requête pour sélectionner toutes les catégories des plages horaires */
    public static final String REQUETE_TOUT_SELECTIONNER_CATEGORIE_PLAGE_HORAIRE =
            "SELECT * FROM " + HelperBDHoraire.NOM_TABLE_CATEGORIE
                    + " WHERE " + HelperBDHoraire.CATEGORIE_HORAIRE_PONCTUELLE + " = 0"
                    + " ORDER BY " + HelperBDHoraire.CATEGORIE_NOM;

    /** Requête pour sélectionner toutes les catégories des horaires ponctuelles */
    public static final String REQUETE_TOUT_SELECTIONNER_CATEGORIE_HORAIRE_PONCTUELLE =
            "SELECT * FROM " + HelperBDHoraire.NOM_TABLE_CATEGORIE
                    + " WHERE " + HelperBDHoraire.CATEGORIE_HORAIRE_PONCTUELLE + " = 1"
                    + " ORDER BY " + HelperBDHoraire.CATEGORIE_NOM;

    /** Requête pour sélectionner toutes fiches plage horaire */
    public static final String REQUETE_TOUT_SELECTIONNER_FICHE_PLAGE_HORAIRE =
            "SELECT * FROM " + HelperBDHoraire.NOM_TABLE_FICHE_PLAGE_HORAIRE + " ORDER BY " + HelperBDHoraire.FICHE_PLAGE_HORAIRE_CLE;

    /** Requête pour sélectionner toutes les fiches horaires ponctuelles */
    public static final String REQUETE_TOUT_SELECTIONNER_FICHE_HORAIRE_PONCTUELLE =
            "SELECT * FROM " + HelperBDHoraire.NOM_TABLE_FICHE_HORAIRE_PONCTUELLE + " ORDER BY " + HelperBDHoraire.FICHE_HORAIRE_PONCTUELLE_CLE;

    /** Requête pour sélectionner tous les jours */
    public static final String REQUETE_TOUT_SELECTIONNER_JOUR =
            "SELECT * FROM " + HelperBDHoraire.NOM_TABLE_JOUR + " ORDER BY " + HelperBDHoraire.JOUR_CLE;

    /** Requête pour sélectionner toutes les catégories avec leurs localisations associées */
    public static final String REQUETE_TOUT_SELECTIONNER_CATEGORIE_LOCALISATION_PLAGE_HORAIRE =
            "SELECT * FROM " + HelperBDHoraire.VUE_CATEGORIE_LOCALISATION
                    + " WHERE " + HelperBDHoraire.CATEGORIE_HORAIRE_PONCTUELLE + " = 0"
                    + " ORDER BY " + HelperBDHoraire.CATEGORIE_CLE;

    /** Requête pour sélectionner toutes les catégories avec leurs localisations associées */
    public static final String REQUETE_TOUT_SELECTIONNER_CATEGORIE_LOCALISATION_HORAIRE_PONCTUEL =
            "SELECT * FROM " + HelperBDHoraire.VUE_CATEGORIE_LOCALISATION
                    + " WHERE " + HelperBDHoraire.CATEGORIE_HORAIRE_PONCTUELLE + " = 1"
                    + " ORDER BY " + HelperBDHoraire.CATEGORIE_CLE;

    /** Requête pour sélectionner toutes les catégories */
    public static final String REQUETE_TOUT_SELECTIONNER_CATEGORIE =
            "SELECT * FROM " + HelperBDHoraire.NOM_TABLE_CATEGORIE + " ORDER BY " + HelperBDHoraire.CATEGORIE_CLE;

    /** Requête pour sélectionner toutes les plages horaires */
    private static final String REQUETE_TOUT_SELECTIONNE_PLAGE_HORAIRE =
            "SELECT * FROM " + HelperBDHoraire.NOM_TABLE_PLAGE_HORAIRE + " ORDER BY " + HelperBDHoraire.PLAGE_HORAIRE_CLE;

    /** Requête pour sélectionner tous les ensembles de plages horaires */
    private static final String REQUETE_TOUT_SELECTIONNE_ENSEMBLE_PLAGE_HORAIRE =
            "SELECT * FROM " + HelperBDHoraire.NOM_TABLE_ENSEMBLE_PLAGE_HORAIRE+ " ORDER BY " + HelperBDHoraire.ENSEMBLE_PLAGE_HORAIRE_CLE;

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
     * Retourne un curseur sur la vue
     * @return le curseur
     */
    public Cursor getCursorAllCategorieLocalisationPlageHoraire() {
        return baseHoraire.rawQuery(REQUETE_TOUT_SELECTIONNER_CATEGORIE_LOCALISATION_PLAGE_HORAIRE, null);
    }

    /**
     * Retourne un curseur sur la vue
     * @return le curseur
     */
    public Cursor getCursorAllCategorieLocalisationHorairePonctuel() {
        return baseHoraire.rawQuery(REQUETE_TOUT_SELECTIONNER_CATEGORIE_LOCALISATION_HORAIRE_PONCTUEL, null);
    }

    /**
     * Retourne un curseur sur toutes les catégories
     */
    public Cursor getCursorAllCategorie() {
        return baseHoraire.rawQuery(REQUETE_TOUT_SELECTIONNER_CATEGORIE, null);
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
    public Cursor getCursorAllCategoriePlageHoraire() {
        return baseHoraire.rawQuery(REQUETE_TOUT_SELECTIONNER_CATEGORIE_PLAGE_HORAIRE, null);
    }

    /**
     * Retourne un curseur sur toutes les catégories
     * @return le curseur
     */
    public Cursor getCursorAllCategorieHorairePonctuelle() {
        return baseHoraire.rawQuery(REQUETE_TOUT_SELECTIONNER_CATEGORIE_HORAIRE_PONCTUELLE, null);
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

    /**
     * Retourne un curseur sur tous les jours
     * @return le curseur
     */
    public Cursor getCursorAllJour() {
        return baseHoraire.rawQuery(REQUETE_TOUT_SELECTIONNER_JOUR, null);
    }

    /**
     * Retourne un curseur sur toutes les plages horaires
     * @return le curseur
     */
    public Cursor getCursorAllPlageHoraire() {
        return baseHoraire.rawQuery(REQUETE_TOUT_SELECTIONNE_PLAGE_HORAIRE, null);
    }

    /**
     * Retourne un curseur sur tous les ensemble de plages horaires
     * @return le curseur
     */
    public Cursor getCursorAllEnsemblePlageHoraire() {
        return baseHoraire.rawQuery(REQUETE_TOUT_SELECTIONNE_ENSEMBLE_PLAGE_HORAIRE, null);
    }

    /**
     * Récupère une fiche plage horaire
     * @param id l'identifiant de la fiche plage horaire
     * @return la fiche plage horaire
     */
    public FichePlageHoraire getFichePlageHoraireById(String id) {
        FichePlageHoraire fichePlageHoraire = new FichePlageHoraire();
        String requete =
                "SELECT * FROM " + HelperBDHoraire.NOM_TABLE_FICHE_PLAGE_HORAIRE + " WHERE "
                + HelperBDHoraire.FICHE_PLAGE_HORAIRE_CLE + " = " + id;
        Cursor cursor = baseHoraire.rawQuery(requete, null);
        cursor.moveToFirst();
        fichePlageHoraire.setId(cursor.getString(FICHE_PLAGE_HORAIRE_NUM_COLONNE_CLE));
        fichePlageHoraire.setNom(cursor.getString(FICHE_PLAGE_HORAIRE_NUM_COLONNE_NOM));
        fichePlageHoraire.setInformation(cursor.getString(FICHE_PLAGE_HORAIRE_NUM_COLONNE_INFORMATION));
        fichePlageHoraire.setCheminPhoto(cursor.getString(FICHE_PLAGE_HORAIRE_NUM_COLONNE_CHEMIN_IMAGE));
        fichePlageHoraire.setIdCategorie(cursor.getString(FICHE_PLAGE_HORAIRE_NUM_COLONNE_CLE_CATEGORIE));
        return fichePlageHoraire;
    }

    /**
     * Récupère une fiche horaire ponctuelle en fonction d'un identifiant
     * @param id l'identifiant de la fiche
     * @return la fiche horaire ponctuelle
     */
    public FicheHorairePonctuelle getFicheHorairePonctuelleById(String id) {
        FicheHorairePonctuelle ficheHorairePonctuelle = new FicheHorairePonctuelle();
        String requete =
                "SELECT * FROM " + HelperBDHoraire.NOM_TABLE_FICHE_HORAIRE_PONCTUELLE + " WHERE "
                        + HelperBDHoraire.FICHE_HORAIRE_PONCTUELLE_CLE + " = " + id;
        Cursor cursor = baseHoraire.rawQuery(requete, null);
        cursor.moveToFirst();
        ficheHorairePonctuelle.setId(cursor.getString(FICHE_HORAIRE_PONCTUELLE_NUM_COLONNE_CLE));
        ficheHorairePonctuelle.setNom(cursor.getString(FICHE_HORAIRE_PONCTUELLE_NUM_COLONNE_NOM));
        ficheHorairePonctuelle.setInformation(cursor.getString(FICHE_HORAIRE_PONCTUELLE_NUM_COLONNE_INFORMATION));
        ficheHorairePonctuelle.setCheminPhoto(cursor.getString(FICHE_HORAIRE_PONCTUELLE_NUM_COLONNE_CHEMIN_IMAGE));
        ficheHorairePonctuelle.setIdCategorie(cursor.getString(FICHE_HORAIRE_PONCTUELLE_NUM_COLONNE_CLE_CATEGORIE));
        return ficheHorairePonctuelle;
    }

    /**
     * Récupère une localisation en fonction d'un identifiant
     * @param idLocalisation l'identifiant de la localisation
     * @return la localisation
     */
    public Localisation getLocalisationById(String idLocalisation) {
        Localisation localisation = new Localisation();
        String requete =
                "SELECT * FROM " + HelperBDHoraire.NOM_TABLE_LOCALISATION + " WHERE "
                        + HelperBDHoraire.LOCALISATION_CLE + " = " + idLocalisation;
        Cursor cursor = baseHoraire.rawQuery(requete, null);
        cursor.moveToFirst();
        localisation.setId(cursor.getString(LOCALISATION_NUM_COLONNE_CLE));
        localisation.setNom(cursor.getString(LOCALISATION_NUM_COLONNE_NOM));
        localisation.setIsDefault(Integer.parseInt(cursor.getString(LOCALISATION_NUM_COLONNE_DEFAUT)));
        return localisation;
    }

    /**
     * Rétourne une catégorie en fonction d'un identifiant
     * @param idCategorie l'id de la catégorie
     * @return la catégorie
     */
    public Categorie getCategorieById(String idCategorie) {
        Categorie categorie = new Categorie();
        String requete =
                "SELECT * FROM " + HelperBDHoraire.NOM_TABLE_CATEGORIE + " WHERE "
                        + HelperBDHoraire.CATEGORIE_CLE + " = " + idCategorie;
        Cursor cursor = baseHoraire.rawQuery(requete, null);
        cursor.moveToFirst();
        categorie.setId(cursor.getString(CATEGORIE_NUM_COLONNE_CLE));
        categorie.setIdLocalisation(cursor.getString(CATEGORIE_NUM_COLONNE_CLE_LOCALISATION));
        categorie.setNom(cursor.getString(CATEGORIE_NUM_COLONNE_NOM));
        categorie.setIsDefault(Integer.parseInt(cursor.getString(CATEGORIE_NUM_COLONNE_DEFAUT)));
        categorie.setIsHorairePonctuelle(Integer.parseInt(cursor.getString(CATEGORIE_NUM_COLONNE_HORAIRE_PONCTUELLE)));
        return categorie;
    }

    /**
     * Récupère le jour en fonction de l'identifiant
     * (utilisé uniquement pour la partie export)
     * @param idJour identifiant du jour
     * @return le jour
     */
    public Jour getJourById(String idJour) {
        Jour jour = new Jour();
        String requete =
                "SELECT * FROM " + HelperBDHoraire.NOM_TABLE_JOUR + " WHERE "
                        + HelperBDHoraire.JOUR_CLE + " = " + idJour;
        Cursor cursor = baseHoraire.rawQuery(requete, null);
        cursor.moveToFirst();
        jour.setId(cursor.getString(JOUR_NUM_COLONNE_CLE));
        jour.setJour(cursor.getString(JOUR_NUM_COLONNE_LIBELLE));
        return jour;
    }

    /**
     * Récupère une fiche horaire ponctuelle en fonction d'un identifiant
     * @param idHorairePonctuelle l'identifiant de l'horaire ponctuelle
     * @return l'horaire ponctuelle
     */
    public HorairePonctuelle getHorairePonctuelleById(String idHorairePonctuelle) {
        HorairePonctuelle horairePonctuelle = new HorairePonctuelle();
        String requete =
                "SELECT * FROM " + HelperBDHoraire.NOM_TABLE_HORAIRE_PONCTUELLE + " WHERE "
                        + HelperBDHoraire.HORAIRE_PONCTUELLE_CLE + " = " + idHorairePonctuelle;
        Cursor cursor = baseHoraire.rawQuery(requete, null);
        cursor.moveToFirst();
        horairePonctuelle.setId(cursor.getString(HORAIRE_PONCTUELLE_NUM_COLONNE_CLE));
        horairePonctuelle.setHoraireDebut(cursor.getString(HORAIRE_PONCTUELLE_NUM_COLONNE_HORAIRE_OUVERTURE));
        horairePonctuelle.setHoraireFin(cursor.getString(HORAIRE_PONCTUELLE_NUM_COLONNE_HORAIRE_FERMETURE));
        horairePonctuelle.setIdJour(cursor.getString(HORAIRE_PONCTUELLE_NUM_COLONNE_CLE_JOUR));
        horairePonctuelle.setIdFicheHorairePonctuelle(cursor.getString(HORAIRE_PONCTUELLE_NUM_COLONNE_CLE_FICHE_HORAIRE_PONCTUELLE));
        return horairePonctuelle;
    }

    /**
     * Récupère un ensemble de plage horaire en fonction d'un id
     * @param idEnsemblePlageHoraire identifiant de l'ensemble que l'on souhaite récupérer
     * @return l'ensemble de plage horaire
     */
    public EnsemblePlageHoraire getEnsemblePlageHoraireById(String idEnsemblePlageHoraire) {
        EnsemblePlageHoraire ensemblePlageHoraire = new EnsemblePlageHoraire();
        String requete =
                "SELECT * FROM " + HelperBDHoraire.NOM_TABLE_ENSEMBLE_PLAGE_HORAIRE + " WHERE "
                        + HelperBDHoraire.ENSEMBLE_PLAGE_HORAIRE_CLE + " = " + idEnsemblePlageHoraire;
        Cursor cursor = baseHoraire.rawQuery(requete, null);
        cursor.moveToFirst();
        ensemblePlageHoraire.setId(cursor.getString(ENSEMBLE_PLAGE_HORAIRE_NUM_COLONNE_CLE));
        ensemblePlageHoraire.setIdPlageHoraireMatin(cursor.getString(ENSEMBLE_PLAGE_HORAIRE_NUM_COLONNE_CLE_PLAGE_HORAIRE_MATIN));
        ensemblePlageHoraire.setIdPlageHoraireSoir(cursor.getString(ENSEMBLE_PLAGE_HORAIRE_NUM_COLONNE_CLE_PLAGE_HORAIRE_SOIR));
        ensemblePlageHoraire.setIdJour(cursor.getString(ENSEMBLE_PLAGE_HORAIRE_NUM_COLONNE_JOUR));
        ensemblePlageHoraire.setIdFichePlageHoraire(cursor.getString(ENSEMBLE_PLAGE_HORAIRE_NUM_COLONNE_CLE_FICHE_PLAGE_HORAIRE));
        return ensemblePlageHoraire;
    }

    /**
     * Récupère une plage horaire en fonction d'un id
     * @param idPlageHoraire identifiant de la plage horaire
     * @return la plage horaire
     */
    public PlageHoraire getPlageHoraireById(String idPlageHoraire) {
        PlageHoraire plageHoraire = new PlageHoraire();
        String requete =
                "SELECT * FROM " + HelperBDHoraire.NOM_TABLE_PLAGE_HORAIRE + " WHERE "
                        + HelperBDHoraire.PLAGE_HORAIRE_CLE + " = " + idPlageHoraire;
        Cursor cursor = baseHoraire.rawQuery(requete, null);
        cursor.moveToFirst();
        if (cursor.getCount() != 0) {
            plageHoraire.setId(cursor.getString(PLAGE_HORAIRE_NUM_COLONNE_CLE));
            plageHoraire.setHoraireOuverture(cursor.getString(PLAGE_HORAIRE_NUM_COLONNE_HORAIRE_OUVERTURE));
            plageHoraire.setHoraireFermeture(cursor.getString(PLAGE_HORAIRE_NUM_COLONNE_HORAIRE_FERMETURE));
            plageHoraire.setEstFerme(Integer.parseInt(cursor.getString(PLAGE_HORAIRE_NUM_COLONNE_EST_FERME)));
        }

        return plageHoraire;
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
        ajoutCategorie.put(HelperBDHoraire.CATEGORIE_HORAIRE_PONCTUELLE, categorie.getIsHorairePonctuelle());
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
        ajoutPlageHoraire.put(HelperBDHoraire.PLAGE_HORAIRE_EST_FERME, plageHoraire.getEstFerme());
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
        ajoutFicheHorairePonctuelle.put(HelperBDHoraire.FICHE_HORAIRE_PONCTUELLE_CLE_CATEGORIE, ficheHorairePonctuelle.getIdCategorie());
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
     * Suppression d'une plage horaire ponctuel
     * @param idPlageHorairePonctuel id de la plage horaire ponctuel à supprimer
     * @return un indicateur sur la suppression
     */
    public int deleteHorairePonctuel(String idPlageHorairePonctuel) {
        return baseHoraire.delete(HelperBDHoraire.NOM_TABLE_HORAIRE_PONCTUELLE,
                HelperBDHoraire.HORAIRE_PONCTUELLE_CLE + " = ?",
                new String[] {idPlageHorairePonctuel});
    }

    /**
     * Suppression d'un ensemble de plage horaire
     * @param idEnsemblePlageHoraire l'id de l'ensemble de plage horaire
     * @return un indicateur sur la suppression
     */
    public int deleteEnsemblePlageHoraire(String idEnsemblePlageHoraire) {
        EnsemblePlageHoraire ensemblePlageHoraire = getEnsemblePlageHoraireById(idEnsemblePlageHoraire);
        baseHoraire.delete(HelperBDHoraire.NOM_TABLE_PLAGE_HORAIRE,
                HelperBDHoraire.PLAGE_HORAIRE_CLE + " = ?",
                new String[] {ensemblePlageHoraire.getIdPlageHoraireMatin()});
        if (ensemblePlageHoraire.getIdPlageHoraireSoir() != null) {
            baseHoraire.delete(HelperBDHoraire.NOM_TABLE_PLAGE_HORAIRE,
                    HelperBDHoraire.PLAGE_HORAIRE_CLE + " = ?",
                    new String[] {ensemblePlageHoraire.getIdPlageHoraireSoir()});
        }
        return baseHoraire.delete(HelperBDHoraire.NOM_TABLE_ENSEMBLE_PLAGE_HORAIRE,
                HelperBDHoraire.ENSEMBLE_PLAGE_HORAIRE_CLE + " = ?",
                new String[] {idEnsemblePlageHoraire});

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
        nouvelleCategorie.put(HelperBDHoraire.CATEGORIE_CLE_LOCALISATION, categorie.getIdLocalisation());
        nouvelleCategorie.put(HelperBDHoraire.CATEGORIE_HORAIRE_PONCTUELLE, categorie.getIsHorairePonctuelle());
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
        nouvellePlageHoraire.put(HelperBDHoraire.PLAGE_HORAIRE_EST_FERME, plageHoraire.getEstFerme());
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
        nouvelleFicheHorairePonctuelle.put(HelperBDHoraire.FICHE_HORAIRE_PONCTUELLE_CLE_CATEGORIE, ficheHorairePonctuelle.getIdCategorie());
        baseHoraire.update(HelperBDHoraire.NOM_TABLE_FICHE_HORAIRE_PONCTUELLE,
                nouvelleFicheHorairePonctuelle,
                HelperBDHoraire.FICHE_HORAIRE_PONCTUELLE_CLE + " = ?",
                new String[] {identifiant});

    }

    /**
     * Récupère la position de la localisation dans la liste
     * @param idLocalisation l'identifiant de la localistion
     * @return la position de la localisation
     */
    public int getPositionByIdLocalisation(String idLocalisation) {
        int position = 0;
        Cursor cursor = baseHoraire.rawQuery(REQUETE_TOUT_SELECTIONNER_LOCALISATION, null);

        while (cursor.moveToNext()) {
            String tmp = cursor.getString(0);
            if (tmp.equals(idLocalisation)) {
                return position;
            }
            position++;
        }
        return -1;
    }

    /**
     * Récupère la position de la categorie plage horaire dans la liste
     * @param idCategoriePlageHoraire l'identifiant de la localistion
     * @return la position de la categorie plage horaire
     */
    public int getPositionByIdCategoriePlageHoraire(String idCategoriePlageHoraire) {
        int position = 0;
        Cursor cursor = baseHoraire.rawQuery(REQUETE_TOUT_SELECTIONNER_CATEGORIE_LOCALISATION_PLAGE_HORAIRE, null);

        while (cursor.moveToNext()) {
            String tmp = cursor.getString(0);
            if (tmp.equals(idCategoriePlageHoraire)) {
                return position;
            }
            position++;
        }
        return -1;
    }

    /**
     * Récupère la position de la categorie plage horaire dans la liste
     * @param idCategorieHorairePonctuel l'identifiant de la categorie
     * @return la position de la categorie plage horaire
     */
    public int getPositionByIdCategorieHorairePonctuel(String idCategorieHorairePonctuel) {
        int position = 0;
        Cursor cursor = baseHoraire.rawQuery(REQUETE_TOUT_SELECTIONNER_CATEGORIE_LOCALISATION_HORAIRE_PONCTUEL, null);

        while (cursor.moveToNext()) {
            String tmp = cursor.getString(0);
            if (tmp.equals(idCategorieHorairePonctuel)) {
                return position;
            }
            position++;
        }
        return -1;
    }

    /**
     * Retourne un curseur sur les catégories de la localisation, uniquement pour les catégories des plages horaires
     * @param idLocalisation l'identifiant
     * @return le curseur
     */
    public Cursor getCursorCategorieByLocalisation(String idLocalisation) {
        String requete =
                "SELECT * FROM " + HelperBDHoraire.NOM_TABLE_CATEGORIE
                        + " WHERE " + HelperBDHoraire.CATEGORIE_CLE_LOCALISATION + " = " + idLocalisation
                        + " AND " + HelperBDHoraire.CATEGORIE_HORAIRE_PONCTUELLE + " = 0";
        return baseHoraire.rawQuery(requete, null);
    }

    /**
     * Retourne un curseur sur les catégories de la localisation, uniquement pour les catégories des plages horaires
     * @param idLocalisation l'identifiant
     * @return le curseur
     */
    public Cursor getCursorCategoriePonctuelsByLocalisation(String idLocalisation) {
        String requete =
                "SELECT * FROM " + HelperBDHoraire.NOM_TABLE_CATEGORIE
                        + " WHERE " + HelperBDHoraire.CATEGORIE_CLE_LOCALISATION + " = " + idLocalisation
                        + " AND " + HelperBDHoraire.CATEGORIE_HORAIRE_PONCTUELLE + " = 1";
        return baseHoraire.rawQuery(requete, null);
    }

    /**
     * @return tout les ensemble horraire d'une fiche horraire
     */
    public ArrayList<EnsemblePlageHoraire> getEnsembleHoraireByFiche(String idFicheHoraire) {
        ArrayList<EnsemblePlageHoraire> listeEnsemble = new ArrayList<>();

        String requete =
                "SELECT * FROM "
                        + HelperBDHoraire.NOM_TABLE_ENSEMBLE_PLAGE_HORAIRE
                        + " WHERE " + HelperBDHoraire.ENSEMBLE_PLAGE_HORAIRE_CLE_FICHE
                        + " = " + idFicheHoraire;

        Cursor cursor = baseHoraire.rawQuery(requete, null);

        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {

                EnsemblePlageHoraire ensemblePlageHoraire = new EnsemblePlageHoraire();

                ensemblePlageHoraire.setId(cursor.getString(0));
                ensemblePlageHoraire.setIdPlageHoraireMatin(cursor.getString(1));
                ensemblePlageHoraire.setIdPlageHoraireSoir(cursor.getString(2));
                ensemblePlageHoraire.setIdJour(cursor.getString(3));
                ensemblePlageHoraire.setIdFichePlageHoraire(cursor.getString(4));

                listeEnsemble.add(ensemblePlageHoraire);
            }
        }


        return listeEnsemble;
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
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                Categorie categorie = new Categorie();
                int isPonctuelle = Integer.parseInt(cursor.getString(CATEGORIE_NUM_COLONNE_HORAIRE_PONCTUELLE));
                categorie = new Categorie(cursor.getString(2),
                        idLocalisation, isPonctuelle);
                categorie.setId(cursor.getString(0));
                listeCategories.add(categorie);
            }
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
     * Retourne un curseur sur la liste des fiches plages horaires avec le nom recherché
     * @param nom le nom recherché
     * @return le curseur
     */
    public Cursor getCursorFichePlageHoraireByNom(String nom) {
        String requete =
                " SELECT * FROM " + HelperBDHoraire.NOM_TABLE_FICHE_PLAGE_HORAIRE
                + " WHERE " + HelperBDHoraire.FICHE_PLAGE_HORAIRE_NOM + " like '%" + nom + "%'";
        return baseHoraire.rawQuery(requete, null);
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

    /**
     * Change la localisation d'une liste de catégories
     * @param categories les catégories à changer
     * @param idLocalisation l'identifiant de la nouvelle localisation
     */
    public void changeCategorieLocalisation(ArrayList<Categorie> categories, String idLocalisation) {
        for (Categorie categorie : categories) {
            categorie.setIdLocalisation(idLocalisation);
            updateCategorie(categorie, categorie.getId());
        }
    }

    /**
     * Récupère un curseur sur une liste de localisation sans celle spécifiée
     * @param idLocalisation l'identifiant de la localisation que l'on souhaite exclure
     * @return le curseur
     */
    public Cursor getCursorLocalisationWithoutOne(String idLocalisation) {
        String requete =
                "SELECT * FROM " + HelperBDHoraire.NOM_TABLE_LOCALISATION
                + " WHERE " + HelperBDHoraire.LOCALISATION_CLE + " != " + idLocalisation;
        return baseHoraire.rawQuery(requete, null);
    }

    /**
     * Détermine si des catégories existe avec une localisation
     * @param idLocalisation l'identifiant de la localisation
     * @return vrai si il existe des catégories pour cette localisation, false sinon
     */
    public boolean conflictWithCategorie(String idLocalisation) {
        return getCategoriesByLocalisation(idLocalisation).size() != 0;
    }

    /**
     * Détermine si des fiches existent avec des fiches
     * @param idCategorie l'identifiant de la catégorie
     * @return true si il y a un conflit, false sinon
     */
    public boolean conflictWithFichePlageHoraire(String idCategorie) {
        String requete =
                "SELECT * FROM " + HelperBDHoraire.NOM_TABLE_FICHE_PLAGE_HORAIRE
                + " WHERE " + HelperBDHoraire.FICHE_PLAGE_HORAIRE_CLE_CATEGORIE + " = " + idCategorie;
        Cursor cursor = baseHoraire.rawQuery(requete, null);
        if (cursor.getCount() != 0) {
            return true;
        }
        requete =
                "SELECT * FROM " + HelperBDHoraire.NOM_TABLE_FICHE_HORAIRE_PONCTUELLE
                + " WHERE " + HelperBDHoraire.FICHE_HORAIRE_PONCTUELLE_CLE_CATEGORIE + " = " + idCategorie;
        cursor = baseHoraire.rawQuery(requete, null);
        if (cursor.getCount() != 0) {
            return true;
        }
        return false;
    }

    /**
     * Récupère une liste de fiche plage horaire en fonction d'un identifiant de catégorie
     * @param idCategorie l'identifiant de la catégorie
     * @return la liste
     */
    public ArrayList<FichePlageHoraire> getAllFichePlageHoraireByIdCategorie(String idCategorie) {
        ArrayList<FichePlageHoraire> fichesPlageHoraires = new ArrayList<>();
        String requete =
                "SELECT * FROM " + HelperBDHoraire.NOM_TABLE_FICHE_PLAGE_HORAIRE
                + " WHERE " + HelperBDHoraire.FICHE_PLAGE_HORAIRE_CLE_CATEGORIE + " = " + idCategorie;
        Cursor cursor = baseHoraire.rawQuery(requete, null);

        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                FichePlageHoraire fichePlageHoraire = new FichePlageHoraire();
                fichePlageHoraire.setIdCategorie(idCategorie);
                fichePlageHoraire.setCheminPhoto(cursor.getString(FICHE_HORAIRE_PONCTUELLE_NUM_COLONNE_CHEMIN_IMAGE));
                fichePlageHoraire.setId(cursor.getString(FICHE_PLAGE_HORAIRE_NUM_COLONNE_CLE));
                fichePlageHoraire.setNom(cursor.getString(FICHE_PLAGE_HORAIRE_NUM_COLONNE_NOM));
                fichePlageHoraire.setInformation(cursor.getString(FICHE_PLAGE_HORAIRE_NUM_COLONNE_INFORMATION));
                fichesPlageHoraires.add(fichePlageHoraire);
            }
        }
        return fichesPlageHoraires;
    }

    /**
     * Récupère une liste de fiche horaire ponctuel en fonction d'un identifiant de catégorie
     * @param idCategorie l'identifiant de la catégorie
     * @return la liste
     */
    public ArrayList<FicheHorairePonctuelle> getAllFicheHorairePonctuelByIdCategorie(String idCategorie) {
        ArrayList<FicheHorairePonctuelle> fichesHorairePonctuel = new ArrayList<>();
        String requete =
                "SELECT * FROM " + HelperBDHoraire.NOM_TABLE_FICHE_PLAGE_HORAIRE
                        + " WHERE " + HelperBDHoraire.FICHE_PLAGE_HORAIRE_CLE_CATEGORIE + " = " + idCategorie;
        Cursor cursor = baseHoraire.rawQuery(requete, null);

        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                FicheHorairePonctuelle ficheHorairePonctuelle = new FicheHorairePonctuelle();
                ficheHorairePonctuelle.setId(cursor.getString(FICHE_HORAIRE_PONCTUELLE_NUM_COLONNE_CLE));
                ficheHorairePonctuelle.setNom(cursor.getString(FICHE_HORAIRE_PONCTUELLE_NUM_COLONNE_NOM));
                ficheHorairePonctuelle.setInformation(cursor.getString(FICHE_HORAIRE_PONCTUELLE_NUM_COLONNE_INFORMATION));
                ficheHorairePonctuelle.setCheminPhoto(cursor.getString(FICHE_HORAIRE_PONCTUELLE_NUM_COLONNE_CHEMIN_IMAGE));
                ficheHorairePonctuelle.setIdCategorie(idCategorie);
                fichesHorairePonctuel.add(ficheHorairePonctuelle);
            }
        }
        return fichesHorairePonctuel;
    }

    /**
     * Retourne un curseur en fonction de la localisation, du lieu et
     * de l'état d'ouverture passés en argument
     * @param localisation la localisation souhaitée, vide si rien n'est spécifiée
     * @param categorie la catégorie souhaitée, vide si rien n'est spécifiée
     * @param ouvert l'état d'ouverture, true si uniquement ouvert false, sinon
     * @return
     */
    public Cursor getCursorFichePlageHoraireByLocalisationAndCategorie(String localisation, String categorie, boolean ouvert) {
        String requete = "SELECT * FROM " + HelperBDHoraire.VUE_FICHE_PLAGE_HORAIRE
                + " WHERE " + HelperBDHoraire.CATEGORIE_HORAIRE_PONCTUELLE + " = 0"
                + " AND categorie like '%" + categorie + "%'"
                + " AND localisation like '%" + localisation + "%';";

        Cursor cursor = baseHoraire.rawQuery(requete, null);

        if (ouvert) {
            Cursor cursorOuvert = getAllFichePlageHoraireOuverte();

            String[] columnNames = {"_id", "nom", "information", "cheminImage", "idCategorie"};
            MatrixCursor cursorFinal = new MatrixCursor(columnNames);

            while (cursor.moveToNext()) {
                while (cursorOuvert.moveToNext()) {
                    if (cursor.getString(0).equals(cursorOuvert.getString(0))) {
                        cursorFinal.addRow(new String[]{cursorOuvert.getString(0),
                                cursorOuvert.getString(1),
                                cursorOuvert.getString(2),
                                cursorOuvert.getString(3),
                                cursorOuvert.getString(4)
                        });
                    }
                }
            }
            return cursorFinal;
        } else {
            return cursor;
        }

    }

    /**
     * Récupère une liste de fiche plage horaire qui sont ouverte
     * @return la liste
     */
    public Cursor getAllFichePlageHoraireOuverte() {
        ArrayList<FichePlageHoraire> fichesPlageHoraireOuverte = new ArrayList<>();

        // On récupère le jour courant
        GregorianCalendar gCalendar = new GregorianCalendar();
        int today = gCalendar.get(gCalendar.DAY_OF_WEEK) - 1;

        String dateCourante = new SimpleDateFormat("HH:mm").format(new Date());
        int heureCourante = Integer.parseInt(dateCourante.split(":")[0]);
        int minuteCourante = Integer.parseInt(dateCourante.split(":")[1]);

        // On récupère toutes les fiches plages horaires
        ArrayList<FichePlageHoraire> fichesPlageHoraire = getAllFichePlageHoraire();
        for (FichePlageHoraire fichePlageHoraire : fichesPlageHoraire) {
            ArrayList<EnsemblePlageHoraire> ensemblesPlageHoraire = getEnsembleHoraireByFiche(fichePlageHoraire.getId());
            for (EnsemblePlageHoraire ensemblePlageHoraire : ensemblesPlageHoraire) {
                if (ensemblePlageHoraire.getIdJour().equals(today + "")) {
                    PlageHoraire plageMatin = getPlageHoraireById(ensemblePlageHoraire.getIdPlageHoraireMatin());
                    if (plageMatin.getEstFerme() == 0) {
                        String[] horaireMatinOuverture = plageMatin.getHoraireOuverture().split(":");
                        String[] horaireMatinFermeture = plageMatin.getHoraireFermeture().split(":");
                        int heureMatinOuverture = Integer.parseInt(horaireMatinOuverture[0]);
                        int minuteMatinOuverture = Integer.parseInt(horaireMatinOuverture[1]);
                        int heureMatinFermeture = Integer.parseInt(horaireMatinFermeture[0]);
                        int minuteMatinFermeture = Integer.parseInt(horaireMatinFermeture[1]);

                        if (heureMatinOuverture < heureCourante && heureCourante < heureMatinFermeture) {
                            fichesPlageHoraireOuverte.add(getFichePlageHoraireById(fichePlageHoraire.getId()));
                        } else if (heureMatinOuverture == heureCourante && heureCourante < heureMatinFermeture) {
                            fichesPlageHoraireOuverte.add(getFichePlageHoraireById(fichePlageHoraire.getId()));
                        } else if (heureMatinOuverture < heureCourante && heureCourante == heureMatinFermeture) {
                            fichesPlageHoraireOuverte.add(getFichePlageHoraireById(fichePlageHoraire.getId()));
                        } else if (heureMatinOuverture == heureCourante && heureMatinFermeture == heureCourante) {
                            if (minuteMatinOuverture < minuteCourante && minuteCourante < minuteMatinFermeture) {
                                fichesPlageHoraireOuverte.add(getFichePlageHoraireById(fichePlageHoraire.getId()));
                            } else if (minuteMatinOuverture == minuteCourante && minuteCourante < minuteMatinFermeture) {
                                fichesPlageHoraireOuverte.add(getFichePlageHoraireById(fichePlageHoraire.getId()));
                            } else if (minuteMatinOuverture < minuteCourante && minuteCourante == minuteMatinFermeture) {
                                fichesPlageHoraireOuverte.add(getFichePlageHoraireById(fichePlageHoraire.getId()));
                            } else if (minuteMatinOuverture == minuteCourante && minuteCourante == minuteMatinFermeture) {
                                fichesPlageHoraireOuverte.add(getFichePlageHoraireById(fichePlageHoraire.getId()));
                            }
                        }
                    }

                    if (ensemblePlageHoraire.getIdPlageHoraireSoir() != null) {
                        PlageHoraire plageSoir = getPlageHoraireById(ensemblePlageHoraire.getIdPlageHoraireSoir());
                        if (plageSoir.getEstFerme() == 0) {

                            String[] horaireSoirOuverture = plageSoir.getHoraireOuverture().split(":");
                            String[] horaireSoirFermeture = plageSoir.getHoraireFermeture().split(":");
                            int heureSoirOuverture = Integer.parseInt(horaireSoirOuverture[0]);
                            int minuteSoirOuverture = Integer.parseInt(horaireSoirOuverture[1]);
                            int heureSoirFermeture = Integer.parseInt(horaireSoirFermeture[0]);
                            int minuteSoirFermeture = Integer.parseInt(horaireSoirFermeture[1]);

                            if (heureSoirOuverture < heureCourante && heureCourante < heureSoirFermeture) {
                                fichesPlageHoraireOuverte.add(getFichePlageHoraireById(fichePlageHoraire.getId()));
                            } else if (heureSoirOuverture == heureCourante && heureCourante < heureSoirFermeture) {
                                fichesPlageHoraireOuverte.add(getFichePlageHoraireById(fichePlageHoraire.getId()));
                            } else if (heureSoirOuverture < heureCourante && heureCourante == heureSoirFermeture) {
                                fichesPlageHoraireOuverte.add(getFichePlageHoraireById(fichePlageHoraire.getId()));
                            } else if (heureSoirOuverture == heureCourante && heureSoirFermeture == heureCourante) {
                                if (minuteSoirOuverture < minuteCourante && minuteCourante < minuteSoirFermeture) {
                                    fichesPlageHoraireOuverte.add(getFichePlageHoraireById(fichePlageHoraire.getId()));
                                } else if (minuteSoirOuverture == minuteCourante && minuteCourante < minuteSoirFermeture) {
                                    fichesPlageHoraireOuverte.add(getFichePlageHoraireById(fichePlageHoraire.getId()));
                                } else if (minuteSoirOuverture < minuteCourante && minuteCourante == minuteSoirFermeture) {
                                    fichesPlageHoraireOuverte.add(getFichePlageHoraireById(fichePlageHoraire.getId()));
                                } else if (minuteSoirOuverture == minuteCourante && minuteCourante == minuteSoirFermeture) {
                                    fichesPlageHoraireOuverte.add(getFichePlageHoraireById(fichePlageHoraire.getId()));
                                }
                            }
                        }
                    }
                }
            }
        }

        /** Transformation de la liste en curseur */
        String[] columnNames = {"_id", "nom", "information", "cheminImage", "idCategorie"};
        MatrixCursor cursor = new MatrixCursor(columnNames);
        for (FichePlageHoraire fichePlageHoraire : fichesPlageHoraireOuverte) {
            cursor.addRow(new String[]{fichePlageHoraire.getId(),
                    fichePlageHoraire.getNom(),
                    fichePlageHoraire.getInformation(),
                    fichePlageHoraire.getCheminPhoto(),
                    fichePlageHoraire.getIdCategorie()
            });
        }
        return cursor;
    }

    /**
     * Récupère toutes les fiches plages horaires présentes dans la base de données
     * @return la liste
     */
    public ArrayList<FichePlageHoraire> getAllFichePlageHoraire() {
        ArrayList<FichePlageHoraire> fichesPlageHoraire = new ArrayList<>();

        Cursor cursor = getCursorAllFichePlageHoraire();

        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                FichePlageHoraire fichePlageHoraire = new FichePlageHoraire();
                fichePlageHoraire.setId(cursor.getString(FICHE_PLAGE_HORAIRE_NUM_COLONNE_CLE));
                fichePlageHoraire.setNom(cursor.getString(FICHE_PLAGE_HORAIRE_NUM_COLONNE_NOM));
                fichePlageHoraire.setInformation(cursor.getString(FICHE_PLAGE_HORAIRE_NUM_COLONNE_INFORMATION));
                fichePlageHoraire.setIdCategorie(cursor.getString(FICHE_PLAGE_HORAIRE_NUM_COLONNE_CLE_CATEGORIE));
                fichePlageHoraire.setCheminPhoto(cursor.getString(FICHE_PLAGE_HORAIRE_NUM_COLONNE_CHEMIN_IMAGE));
                fichesPlageHoraire.add(fichePlageHoraire);
            }
        }

        return fichesPlageHoraire;
    }


    /**
     * Récupère un ensemble de plage horaire en fonction d'une fiche et d'un jour
     * @param idFiche l'identifiant de la fiche
     * @param idJour l'identifiant du jour
     * @return l'enseble de plage horaire
     */
    public EnsemblePlageHoraire getEnsemblePlageHoraireByIdFicheAndJour(String idFiche, String idJour) {
        EnsemblePlageHoraire ensemblePlageHoraire = new EnsemblePlageHoraire();
        String requete =
                "SELECT * FROM " + HelperBDHoraire.NOM_TABLE_ENSEMBLE_PLAGE_HORAIRE
                + " WHERE " + HelperBDHoraire.ENSEMBLE_PLAGE_HORAIRE_CLE_FICHE + " = " + idFiche
                + " AND " + HelperBDHoraire.ENSEMBLE_PLAGE_HORAIRE_CLE_JOUR + " = " + idJour;
        Cursor cursor = baseHoraire.rawQuery(requete, null);
        cursor.moveToFirst();

        if (cursor.getCount() != 0) {
            ensemblePlageHoraire.setId(cursor.getString(ENSEMBLE_PLAGE_HORAIRE_NUM_COLONNE_CLE));
            ensemblePlageHoraire.setIdFichePlageHoraire(idFiche);
            ensemblePlageHoraire.setIdJour(cursor.getString(ENSEMBLE_PLAGE_HORAIRE_NUM_COLONNE_JOUR));
            ensemblePlageHoraire.setIdPlageHoraireMatin(cursor.getString(ENSEMBLE_PLAGE_HORAIRE_NUM_COLONNE_CLE_PLAGE_HORAIRE_MATIN));
            ensemblePlageHoraire.setIdPlageHoraireSoir(cursor.getString(ENSEMBLE_PLAGE_HORAIRE_NUM_COLONNE_CLE_PLAGE_HORAIRE_SOIR));
        }

        return ensemblePlageHoraire;
    }

    /**
     * Retourne un jour en fonction de son nom
     * @param nom le nom du jour
     * @return le jour
     */
    public Jour getJourByNom(String nom) {
        Jour jour = new Jour();
        String requete =
                " SELECT * FROM " + HelperBDHoraire.NOM_TABLE_JOUR
                + " WHERE " + HelperBDHoraire.JOUR_LIBELLE + " = '" + nom + "'";
        Cursor cursor = baseHoraire.rawQuery(requete, null);
        cursor.moveToFirst();
        jour.setId(cursor.getString(JOUR_NUM_COLONNE_CLE));
        jour.setJour(cursor.getString(JOUR_NUM_COLONNE_LIBELLE));
        return jour;
    }

    /**
     * Retourne un curseur sur les horaires ponctuelles en fonction de la fiche
     * @param idFiche
     * @return
     */
    public Cursor getCursorAllHorairePonctuelleByIdFiche(String idFiche) {
        String requete =
                "SELECT * FROM " + HelperBDHoraire.VUE_HORAIRE_PONCTUEL
                + " WHERE idFiche " + " = " + idFiche
                + " ORDER BY " + HelperBDHoraire.HORAIRE_PONCTUELLE_CLE;
        return baseHoraire.rawQuery(requete, null);
    }

    /**
     * Retourne la localisation qui a le nom passer en paramettre
     * @param name le nom de la localisation
     * @return la localisation
     */
    public Localisation getLocalisationByName(String name) {
        Localisation localisation = new Localisation();
        String requete =
                " SELECT * FROM " + HelperBDHoraire.NOM_TABLE_LOCALISATION
                        + " WHERE " + HelperBDHoraire.LOCALISATION_NOM+ " = '" + name + "'";
        Cursor cursor = baseHoraire.rawQuery(requete, null);
        cursor.moveToFirst();

        if (cursor.getCount() != 0) {
            localisation.setId(cursor.getString(LOCALISATION_NUM_COLONNE_CLE));
            localisation.setNom(cursor.getString(LOCALISATION_NUM_COLONNE_NOM));
        }
        return localisation;
    }

    /**
     * Retourne la catégorie de Plage Horaire dont le nom et l'id de la localisation sont passer en
     * paramettre
     * @param name le nom de la catégorie
     * @param idLocalisation l'id de la localisation
     * @param isPonctuelle 0 => Non / 1 => Oui
     * @return la localisation
     */
    public Categorie getCategorieByNameByLocalisation(String name, String idLocalisation, int isPonctuelle) {
        Categorie categorie = new Categorie();
        String requete =
                " SELECT * FROM " + HelperBDHoraire.NOM_TABLE_CATEGORIE
                        + " WHERE " + HelperBDHoraire.LOCALISATION_NOM + " = '" + name + "'"
                        + " AND " + HelperBDHoraire.CATEGORIE_CLE_LOCALISATION + " = " + idLocalisation
                        + " AND " + HelperBDHoraire.CATEGORIE_HORAIRE_PONCTUELLE + " = " + isPonctuelle;
        Cursor cursor = baseHoraire.rawQuery(requete, null);
        cursor.moveToFirst();

        if (cursor.getCount() != 0) {
            categorie.setId(cursor.getString(CATEGORIE_NUM_COLONNE_CLE));
            categorie.setNom(cursor.getString(CATEGORIE_NUM_COLONNE_NOM));
            categorie.setIdLocalisation(cursor.getString(CATEGORIE_NUM_COLONNE_CLE_LOCALISATION));
            categorie.setIsDefault(cursor.getInt(CATEGORIE_NUM_COLONNE_DEFAUT));
            categorie.setIsHorairePonctuelle(cursor.getInt(CATEGORIE_NUM_COLONNE_HORAIRE_PONCTUELLE));
        }
        return categorie;
    }
}
