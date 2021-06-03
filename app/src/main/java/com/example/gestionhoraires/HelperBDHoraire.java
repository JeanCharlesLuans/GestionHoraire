package com.example.gestionhoraires;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HelperBDHoraire extends SQLiteOpenHelper {

    /** Activation des clés étrangères */
    private static final String ACTIVATION_CLES_ETRANGERES = "PRAGMA foreign_keys = ON;";

    //// Localisation ////
    /** Nom de la table de localisation */
    public static final String NOM_TABLE_LOCALISATION = "Localisation";

    /** Nom du champs correspondant à la l'identifiant dans la table */
    public static final String LOCALISATION_CLE = "_id";

    /** Nom du champs correspondant au nom dans la table */
    public static final String LOCALISATION_NOM = "nom";

    /** Nom du champs indiquant si la localisation est celle par défaut */
    public static final String LOCALISATION_IS_DEFAULT = "isDefault";

    //// Categorie ////
    /** Nom de la table Categorie */
    public static final String NOM_TABLE_CATEGORIE = "Categorie";

    /** Nom du champs correspondant à l'identifiant dans la table */
    public static final String CATEGORIE_CLE = "_id";

    /** Nom du champs correspondant au nom dans la table */
    public static final String CATEGORIE_NOM = "nom";

    /** Nom du champs correspondant à la clé étrangère de localisation */
    public static final String CATEGORIE_CLE_LOCALISATION = "idLocalisation";

    /** Nom du champs indiquant si la catégorie est celle par défaut */
    public static final String CATEGORIE_IS_DEFAULT = "isDefault";

    /** Nom du champs indiquant si la catégorie est pour les plages horaires ou pour les horaires ponctuelles */
    public static final String CATEGORIE_HORAIRE_PONCTUELLE = "isHorairePonctuelle";

    //// FichePlageHoraire ////
    /** Nom de la table de FichePlageHoraire */
    public static final String NOM_TABLE_FICHE_PLAGE_HORAIRE = "FichePlageHoraire";

    /** Nom du champs correspondant à la l'identifiant dans la table */
    public static final String FICHE_PLAGE_HORAIRE_CLE = "_id";

    /** Nom du champs correspondant au nom dans la table */
    public static final String FICHE_PLAGE_HORAIRE_NOM = "nom";

    /** Nom du champs correspondant à l'information dans la table */
    public static final String FICHE_PLAGE_HORAIRE_INFORMATION = "information";

    /** Nom du champs correspondant au chemin de l'image dans la table */
    public static final String FICHE_PLAGE_HORAIRE_CHEMIN_IMAGE = "cheminImage";

    /** Nom du champs correspondant à la clé étrangère de la catégorie */
    public static final String FICHE_PLAGE_HORAIRE_CLE_CATEGORIE = "idCategorie";

    ////  EnsemblePlageHoraire /////
    /** Nom de la table EnsemblePlageHoraire */
    public static final String NOM_TABLE_ENSEMBLE_PLAGE_HORAIRE = "EnsemblePlageHoraire";

    /** Nom du champs correspondant à l'identifiant dans la table */
    public static final String ENSEMBLE_PLAGE_HORAIRE_CLE = "_id";

    /** Clé étrangère correspondant à la plage horaire du matin ou de toute la journée */
    public static final String ENSEMBLE_PLAGE_HORAIRE_CLE_HORAIRE_MATIN = "idHoraireMatin";

    /** Clé étrangère correspondant à l'identifiant de la plage horaire du soir */
    public static final String ENSEMBLE_PLAGE_HORAIRE_CLE_HORAIRE_SOIR = "idHoraireSoir";

    /** Clé étrangère correspondant au jour de la plage horaire */
    public static final String ENSEMBLE_PLAGE_HORAIRE_CLE_JOUR = "idJour";

    /** Clé étrangère correspondant à la fiche plage horaire*/
    public static final String ENSEMBLE_PLAGE_HORAIRE_CLE_FICHE = "idFichePlageHoraire";

    //// PlageHoraire /////
    /** Nom de la table PlageHoraire */
    public static final String NOM_TABLE_PLAGE_HORAIRE = "PlageHoraire";

    /** Nom du champs correspondant à l'identifiant dans la table */
    public static final String PLAGE_HORAIRE_CLE = "_id";

    /** Nom du champs correspondant à l'horaire d'ouverture */
    public static final String PLAGE_HORAIRE_OUVERTURE = "horaireOuverture";

    /** Nom du champs correspondant à l'horaire de fermeture */
    public static final String PLAGE_HORAIRE_FERMETURE = "horaireFermeture";

    /** Nom du champs correspondant à l'état de l'horaire d'ouverture */
    public static final String PLAGE_HORAIRE_ETAT_OUVERTURE = "etatOuverture";

    /** Nom du champs correspondant à l'état de l'horaire de fermeture */
    public static final String PLAGE_HORAIRE_ETAT_FERMETURE = "etatFermeture";

    /** Nom du champs correspondant à l'indicateur de fermeture */
    public static final String PLAGE_HORAIRE_EST_FERME = "estFerme";

    //// FicheHorairePonctuelle ////
    /** Nom de la Table FicheHorairePonctuelle */
    public static final String NOM_TABLE_FICHE_HORAIRE_PONCTUELLE = "FicheHorairePonctuelle";

    /** Nom du champs correspondant à la l'identifiant dans la table */
    public static final String FICHE_HORAIRE_PONCTUELLE_CLE = "_id";

    /** Nom du champs correspondant au nom dans la table */
    public static final String FICHE_HORAIRE_PONCTUELLE_NOM = "nom";

    /** Nom du champs correspondant à l'information dans la table */
    public static final String FICHE_HORAIRE_PONCTUELLE_INFORMATION = "information";

    /** Nom du champs correspondant au chemin de l'image dans la table */
    public static final String FICHE_HORAIRE_PONCTUELLE_IMAGE = "cheminImage";

    /** Nom du champs correspondant à l'identifiant de la catégorie */
    public static final String FICHE_HORAIRE_PONCTUELLE_CLE_CATEGORIE = "idCategorie";

    //// HorairePonctuelle ////
    /** Nom de la Table FicheHorairePonctuelle */
    public static final String NOM_TABLE_HORAIRE_PONCTUELLE = "HorairePonctuelle";

    /** Nom du champs correspondant à la l'identifiant dans la table */
    public static final String HORAIRE_PONCTUELLE_CLE = "_id";

    /** Nom du champs correspondant a l'horaire d'ouverture dans la table */
    public static final String HORAIRE_PONCTUELLE_OUVERTURE = "horaireOuverture";

    /** Nom du champs correspondant à l'information dans la table */
    public static final String HORAIRE_PONCTUELLE_FERMETURE = "horaireFermeture";

    /** Nom du champs correspondant au chemin de l'image dans la table */
    public static final String HORAIRE_PONCTUELLE_CLE_JOUR = "idJour";

    /** Nom du champs correspondant au chemin de l'image dans la table */
    public static final String HORAIRE_PONCTUELLE_CLE_FICHE = "idFicheForairePonctuelle";

    //// Jour ////
    /** Nom de la table Jour */
    public static final String NOM_TABLE_JOUR = "Jour";

    /** Nom du champs correspondant à l'identifiant dans la table */
    public static final String JOUR_CLE = "_id";

    /** Nom du champs correspondant au libelle dans la table */
    public static final String JOUR_LIBELLE = "libelle";

    /** Vue des catégories avec leur localisation */
    public  static final String VUE_CATEGORIE_LOCALISATION = "V_Categorie";

    /** Vue des fiches plages horaires */
    public static final String VUE_FICHE_PLAGE_HORAIRE = "V_Fiche_Plage_Horaire";

    /** Vue des horaires ponctuels */
    public static final String VUE_HORAIRE_PONCTUEL = "V_Horaire_Ponctuel";

    //// Création des tables ////
    /** Requête pour la création de la table JOUR */
    private static final  String CREATION_TABLE_JOUR =
            "CREATE TABLE " + NOM_TABLE_JOUR + " ( "
                    + JOUR_CLE + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + JOUR_LIBELLE + " TEXT"
                    +");" ;


    /** Requête pour la création de la table LOCALISATION */
    private static final  String CREATION_TABLE_LOCALISATION =
            "CREATE TABLE " + NOM_TABLE_LOCALISATION + " ( "
                    + LOCALISATION_CLE + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + LOCALISATION_NOM + " TEXT, "
                    + LOCALISATION_IS_DEFAULT +  " INTEGER CHECK (" + LOCALISATION_IS_DEFAULT + "= 0 OR " + LOCALISATION_IS_DEFAULT + "= 1)"
                    +");" ;

    /** Requête pour la création de la table CATEGORIE */
    private static final  String CREATION_TABLE_CATEGORIE =
            "CREATE TABLE " + NOM_TABLE_CATEGORIE + " ( "
                    + CATEGORIE_CLE + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + CATEGORIE_CLE_LOCALISATION + " INTEGER,"
                    + CATEGORIE_NOM + " TEXT, "
                    + CATEGORIE_IS_DEFAULT + " INTEGER CHECK (" + CATEGORIE_IS_DEFAULT + "= 0 OR " + CATEGORIE_IS_DEFAULT + "= 1),"
                    + CATEGORIE_HORAIRE_PONCTUELLE +  " INTEGER CHECK (" + CATEGORIE_HORAIRE_PONCTUELLE + "= 0 OR " + CATEGORIE_HORAIRE_PONCTUELLE + "= 1),"
                    + "FOREIGN KEY(" + CATEGORIE_CLE_LOCALISATION + ") REFERENCES "+ NOM_TABLE_LOCALISATION +"(" + LOCALISATION_CLE + ")"
                    + " ON DELETE CASCADE"
                    +");";


    /* Création des tables des plages horaires */
    /** Requête pour la création de la table FICHE_PLAGE_HORAIRE*/
    private static final  String CREATION_TABLE_FICHE_PLAGE_HORAIRE =
            "CREATE TABLE " + NOM_TABLE_FICHE_PLAGE_HORAIRE + " ( "
                    + FICHE_PLAGE_HORAIRE_CLE + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + FICHE_PLAGE_HORAIRE_NOM + " TEXT, "
                    + FICHE_PLAGE_HORAIRE_INFORMATION  + " TEXT,"
                    + FICHE_PLAGE_HORAIRE_CHEMIN_IMAGE + " TEXT,"
                    + FICHE_PLAGE_HORAIRE_CLE_CATEGORIE + " INTEGER,"
                    + "FOREIGN KEY(" + FICHE_PLAGE_HORAIRE_CLE_CATEGORIE + ") REFERENCES "+ NOM_TABLE_CATEGORIE +"(" + CATEGORIE_CLE + ")"
                    +");";

    /** Requête pour la création de la table PLAGE_HORAIRE*/
    private static final  String CREATION_TABLE_PLAGE_HORAIRE =
            "CREATE TABLE " + NOM_TABLE_PLAGE_HORAIRE + " ( "
                    + PLAGE_HORAIRE_CLE + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + PLAGE_HORAIRE_OUVERTURE + " TEXT, "
                    + PLAGE_HORAIRE_FERMETURE + " TEXT, "
                    + PLAGE_HORAIRE_ETAT_OUVERTURE + " INTEGER,"
                    + PLAGE_HORAIRE_ETAT_FERMETURE + " INTEGER,"
                    + PLAGE_HORAIRE_EST_FERME + " INTEGER"
                    +");";

    /** Requête pour la création de la table ENSEMBLE_PLAGE_HORAIRE*/
    private static final  String CREATION_TABLE_ENSEMBLE_PLAGE_HORAIRE =
            "CREATE TABLE " + NOM_TABLE_ENSEMBLE_PLAGE_HORAIRE + " ( "
                    + ENSEMBLE_PLAGE_HORAIRE_CLE + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + ENSEMBLE_PLAGE_HORAIRE_CLE_HORAIRE_MATIN + " INTEGER, "
                    + ENSEMBLE_PLAGE_HORAIRE_CLE_HORAIRE_SOIR + " INTEGER, "
                    + ENSEMBLE_PLAGE_HORAIRE_CLE_JOUR + " INTGER,"
                    + ENSEMBLE_PLAGE_HORAIRE_CLE_FICHE + " INTEGER,"
                    + "FOREIGN KEY(" + ENSEMBLE_PLAGE_HORAIRE_CLE_HORAIRE_MATIN + ") REFERENCES "+ NOM_TABLE_PLAGE_HORAIRE +"(" + PLAGE_HORAIRE_CLE + ") ON DELETE CASCADE,"
                    + "FOREIGN KEY(" + ENSEMBLE_PLAGE_HORAIRE_CLE_HORAIRE_SOIR + ") REFERENCES "+ NOM_TABLE_PLAGE_HORAIRE +"(" + PLAGE_HORAIRE_CLE + ") ON DELETE CASCADE,"
                    + "FOREIGN KEY(" + ENSEMBLE_PLAGE_HORAIRE_CLE_JOUR + ") REFERENCES "+ NOM_TABLE_JOUR +"(" + JOUR_CLE + "),"
                    + "FOREIGN KEY(" + ENSEMBLE_PLAGE_HORAIRE_CLE_FICHE + ") REFERENCES "+ NOM_TABLE_FICHE_PLAGE_HORAIRE +"(" + FICHE_PLAGE_HORAIRE_CLE + ") ON DELETE CASCADE"
                    +");";


    /* Création des tables des horaires ponctuelles */
    /** Requête pour la création de la table FICHE_HORAIRE_PONCTUELLE*/
    private static final  String CREATION_TABLE_FICHE_HORAIRE_PONCTUELLE =
            "CREATE TABLE " + NOM_TABLE_FICHE_HORAIRE_PONCTUELLE + " ( "
                    + FICHE_HORAIRE_PONCTUELLE_CLE + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + FICHE_HORAIRE_PONCTUELLE_NOM + " TEXT, "
                    + FICHE_HORAIRE_PONCTUELLE_INFORMATION  + " TEXT,"
                    + FICHE_HORAIRE_PONCTUELLE_IMAGE + " TEXT,"
                    + FICHE_HORAIRE_PONCTUELLE_CLE_CATEGORIE + " TEXT"
                    +");";

    /** Requête pour la création de la table FICHE_HORAIRE_PONCTUELLE*/
    private static final  String CREATION_TABLE_HORAIRE_PONCTUELLE =
            "CREATE TABLE " + NOM_TABLE_HORAIRE_PONCTUELLE + " ( "
                    + HORAIRE_PONCTUELLE_CLE + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + HORAIRE_PONCTUELLE_OUVERTURE + " TEXT, "
                    + HORAIRE_PONCTUELLE_FERMETURE + " TEXT,"
                    + HORAIRE_PONCTUELLE_CLE_JOUR  + " INTEGER,"
                    + HORAIRE_PONCTUELLE_CLE_FICHE + " INTEGER,"
                    + "FOREIGN KEY(" + HORAIRE_PONCTUELLE_CLE_JOUR + ") REFERENCES "+ NOM_TABLE_JOUR +"(" + JOUR_CLE + "),"
                    + "FOREIGN KEY(" + HORAIRE_PONCTUELLE_CLE_FICHE + ") REFERENCES "+ NOM_TABLE_FICHE_HORAIRE_PONCTUELLE +"(" + FICHE_HORAIRE_PONCTUELLE_CLE + ") ON DELETE CASCADE"
                    +");";

    /** Requête de création d'une vue pour les catégories */
    private static final String CREATION_VUE_CATEGORIE_LOCALISATION =
            "CREATE VIEW " + VUE_CATEGORIE_LOCALISATION + " AS "
            + "SELECT " + NOM_TABLE_CATEGORIE + "." + CATEGORIE_CLE + ", "
            + NOM_TABLE_CATEGORIE + "." + CATEGORIE_NOM + ", "
            + NOM_TABLE_CATEGORIE + "." + CATEGORIE_CLE_LOCALISATION + ", "
            + NOM_TABLE_CATEGORIE + "." + CATEGORIE_IS_DEFAULT + ", "
            + NOM_TABLE_LOCALISATION + "." + LOCALISATION_NOM + ", "
            + NOM_TABLE_CATEGORIE + "." + CATEGORIE_HORAIRE_PONCTUELLE
            + " FROM " + NOM_TABLE_CATEGORIE + " INNER JOIN " + NOM_TABLE_LOCALISATION + " ON "
            + NOM_TABLE_CATEGORIE + "." + CATEGORIE_CLE_LOCALISATION + " = "
            + NOM_TABLE_LOCALISATION + "." + LOCALISATION_CLE;

    /** Requête de création d'une vue pour les fiches plages horaires */
    private final String CREATION_VUE_FICHE_PLAGE_HORAIRE =
            "CREATE VIEW " + VUE_FICHE_PLAGE_HORAIRE + " AS "
            + "SELECT " + NOM_TABLE_FICHE_PLAGE_HORAIRE + "." + FICHE_PLAGE_HORAIRE_CLE + ", "
            + NOM_TABLE_FICHE_PLAGE_HORAIRE + "." + FICHE_PLAGE_HORAIRE_NOM + ", "
            + NOM_TABLE_FICHE_PLAGE_HORAIRE + "." + FICHE_PLAGE_HORAIRE_INFORMATION + ", "
            + NOM_TABLE_CATEGORIE + "." + CATEGORIE_NOM + " AS categorie, "
            + NOM_TABLE_CATEGORIE + "." + CATEGORIE_HORAIRE_PONCTUELLE + ", "
            + NOM_TABLE_LOCALISATION + "." + LOCALISATION_NOM + " AS localisation "
            + " FROM " + NOM_TABLE_FICHE_PLAGE_HORAIRE + " INNER JOIN " + NOM_TABLE_CATEGORIE + " ON "
            + NOM_TABLE_CATEGORIE + "." + CATEGORIE_CLE + " = "
            + NOM_TABLE_FICHE_PLAGE_HORAIRE + "." + FICHE_PLAGE_HORAIRE_CLE_CATEGORIE
            + " INNER JOIN " + NOM_TABLE_LOCALISATION + " ON "
            + NOM_TABLE_CATEGORIE + "." + CATEGORIE_CLE_LOCALISATION + " = "
            + NOM_TABLE_LOCALISATION + "." + LOCALISATION_CLE + ";";

    /** Requête de création d'une vue pour les horaires ponctuels */
    private  final String CREATION_VUE_HORAIRE_PONCTUEL =
            "CREATE VIEW " + VUE_HORAIRE_PONCTUEL + " AS "
            + "SELECT " + NOM_TABLE_HORAIRE_PONCTUELLE + "." + HORAIRE_PONCTUELLE_CLE + ", "
            + NOM_TABLE_JOUR + "." + JOUR_LIBELLE + " AS libelle, "
            + NOM_TABLE_HORAIRE_PONCTUELLE + "." + HORAIRE_PONCTUELLE_CLE_FICHE + " AS idFiche, "
            + NOM_TABLE_HORAIRE_PONCTUELLE + "." + HORAIRE_PONCTUELLE_OUVERTURE + " AS horaireOuverture, "
            + NOM_TABLE_HORAIRE_PONCTUELLE + "." + HORAIRE_PONCTUELLE_FERMETURE + " AS horaireFermeture"
            + " FROM " + NOM_TABLE_HORAIRE_PONCTUELLE + " INNER JOIN " + NOM_TABLE_JOUR + " ON "
            + NOM_TABLE_HORAIRE_PONCTUELLE + "." + HORAIRE_PONCTUELLE_CLE_JOUR + " = "
            + NOM_TABLE_JOUR + "." + JOUR_CLE + ";";


    /** Requête pour supprimer la table JOUR */
    public static final String SUPPRIMER_TABLE_JOUR =
            "DROP TABLE IF EXISTS " + NOM_TABLE_JOUR+ " ;" ;

    /** Requête pour supprimer la table LOCALISATION */
    public static final String SUPPRIMER_TABLE_LOCALISATION =
            "DROP TABLE IF EXISTS " + NOM_TABLE_LOCALISATION + " ;" ;

    /** Requête pour supprimer la table CATEGORIE */
    public static final String SUPPRIMER_TABLE_CATEGORIE =
            "DROP TABLE IF EXISTS " + NOM_TABLE_CATEGORIE + " ;" ;

    /** Requête pour supprimer la table FICHE_PLAGE_HORAIRE */
    public static final String SUPPRIMER_TABLE_FICHE_PLAGE_HORAIRE =
            "DROP TABLE IF EXISTS " + NOM_TABLE_FICHE_HORAIRE_PONCTUELLE + " ;" ;

    /** Requête pour supprimer la table ENSEMBLE_PLAGE_HORAIRE */
    public static final String SUPPRIMER_TABLE_ENSEMBLE_PLAGE_HORAIRE =
            "DROP TABLE IF EXISTS " + NOM_TABLE_ENSEMBLE_PLAGE_HORAIRE + " ;" ;

    /** Requête pour supprimer la table PLAGE_HORAIRE */
    public static final String SUPPRIMER_TABLE_PLAGE_HORAIRE =
            "DROP TABLE IF EXISTS " + NOM_TABLE_PLAGE_HORAIRE + " ;" ;

    /** Requête pour supprimer la table SUPPRIMER_HORAIRE_PONCTUELLE */
    public static final String SUPPRIMER_TABLE_HORAIRE_PONCTUELLE =
            "DROP TABLE IF EXISTS " + NOM_TABLE_HORAIRE_PONCTUELLE + " ;" ;

    /** Requête pour supprimer la table SUPPRIMER_HORAIRE_PONCTUELLE */
    public static final String SUPPRIMER_TABLE_FICHE_HORAIRE_PONCTUELLE =
            "DROP TABLE IF EXISTS " + NOM_TABLE_FICHE_HORAIRE_PONCTUELLE + " ;" ;

    /**
     * Constructeur de la classe
     * @param context le contexte d'éxécution
     * @param nom le nom de la base de données
     * @param fabrique la fabrique
     * @param version la version de l'application
     */
    public HelperBDHoraire(Context context, String nom, SQLiteDatabase.CursorFactory fabrique, int version) {
        super(context, nom, fabrique, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        initTableBD(db);
        initJourSemaine(db);
        initLocalisation(db);
        initCategorie(db);

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL(ACTIVATION_CLES_ETRANGERES);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Suppression
        db.execSQL(SUPPRIMER_TABLE_JOUR);

        db.execSQL(SUPPRIMER_TABLE_LOCALISATION);
        db.execSQL(SUPPRIMER_TABLE_CATEGORIE);

        db.execSQL(SUPPRIMER_TABLE_FICHE_PLAGE_HORAIRE);
        db.execSQL(SUPPRIMER_TABLE_FICHE_HORAIRE_PONCTUELLE);

        db.execSQL(SUPPRIMER_TABLE_HORAIRE_PONCTUELLE);
        db.execSQL(SUPPRIMER_TABLE_PLAGE_HORAIRE);
        db.execSQL(SUPPRIMER_TABLE_ENSEMBLE_PLAGE_HORAIRE);

        onCreate(db);
    }

    private void initTableBD(SQLiteDatabase db) {
        // Création des tables
        db.execSQL(CREATION_TABLE_JOUR);

        db.execSQL(CREATION_TABLE_LOCALISATION);
        db.execSQL(CREATION_TABLE_CATEGORIE);

        db.execSQL(CREATION_TABLE_FICHE_PLAGE_HORAIRE);
        db.execSQL(CREATION_TABLE_FICHE_HORAIRE_PONCTUELLE);

        db.execSQL(CREATION_TABLE_HORAIRE_PONCTUELLE);
        db.execSQL(CREATION_TABLE_PLAGE_HORAIRE);
        db.execSQL(CREATION_TABLE_ENSEMBLE_PLAGE_HORAIRE);

        db.execSQL(CREATION_VUE_CATEGORIE_LOCALISATION);
        db.execSQL(CREATION_VUE_FICHE_PLAGE_HORAIRE);
        db.execSQL(CREATION_VUE_HORAIRE_PONCTUEL);
    }

    private void initJourSemaine(SQLiteDatabase db) {
        ContentValues enregistrement = new ContentValues();

        enregistrement.put(JOUR_LIBELLE, "Lundi");
        db.insert(NOM_TABLE_JOUR, JOUR_CLE, enregistrement);

        enregistrement.put(JOUR_LIBELLE, "Mardi");
        db.insert(NOM_TABLE_JOUR, JOUR_CLE, enregistrement);

        enregistrement.put(JOUR_LIBELLE, "Mercredi");
        db.insert(NOM_TABLE_JOUR, JOUR_CLE, enregistrement);

        enregistrement.put(JOUR_LIBELLE, "Jeudi");
        db.insert(NOM_TABLE_JOUR, JOUR_CLE, enregistrement);

        enregistrement.put(JOUR_LIBELLE, "Vendredi");
        db.insert(NOM_TABLE_JOUR, JOUR_CLE, enregistrement);

        enregistrement.put(JOUR_LIBELLE, "Samedi");
        db.insert(NOM_TABLE_JOUR, JOUR_CLE, enregistrement);

        enregistrement.put(JOUR_LIBELLE, "Dimanche");
        db.insert(NOM_TABLE_JOUR, JOUR_CLE, enregistrement);
    }

    private void initLocalisation(SQLiteDatabase db) {
        ContentValues enregistrement = new ContentValues();

        enregistrement.put(LOCALISATION_NOM, "Default");
        enregistrement.put(LOCALISATION_IS_DEFAULT, 1);

        db.insert(NOM_TABLE_LOCALISATION, LOCALISATION_CLE, enregistrement);
    }

    private void initCategorie(SQLiteDatabase db) {
        ContentValues enregistrement = new ContentValues();

        enregistrement.put(CATEGORIE_NOM, "Principale");
        enregistrement.put(CATEGORIE_CLE_LOCALISATION, 1);
        enregistrement.put(CATEGORIE_IS_DEFAULT, 1);
        enregistrement.put(CATEGORIE_HORAIRE_PONCTUELLE, 0);

        db.insert(NOM_TABLE_CATEGORIE, CATEGORIE_CLE, enregistrement);

        enregistrement = new ContentValues();

        enregistrement.put(CATEGORIE_NOM, "Principale");
        enregistrement.put(CATEGORIE_CLE_LOCALISATION, 1);
        enregistrement.put(CATEGORIE_IS_DEFAULT, 1);
        enregistrement.put(CATEGORIE_HORAIRE_PONCTUELLE, 1);

        db.insert(NOM_TABLE_CATEGORIE, CATEGORIE_CLE, enregistrement);
    }
}