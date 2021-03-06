package com.example.gestionhoraires;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gestionhoraires.beans.Categorie;
import com.example.gestionhoraires.beans.EnsemblePlageHoraire;
import com.example.gestionhoraires.beans.FichePlageHoraire;

import com.example.gestionhoraires.beans.HorairePonctuelle;
import com.example.gestionhoraires.beans.Jour;
import com.example.gestionhoraires.beans.Localisation;
import com.example.gestionhoraires.beans.PlageHoraire;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /** Code identifiant modification */
    private static final String CODE_IDENTIFICATION = "IDENTIFIANT_MODIFICATION";

    /** Identifiant de l'intention pour la gestion des cat??gories */
    private final int CODE_GESTION_CATEGORIE = 10;

    /** Identifiant de l'intention pour la gestion des localisations */
    private final int CODE_GESTION_LOCALISATION = 20;

    /** Identifiant de l'intention pour l'ajout de la plage horaire */
    private final int CODE_PLAGE_HORAIRE = 30;

    /** Identifiant de l'intention pour l'ajout d'une fiche horaire ponctuel '*/
    private final int CODE_FICHE_HORAIRE_PONCTUEL = 40;

    /** Cl?? pour le message transmis par l'activit?? secondaire */
    public final static String CLE_H_PONCTUEL = "com.example.gestionhoraires.PONCTUEL";

    /** Identifiant de modification de fiche plage horaire */
    private final static String CODE_MODIFICATION = "MODIFIER";

    /** Identifiant de mofication de fiche horaire ponctuel */
    private final static String CODE_MODIFICATION_PONCTUEL = "MODIFIER_PONCTUEL";

    /** barre d'outils de l'applications */
    private Toolbar maBarreOutil;

    /**
     * Table d'onglets g??r??e par l'activit??
     */
    private TabHost lesOnglets;

    /** vrai si l'onglet actif et l'onglet horaire ponctuel, faux sinon */
    boolean isHorairePonctuel;

    /** num??ro de l'onglet des plages horaires */
    private final int TAB_PLAGE_HORAIRE = 0;

    /** num??ro de l'onglet des horaires ponctuels */
    private final int TAB_H_PONCTUEL = 1;

    /** bouton flottant servant a ajouter une fiche horaire */
    private FloatingActionButton floatingActionButtonAJout;

    /** bouton flottant servant a l'export */
    private ExtendedFloatingActionButton floatingActionButtonExport;

    /** Objet destin?? ?? faciliter l'acc??s ?? la table des horaires */
    private HoraireDAO accesHoraires;

    // ONGLET 1

    /** Curseur sur l'ensemble des plages horaires de la base */
    private Cursor curseurPlageHoraire;

    /** Liste pr??senter dans le premiere onglet de l'application */
    private ListView listViewPlageHoraire;

    /** Adaptateur permettant de g??rer la liste des plage horaire */
    private SimpleCursorAdapter plageHoraireAdaptateur;

    // ONGLET 2

    /** Curseur sur l'ensemble des horaires ponctuelles de la base */
    private Cursor curseurHorairesPonctuelles; // TODO v??rifier si on a vraiment besoin de deux curseur.

    /** Liste pr??senter dans le deuxieme onglet de l'application */
    private ListView listViewHPonctuelles;

    /** Adaptateur permettant de g??rer la liste des horaire ponctuelle */
    private SimpleCursorAdapter horairesPonctuellesAdapteur;

    /** Indicateur ouvert des filtres */
    private boolean ouvert;

    /** Indicateur de modification */
    private boolean modification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // acc??s au DAO
        accesHoraires = new HoraireDAO(this);
        accesHoraires.open();

        curseurPlageHoraire = accesHoraires.getCursorAllFichePlageHoraire();
        curseurHorairesPonctuelles = accesHoraires.getCursorAllFicheHorairePonctuelle();

        verifyStoragePermissions(this);

        // Liste de l'onglet 1 : Plages Horaires
        setPlageHoraireAdapter();

        // Liste de l'onglet 2 : Horaires Ponctuelles
        setHPonctuelAdapter();

        registerForContextMenu(listViewPlageHoraire);
        registerForContextMenu(listViewHPonctuelles);

        // On ajoute la ToolBar
        maBarreOutil = findViewById(R.id.main_tool_bar);
        setSupportActionBar(maBarreOutil);

        // On ajoute les 2 onglets
        lesOnglets = (TabHost) findViewById(R.id.tableOnglet);
        lesOnglets.setup();
        TabHost.TabSpec specification = lesOnglets.newTabSpec("onglet_plage_horaire");
        specification.setIndicator(getResources().getString(R.string.onglet_plage_horaire));
        specification.setContent(R.id.onglet_plage_horaire);
        lesOnglets.addTab(specification);
        lesOnglets.addTab(lesOnglets.newTabSpec("onglet_horaires_ponctuelles")
                .setIndicator(getResources().getString(R.string.onglet_horaires_ponctuelles) )
                .setContent(R.id.onglet_horaires_ponctuelles));
        changeStyleOnglet();
        lesOnglets.setOnTabChangedListener(
                new TabHost.OnTabChangeListener() {
                    @Override
                    public void onTabChanged(String tabId) {

                        // on met la bonne couleurs pour les onglets
                        changeStyleOnglet();


                        /*
                         * on enleve la visibiliter de plusieur options du menu
                         */
                        isHorairePonctuel = tabId.equals("onglet_horaires_ponctuelles");
                        setToolBarVisibility(!isHorairePonctuel);
                    }
                }
        );
        lesOnglets.setCurrentTab(TAB_PLAGE_HORAIRE);
        isHorairePonctuel = false;

        // On ajoute un bouton flotant
        floatingActionButtonAJout = findViewById(R.id.fab_ajout);
        floatingActionButtonExport = findViewById(R.id.fab_export);
        floatingActionButtonAJout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (lesOnglets.getCurrentTab()){
                    case TAB_PLAGE_HORAIRE:
                        ajouterPlageHoraire();
                        break;
                    case TAB_H_PONCTUEL:
                        ajouterHorairePonctuel();
                        break;
                }
            }
        });
    }

    /**
     * M??thode invoqu??e automatiquement lorsque l'utiisateur active un menu contextuel
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        new MenuInflater(this).inflate(R.menu.menu_contextuel_settings, menu);
        if (isHorairePonctuel) {
            MenuItem export = menu.findItem(R.id.export_option);
            export.setVisible(false);
        }
    }

    /**
     * M??thode invoqu??e automatiquement lorsque l'utilisateur choisira une option
     * dans le menu contextuel associ?? ?? la liste
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        /*
         *  on acc??de ?? des informations suppl??mentaires sur l'??l??memt cliqu?? dans la liste
         */
        AdapterView.AdapterContextMenuInfo information =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        // selon l'option s??lectionn??e dans le menu, on r??alise le traitement ad??quat
        switch(item.getItemId()) {
            case R.id.supprimer :   // supprimer un ??l??ment
                if (lesOnglets.getCurrentTab() == TAB_H_PONCTUEL) {
                    accesHoraires.deleteFicheHorairePonctuelle(curseurHorairesPonctuelles.getString(HoraireDAO.HORAIRE_PONCTUELLE_NUM_COLONNE_CLE));
                } else {
                    accesHoraires.deleteFichePlageHoraire(curseurPlageHoraire.getString(HoraireDAO.FICHE_PLAGE_HORAIRE_NUM_COLONNE_CLE));
                }
                curseurHorairesPonctuelles = accesHoraires.getCursorAllFicheHorairePonctuelle();
                horairesPonctuellesAdapteur.swapCursor(curseurHorairesPonctuelles);
                curseurPlageHoraire = accesHoraires.getCursorAllFichePlageHoraire();
                plageHoraireAdaptateur.swapCursor(curseurPlageHoraire);
                onContentChanged();
                break;
            case R.id.export_option:
                if (lesOnglets.getCurrentTab() == TAB_PLAGE_HORAIRE) {
                    Cursor curseur = (Cursor) plageHoraireAdaptateur.getItem(information.position);
                    exportationSMS(curseur.getString(HoraireDAO.FICHE_PLAGE_HORAIRE_NUM_COLONNE_CLE));
                }
                break;
            case R.id.modifier :
                modification = true;
                if (lesOnglets.getCurrentTab() == TAB_PLAGE_HORAIRE) {
                    Intent plageHoraire = new Intent(MainActivity.this,
                            PlageHoraireActivity.class);
                    plageHoraire.putExtra(CODE_MODIFICATION, true);
                    plageHoraire.putExtra(CODE_IDENTIFICATION,
                            curseurPlageHoraire.getString(HoraireDAO.FICHE_PLAGE_HORAIRE_NUM_COLONNE_CLE));
                    startActivityForResult(plageHoraire, CODE_PLAGE_HORAIRE);
                } else {
                    Intent ficheHorairePonctuel = new Intent(MainActivity.this,
                            HorairePonctuelActivity.class);
                    ficheHorairePonctuel.putExtra(CODE_MODIFICATION_PONCTUEL, true);
                    ficheHorairePonctuel.putExtra(CODE_IDENTIFICATION,
                            curseurHorairesPonctuelles.getString(HoraireDAO.FICHE_HORAIRE_PONCTUELLE_NUM_COLONNE_CLE));
                    startActivityForResult(ficheHorairePonctuel, CODE_FICHE_HORAIRE_PONCTUEL);
                }

                break;
            case R.id.annuler :		 // retour ?? la liste principale
                break;

        }
        return (super.onContextItemSelected(item));
    }

    /**
     * M??thode invoqu??e ?? la premi??re activation du menu d'options
     * @param menuActivite menu d'option activ??
     * @return un bool??en ??gal ?? vrai si le menu a pu ??tre cr????
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menuActivite) {
        getMenuInflater().inflate(R.menu.menu_main_tool_bar, menuActivite);

        MenuItem itemRecherche = menuActivite.findItem(R.id.recherche);

        SearchView vuePourRecherche = (SearchView) itemRecherche.getActionView();
        vuePourRecherche.setQueryHint(getResources().getString(R.string.aide_recherche));
        vuePourRecherche.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            /**
             * M??thode invoqu??e quand l'utilisateur valide la recherche,
             * i.e. quand il clique sur la loupe du clavier virtuel
             * @param query texte tap?? par l'utilisateur dans la zone de saisie
             * @return vrai si la recherche a pu ??tre g??r??e
             */
            @Override
            public boolean onQueryTextSubmit(String query) {
                curseurPlageHoraire = accesHoraires.getCursorFichePlageHoraireByNom(query);
                plageHoraireAdaptateur.swapCursor(curseurPlageHoraire);
                onContentChanged();
                return true;
            }
            /**
             * M??thode invoqu??e quand l'utilisateur modifie le texte de la recherche
             * @param s texte modifie
             * @return vrai si le changement de texte a pu ??tre g??r??
             */
            @Override
            public boolean onQueryTextChange(String s) {
                curseurPlageHoraire = accesHoraires.getCursorAllFichePlageHoraire();
                plageHoraireAdaptateur.swapCursor(curseurPlageHoraire);
                onContentChanged();
                return true;
            }
        });
        return true;
    }

    /**
     * Appel??e automatiqument chaque fois que l'utilisateur s??lectionne une option
     * du menu d'options
     * @param item option du menu s??lectionn??e par l'utilisateur
     * @return un bool??en ??gal ?? vrai si l'option choisie a pu ??tre correctement trait??e
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.filtre:
                afficherFiltre();
                break;
            case R.id.import_option :
                showDialogImport();
                break;
            case R.id.export_option :
                selectionnerElement();
                break;
            case R.id.settings_gestion_categorie :
                Intent catego = new Intent(MainActivity.this,
                        CategorieActivity.class);
                catego.putExtra(MainActivity.CLE_H_PONCTUEL, lesOnglets.getCurrentTab() == TAB_H_PONCTUEL);
                startActivityForResult(catego, CODE_GESTION_CATEGORIE);
                break;
            case R.id.settings_gestion_localisation :
                Intent locali = new Intent(MainActivity.this,
                        LocalisationActivity.class);
                startActivityForResult(locali, CODE_GESTION_LOCALISATION);
                break;
            case R.id.annuler_option :
                break;
            case R.id.aide :
                afficheAide();
                break;
        }
        curseurPlageHoraire = accesHoraires.getCursorAllFichePlageHoraire();
        plageHoraireAdaptateur.swapCursor(curseurPlageHoraire);
        onContentChanged();
        return (super.onOptionsItemSelected(item));
    }

    /**
     * affiche une fen??tre de dialogue a l'utilisateur pourqu'il puisse chosir
     * entre les diff??rent mode d'importation
     */
    private void showDialogImport() {
        final View boiteSaisie = getLayoutInflater().inflate(R.layout.saisie_import, null);

        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.titre_boite_import))
                .setView(boiteSaisie)
                .setPositiveButton(getResources().getString(R.string.bouton_positif),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                RadioGroup boutonMode =
                                        boiteSaisie.findViewById(R.id.groupe_import);

                                switch (boutonMode.getCheckedRadioButtonId()) {
                                    case R.id.option_import_csv:
                                        importationCSV();
                                        break;
                                    case R.id.option_import_json:
                                        importationJSON();
                                        break;
                                }
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.bouton_negatif), null)
                .show();

    }

    /**
     * affiche une fen??tre de dialogue ?? l'utilisateur pour qu'il puissen choisir
     * entre les diff??rent mode d'exportation de JSON
     */
    private void showDialogExportJson(File aExporter) {
        final View boiteSaisie = getLayoutInflater().inflate(R.layout.saisie_export_json, null);

        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.titre_boite_export))
                .setView(boiteSaisie)
                .setPositiveButton(getResources().getString(R.string.bouton_positif),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                RadioGroup boutonMode =
                                        boiteSaisie.findViewById(R.id.groupe_export_json);

                                switch (boutonMode.getCheckedRadioButtonId()) {
                                    case R.id.option_export_mail:
                                        composeMailMessage(aExporter);
                                        break;
                                    case R.id.option_export_nfc:
                                        // TODO export nfc

                                        break;
                                }
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.bouton_negatif), null)
                .show();

    }

    /**
     * permet a l'utilisateur de slectionner des ??l??ment de la liste,
     * affiche aussi un boutons d'export et change l'??tat de la toolbar
     */
    private void selectionnerElement() {
        setPlageHoraireAdapterForExport();


        listViewPlageHoraire.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView checkedTextView = (CheckedTextView) view.findViewById(android.R.id.text1);
                checkedTextView.setChecked(!checkedTextView.isChecked());
            }
        });

        // changement de l'??tat de la toolbar
        setToolBarVisibility(false, false);
        maBarreOutil.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        maBarreOutil.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // retour a la normale de l'interface
                resetInterface();
            }
        });

        // ajout du bouton de confirmation
        floatingActionButtonAJout.setVisibility(View.GONE);
        floatingActionButtonExport.setVisibility(View.VISIBLE);
        floatingActionButtonExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Integer> checkedItemIds = getCheckedItemPositions(listViewPlageHoraire);
                FichePlageHoraire[] listeFiche = new FichePlageHoraire[checkedItemIds.size()];

                for (int i = 0; i < checkedItemIds.size(); i++) {
                    Cursor tmp = (Cursor) plageHoraireAdaptateur.getItem(checkedItemIds.get(i));
                    String id = tmp.getString(HoraireDAO.FICHE_PLAGE_HORAIRE_NUM_COLONNE_CLE);
                    listeFiche[i] = accesHoraires.getFichePlageHoraireById(id);
                }

                showDialogExportJson(exportationJSON(listeFiche));

                // when everything is ok
                resetInterface();
            }
        });
    }

    /**
     * retourne une liste de position correspondant au ??l??ment selectionner de la liste
     * @param listViewPlageHoraire
     * @return une liste de positions
     */
    private ArrayList<Integer> getCheckedItemPositions(ListView listViewPlageHoraire) {
        ArrayList<Integer> aReturn = new ArrayList<Integer>();
        for(int i = 0; i < listViewPlageHoraire.getAdapter().getCount(); i++) {
            CheckedTextView checkedTextView = listViewPlageHoraire.getChildAt(i).findViewById(android.R.id.text1);
            boolean isChecked = checkedTextView.isChecked();
            if (isChecked) {
                aReturn.add(i);
            }
        }
        return aReturn;
    }

    /**
     * r??initalise une partie de l'interface dans son ??tat intial
     * r??tablie la visibilit?? de la toolbar
     * les boutons flottants
     * la liste et son adaptateur
     */
    private void resetInterface() {
        setToolBarVisibility(true, true);
        floatingActionButtonAJout.setVisibility(View.VISIBLE);
        floatingActionButtonExport.setVisibility(View.GONE);
        maBarreOutil.setNavigationIcon(null);
        maBarreOutil.setNavigationOnClickListener(null);
        setPlageHoraireAdapter();
    }

    /**
     * initialise la listeView et son adaptateur en vue d'un futur export
     * la layout est celui contenant des checkbox afin que l'utilisateur
     * puisse choisir des ??l??ments de la liste
     */
    private void setPlageHoraireAdapterForExport() {
        listViewPlageHoraire = findViewById(R.id.liste_plage_horaire);
        plageHoraireAdaptateur = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_multiple_choice, //R.layout.ligne_liste_plage_horaire,
                curseurPlageHoraire,
                new String[] {HelperBDHoraire.FICHE_PLAGE_HORAIRE_NOM},
                new int[] {android.R.id.text1}, 0);
        listViewPlageHoraire.setAdapter(plageHoraireAdaptateur);
        plageHoraireAdaptateur.notifyDataSetChanged();
    }

    /**
     * initialise la listeView et son adaptateur avec les plage Horaire
     */
    private void setPlageHoraireAdapter() {
        listViewPlageHoraire = findViewById(R.id.liste_plage_horaire);
        plageHoraireAdaptateur = new SimpleCursorAdapter(this,
                R.layout.ligne_liste_plage_horaire,
                curseurPlageHoraire,
                new String[] {HelperBDHoraire.FICHE_PLAGE_HORAIRE_NOM,
                        HelperBDHoraire.FICHE_PLAGE_HORAIRE_INFORMATION},
                new int[] {R.id.name,
                        R.id.information}, 0);
        listViewPlageHoraire.setAdapter(plageHoraireAdaptateur);
        plageHoraireAdaptateur.notifyDataSetChanged();
    }

    /**
     * initialise la listeView et son adaptateur avec les horaires ponctuels
     */
    private void setHPonctuelAdapter() {
        listViewHPonctuelles = findViewById(R.id.liste_horaires_ponctuelles);
        horairesPonctuellesAdapteur = new SimpleCursorAdapter(this,
                R.layout.ligne_liste_horaire_ponctuel,
                curseurHorairesPonctuelles,
                new String[] {HelperBDHoraire.FICHE_HORAIRE_PONCTUELLE_NOM,
                        HelperBDHoraire.FICHE_HORAIRE_PONCTUELLE_INFORMATION},
                new int[] {R.id.name,
                        R.id.categorie}, 0);
        listViewHPonctuelles.setAdapter(horairesPonctuellesAdapteur);
        horairesPonctuellesAdapteur.notifyDataSetChanged();
    }

    /**
     * permet de changer la visibilit?? des ??l??ments de barre d'outils
     * les ??l??ment qui vont changer d'??tat seront :
     *  le menu recherche
     *  le menu des filtre
     *  le menu d'import
     *  le menu d'export
     * @param visibility
     */
    private void setToolBarVisibility(boolean visibility) {
        Menu menu = maBarreOutil.getMenu();
        MenuItem recherche = menu.findItem(R.id.recherche);
        MenuItem filtre = menu.findItem(R.id.filtre);
        MenuItem import_option = menu.findItem(R.id.import_option);
        MenuItem export_option = menu.findItem(R.id.export_option);
        recherche.setVisible(visibility);
        filtre.setVisible(visibility);
        import_option.setVisible(visibility);
        export_option.setVisible(visibility);
    }

    /**
     * permet de changer la visibilit?? des ??l??ments de barre d'outils
     * les ??l??ment qui vont changer d'??tat selon "visibility' seront :
     *  le menu recherche
     *  le menu des filtre
     *  le menu d'import
     *  le menu d'export
     * les ??l??ment qui vont changer d'??tat selon "visibilityOfOther' seront :
     *  le menu gestion cat??gorie
     *  le menu gestion horaire
     *  le'option annuler
     * @param visibility
     * @param visibilityOfOther
     */
    private void setToolBarVisibility(boolean visibility, boolean visibilityOfOther) {
        setToolBarVisibility(visibility);
        Menu menu = maBarreOutil.getMenu();
        MenuItem gestionCatego = menu.findItem(R.id.settings_gestion_categorie);
        MenuItem gestionLoc = menu.findItem(R.id.settings_gestion_localisation);
        MenuItem annulerOption = menu.findItem(R.id.annuler_option);
        gestionCatego.setVisible(visibilityOfOther);
        gestionLoc.setVisible(visibilityOfOther);
        annulerOption.setVisible(visibilityOfOther);
    }

    /**
     * Permet l'envoi de SMS
     * @param message
     */
    public void composeSmsMessage(String message) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("sms:"));
        intent.putExtra("sms_body", message);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, getString(R.string.toast_erreur_sms), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Envoie du fichier JSON par mail
     * @param file Fichier a envoyer
     */
    private void composeMailMessage(File file) {

        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, "");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Fiche Gestion Horaire");
        emailIntent.setType("message/rfc882");
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file));

        startActivity(Intent.createChooser(emailIntent, "Envoi Mail"));

    }

    /**
     * Change le style des onglets
     */
    private void changeStyleOnglet() {
        for (int i = 0; i < lesOnglets.getTabWidget().getChildCount(); i++) {
            TextView tv = (TextView) lesOnglets.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(getResources().getColor(R.color.gris));
        }
        TextView tv = (TextView) lesOnglets.getCurrentTabView().findViewById(android.R.id.title);
        tv.setTextColor(getResources().getColor(R.color.secondary));
    }

    /**
     * permet l'ajout d'un plage horaire en appelant une nouvelle activit??
     */
    private void ajouterPlageHoraire() {
        Intent plageHoraire = new Intent(MainActivity.this,
                PlageHoraireActivity.class);
        startActivityForResult(plageHoraire, CODE_PLAGE_HORAIRE);
    }

    /**
     * permet l'ajout d'une horaire ponctuel grace a une boite de dialogue
     */
    private void ajouterHorairePonctuel() {
        Intent plageHoraire = new Intent(MainActivity.this,
                HorairePonctuelActivity.class);
        startActivityForResult(plageHoraire, CODE_FICHE_HORAIRE_PONCTUEL);
    }

    /**
     * Affiche une boite de dialogue pour la selection des filtre a appliquer sur la liste
     * l'utilisateur pourra ensuite appliquer les filtre a la liste
     */
    private void afficherFiltre() {
        final View boiteSaisie = getLayoutInflater().inflate(R.layout.selection_filtre, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.titre_boite_filtre))
                .setView(boiteSaisie)
                .setPositiveButton(getResources().getString(R.string.bouton_appliquer), null)
                .setNeutralButton(getResources().getString(R.string.bouton_effacer),null)
                .setNegativeButton(getResources().getString(R.string.bouton_negatif), null)
                .create();

        // ajout des listener au la boite de dialogue
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button boutonEffacer = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                Button boutonAppliquer = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                CheckBox check_localisation = dialog.findViewById(R.id.filtre_localisation);
                CheckBox check_categorie = dialog.findViewById(R.id.filtre_categorie);
                CheckBox check_ouvert = dialog.findViewById(R.id.filtre_open);
                Spinner spin_localisation = dialog.findViewById(R.id.localisation_spinner);
                Spinner spin_categorie = dialog.findViewById(R.id.categorie_spinner);

                // Remplissage des spinners
                SimpleCursorAdapter adapterLocalisation = getAdapterLocalisation();
                SimpleCursorAdapter adapterCategorie = getAdapterCategoriePlageHoraire();
                adapterLocalisation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                adapterCategorie.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin_localisation.setAdapter(adapterLocalisation);
                spin_categorie.setAdapter(adapterCategorie);

                /* D??laration d'un Constraint Layout qui servira a la mosification de ce
                 dernier lorsque on voula faire apparaitre les liste d??roulate*/
                ConstraintLayout constraintLayout =  dialog.findViewById(R.id.parent_layout);

                // Ev??nements sur les checkbox
                check_localisation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // affiche la liste et deplace les ??l??ment en dessous si la checkBox est selectionner
                        toggleSpinnerLocalisation(constraintLayout,
                                                  check_localisation.isChecked(), spin_localisation);

                        SimpleCursorAdapter adapter;
                        if (!check_localisation.isChecked()) {
                             adapter = getAdapterCategoriePlageHoraire();
                        } else {
                            String idLocalisation = spin_localisation.getSelectedItemId() + "";
                            adapter = getAdapterCategorieByLocalisation(idLocalisation);
                        }

                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spin_categorie.setAdapter(adapter);
                        toggleSpinnerCategorie(constraintLayout,
                                check_categorie.isChecked(), spin_categorie);
                    }
                });
                check_categorie.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SimpleCursorAdapter adapter;

                        if (!check_localisation.isChecked()) {
                            adapter = getAdapterCategoriePlageHoraire();
                        } else {
                            String idLocalisation = spin_localisation.getSelectedItemId() + "";
                            adapter = getAdapterCategorieByLocalisation(idLocalisation);
                        }
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spin_categorie.setAdapter(adapter);
                        // affiche la liste et deplace les ??l??ment en dessous si la checkBox est selectionner
                        toggleSpinnerCategorie(constraintLayout,
                                               check_categorie.isChecked(), spin_categorie);

                    }
                });

                spin_localisation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (check_categorie.isChecked()) {
                            String idLocalisation = spin_localisation.getSelectedItemId() + "";
                            SimpleCursorAdapter adapter;
                            adapter = getAdapterCategorieByLocalisation(idLocalisation);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spin_categorie.setAdapter(adapter);
                            toggleSpinnerCategorie(constraintLayout,
                                    check_categorie.isChecked(), spin_categorie);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                // Ev??nements sur les boutons
                boutonEffacer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        check_localisation.setChecked(false);
                        toggleSpinnerLocalisation(constraintLayout,
                                false, spin_localisation);
                        toggleSpinnerCategorie(constraintLayout,
                                false, spin_categorie);
                        check_categorie.setChecked(false);
                        check_ouvert.setChecked(false);
                    }
                });
                boutonAppliquer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String strCategorie = "";
                        String strLocalisation = "";
                        boolean estOuvert = true;   // stub, ne fonctionne pas
                        if (check_categorie.isChecked() && spin_categorie.getSelectedItem() != null) {
                            Categorie categorie = accesHoraires.getCategorieById(spin_categorie.getSelectedItemId() + "");
                            strCategorie += categorie.getNom();

                        }
                        if (check_localisation.isChecked()) {
                            Localisation localisation = accesHoraires.getLocalisationById(spin_localisation.getSelectedItemId() + "");
                            strLocalisation += localisation.getNom();
                        }
                        curseurPlageHoraire = accesHoraires.getCursorFichePlageHoraireByLocalisationAndCategorie(strLocalisation, strCategorie, check_ouvert.isChecked());
                        /*if (check_ouvert.isChecked()) {
                            Log.i("ouvert", "coucou");
                            curseurPlageHoraire = accesHoraires.getAllFichePlageHoraireOuverte();
                        }*/
                        plageHoraireAdaptateur.swapCursor(curseurPlageHoraire);
                        onContentChanged();
                        //ferme la dialog quand tout est bon
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }

    /**
     * permet de rendre visible ou de cacher le spinner localisation
     * @param constraintLayout layout parent de la boite de dialogue du spinner
     * @param isVisible vrai si le spinner doit devenir visible
     * @param spin_localisation spinner a afficher
     */
    private void toggleSpinnerLocalisation(
            ConstraintLayout constraintLayout,
            boolean isVisible,
            Spinner spin_localisation) {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        if (isVisible) {
            constraintSet.connect(R.id.filtre_categorie,ConstraintSet.TOP,R.id.localisation_spinner,ConstraintSet.BOTTOM,0);
            constraintSet.applyTo(constraintLayout);
            spin_localisation.setVisibility(View.VISIBLE);
        } else {
            constraintSet.connect(R.id.filtre_categorie,ConstraintSet.TOP,R.id.filtre_localisation,ConstraintSet.BOTTOM,0);
            constraintSet.applyTo(constraintLayout);
            spin_localisation.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * permet de rendre visible ou de cacher le spinner categorie
     * @param constraintLayout layout parent de la boite de dialogue du spinner
     * @param isVisible vrai si le spinner doit devenir visible
     * @param spin_localisation spinner a afficher
     */
    private void toggleSpinnerCategorie(
            ConstraintLayout constraintLayout,
            boolean isVisible,
            Spinner spin_localisation) {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        if (isVisible) {
            constraintSet.connect(R.id.filtre_open,ConstraintSet.TOP,R.id.categorie_spinner,ConstraintSet.BOTTOM,0);
            constraintSet.applyTo(constraintLayout);
            spin_localisation.setVisibility(View.VISIBLE);
        } else {
            constraintSet.connect(R.id.filtre_open,ConstraintSet.TOP,R.id.filtre_categorie,ConstraintSet.BOTTOM,0);
            constraintSet.applyTo(constraintLayout);
            spin_localisation.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * @return un Adapter contenant l'ensemble des localisation
     */
    private SimpleCursorAdapter getAdapterLocalisation() {
        return new SimpleCursorAdapter(this,
                android.R.layout.simple_spinner_item,
                accesHoraires.getCursorAllLocalisation(),
                new String[] {HelperBDHoraire.LOCALISATION_NOM},
                new int[] {android.R.id.text1,}, 0);
    }

    /**
     * @return un Adapter contenant l'ensemble des jour d'une semaine
     */
    private SimpleCursorAdapter getAdapterJour() {
        return new SimpleCursorAdapter(this,
                android.R.layout.simple_spinner_item,
                accesHoraires.getCursorAllJour(),
                new String[] {HelperBDHoraire.JOUR_LIBELLE},
                new int[] {android.R.id.text1,}, 0);
    }

    /**
     * @return un Adapter contenant l'ensemble des Cat??gorie
     */
    private SimpleCursorAdapter getAdapterCategoriePlageHoraire() {
        return new SimpleCursorAdapter(this,
                android.R.layout.simple_spinner_item,
                accesHoraires.getCursorAllCategoriePlageHoraire(),
                new String[] {HelperBDHoraire.CATEGORIE_NOM},
                new int[] {android.R.id.text1,}, 0);
    }

    /**
     * Retourne un adapter sur une liste de cat??gorie en fonction d'une localisation
     * @param idLocalisation
     * @return
     */
    private SimpleCursorAdapter getAdapterCategorieByLocalisation(String idLocalisation) {
        return new SimpleCursorAdapter(this,
                android.R.layout.simple_spinner_item,
                accesHoraires.getCursorCategorieByLocalisation(idLocalisation),
                new String[] {HelperBDHoraire.CATEGORIE_NOM},
                new int[] {android.R.id.text1,}, 0);
    }

    /**
     * TODO commentaire Tanguy
     * @param localisation
     * @param categorie
     * @param ouvert
     * @return
     */
    private SimpleCursorAdapter getAdapterPlageHoraireByFiltres(String localisation, String categorie, boolean ouvert) {
        return new SimpleCursorAdapter(this,
                R.layout.ligne_liste_plage_horaire,
                accesHoraires.getCursorFichePlageHoraireByLocalisationAndCategorie(localisation, categorie, ouvert),
                new String[] {HelperBDHoraire.FICHE_PLAGE_HORAIRE_NOM,
                HelperBDHoraire.FICHE_PLAGE_HORAIRE_INFORMATION},
                new int[] {R.id.name, R.id.information}, 0);
    }

    /**
     * Affiche une boite de dialogue d'aide a l'utilisateur
     */
    private void afficheAide() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.titre_aide))
                .setMessage(getResources().getString(R.string.message_aide_main))
                .setPositiveButton(R.string.bouton_positif, null)
                .show();
    }

    /**
     * M??thode appel?? lors du retour d'une activit?? fille
     * @param requestCode
     * @param resultCode
     * @param returnedIntent
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent returnedIntent) {
        super.onActivityResult(requestCode, resultCode, returnedIntent);
        switch(requestCode) {
            case CODE_GESTION_CATEGORIE:
                curseurPlageHoraire = accesHoraires.getCursorAllFichePlageHoraire();
                plageHoraireAdaptateur.swapCursor(curseurPlageHoraire);
                onContentChanged();
                curseurHorairesPonctuelles = accesHoraires.getCursorAllFicheHorairePonctuelle();
                horairesPonctuellesAdapteur.swapCursor(curseurHorairesPonctuelles);
                onContentChanged();
                break;
            case CODE_GESTION_LOCALISATION:
                break;
            case CODE_PLAGE_HORAIRE:
                if (resultCode != RESULT_OK) {
                    if (!modification) {
                        Cursor cursor = accesHoraires.getCursorAllFichePlageHoraire();
                        cursor.moveToLast();
                        accesHoraires.deleteFichePlageHoraire(cursor.getString(0));
                    }

                }
                curseurPlageHoraire = accesHoraires.getCursorAllFichePlageHoraire();
                plageHoraireAdaptateur.swapCursor(curseurPlageHoraire);
                onContentChanged();
                break;
            case CODE_FICHE_HORAIRE_PONCTUEL :
                if (resultCode != RESULT_OK) {
                    if (!modification) {
                        Cursor cursor = accesHoraires.getCursorAllFicheHorairePonctuelle();
                        cursor.moveToLast();
                        accesHoraires.deleteFicheHorairePonctuelle(cursor.getString(0));
                    }
                }
                curseurHorairesPonctuelles = accesHoraires.getCursorAllFicheHorairePonctuelle();
                horairesPonctuellesAdapteur.swapCursor(curseurHorairesPonctuelles);
                onContentChanged();
                break;
            default:
                break;
        }
        modification = false;
    }


	/**
     * Serialisation des objets pour l'exportation en JSON
     * Il permet d'exporter le nom, sa localisation, les horaire d'ouverture et et de fermetures
     * et les information
     * @param listeFichePlageHoraires a exporter en JSON
     */
    private File exportationJSON(FichePlageHoraire[] listeFichePlageHoraires) {

        JSONArray liste = new JSONArray();

        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File fichierJSON = new File(root, "FichesHoraire.json");
        try {
            for (int i = 0; i < listeFichePlageHoraires.length; i++) {
                liste.put(listeFichePlageHoraires[i].getJson(accesHoraires));
            }

            Log.i("JSON", liste.toString());

            FileOutputStream fos = new FileOutputStream(fichierJSON);
            Writer w = new BufferedWriter(new OutputStreamWriter(fos));
            w.write(liste.toString());
            w.flush();


        } catch (JSONException err) {
            Log.e("JSON", err.getMessage());
            Toast.makeText(this, getString(R.string.json_err_creation), Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException err) {
            Log.e("JSON", err.getMessage());
            Toast.makeText(this, getString(R.string.json_err_ouverture), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e("JSON", e.getMessage());
            Toast.makeText(this, getString(R.string.json_err_ecriture), Toast.LENGTH_LONG).show();
        }

        Toast.makeText(this, getString(R.string.export_terminee), Toast.LENGTH_LONG).show();
        return fichierJSON;
    }

    /**
     * Envoie d'une fiches horaire par SMS
     * @param id de la fiche a envoyer
     */
    private void exportationSMS(String id) {

        // recherche de la fiche horraire
        FichePlageHoraire aEnvoyer = accesHoraires.getFichePlageHoraireById(id);

        // Cat??gorie de la fiche a envoyer
        Categorie categorie = accesHoraires.getCategorieById(aEnvoyer.getIdCategorie());

        // Localisation de la cat??gorie de la fiche
        Localisation localisation = accesHoraires.getLocalisationById(categorie.getIdLocalisation());

        // Recuperation des inforamtion li?? a la cat??gorie ou a la localisation
        String nomLocalisation = localisation.getNom();
        String nomCategorie = categorie.getNom();

        // Information de la fiche
        String nomFiche = aEnvoyer.getNom();
        String informationFiche = aEnvoyer.getInformation();

        // R??cup??ration des inforamtions des ensemble de la fiche
        ArrayList<EnsemblePlageHoraire> listeEnsemblePlageHoraire
                = accesHoraires.getEnsembleHoraireByFiche(aEnvoyer.getId());

        // Formatage du message
        String message = "Localisation : " + nomLocalisation + '\n'
                       + "Cat??gorie : " + nomCategorie + '\n'
                       + "NOM : " + nomFiche + '\n'
                       + "    Information : " + informationFiche + '\n';

        for (int i = 0; i < listeEnsemblePlageHoraire.size(); i++) {
            PlageHoraire matin = accesHoraires.getPlageHoraireById(
                    listeEnsemblePlageHoraire.get(i).getIdPlageHoraireMatin());
            PlageHoraire soir = accesHoraires.getPlageHoraireById(
                    listeEnsemblePlageHoraire.get(i).getIdPlageHoraireSoir());

            Jour jour = accesHoraires.getJourById(listeEnsemblePlageHoraire.get(i).getIdJour());

            message += "    Horaires du " + jour.getJour() + " :\n";
            message += "        Matin : " +  matin.getHoraireOuverture() + " - " + matin.getHoraireFermeture() + '\n';
            message += "        Soir  : "  +  soir.getHoraireOuverture() + " - " + soir.getHoraireFermeture() +'\n';
        }

        composeSmsMessage(message);
        Log.e("SMS", message);
    }

    /**
     * Deserialiser un fichier JSON, puis l'importe dans la BD
     */
    private void importationJSON() {

        // Fichier a JSON
        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File fichier = new File(root, "FichesHoraire.json");

        BufferedReader reader;
        String ligne;

        JSONArray fichierJSON;

        int nombre;

        Cursor curseur;

        try {
            // Ouverture du fichier
            reader = new BufferedReader(new FileReader(fichier));
            ligne = reader.readLine();

            fichierJSON = new JSONArray(ligne);

            // Nombre de fiche cr??er
            nombre = fichierJSON.length();
            Log.e("Importation", "Nombre de fiche : " + nombre);

            // Lecture des fiches
            for (int i = 0; i < nombre; i++) {
                // R??cup??ration du premier JSON qui contient la cat??gorie,
                // la localisation et la fiche horraire
                JSONObject ensembleJSON = fichierJSON.getJSONObject(i);

                // Cr??ation de la localisation, si elle n'existe pas d??ja
                JSONObject localisationJSON = ensembleJSON.getJSONObject(HelperBDHoraire.NOM_TABLE_LOCALISATION);
                Localisation localisation = accesHoraires.getLocalisationByName(localisationJSON.getString(HelperBDHoraire.LOCALISATION_NOM));
                if (localisation.getId() == null && localisationJSON.getInt(HelperBDHoraire.LOCALISATION_IS_DEFAULT) == 0) {
                    localisation.setNom(localisationJSON.getString(HelperBDHoraire.LOCALISATION_NOM));
                    localisation.setIsDefault(0);

                    accesHoraires.addLocalisation(localisation);
                    curseur = accesHoraires.getCursorAllLocalisation();
                    curseur.moveToLast();
                    localisation.setId(curseur.getString(HoraireDAO.LOCALISATION_NUM_COLONNE_CLE));
                }

                // Cr??ation de la cat??gorie, si elle n'existe pas d??ja
                JSONObject categorieJSON = ensembleJSON.getJSONObject(HelperBDHoraire.NOM_TABLE_CATEGORIE);
                Categorie categorie = accesHoraires.getCategorieByNameByLocalisation(categorieJSON.getString(HelperBDHoraire.CATEGORIE_NOM), localisation.getId(), 0);
                Log.e("DEBUG", categorie.toString());
                if (categorie.getId() == null) {

                    categorie.setNom(categorieJSON.getString(HelperBDHoraire.CATEGORIE_NOM));
                    categorie.setIsDefault(0);
                    categorie.setIsHorairePonctuelle(0);
                    categorie.setIdLocalisation(localisation.getId());

                    accesHoraires.addCategorie(categorie);
                    curseur = accesHoraires.getCursorAllCategorie();
                    curseur.moveToLast();
                    categorie.setId(curseur.getString(HoraireDAO.CATEGORIE_NUM_COLONNE_CLE));

                    Log.e("DEBUG", "Cr??ation cat??gorie");
                    Log.e("DEBUG", categorie.getNom());
                }

                // Cr??ation de la fiche a partir du JSON : r??cup??ration du nom et des informations
                FichePlageHoraire ficheTmp = new FichePlageHoraire();
                JSONObject ficheJSON = ensembleJSON.getJSONObject(HelperBDHoraire.NOM_TABLE_FICHE_PLAGE_HORAIRE);
                ficheTmp.setNom(ficheJSON.getString(HelperBDHoraire.FICHE_PLAGE_HORAIRE_NOM));
                ficheTmp.setInformation(ficheJSON.getString(HelperBDHoraire.FICHE_PLAGE_HORAIRE_INFORMATION));
                ficheTmp.setIdCategorie(categorie.getId());

                // R??cup??ration de l'ID de la fiche
                accesHoraires.addFichePlageHoraire(ficheTmp);
                curseur = accesHoraires.getCursorAllFichePlageHoraire();
                curseur.moveToLast();
                ficheTmp.setId(curseur.getString(HoraireDAO.FICHE_PLAGE_HORAIRE_NUM_COLONNE_CLE));

                // Cr??ation de la liste des ensembles
                JSONArray listeEnsembleHoraireJSON = ensembleJSON.getJSONArray(HelperBDHoraire.NOM_TABLE_ENSEMBLE_PLAGE_HORAIRE);
                Log.e("DEBUG import", listeEnsembleHoraireJSON.toString());

                for(int j = 0; j < listeEnsembleHoraireJSON.length(); j ++) {

                    // recupere un ensemble horaire de la liste pour le traiter
                    JSONObject ensembleHoraireJSON = listeEnsembleHoraireJSON.getJSONObject(j);

                    // Recupere l'ID du jour de l'ensemble
                    String idJour = ensembleHoraireJSON.getString(HelperBDHoraire.ENSEMBLE_PLAGE_HORAIRE_CLE_JOUR);

                    // Recupere l'objet JSON de la plage horaire matin
                    JSONObject matinJSON = ensembleHoraireJSON.getJSONObject(HelperBDHoraire.ENSEMBLE_PLAGE_HORAIRE_CLE_HORAIRE_MATIN);

                    // Recupere l'objet JSON de la plage horaire soir
                    JSONObject soirJSON = ensembleHoraireJSON.getJSONObject(HelperBDHoraire.ENSEMBLE_PLAGE_HORAIRE_CLE_HORAIRE_MATIN);

                    // Ensemble a ajouter a la BD
                    EnsemblePlageHoraire ensemblePlageHoraire;

                    // Cr??ation de la plage horraire matin
                    PlageHoraire plageHoraireMatin = new PlageHoraire(
                            matinJSON.getString(HelperBDHoraire.PLAGE_HORAIRE_OUVERTURE),
                            matinJSON.getString(HelperBDHoraire.PLAGE_HORAIRE_FERMETURE),
                            matinJSON.getInt(HelperBDHoraire.PLAGE_HORAIRE_EST_FERME)
                    );

                    // Ajout de la plage horaire a la BD et r??cup??ration de l'ID
                    accesHoraires.addPlageHoraire(plageHoraireMatin);
                    curseur = accesHoraires.getCursorAllPlageHoraire();
                    curseur.moveToLast();
                    plageHoraireMatin.setId(curseur.getString(HoraireDAO.PLAGE_HORAIRE_NUM_COLONNE_CLE));

                    // Matin et soir
                    if (!soirJSON.getString(HelperBDHoraire.PLAGE_HORAIRE_OUVERTURE).equals("")
                            && soirJSON.getString(HelperBDHoraire.PLAGE_HORAIRE_OUVERTURE).equals("0")) {

                        PlageHoraire plageHoraireSoir = new PlageHoraire(
                                soirJSON.getString(HelperBDHoraire.PLAGE_HORAIRE_OUVERTURE),
                                soirJSON.getString(HelperBDHoraire.PLAGE_HORAIRE_FERMETURE),
                                soirJSON.getInt(HelperBDHoraire.PLAGE_HORAIRE_EST_FERME)
                        );

                        accesHoraires.addPlageHoraire(plageHoraireSoir);
                        curseur = accesHoraires.getCursorAllPlageHoraire();
                        curseur.moveToLast();
                        plageHoraireSoir.setId(curseur.getString(HoraireDAO.PLAGE_HORAIRE_NUM_COLONNE_CLE));

                        ensemblePlageHoraire = new EnsemblePlageHoraire(
                                plageHoraireMatin.getId(),
                                plageHoraireSoir.getId(),
                                idJour,
                                ficheTmp.getId()
                        );
                    } else {
                        ensemblePlageHoraire = new EnsemblePlageHoraire(
                                plageHoraireMatin.getId(),
                                idJour,
                                ficheTmp.getId()
                        );
                    }

                    accesHoraires.addEnsemblePlageHoraire(ensemblePlageHoraire);

                }
            }

        } catch (IOException | JSONException err) {
            Toast.makeText(this, getString(R.string.erreur_ouverture), Toast.LENGTH_LONG).show();
            err.printStackTrace();
        }

        //  Actualisation de la page
        curseurPlageHoraire = accesHoraires.getCursorAllFichePlageHoraire();
        plageHoraireAdaptateur.swapCursor(curseurPlageHoraire);
        onContentChanged();
        Toast.makeText(this, R.string.import_terminee, Toast.LENGTH_LONG).show();

    }

    /**
     * D??s??rialise un fichier CSV et ajoute son contenu a la BD applicative
     */
    private void importationCSV() {

        final int LOCALISATION = 0;
        final int CATEGORIE = 1;
        final int NOM = 2;
        final int INFORMATION = 3;
        final int LUNDI = 4;
        final int MARDI = 8;
        final int MERCREDI = 12;
        final int JEUDI = 16;
        final int VENDREDI = 20;
        final int SAMEDI = 24;
        final int DIMANCHE = 28;

        Cursor cursor;

        Localisation localisation;
        Categorie categorie;
        FichePlageHoraire fichePlageHoraire;

        BufferedReader reader;
        String ligne;
        String[] contenu;

        verifyStoragePermissions(this);

        File fichier = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "FichesHoraire.csv");

        try {
            // Ouverture du fichier
            reader = new BufferedReader(new FileReader(fichier));

            //Parcours des fiche horaire
            while((ligne = reader.readLine()) != null) {

                contenu = ligne.split(";", -2);

                Log.e("Insercion CSV", ligne);
                Log.e("Taille tableau", "" + contenu.length);

                //Si le tableau est rempli, et si ce n'est pas l'entete
                if (!contenu[LOCALISATION].equals("Ville") && !contenu[LOCALISATION].equals("")) {

                    localisation = accesHoraires.getLocalisationByName(contenu[LOCALISATION]);

                    // Cr??ation de la localisation si elle n'exite pas
                    if (localisation.getId() == null) {
                        localisation.setNom(contenu[LOCALISATION]);
                        accesHoraires.addLocalisation(localisation);

                        cursor = accesHoraires.getCursorAllLocalisation();
                        cursor.moveToLast();
                        localisation.setId(cursor.getString(HoraireDAO.LOCALISATION_NUM_COLONNE_CLE));

                        Log.e("BD", "Ajout de localisation dans la BD, id : " + localisation.getId());

                    }

                    categorie = accesHoraires.getCategorieByNameByLocalisation(contenu[CATEGORIE], localisation.getId(), 0);

                    // Cr??ation de la cat??gorie si elle n'existe pas
                    if (categorie.getId() == null) {
                        categorie.setNom(contenu[CATEGORIE]);
                        categorie.setIsDefault(0);
                        categorie.setIsHorairePonctuelle(0);
                        categorie.setIdLocalisation(localisation.getId());
                        accesHoraires.addCategorie(categorie);
                        Log.e("BD", "Ajout de cat??gorie dans la BD");

                        cursor = accesHoraires.getCursorAllCategoriePlageHoraire();
                        cursor.moveToLast();
                        categorie.setId(cursor.getString(HoraireDAO.CATEGORIE_NUM_COLONNE_CLE));
                    }

                    // Cr??ation des fiches plages horraires
                    fichePlageHoraire = new FichePlageHoraire(contenu[NOM],
                                                              categorie.getId(),
                                                              contenu[INFORMATION],
                                                 null);

                    accesHoraires.addFichePlageHoraire(fichePlageHoraire);

                    cursor = accesHoraires.getCursorAllFichePlageHoraire();
                    cursor.moveToLast();
                    fichePlageHoraire.setId(cursor.getString(HoraireDAO.FICHE_PLAGE_HORAIRE_NUM_COLONNE_CLE));

                    // Cr??ation des ensemble horaire et des plages horaire
                    ajoutEnsemblePlageHorraire(contenu, LUNDI, fichePlageHoraire.getId());
                    ajoutEnsemblePlageHorraire(contenu, MARDI, fichePlageHoraire.getId());
                    ajoutEnsemblePlageHorraire(contenu, MERCREDI, fichePlageHoraire.getId());
                    ajoutEnsemblePlageHorraire(contenu, JEUDI, fichePlageHoraire.getId());
                    ajoutEnsemblePlageHorraire(contenu, VENDREDI, fichePlageHoraire.getId());
                    ajoutEnsemblePlageHorraire(contenu, SAMEDI, fichePlageHoraire.getId());
                    ajoutEnsemblePlageHorraire(contenu, DIMANCHE, fichePlageHoraire.getId());

                }
            }
            curseurPlageHoraire = accesHoraires.getCursorAllFichePlageHoraire();
            plageHoraireAdaptateur.swapCursor(curseurPlageHoraire);
            onContentChanged();
            Toast.makeText(this, R.string.import_terminee, Toast.LENGTH_LONG).show();

        }catch (IOException err) {
            Log.e("CSV", err.toString());
        }
    }

    /**
     * Ajoute a la BD les plage horraire et l'ensemble a une fiche horraire
     * @param tableau tableau du contenu a ajouter
     * @param indexJour index du tableau ou commance l'ensemble horraire (m??me si null)
     */
    private void ajoutEnsemblePlageHorraire(String[] tableau, int indexJour, String idFiche) {

        String idJour = Integer.toString(indexJour / 4);

        Log.e("TAG", "ID fiche : " + idFiche);
        Log.e("TAG", "ID jour : " + idJour);

        Cursor cursor;

            if (tableau[indexJour + 2].equals("") && tableau[indexJour + 3].equals("")) {
                /* TOUTE LA JOURNEE */
                PlageHoraire plageHoraire;
                if (!tableau[indexJour].equals("ferme")) {
                    // OUVERT
                    plageHoraire = new PlageHoraire(tableau[indexJour], tableau[indexJour + 1], 0);
                    accesHoraires.addPlageHoraire(plageHoraire);

                    cursor = accesHoraires.getCursorAllPlageHoraire();
                    cursor.moveToLast();
                    plageHoraire.setId(cursor.getString(HoraireDAO.PLAGE_HORAIRE_NUM_COLONNE_CLE));
                } else {
                    // FERME
                    plageHoraire = new PlageHoraire("00:00", "00:00", 1);
                    accesHoraires.addPlageHoraire(plageHoraire);

                    cursor = accesHoraires.getCursorAllPlageHoraire();
                    cursor.moveToLast();
                    plageHoraire.setId(cursor.getString(HoraireDAO.PLAGE_HORAIRE_NUM_COLONNE_CLE));
                }

                EnsemblePlageHoraire ensemblePlageHoraire = new EnsemblePlageHoraire(plageHoraire.getId(), idJour, idFiche);
                accesHoraires.addEnsemblePlageHoraire(ensemblePlageHoraire);

            } else {
                /* MATIN + APREM */
                PlageHoraire plageHoraireMatin;

                if (!tableau[indexJour].equals("ferme")) {
                    plageHoraireMatin = new PlageHoraire(tableau[indexJour], 1, tableau[indexJour + 1], 1, 0);
                } else {
                    plageHoraireMatin = new PlageHoraire("00:00", "00:00", 1);
                }

                accesHoraires.addPlageHoraire(plageHoraireMatin);
                cursor = accesHoraires.getCursorAllPlageHoraire();
                cursor.moveToLast();
                plageHoraireMatin.setId(cursor.getString(HoraireDAO.PLAGE_HORAIRE_NUM_COLONNE_CLE));

                PlageHoraire plageHoraireSoir;

                if (!tableau[indexJour + 2].equals("ferme")) {
                    plageHoraireSoir = new PlageHoraire(tableau[indexJour + 2], 1, tableau[indexJour + 3], 1, 0);
                } else {
                    plageHoraireSoir = new PlageHoraire("00:00", "00:00", 1);
                }

                accesHoraires.addPlageHoraire(plageHoraireSoir);
                cursor = accesHoraires.getCursorAllPlageHoraire();
                cursor.moveToLast();
                plageHoraireSoir.setId(cursor.getString(HoraireDAO.PLAGE_HORAIRE_NUM_COLONNE_CLE));

                EnsemblePlageHoraire ensemblePlageHoraire = new EnsemblePlageHoraire(plageHoraireMatin.getId(), plageHoraireSoir.getId(), idJour, idFiche);
                accesHoraires.addEnsemblePlageHoraire(ensemblePlageHoraire);

        }
    }

    /**
     * Verifie les permission pour l'ecriture et la lecture de fichier de l'application
     * Si ce n'est pas le cas, alors une modale s'ouvre pour demander les permission a l'utilisateur
     * @param activity
     */
    public void verifyStoragePermissions(Activity activity) {
        // Check si permission de lecture
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        //Demande si ce n'est pas le cas
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        } else {
            //DEBUG
            Log.e("Permission", "Permission ok");
        }
    }
}
