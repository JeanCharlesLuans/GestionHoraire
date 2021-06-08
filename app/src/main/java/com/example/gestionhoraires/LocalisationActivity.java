package com.example.gestionhoraires;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.method.CharacterPickerDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.gestionhoraires.beans.Categorie;
import com.example.gestionhoraires.beans.Localisation;

import java.util.ArrayList;

public class LocalisationActivity extends AppCompatActivity {

    /** barre d'outils de l'applications */
    private Toolbar maBarreOutil;

    /** Curseur sur l'ensemble des frais de la base */
    private Cursor curseurSurBase;

    /** Objet destiné à faciliter l'accès à la table */
    private HoraireDAO accesHoraire;

    /** Liste contenant les catégorie à afficher */
    private ArrayList<String> listeLocalisation;

    /** Adaptateur permettant de gérer la liste */
    private SimpleCursorAdapter adaptateur;

    /** Liste présenter dans l'application */
    private ListView listeVueLocalisation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // On ajoute la ToolBar
        maBarreOutil = findViewById(R.id.settings_tool_bar);
        setSupportActionBar(maBarreOutil);
        maBarreOutil.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        maBarreOutil.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retour();
            }
        });
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) maBarreOutil.findViewById(R.id.toolbar_title);
        mTitle.setText(getString(R.string.label_gestion_localisation));

        listeLocalisation = new ArrayList<String>();
        listeVueLocalisation = findViewById(R.id.liste_gestion);

        accesHoraire = new HoraireDAO(this);
        accesHoraire.open();
        curseurSurBase = accesHoraire.getCursorAllLocalisation();

        // On crée un adpateur pour rassembler les données à afficher
        adaptateur = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                curseurSurBase,
                new String[] {HelperBDHoraire.LOCALISATION_NOM},
                new int[] {android.R.id.text1}, 0);
        listeVueLocalisation.setAdapter(adaptateur);

        // on précise qu'un menu est associé à la liste qui correspond à l'activité
        registerForContextMenu(listeVueLocalisation);
    }

    /**
     * Méthode invoquée à la première activation du menu d'options
     * @param menuActivite menu d'option activé
     * @return un booléen égal à vrai si le menu a pu être créé
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menuActivite) {
        getMenuInflater().inflate(R.menu.menu_settings_tool_bar, menuActivite);
        return super.onCreateOptionsMenu(menuActivite);
    }

    /**
     * Appelée automatiqument chaque fois que l'utilisateur sélectionne une option
     * du menu d'options
     * @param item option du menu sélectionnée par l'utilisateur
     * @return un booléen égal à vrai si l'option choisie a pu être correctement traitée
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.ajouter:
                ajouterLocalisation();
                break;
            case R.id.retour :
                retour();
                break;
            case R.id.annuler_option :
                break;
            case R.id.aide :
                afficheAide();
                break;
        }
        return (super.onOptionsItemSelected(item));
    }

    /**
     * Méthode invoquée automatiquement lorsque l'utiisateur active un menu contextuel
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {

        /*
         *  on désérialise le fichier XML décriant le menu à l'argument menu
         */
        new MenuInflater(this).inflate(R.menu.menu_contextuel_settings, menu);
        MenuItem export = menu.findItem(R.id.export_option);
        export.setVisible(false);
    }

    /**
     * Méthode invoquée automatiquement lorsque l'utilisateur choisira une option
     * dans le menu contextuel associé à la liste
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        /*
         *  on accéde à des informations supplémentaires sur l'élémemt cliqué dans la liste
         */
        AdapterView.AdapterContextMenuInfo information =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        // selon l'option sélectionnée dans le menu, on réalise le traitement adéquat
        switch(item.getItemId()) {
            case R.id.supprimer :   // supprimer un élément
                supprimerLocalisation();
                break;
            case R.id.modifier :
                modifierLocalisation();
                break;
            case R.id.annuler :		 // retour à la liste principale
                break;

        }
        return (super.onContextItemSelected(item));
    }

    /**
     * Permet la suppression d'un localisation avec la gestion des erreurs
     */
    private void supprimerLocalisation() {
        String identifiant = curseurSurBase.getString(accesHoraire.LOCALISATION_NUM_COLONNE_CLE);
        /* Si la localisation n'est pas celle par défaut */
        if (!curseurSurBase.getString(accesHoraire.LOCALISATION_NUM_COLONNE_DEFAUT).equals("0")) {
            Toast.makeText(this, R.string.toast_localisation_defaut, Toast.LENGTH_LONG).show();
        } else {
            // on affiche une boite de confirmation
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.alerte_suppression))
                    .setNegativeButton(getResources().getString(R.string.bouton_non), null)
                    .setPositiveButton(getResources().getString(R.string.bouton_oui),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    boolean isConflit = accesHoraire.conflictWithCategorie(identifiant);
                                    gestionConflit(isConflit, identifiant);
                                }
                            })
                    .show();
        }

        curseurSurBase = accesHoraire.getCursorAllLocalisation();
        adaptateur.swapCursor(curseurSurBase);
        onContentChanged();
    }

    private void gestionConflit(boolean isConflit, String identifiant){
        if (isConflit) {
            // Si il y a un conflit, on laisse le choix a l'utilisateur de l'actiona effectuer
            AlertDialog dialogConflit = new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.titre_alerte_conflit))
                    .setMessage(getResources().getString(R.string.alerte_conflit))
                    .setNeutralButton(getResources().getString(R.string.bouton_negatif), null)
                    .setNegativeButton(getResources().getString(R.string.delete_all), null)
                    .setPositiveButton(getResources().getString(R.string.move_all), null)
                    .create();

            dialogConflit.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button boutonDeleteAll = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                    Button boutonMove = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                    boutonDeleteAll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            accesHoraire.deleteLocalisation(identifiant);
                            // when everything is ok
                            dialogConflit.dismiss();
                            curseurSurBase = accesHoraire.getCursorAllLocalisation();
                            adaptateur.swapCursor(curseurSurBase);
                            onContentChanged();
                        }
                    });
                    boutonMove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            moveLocalisationDialog(identifiant);
                            dialogConflit.dismiss();
                        }
                    });
                }
            });

            dialogConflit.show();
        } else {
            // si il n'y a pas de conflit, on supprime
            accesHoraire.deleteLocalisation(identifiant);
        }

        curseurSurBase = accesHoraire.getCursorAllLocalisation();
        adaptateur.swapCursor(curseurSurBase);
        onContentChanged();
    }

    /**
     * affiche une fenetre de dialogue a l'utilisateur pour qu'il puissen choisir la
     * liste qui recevras les localisation
     */
    private void moveLocalisationDialog(String identifiant) {
        final View boiteSaisie = getLayoutInflater().inflate(R.layout.saisie_spinner_localisation, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.title_move_localisation))
                .setView(boiteSaisie)
                .setPositiveButton(getResources().getString(R.string.bouton_deplcaer), null)
                .setNegativeButton(getResources().getString(R.string.bouton_negatif), null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Spinner localisationSpinner = ((AlertDialog) dialog).findViewById(R.id.spinner_localisation);
                SimpleCursorAdapter adapterLocalisation = getAdapterLocalisationWithoutOne(identifiant);
                adapterLocalisation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                localisationSpinner.setAdapter(adapterLocalisation);


                Button boutonMove = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                boutonMove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<Categorie> categories = accesHoraire.getCategoriesByLocalisation(identifiant);
                        accesHoraire.changeCategorieLocalisation(categories, localisationSpinner.getSelectedItemId() + "");
                        accesHoraire.deleteLocalisation(identifiant);
                        // when everything is ok
                        dialog.dismiss();
                        curseurSurBase = accesHoraire.getCursorAllLocalisation();
                        adaptateur.swapCursor(curseurSurBase);
                        onContentChanged();
                    }
                });
            }
        });

        dialog.show();
    }

    /**
     * @return un Adapter contenant l'ensemble des localisation sauf une
     */
    private SimpleCursorAdapter getAdapterLocalisationWithoutOne(String idLocalisation) {
        return new SimpleCursorAdapter(this,
                android.R.layout.simple_spinner_item,
                accesHoraire.getCursorLocalisationWithoutOne(idLocalisation),
                new String[] {HelperBDHoraire.LOCALISATION_NOM},
                new int[] {android.R.id.text1,}, 0);
    }

    /**
     * Permet l'ajout d'un catégorie a la base de donnée et a la liste
     */
    private void ajouterLocalisation() {
        final View boiteSaisie = getLayoutInflater().inflate(R.layout.saisie_localisation, null);

        /** Création de l'alerte si les données sont invalides */
        AlertDialog.Builder alerte = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.alerte_titre))
                .setMessage(getResources().getString(R.string.alerte_nom))
                .setPositiveButton(getResources().getString(R.string.bouton_positif), null);

        /** Création de l'alerte si les données sont invalides */
        AlertDialog.Builder alerteNom = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.alerte_titre))
                .setMessage(getResources().getString(R.string.alerte_nom_erreur))
                .setPositiveButton(getResources().getString(R.string.bouton_positif), null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.ajout_localisation))
                .setView(boiteSaisie)
                .setPositiveButton(getResources().getString(R.string.bouton_positif), null)
                .setNegativeButton(getResources().getString(R.string.bouton_negatif), null)
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText edt_nom = dialog.findViewById(R.id.edt_nom);
                        String name = edt_nom.getText().toString();
                        if (!name.equals("")) {
                            if (accesHoraire.getLocalisationByName(edt_nom.getText().toString()) != null) {
                                alerteNom.show();
                            } else {
                                Localisation localisation = new Localisation(name);
                                accesHoraire.addLocalisation(localisation);
                            }
                        } else {
                            alerte.show();
                        }

                        curseurSurBase = accesHoraire.getCursorAllLocalisation();
                        adaptateur.swapCursor(curseurSurBase);
                        onContentChanged();
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }

    /**
     * Permet de modifier une localisation
     */
    public void modifierLocalisation() {
        final View boiteSaisie = getLayoutInflater().inflate(R.layout.saisie_localisation, null);

        String identifiant = curseurSurBase.getString(accesHoraire.LOCALISATION_NUM_COLONNE_CLE);

        /** Création de l'alerte si les données sont invalides */
        AlertDialog.Builder alerte = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.alerte_titre))
                .setMessage(getResources().getString(R.string.alerte_nom))
                .setPositiveButton(getResources().getString(R.string.bouton_positif), null);

        EditText edtNom = boiteSaisie.findViewById(R.id.edt_nom);

        String titreBoite = getResources().getString(R.string.modifier_localisation);
        if (curseurSurBase.getString(accesHoraire.LOCALISATION_NUM_COLONNE_DEFAUT).equals("1")) {
            titreBoite += " (localisation par défaut)";
        }
        String nom = curseurSurBase.getString(accesHoraire.LOCALISATION_NUM_COLONNE_NOM);
        edtNom.setText(nom);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(titreBoite)
                .setView(boiteSaisie)
                .setPositiveButton(getResources().getString(R.string.bouton_positif), null)
                .setNegativeButton(getResources().getString(R.string.bouton_negatif), null)
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText edt_nom = dialog.findViewById(R.id.edt_nom);
                        String name = edt_nom.getText().toString();
                        if (!name.equals("")) {
                            Localisation localisation = new Localisation(name);
                            accesHoraire.updateLocalisation(localisation, identifiant);
                        } else {
                            alerte.show();
                        }

                        curseurSurBase = accesHoraire.getCursorAllLocalisation();
                        adaptateur.swapCursor(curseurSurBase);
                        onContentChanged();
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }

    /**
     * retourne a l'activiter principale
     */
    private void retour() {
        Intent intentionRetour = new Intent();
        setResult(Activity.RESULT_OK, intentionRetour);
        finish();
    }

    /**
     * Affiche une boite de dialogue d'aide a l'utilisateur
     */
    private void afficheAide() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.titre_aide))
                .setMessage(getResources().getString(R.string.message_aide))
                .setPositiveButton(R.string.bouton_positif, null)
                .show();
    }
}
