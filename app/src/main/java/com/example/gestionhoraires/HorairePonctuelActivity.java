package com.example.gestionhoraires;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.os.ParcelableCompatCreatorCallbacks;

import com.example.gestionhoraires.beans.FicheHorairePonctuelle;
import com.example.gestionhoraires.beans.FichePlageHoraire;
import com.example.gestionhoraires.beans.HorairePonctuelle;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HorairePonctuelActivity extends AppCompatActivity {

    /** Objet destiné à faciliter l'accès à la table des horaires */
    private HoraireDAO accesHoraires;

    /** barre d'outils de l'applications */
    private Toolbar maBarreOutil;

    /** Liste présenter dans le deuxieme onglet de l'application */
    private ListView listViewHPonctuel;

    /** Adaptateur permettant de gérer la liste des horaire ponctuelle */
    private SimpleCursorAdapter horairesPonctuelAdapteur;

    /** EditText contenant le nom */
    private EditText editTextNom;

    /** Spinner contenant les localisations */
    private Spinner spinnerLocalisation;

    /** Spinner contenant les Catégories */
    private Spinner spinnerCategorie;

    /** Curseur contenant les horaires ponctuels de la fiche */
    private Cursor curseurSurBase;

    /** La fiche horaire ponctuel */
    private FicheHorairePonctuelle ficheHorairePonctuelle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fiche_ponctuel);

        // accès au DAO
        accesHoraires = new HoraireDAO(this);
        accesHoraires.open();

        maBarreOutil = findViewById(R.id.fiche_ponctuel_tool_bar);
        setSupportActionBar(maBarreOutil);
        maBarreOutil.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        maBarreOutil.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retour();
            }
        });

        ficheHorairePonctuelle = new FicheHorairePonctuelle();
        accesHoraires.addFicheHorairePonctuelle(ficheHorairePonctuelle);
        Cursor cursor = accesHoraires.getCursorAllFicheHorairePonctuelle();
        cursor.moveToLast();
        ficheHorairePonctuelle.setId(cursor.getString(0));

        curseurSurBase = accesHoraires.getCursorAllHorairePonctuelleByIdFiche(ficheHorairePonctuelle.getId());

        // on remplie la liste
        listViewHPonctuel = findViewById(R.id.liste_horaires_ponctuel);
        horairesPonctuelAdapteur = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2,
                curseurSurBase,
                new String[] {"libelle",
                        "horaireOuverture"},
                new int[] {android.R.id.text1,
                        android.R.id.text2}, 0);
        listViewHPonctuel.setAdapter(horairesPonctuelAdapteur);
        registerForContextMenu(listViewHPonctuel);

        // on récupere les widgets
        editTextNom = findViewById(R.id.editText_nom);
        spinnerLocalisation = findViewById(R.id.spinner_localisation);
        spinnerCategorie = findViewById(R.id.spinner_categorie);

        SimpleCursorAdapter adapterLocalisation = getAdapterLocalisation();
        adapterLocalisation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocalisation.setAdapter(adapterLocalisation);

        spinnerLocalisation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SimpleCursorAdapter adapterCategorie = getAdapterCategoriePonctuelsByLocalisation(spinnerLocalisation.getSelectedItemId() + "");
                adapterCategorie.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategorie.setAdapter(adapterCategorie);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // On ajoute un bouton flotant
        ExtendedFloatingActionButton fab = findViewById(R.id.fab_ajout_fiche);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO ajout fiche horaire ponctuel
            }
        });
    }

    /**
     * Méthode invoquée à la première activation du menu d'options
     * @param menuActivite menu d'option activé
     * @return un booléen égal à vrai si le menu a pu être créé
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menuActivite) {
        getMenuInflater().inflate(R.menu.menu_plage_horaire_tool_bar, menuActivite);
        return super.onCreateOptionsMenu(menuActivite);
    }

    /**
     * Méthode invoquée automatiquement lorsque l'utiisateur active un menu contextuel
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        new MenuInflater(this).inflate(R.menu.menu_contextuel_ponctuel, menu);
    }

    /**
     * Méthode invoquée automatiquement lorsque l'utilisateur choisira une option
     * dans le menu contextuel associé à la liste
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        String identifiant = curseurSurBase.getString(HoraireDAO.HORAIRE_PONCTUELLE_NUM_COLONNE_CLE);

        AdapterView.AdapterContextMenuInfo information =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        // selon l'option sélectionnée dans le menu, on réalise le traitement adéquat
        switch(item.getItemId()) {
            case R.id.supprimer :   // supprimer un élément
                accesHoraires.deleteHorairePonctuel(identifiant);
                break;
            case R.id.modifier :
                modifierHoraire(identifiant);
                break;
            case R.id.annuler :		 // retour à la liste principale
                break;

        }
        curseurSurBase = accesHoraires.getCursorAllHorairePonctuelleByIdFiche(ficheHorairePonctuelle.getId());
        horairesPonctuelAdapteur.swapCursor(curseurSurBase);
        onContentChanged();
        return (super.onContextItemSelected(item));
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
     * retourne a l'activiter principale
     */
    private void retour() {
        Intent intentionRetour = new Intent();
        setResult(Activity.RESULT_OK, intentionRetour);
        finish();
    }

    /**
     * Permet l'ajout d'une horaire a la liste des horaire ponctuelles
     * @param view
     */
    public void ajoutHoraire(View view) {
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
                        HorairePonctuelle horairePonctuelle;
                        if (checkBoxAjoutFin.isChecked()) {
                            horairePonctuelle = new HorairePonctuelle(editTextHeure.getText().toString(),
                                    spinnerJour.getSelectedItemId() + "",
                                    ficheHorairePonctuelle.getId());
                        } else {
                            horairePonctuelle = new HorairePonctuelle(editTextHeure.getText().toString(),
                                    editTextHeureFin.getText().toString(),
                                    spinnerJour.getSelectedItemId() + "",
                                    ficheHorairePonctuelle.getId());
                        }
                        accesHoraires.addHorairePonctuelle(horairePonctuelle);
                        curseurSurBase = accesHoraires.getCursorAllHorairePonctuelleByIdFiche(ficheHorairePonctuelle.getId());
                        horairesPonctuelAdapteur.swapCursor(curseurSurBase);
                        onContentChanged();
                        dialog.dismiss();
                    }
                });
            }
        });

        dialog.show();
    }

    /**
     * Permet la modification d'une horaire ponctuel
     * @param idHorairePonctuel
     */
    public void modifierHoraire(String idHorairePonctuel) {
        final View boiteSaisie = getLayoutInflater().inflate(R.layout.ajout_h_ponctuel, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.update_horaire_ponctuel)
                .setView(boiteSaisie)
                .setPositiveButton(getResources().getString(R.string.option_modifier), null)
                .setNeutralButton(getResources().getString(R.string.bouton_negatif),null)
                .create();

        HorairePonctuelle horairePonctuelle = accesHoraires.getHorairePonctuelleById(idHorairePonctuel);

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

                rowHeureFin.setVisibility(horairePonctuelle.getHoraireFin() == null ? View.VISIBLE : View.INVISIBLE);

                editTextHeure.setText(horairePonctuelle.getHoraireDebut());
                spinnerJour.setSelection(Integer.parseInt(horairePonctuelle.getIdJour()) - 1);

                if (horairePonctuelle.getHoraireFin() == null) {
                    editTextHeureFin.setText(horairePonctuelle.getHoraireFin());
                }

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
                        HorairePonctuelle horairePonctuelle;
                        if (checkBoxAjoutFin.isChecked()) {
                            horairePonctuelle = new HorairePonctuelle(editTextHeure.getText().toString(),
                                    spinnerJour.getSelectedItemId() + "",
                                    ficheHorairePonctuelle.getId());
                        } else {
                            horairePonctuelle = new HorairePonctuelle(editTextHeure.getText().toString(),
                                    editTextHeureFin.getText().toString(),
                                    spinnerJour.getSelectedItemId() + "",
                                    ficheHorairePonctuelle.getId());
                        }
                        accesHoraires.updateHorairePonctuelle(horairePonctuelle, idHorairePonctuel);
                        curseurSurBase = accesHoraires.getCursorAllHorairePonctuelleByIdFiche(ficheHorairePonctuelle.getId());
                        horairesPonctuelAdapteur.swapCursor(curseurSurBase);
                        onContentChanged();
                        dialog.dismiss();
                    }
                });
            }
        });

        dialog.show();
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
     * Retourne un adapter sur une liste de catégorie en fonction d'une localisation
     * @param idLocalisation
     * @return
     */
    private SimpleCursorAdapter getAdapterCategoriePonctuelsByLocalisation(String idLocalisation) {
        return new SimpleCursorAdapter(this,
                android.R.layout.simple_spinner_item,
                accesHoraires.getCursorCategoriePonctuelsByLocalisation(idLocalisation),
                new String[] {HelperBDHoraire.CATEGORIE_NOM},
                new int[] {android.R.id.text1,}, 0);
    }
}
