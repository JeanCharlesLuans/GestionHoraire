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
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class LocalisationActivity extends AppCompatActivity {

    /** barre d'outils de l'applications */
    private Toolbar maBarreOutil;

    /** Curseur sur l'ensemble des frais de la base */
    private Cursor curseurSurBase;

    /** Objet destiné à faciliter l'accès à la table */
    //private HoraireDAO accesHoraire; // TODO CHANGER TYPE

    /** Liste contenant les catégorie à afficher */
    private ArrayList<String> listeLocalisation;

    /** Adaptateur permettant de gérer la liste */
    private SimpleCursorAdapter adaptateur;

    /** Liste présenter dans l'application */
    private ListView ListeVueLocalisation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // On ajoute la ToolBar
        maBarreOutil = findViewById(R.id.settings_tool_bar);
        setSupportActionBar(maBarreOutil);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) maBarreOutil.findViewById(R.id.toolbar_title);
        mTitle.setText(getString(R.string.label_gestion_localisation));

        listeLocalisation = new ArrayList<String>();
        ListeVueLocalisation = findViewById(R.id.liste_gestion);

        //accesFrais = new FraisDAO(this); //TODO Changer type
        //accesFrais.open();
        //curseurSurBase = accesFrais.getCursorFrais();

        // On crée un adpateur pour rassembler les données à afficher
        adaptateur = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                curseurSurBase,
                new String[] {"nom"}, // TODO nom Colonne
                new int[] {android.R.id.text1,}, 0);
        ListeVueLocalisation.setAdapter(adaptateur);

        // on précise qu'un menu est associé à la liste qui correspond à l'activité
        registerForContextMenu(ListeVueLocalisation);
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
                Intent intentionRetour = new Intent();
                setResult(Activity.RESULT_OK, intentionRetour);
                finish();
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
                // supprimer element()  // TODO action supprimer
                break;
            case R.id.modifier :
                //modifierElement(information.id); // TODO action modifier
                break;
            case R.id.annuler :		 // retour à la liste principale
                break;

        }
        return (super.onContextItemSelected(item));
    }

    /**
     * Permet l'ajout d'un catégorie a la base de donnée et a la liste
     */
    private void ajouterLocalisation() {
        final View boiteSaisie = getLayoutInflater().inflate(R.layout.saisie_localisation, null);

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
                        //TODO add
                        //close when everythings is OK
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
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
