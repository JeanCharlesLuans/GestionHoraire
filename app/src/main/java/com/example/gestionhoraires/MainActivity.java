package com.example.gestionhoraires;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import static android.graphics.Color.rgb;

public class MainActivity extends AppCompatActivity {

    /** Identifiant de l'intention pour la gestion des catégories */
    private final int CODE_GESTION_CATEGORIE = 10;

    /** Identifiant de l'intention pour la gestion des localisations */
    private final int CODE_GESTION_LOCALISATION = 20;

    /** barre d'outils de l'applications */
    private Toolbar maBarreOutil;

    /**
     * Table d'onglets gérée par l'activité
     */
    private TabHost lesOnglets;

    /** Objet destiné à faciliter l'accès à la table des horaires */
    private HoraireDAO accesHoraires;

    // ONGLET 1

    /** Curseur sur l'ensemble des plages horaires de la base */
    private Cursor curseurPlageHoraire;

    /** Liste contenant les plages horaires à afficher */
    private ArrayList<String> listePlageHoraire;

    /** Liste présenter dans le premiere onglet de l'application */
    private ListView ListViewPlageHoraire;

    /** Adaptateur permettant de gérer la liste des plage horaire */
    private SimpleCursorAdapter plageHoraireAdaptateur;

    // ONGLET 2

    /** Curseur sur l'ensemble des horaires ponctuelles de la base */
    private Cursor curseurHorairesPonctuelles; // TODO vérifier si on a vraiment besoin de deux curseur.

    /** Liste contenant les horaires ponctuelles à afficher */
    private ArrayList<String> listeHPonctuelles;

    /** Liste présenter dans le deuxieme onglet de l'application */
    private ListView ListViewHPonctuelles;

    /** Adaptateur permettant de gérer la liste des horaire ponctuelle */
    private SimpleCursorAdapter horairesPonctuellesAdapteur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // accès au DAO
        accesHoraires = new HoraireDAO(this);
        accesHoraires.open();
//        curseurPlageHoraire = accesHoraires.getCursorAllFichePlageHoraire();
//        curseurHorairesPonctuelles = accesHoraires.getCursorAllFicheHorairePonctuelle();

        // Liste de l'onglet 1 : Plages Horaires
        listePlageHoraire = new ArrayList<String>();
        ListViewPlageHoraire = findViewById(R.id.liste_plage_horaire);
        plageHoraireAdaptateur = new SimpleCursorAdapter(this,
                R.layout.ligne_liste_plage_horaire,
                curseurPlageHoraire,
                new String[] {"nom",  //TODO ici regarder les colonne dans ligne_liste.xml
                        "information"},      //TODO a adapter comme GestionBDCuisson.CUISSON_ALIMENT,
                new int[] {R.id.name,
                        R.id.information}, 0);
        ListViewPlageHoraire.setAdapter(plageHoraireAdaptateur);

        // Liste de l'onglet 2 : Horaires Ponctuelles
        listeHPonctuelles = new ArrayList<String>();
        ListViewHPonctuelles = findViewById(R.id.liste_horaires_ponctuelles);
        horairesPonctuellesAdapteur = new SimpleCursorAdapter(this,
                R.layout.ligne_liste_horaires_ponctuelles,
                curseurHorairesPonctuelles,
                new String[] {"nom",  //TODO ici regarder les colonne dans ligne_liste.xml
                        "jour_semaine",      //TODO a adapter comme GestionBDCuisson.CUISSON_ALIMENT
                        "horaire" },
                new int[] {R.id.name,
                        R.id.jour_semaine,
                        R.id.horaire}, 0);
        ListViewHPonctuelles.setAdapter(horairesPonctuellesAdapteur);

        registerForContextMenu(ListViewPlageHoraire);
        registerForContextMenu(ListViewHPonctuelles);
        // TODO Menu Contextuel des listes

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
                        Menu menu = maBarreOutil.getMenu();
                        MenuItem recherche = menu.findItem(R.id.recherche);
                        MenuItem filtre = menu.findItem(R.id.filtre);
                        MenuItem import_option = menu.findItem(R.id.import_option);
                        MenuItem export_option = menu.findItem(R.id.export_option);
                        MenuItem settings_option = menu.findItem(R.id.settings_option);
                        MenuItem annuler_option = menu.findItem(R.id.annuler_option);
                        recherche.setVisible(visibility);
                        filtre.setVisible(visibility);
                        import_option.setVisible(visibility);
                        export_option.setVisible(visibility);
                        settings_option.setVisible(visibility);
                        annuler_option.setVisible(visibility);
                    }
                }
        );
        lesOnglets.setCurrentTab(0);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO action ajouter suivant onglet (+ faire methode pour connaitre onglet select)
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
                break;
            case R.id.export_option :
                break;
            case R.id.settings_option :
                GestionParametre();
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
     * affiche une boite de dialogue a l'utilisateur pour choisir entre
     * la gestion des Catégorie ou des Localisation
     * Envoie une intention a l'activité correspondante au choix de l'utilisateur
     */
    private void GestionParametre() {
        // on désérialise le layout qui est associé à la boîte de saisie
        final View boiteSaisie = getLayoutInflater().inflate(R.layout.saisie_gestion, null);

        /*
         * Création d'une boîte de dialogue
         */
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.titre_boite_parametre))
                .setView(boiteSaisie)
                .setPositiveButton(getResources().getString(R.string.bouton_positif),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                RadioGroup boutonMode =
                                        boiteSaisie.findViewById(R.id.groupe_gestion);

                                switch (boutonMode.getCheckedRadioButtonId()) {
                                    case R.id.option_gestion_categorie:
                                        Intent catego = new Intent(MainActivity.this,
                                                                    CategorieActivity.class);
                                        startActivityForResult(catego, CODE_GESTION_CATEGORIE);
                                        break;
                                    case R.id.option_gestion_localisation:
                                        Intent locali = new Intent(MainActivity.this,
                                                LocalisationActivity.class);
                                        startActivityForResult(locali, CODE_GESTION_LOCALISATION);
                                        break;
                                }
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.bouton_negatif), null)
                .show();
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
                SimpleCursorAdapter adapterCategorie = getAdapterCategorie();
                adapterLocalisation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                adapterCategorie.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin_localisation.setAdapter(adapterLocalisation);
                spin_categorie.setAdapter(adapterCategorie);

                /* Délaration d'un Constraint Layout qui servira a la mosification de ce
                 dernier lorsque on voula faire apparaitre les liste déroulate*/
                ConstraintLayout constraintLayout =  dialog.findViewById(R.id.parent_layout);
                ConstraintSet constraintSet = new ConstraintSet();

                // Evènements sur les checkbox
                check_localisation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // affiche la liste et deplace les élément en dessous si la checkBox est selectionner
                        toggleSpinnerLocalisation(constraintLayout, constraintSet,
                                                  check_localisation.isChecked(), spin_localisation);
                    }
                });
                check_categorie.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // affiche la liste et deplace les élément en dessous si la checkBox est selectionner
                        toggleSpinnerCategorie(constraintLayout, constraintSet,
                                               check_categorie.isChecked(), spin_categorie);
                    }
                });

                // Evènements sur les boutons
                boutonEffacer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        check_localisation.setChecked(false);
                        toggleSpinnerLocalisation(constraintLayout, constraintSet,
                                check_localisation.isChecked(), spin_localisation);
                        check_categorie.setChecked(false);
                        check_ouvert.setChecked(false);
                    }
                });
                boutonAppliquer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO Do something

                        //ferme la dialog quand tout est bon
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }

    private void toggleSpinnerLocalisation(
            ConstraintLayout constraintLayout,
            ConstraintSet constraintSet,
            boolean isChecked,
            Spinner spin_localisation) {
        constraintSet.clone(constraintLayout);
        if (isChecked) {
            constraintSet.connect(R.id.filtre_categorie,ConstraintSet.TOP,R.id.localisation_spinner,ConstraintSet.BOTTOM,0);
            constraintSet.applyTo(constraintLayout);
            spin_localisation.setVisibility(View.VISIBLE);
        } else {
            constraintSet.connect(R.id.filtre_categorie,ConstraintSet.TOP,R.id.filtre_localisation,ConstraintSet.BOTTOM,0);
            constraintSet.applyTo(constraintLayout);
            spin_localisation.setVisibility(View.INVISIBLE);
        }
    }
    private void toggleSpinnerCategorie(
            ConstraintLayout constraintLayout,
            ConstraintSet constraintSet,
            boolean isChecked,
            Spinner spin_localisation) {
        constraintSet.clone(constraintLayout);
        if (isChecked) {
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
                new String[] {"nom"}, // TODO nom Colonne
                new int[] {android.R.id.text1,}, 0);
    }

    /**
     * @return un Adapter contenant l'ensemble des Catégorie
     */
    private SimpleCursorAdapter getAdapterCategorie() {
        return new SimpleCursorAdapter(this,
                android.R.layout.simple_spinner_item,
                accesHoraires.getCursorAllCategorie(),
                new String[] {"nom"}, // TODO nom Colonne
                new int[] {android.R.id.text1,}, 0);
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
        switch(requestCode) {
            case CODE_GESTION_CATEGORIE:
                // TODO Avons nous besoin de faire qq chose au retour ?
                break;
            case CODE_GESTION_LOCALISATION:
                break;
        }
    }
}