package com.example.gestionhoraires;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import static android.graphics.Color.rgb;

public class MainActivity extends AppCompatActivity {

    /** barre d'outils de l'applications */
    private Toolbar maBarreOutil;

    /**
     * Table d'onglets gérée par l'activité
     */
    private TabHost lesOnglets;

    /** Objet destiné à faciliter l'accès à la table des plages horaires */
    //private HoraireDAO accesHoraires; // TODO CHANGER TYPE

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
        //accesHoraires = new HoraireDAO(this); //TODO Changer type
        //accesHoraires.open();
        //curseurPlageHoraire = accesHoraires.getCursorPlageHoraire();
        //curseurHorairesPonctuelles = accesHoraires.getCursorHPonctuelles();

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

        // On ajoute ka ToolBar
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
        changeColorOnglet();
        lesOnglets.setOnTabChangedListener(
                new TabHost.OnTabChangeListener() {
                    @Override
                    public void onTabChanged(String tabId) {

                        // on met la bonne couleurs pour les onglets
                        changeColorOnglet();

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

    private void changeColorOnglet() {
        for (int i = 0; i < lesOnglets.getTabWidget().getChildCount(); i++) {
            TextView tv = (TextView) lesOnglets.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(getResources().getColor(R.color.gris));
        }
        TextView tv = (TextView) lesOnglets.getCurrentTabView().findViewById(android.R.id.title);
        tv.setTextColor(getResources().getColor(R.color.secondary));
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
                break;
            case R.id.import_option :
                break;
            case R.id.export_option :
                break;
            case R.id.settings_option :
                break;
            case R.id.annuler_option :
                break;
            case R.id.aide :
                afficheAide();
                break;
        }
        return (super.onOptionsItemSelected(item));
    }

    private void afficheAide() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.titre_aide))
                .setMessage(getResources().getString(R.string.message_aide))
                .setPositiveButton(R.string.bouton_positif, null)
                .show();
    }
}