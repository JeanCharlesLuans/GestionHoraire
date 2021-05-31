package com.example.gestionhoraires;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gestionhoraires.beans.Categorie;
import com.example.gestionhoraires.beans.EnsemblePlageHoraire;
import com.example.gestionhoraires.beans.FichePlageHoraire;
import android.widget.TimePicker;

import com.example.gestionhoraires.beans.Jour;
import com.example.gestionhoraires.beans.Localisation;
import com.example.gestionhoraires.beans.PlageHoraire;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    /** Identifiant de l'intention pour la gestion des catégories */
    private final int CODE_GESTION_CATEGORIE = 10;

    /** Identifiant de l'intention pour la gestion des localisations */
    private final int CODE_GESTION_LOCALISATION = 20;

    /** Identifiant de l'intention pour l'ajout de la plage horaire */
    private final int CODE_PLAGE_HORAIRE = 30;

    /** Clé pour le message transmis par l'activité secondaire */
    public final static String CLE_H_PONCTUEL = "com.example.gestionhoraires.PONCTUEL";

    /** barre d'outils de l'applications */
    private Toolbar maBarreOutil;

    /**
     * Table d'onglets gérée par l'activité
     */
    private TabHost lesOnglets;

    /** numéro de l'onglet des plages horaires */
    private final int TAB_PLAGE_HORAIRE = 0;

    /** numéro de l'onglet des horaires ponctuels */
    private final int TAB_H_PONCTUEL = 1;

    /** bouton flottant servant a ajouter une fiche horaire */
    private FloatingActionButton floatingActionButtonAJout;

    /** bouton flottant servant a l'export */
    private ExtendedFloatingActionButton floatingActionButtonExport;

    /** Objet destiné à faciliter l'accès à la table des horaires */
    private HoraireDAO accesHoraires;

    // ONGLET 1

    /** Curseur sur l'ensemble des plages horaires de la base */
    private Cursor curseurPlageHoraire;

    /** Liste présenter dans le premiere onglet de l'application */
    private ListView listViewPlageHoraire;

    /** Adaptateur permettant de gérer la liste des plage horaire */
    private SimpleCursorAdapter plageHoraireAdaptateur;

    // ONGLET 2

    /** Curseur sur l'ensemble des horaires ponctuelles de la base */
    private Cursor curseurHorairesPonctuelles; // TODO vérifier si on a vraiment besoin de deux curseur.

    /** Liste présenter dans le deuxieme onglet de l'application */
    private ListView listViewHPonctuelles;

    /** Adaptateur permettant de gérer la liste des horaire ponctuelle */
    private SimpleCursorAdapter horairesPonctuellesAdapteur;

    /** Indicateur ouvert des filtres */
    private boolean ouvert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // accès au DAO
        accesHoraires = new HoraireDAO(this);
        accesHoraires.open();

        curseurPlageHoraire = accesHoraires.getCursorAllFichePlageHoraire();
//        curseurHorairesPonctuelles = accesHoraires.getCursorAllFicheHorairePonctuelle();

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


                        /* on enleve la possibiliter de filtrer la liste quand
                         * l'utilisateur est sur le deuxieme onglet
                         */
                        boolean visibility = !tabId.equals("onglet_horaires_ponctuelles");
                        setToolBarVisibility(visibility);
                    }
                }
        );
        lesOnglets.setCurrentTab(TAB_PLAGE_HORAIRE);

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
     * Méthode invoquée automatiquement lorsque l'utiisateur active un menu contextuel
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
// TODO Menu Contextuel des listes
        new MenuInflater(this).inflate(R.menu.menu_contextuel_settings, menu);
    }

    /**
     * Méthode invoquée automatiquement lorsque l'utilisateur choisira une option
     * dans le menu contextuel associé à la liste
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
// TODO Menu Contextuel des listes
        /*
         *  on accéde à des informations supplémentaires sur l'élémemt cliqué dans la liste
         */
        AdapterView.AdapterContextMenuInfo information =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        // selon l'option sélectionnée dans le menu, on réalise le traitement adéquat
        switch(item.getItemId()) {
            case R.id.supprimer :   // supprimer un élément
                accesHoraires.deleteFichePlageHoraire(curseurPlageHoraire.getString(accesHoraires.FICHE_HORAIRE_PONCTUELLE_NUM_COLONNE_CLE));
                break;
            case R.id.export_option:
                // TODO export SMS
                break;
            case R.id.modifier :
                //modifierElement(information.id); // TODO action modifier
                break;
            case R.id.annuler :		 // retour à la liste principale
                break;

        }
        curseurPlageHoraire = accesHoraires.getCursorAllFichePlageHoraire();
        plageHoraireAdaptateur.swapCursor(curseurPlageHoraire);
        onContentChanged();
        return (super.onContextItemSelected(item));
    }

    /**
     * Méthode invoquée à la première activation du menu d'options
     * @param menuActivite menu d'option activé
     * @return un booléen égal à vrai si le menu a pu être créé
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menuActivite) {
        getMenuInflater().inflate(R.menu.menu_main_tool_bar, menuActivite);

        MenuItem itemRecherche = menuActivite.findItem(R.id.recherche);

        SearchView vuePourRecherche = (SearchView) itemRecherche.getActionView();
        vuePourRecherche.setQueryHint(getResources().getString(R.string.aide_recherche));
        vuePourRecherche.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            /**
             * Méthode invoquée quand l'utilisateur valide la recherche,
             * i.e. quand il clique sur la loupe du clavier virtuel
             * @param query texte tapé par l'utilisateur dans la zone de saisie
             * @return vrai si la recherche a pu être gérée
             */
            @Override
            public boolean onQueryTextSubmit(String query) {
                //TODO recherche = query
                return true;
            }
            /**
             * Méthode invoquée quand l'utilisateur modifie le texte de la recherche
             * @param s texte modifie
             * @return vrai si le changement de texte a pu être géré
             */
            @Override
            public boolean onQueryTextChange(String s) {
                return true;
            }
        });
        return true;
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
     * affiche une fenêtre de dialogue a l'utilisateur pourqu'il puisse chosir
     * entre les différent mode d'importation
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
                                        // TODO import CSV
                                        initData();
                                        curseurPlageHoraire = accesHoraires.getCursorAllFichePlageHoraire();
                                        plageHoraireAdaptateur.swapCursor(curseurPlageHoraire);
                                        onContentChanged();
                                        break;
                                    case R.id.option_import_json:
                                        // TODO import JSON
                                        break;
                                }
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.bouton_negatif), null)
                .show();

    }

    /**
     * affiche une fenêtre de dialogue à l'utilisateur pour qu'il puissen choisir
     * entre les différent mode d'exportation de JSON
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
     * permet a l'utilisateur de slectionner des élément de la liste,
     * affiche aussi un boutons d'export et change l'état de la toolbar
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

        // changement de l'état de la toolbar
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
//                showDialogExportJson(exportationJSON()); TODO export JSON

                // when everything is ok
                resetInterface();
            }
        });
    }

    /**
     * retourne une liste de position correspondant au élément selectionner de la liste
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
     * réinitalise une partie de l'interface dans son état intial
     * rétablie la visibilité de la toolbar
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
     * puisse choisir des éléments de la liste
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
                R.layout.ligne_liste_horaires_ponctuelles,
                curseurHorairesPonctuelles,
                new String[] {HelperBDHoraire.FICHE_HORAIRE_PONCTUELLE_NOM,
                        "jour_semaine", // TODO euh stp change moi ca Tanguy
                        "horaire" },
                new int[] {R.id.name,
                        R.id.jour_semaine,
                        R.id.horaire}, 0);
        listViewHPonctuelles.setAdapter(horairesPonctuellesAdapteur);
        horairesPonctuellesAdapteur.notifyDataSetChanged();
    }

    /**
     * permet de changer la visibilité des éléments de barre d'outils
     * les élément qui vont changer d'état seront :
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
     * permet de changer la visibilité des éléments de barre d'outils
     * les élément qui vont changer d'état selon "visibility' seront :
     *  le menu recherche
     *  le menu des filtre
     *  le menu d'import
     *  le menu d'export
     * les élément qui vont changer d'état selon "visibilityOfOther' seront :
     *  le menu gestion catégorie
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
     * @param
     */
    private void composeMailMessage(File file) {

        //String localisationFichier = "/data/data/com/example/gestionhoraires/files/fichier.json";

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
     * permet l'ajout d'un plage horaire en appelant une nouvelle activité
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
        final View boiteSaisie = getLayoutInflater().inflate(R.layout.ajout_h_ponctuel, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.ajout_horaires_ponctuelles)
                .setView(boiteSaisie)
                .setPositiveButton(getResources().getString(R.string.bouton_ajouter), null)
                .setNeutralButton(getResources().getString(R.string.bouton_negatif),null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button boutonAjout = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                EditText editTextHeure = ((AlertDialog) dialog).findViewById(R.id.heure_editText);
                EditText editTextHeureFin = ((AlertDialog) dialog).findViewById(R.id.heure_fin_editText);
                Button bouton_heure = ((AlertDialog) dialog).findViewById(R.id.btn_heure);
                Button bouton_heure_fin = ((AlertDialog) dialog).findViewById(R.id.btn_heure_fin);
                CheckBox checkBoxAjoutFin = ((AlertDialog) dialog).findViewById(R.id.checkbox_ajout_fin);
                TableRow rowHeureFin = ((AlertDialog) dialog).findViewById(R.id.table_row_heure_fin);
                Spinner spinnerJour = ((AlertDialog) dialog).findViewById(R.id.jour_spinner);

                //remplissage du Spinner
                SimpleCursorAdapter adapterJour = getAdapterJour();
                adapterJour.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerJour.setAdapter(adapterJour);

                // listener pour l'affichage de l'ajout de l'heure de fin
                checkBoxAjoutFin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkBoxAjoutFin.isChecked()) {
                            rowHeureFin.setVisibility(View.VISIBLE);
                        } else {
                            rowHeureFin.setVisibility(View.INVISIBLE);
                        }
                    }
                });
                // listener pour ajouter l'heure a l'editText
                bouton_heure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                editTextHeure.setText(hourOfDay + ":" + minute );
                            }
                        };
                        TimePickerDialog timePickerDialog = new TimePickerDialog(((AlertDialog) dialog).getContext(),
                                R.style.timePickerDialog, timeSetListener, 12, 30, true);
                        timePickerDialog.show();
                    }
                });
                // listener pour ajouter l'heure a l'editText
                bouton_heure_fin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                editTextHeureFin.setText(hourOfDay + ":" + minute );
                            }
                        };
                        TimePickerDialog timePickerDialog = new TimePickerDialog(((AlertDialog) dialog).getContext(),
                                R.style.timePickerDialog, timeSetListener, 12, 30, true);
                        timePickerDialog.show();
                    }
                });
                // listener pour ajouter l'horaire ponctuelle
                boutonAjout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // TODO action ajout horaire ponctuelle
                    }
                });
            }
        });

        dialog.show();
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

                /* Délaration d'un Constraint Layout qui servira a la mosification de ce
                 dernier lorsque on voula faire apparaitre les liste déroulate*/
                ConstraintLayout constraintLayout =  dialog.findViewById(R.id.parent_layout);

                // Evènements sur les checkbox
                check_localisation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // affiche la liste et deplace les élément en dessous si la checkBox est selectionner
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
                        // affiche la liste et deplace les élément en dessous si la checkBox est selectionner
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

                // Evènements sur les boutons
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
                        // TODO Do something
                        if (check_categorie.isChecked() && spin_categorie.isSelected()) {
                            Categorie categorie = accesHoraires.getCategorieById(spin_categorie.getSelectedItemId() + "");
                            strCategorie += categorie.getNom();
                        }
                        if (check_localisation.isChecked()) {
                            Localisation localisation = accesHoraires.getLocalisationById(spin_localisation.getSelectedItemId() + "");
                            strLocalisation += localisation.getNom();
                        }
                        curseurPlageHoraire = accesHoraires.getCursorFichePlageHoraireByLocalisationAndCategorie(strLocalisation, strCategorie, true);
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
     * @return un Adapter contenant l'ensemble des Catégorie
     */
    private SimpleCursorAdapter getAdapterCategoriePlageHoraire() {
        return new SimpleCursorAdapter(this,
                android.R.layout.simple_spinner_item,
                accesHoraires.getCursorAllCategoriePlageHoraire(),
                new String[] {HelperBDHoraire.CATEGORIE_NOM},
                new int[] {android.R.id.text1,}, 0);
    }

    /**
     * Retourne un adapter sur une liste de catégorie en fonction d'une localisation
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
     * TODO commentaire Tanguy ou JC
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
                .setMessage(getResources().getString(R.string.message_aide))
                .setPositiveButton(R.string.bouton_positif, null)
                .show();
    }

    /**
     * Méthode appelé lors du retour d'une activité fille
     * @param requestCode
     * @param resultCode
     * @param returnedIntent
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent returnedIntent) {
        super.onActivityResult(requestCode, resultCode, returnedIntent);
        // TODO Avons nous besoin de faire qq chose au retour ?
        switch(requestCode) {
            case CODE_GESTION_CATEGORIE:
                break;
            case CODE_GESTION_LOCALISATION:
                break;
        }
    }


	/**
     * Serialisation des objets pour l'exportation en JSON
     * Il permet d'exporter le nom, sa localisation, les horaire d'ouverture et et de fermetures
     * et les information
     * @param listeFichePlageHoraires a exporter en JSON
     */
    private File exportationJSON(FichePlageHoraire[] listeFichePlageHoraires) {

        JSONArray liste = new JSONArray();

        listeFichePlageHoraires[0].setId("1"); // TODO l'enlever STUB

        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File fichierJSON = new File(root, "FichierJSON");
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
            Toast.makeText(this, "Une erreur c'est produite durant la création du JSON", Toast.LENGTH_LONG);
        } catch (FileNotFoundException err) {
            Log.e("JSON", err.getMessage());
            Toast.makeText(this, "Une erreur c'est produite durant la création du fichier", Toast.LENGTH_LONG);
        } catch (IOException e) {
            Log.e("JSON", e.getMessage());
            Toast.makeText(this, "Une erreur c'est produite durant l'ecriture du fichier", Toast.LENGTH_LONG);
        }

        Toast.makeText(this, "Exportation du JSON terminé", Toast.LENGTH_LONG).show();
        return fichierJSON;
    }

    /**
     * Envoie d'une fiches horaire par SMS
     * @param aEnvoyer la fiche a envoyer
     */
    private void exportationSMS(FichePlageHoraire aEnvoyer) {

        aEnvoyer.setId("1"); // TODO l'enlever STUB

        String[] tabEnsemble;

        // Catégorie de la fiche a envoyer
        Categorie categorie = accesHoraires.getCategorieById(aEnvoyer.getIdCategorie());

        // Localisation de la catégorie de la fiche
        Localisation localisation = accesHoraires.getLocalisationById(categorie.getIdLocalisation());

        // Recuperation des inforamtion lié a la catégorie ou a la localisation
        String nomLocalisation = localisation.getNom();
        String nomCategorie = categorie.getNom();

        // Information de la fiche
        String nomFiche = aEnvoyer.getNom();
        String informationFiche = aEnvoyer.getInformation();

        // Récupération des inforamtions des ensemble de la fiche
        ArrayList<EnsemblePlageHoraire> listeEnsemblePlageHoraire
                = accesHoraires.getEnsembleHorraireOfFiche(aEnvoyer.getId());

        // Formatage du message
        String message = "Localisation : " + nomLocalisation + '\n'
                       + "Catégorie : " + nomCategorie + '\n'
                       + "NOM : " + nomFiche + '\n'
                       + "    Information : " + informationFiche + '\n';

        for (int i = 0; i < listeEnsemblePlageHoraire.size(); i++) {
            PlageHoraire matin = accesHoraires.getPlageHoraireById(
                    listeEnsemblePlageHoraire.get(i).getIdPlageHoraireMatin());
            PlageHoraire soir = accesHoraires.getPlageHoraireById(
                    listeEnsemblePlageHoraire.get(i).getIdPlageHoraireSoir());

            Jour jour = accesHoraires.getJourById(listeEnsemblePlageHoraire.get(i).getIdJour());

            message += "    Horraires du " + jour.getJour() + " :\n";
            message += "        Matin : " +  matin.getHoraireOuverture() + " - " + matin.getHoraireFermeture() + '\n';
            message += "        Soir : "  +  soir.getHoraireOuverture() + " - " + soir.getHoraireFermeture() +'\n';
        }

        composeSmsMessage(message);
        Log.e("SMS", message);
    }

    /**
     * STUB initialise des datas dans la BD
     */
    public void initData() {

        PlageHoraire[] listePlage =
                {
                        new PlageHoraire("10:00", 1, "12:00", 1, 0),
                        new PlageHoraire("14:00", 1, "18:00", 1, 0),

                        new PlageHoraire("08:00", 1, "14:00", 1, 0),
                        new PlageHoraire("16:00", 1, "20:00", 1, 0),

                        new PlageHoraire("08:00", 1, "", 0, 0),
                        new PlageHoraire("", 0, "20:00", 1, 0),

                        new PlageHoraire("10:00", 1, "12:00", 1, 0),
                        new PlageHoraire("10:00", 1, "12:00", 1, 0),

                        new PlageHoraire("10:00", 1, "12:00", 1, 0),
                        new PlageHoraire("10:00", 1, "12:00", 1, 0),

                        new PlageHoraire("10:00", 1, "12:00", 1, 0),
                        new PlageHoraire("10:00", 1, "12:00", 1, 0),

                        new PlageHoraire("10:00", 1, "12:00", 1, 0),
                        new PlageHoraire("10:00", 1, "12:00", 1, 0),
                };

        EnsemblePlageHoraire[] listeEnsemble =
                {
                        new EnsemblePlageHoraire("1","2","1", "1"),
                        new EnsemblePlageHoraire("3","4","2", "1"),
                        new EnsemblePlageHoraire("5","6","3", "1"),
                        new EnsemblePlageHoraire("7","8","4", "1"),
                        new EnsemblePlageHoraire("9","10","5", "1"),
                        new EnsemblePlageHoraire("11","12","6", "1"),
                        new EnsemblePlageHoraire("13","14","7", "1")
                };

        FichePlageHoraire[] listeFichePlageHorraire =
                {
                        new FichePlageHoraire("Nom 0", "1", "Information 0", "c:/photo0"),
                        new FichePlageHoraire("Nom 1", "1", "Information 1", "c:/photo1"),
                        new FichePlageHoraire("Nom 2", "1", "Information 2", "c:/photo2"),
                        new FichePlageHoraire("Nom 3", "1", "Information 3", "c:/photo3"),
                };

        for (int i = 0; i < listePlage.length; i++) {
            accesHoraires.addPlageHoraire(listePlage[i]);
        }

        for (int i = 0; i < listeFichePlageHorraire.length; i++) {
            accesHoraires.addFichePlageHoraire(listeFichePlageHorraire[i]);
        }

        for (int i = 0; i < listeEnsemble.length; i++) {
            accesHoraires.addEnsemblePlageHoraire(listeEnsemble[i]);
        }

        Log.i("IMPORTATION", "Fin");
    }
}
