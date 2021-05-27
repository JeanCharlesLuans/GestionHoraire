package com.example.gestionhoraires;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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

public class CategorieActivity extends AppCompatActivity {

    /** barre d'outils de l'applications */
    private Toolbar maBarreOutil;

    /** Curseur sur l'ensemble des frais de la base */
    private Cursor curseurSurBase;

    /** Objet destiné à faciliter l'accès à la table */
    private HoraireDAO accesHoraire;

    /** Liste contenant les catégorie à afficher */
    private ArrayList<String> listeCategorie;

    /** Adaptateur permettant de gérer la liste */
    private SimpleCursorAdapter adaptateur;

    /** Liste présenter dans l'application */
    private ListView listeVueCategorie;

    /**
     * Vrai si l'activité a été lancer depuis l'onglet des horaire ponctuel
     * Il servira a afficher la bonne liste de catégorie
     */
    private boolean isPonctuel; // TODO utiliser isPonctuel

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // On récupère les options de l'intentions
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                isPonctuel = false;
            } else {
                isPonctuel = extras.getBoolean(MainActivity.CLE_H_PONCTUEL);
            }
        } else {
            isPonctuel = (boolean) savedInstanceState.getSerializable(MainActivity.CLE_H_PONCTUEL);
        }

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
        mTitle.setText(getString(R.string.label_gestion_categorie));

        listeCategorie = new ArrayList<String>();
        listeVueCategorie = findViewById(R.id.liste_gestion);

        accesHoraire = new HoraireDAO(this);
        accesHoraire.open();
        curseurSurBase = isPonctuel ? accesHoraire.getCursorAllCategorieLocalisationHorairePonctuel() : accesHoraire.getCursorAllCategorieLocalisationPlageHoraire();
        curseurSurBase.moveToFirst();

        // On crée un adpateur pour rassembler les données à afficher
        adaptateur = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2,
                curseurSurBase,
                new String[] {HelperBDHoraire.CATEGORIE_NOM,
                "nom:1"},
                new int[] {android.R.id.text1,
                android.R.id.text2}, 0);
        listeVueCategorie.setAdapter(adaptateur);

        // on précise qu'un menu est associé à la liste qui correspond à l'activité
        registerForContextMenu(listeVueCategorie);
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
                ajouterCategorie();
                break;
            case R.id.deplacer :
                // TODO méthode déplacer
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
                supprimerCategorie();
                break;
            case R.id.modifier :
                if (curseurSurBase.getString(3).equals("0")) {
                    modifierCategorie();
                } else {
                    Toast.makeText(this, R.string.toast_categorie_defaut_modifier, Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.annuler :		 // retour à la liste principale
                break;

        }
        return (super.onContextItemSelected(item));
    }

    /**
     * permet la suppression d'une catégorie
     */
    private void supprimerCategorie() {
        String identifiant = curseurSurBase.getString(accesHoraire.LOCALISATION_NUM_COLONNE_CLE);
        if (!curseurSurBase.getString(3).equals("0")) {
            Toast.makeText(this, R.string.toast_categorie_defaut_supprimer, Toast.LENGTH_LONG).show();
        } else {
            // on affiche une boite de confirmation
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.alerte_suppression))
                    .setNegativeButton(getResources().getString(R.string.bouton_non), null)
                    .setPositiveButton(getResources().getString(R.string.bouton_oui),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    accesHoraire.deleteCategorie(identifiant);
                                    curseurSurBase = isPonctuel ? accesHoraire.getCursorAllCategorieLocalisationHorairePonctuel() : accesHoraire.getCursorAllCategorieLocalisationPlageHoraire();
                                    adaptateur.swapCursor(curseurSurBase);
                                    onContentChanged();
                                }
                            })
                    .show();
        }

        curseurSurBase = isPonctuel ? accesHoraire.getCursorAllCategorieLocalisationHorairePonctuel() : accesHoraire.getCursorAllCategorieLocalisationPlageHoraire();
        adaptateur.swapCursor(curseurSurBase);
        onContentChanged();
    }

    /**
     * Permet l'ajout d'un catégorie a la base de donnée et a la liste
     */
    private void ajouterCategorie() {
        final View boiteSaisie = getLayoutInflater().inflate(R.layout.saisie_categorie, null);

        /** Création de l'alerte si les données sont invalides */
        AlertDialog alerte = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.alerte_titre))
                .setMessage(getResources().getString(R.string.alerte_nom))
                .setPositiveButton(getResources().getString(R.string.bouton_positif), null)
                .create();

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.ajout_categorie))
                .setView(boiteSaisie)
                .setPositiveButton(getResources().getString(R.string.bouton_positif), null)
                .setNegativeButton(getResources().getString(R.string.bouton_negatif), null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                // on remplit le spinner
                Spinner spinLocalisation = dialog.findViewById(R.id.localisation_spinner);
                SimpleCursorAdapter adapterLocalisation = new SimpleCursorAdapter(dialog.getContext(),
                        android.R.layout.simple_spinner_item,
                        accesHoraire.getCursorAllLocalisation(),
                        new String[] {HelperBDHoraire.CATEGORIE_NOM},
                        new int[] {android.R.id.text1,}, 0);;
                adapterLocalisation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinLocalisation.setAdapter(adapterLocalisation);

                Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int ponctuel = isPonctuel ? 1 : 0;
                        EditText edt_nom = dialog.findViewById(R.id.edt_nom);
                        String name = edt_nom.getText().toString();
                        if (!name.equals("")) {
                            accesHoraire.addCategorie(new Categorie(name, spinLocalisation.getSelectedItemId() + "", ponctuel));
                        } else {
                            alerte.show();
                        }
                        curseurSurBase = isPonctuel ? accesHoraire.getCursorAllCategorieLocalisationHorairePonctuel() : accesHoraire.getCursorAllCategorieLocalisationPlageHoraire();
                        adaptateur.swapCursor(curseurSurBase);
                        onContentChanged();
                        //close when everythings is OK
                        dialog.dismiss();
                    }
                });

            }
        });
        dialog.show();
    }

    /**
     * Modifie la catégorie sélectionnée
     */
    public void modifierCategorie() {
        final View boiteSaisie = getLayoutInflater().inflate(R.layout.saisie_categorie, null);

        String identifiant = curseurSurBase.getString(0);
        String identifiantLocalisation = curseurSurBase.getString(2);
        String name = curseurSurBase.getString(1);


        /** Création de l'alerte si les données sont invalides */
        AlertDialog.Builder alerte = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.alerte_titre))
                .setMessage(getResources().getString(R.string.alerte_nom))
                .setPositiveButton(getResources().getString(R.string.bouton_positif), null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.ajout_categorie))
                .setView(boiteSaisie)
                .setPositiveButton(getResources().getString(R.string.bouton_positif), null)
                .setNegativeButton(getResources().getString(R.string.bouton_negatif), null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                // on remplit le spinner
                Spinner spinLocalisation = dialog.findViewById(R.id.localisation_spinner);
                SimpleCursorAdapter adapterLocalisation = new SimpleCursorAdapter(dialog.getContext(),
                        android.R.layout.simple_spinner_item,
                        accesHoraire.getCursorAllLocalisation(),
                        new String[] {HelperBDHoraire.CATEGORIE_NOM},
                        new int[] {android.R.id.text1,}, 0);;
                adapterLocalisation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinLocalisation.setAdapter(adapterLocalisation);
                spinLocalisation.setSelection(accesHoraire.getPositionByIdLocalisation(identifiantLocalisation));
                Log.i("Position", accesHoraire.getPositionByIdLocalisation(identifiantLocalisation) + "");
                EditText edtNom = dialog.findViewById(R.id.edt_nom);
                edtNom.setText(name);

                Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String name = edtNom.getText().toString();
                        int ponctuel = isPonctuel ? 1 : 0;
                        if (!name.equals("")) {
                            accesHoraire.updateCategorie(new Categorie(name, spinLocalisation.getSelectedItemId() + "", ponctuel), identifiant);
                        } else {
                            alerte.show();
                        }
                        curseurSurBase = isPonctuel ? accesHoraire.getCursorAllCategorieLocalisationHorairePonctuel() : accesHoraire.getCursorAllCategorieLocalisationPlageHoraire();
                        adaptateur.swapCursor(curseurSurBase);
                        onContentChanged();
                        //close when everythings is OK
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
