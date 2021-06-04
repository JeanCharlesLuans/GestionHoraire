package com.example.gestionhoraires;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.os.ParcelableCompatCreatorCallbacks;

import com.example.gestionhoraires.beans.Categorie;
import com.example.gestionhoraires.beans.FicheHorairePonctuelle;
import com.example.gestionhoraires.beans.FichePlageHoraire;
import com.example.gestionhoraires.beans.HorairePonctuelle;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HorairePonctuelActivity extends AppCompatActivity {

    /** Code identifiant modification */
    private static final String CODE_IDENTIFICATION = "IDENTIFIANT_MODIFICATION";

    /** Identifiant de mofication de fiche horaire ponctuel */
    private final static String CODE_MODIFICATION_PONCTUEL = "MODIFIER_PONCTUEL";

    /** Identifiant de l'intention pour l'acces a la camera */
    private static final int TAKE_PICTURE = 1;

    /** Identifiant de l'intention pour l'acces a la gallerie d'image */
    private static final int PICK_IMAGE = 2;

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

    /** EditText information */
    private EditText editTextInformation;

    /** Spinner contenant les localisations */
    private Spinner spinnerLocalisation;

    /** Spinner contenant les Catégories */
    private Spinner spinnerCategorie;

    /** Curseur contenant les horaires ponctuels de la fiche */
    private Cursor curseurSurBase;

    /** La fiche horaire ponctuel */
    private FicheHorairePonctuelle ficheHorairePonctuelle;

    /** La photo de la fiche */
    private ImageView imageView;

    /** Path de l'image */
    private String imagePath;

    /** Indicateur de moficiation */
    private boolean modification;

    /** Identifiant de l'horaire ponctuel à modifier */
    private String idFicheHorairePonctuel;

    /** Indicateur de premier passage */
    private boolean indicateurPremierPassage;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fiche_ponctuel);

        // accès au DAO
        accesHoraires = new HoraireDAO(this);
        accesHoraires.open();

        indicateurPremierPassage = true;

        // on récupere les widgets
        editTextNom = findViewById(R.id.editText_nom);
        editTextInformation = findViewById(R.id.editText_info);
        spinnerLocalisation = findViewById(R.id.spinner_localisation);
        spinnerCategorie = findViewById(R.id.spinner_categorie);
        imageView = findViewById(R.id.imageView);

        modification = getIntent().getBooleanExtra(CODE_MODIFICATION_PONCTUEL, false);
        if (modification) {
            idFicheHorairePonctuel = getIntent().getStringExtra(CODE_IDENTIFICATION) + "";
            ficheHorairePonctuelle = accesHoraires.getFicheHorairePonctuelleById(idFicheHorairePonctuel);
            curseurSurBase = accesHoraires.getCursorAllHorairePonctuelleByIdFiche(idFicheHorairePonctuel);
            editTextNom.setText(ficheHorairePonctuelle.getNom());
            editTextInformation.setText(ficheHorairePonctuelle.getInformation());
            if (ficheHorairePonctuelle.getCheminPhoto() != null) {
                imageView.setImageURI(Uri.parse(ficheHorairePonctuelle.getCheminPhoto()));
                imagePath = ficheHorairePonctuelle.getCheminPhoto();
            }


        } else {
            ficheHorairePonctuelle = new FicheHorairePonctuelle();
            accesHoraires.addFicheHorairePonctuelle(ficheHorairePonctuelle);
            Cursor cursor = accesHoraires.getCursorAllFicheHorairePonctuelle();
            cursor.moveToLast();
            ficheHorairePonctuelle.setId(cursor.getString(0));
            curseurSurBase = accesHoraires.getCursorAllHorairePonctuelleByIdFiche(ficheHorairePonctuelle.getId());
        }


        maBarreOutil = findViewById(R.id.fiche_ponctuel_tool_bar);
        setSupportActionBar(maBarreOutil);
        maBarreOutil.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        maBarreOutil.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!modification) {
                    accesHoraires.deleteFicheHorairePonctuelle(ficheHorairePonctuelle.getId());
                }
                retour();
            }
        });

        // on remplie la liste
        listViewHPonctuel = findViewById(R.id.liste_horaires_ponctuel);
        horairesPonctuelAdapteur = new SimpleCursorAdapter(this,
                R.layout.ligne_liste_fiche_horaire_ponctuel,
                curseurSurBase,
                new String[] {HelperBDHoraire.JOUR_LIBELLE,
                        HelperBDHoraire.HORAIRE_PONCTUELLE_OUVERTURE,
                        HelperBDHoraire.HORAIRE_PONCTUELLE_FERMETURE},
                new int[] {R.id.jour_semaine,
                        R.id.horaire_ouverture,
                        R.id.horaire_fermeture}, 0);
        listViewHPonctuel.setAdapter(horairesPonctuelAdapteur);
        registerForContextMenu(listViewHPonctuel);

        SimpleCursorAdapter adapterLocalisation = getAdapterLocalisation();
        adapterLocalisation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocalisation.setAdapter(adapterLocalisation);

        spinnerLocalisation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SimpleCursorAdapter adapterCategorie = getAdapterCategoriePonctuelsByLocalisation(spinnerLocalisation.getSelectedItemId() + "");
                adapterCategorie.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategorie.setAdapter(adapterCategorie);
                if (modification && indicateurPremierPassage) {
                    indicateurPremierPassage = false;
                    System.out.println(accesHoraires.getPositionByIdCategorieHorairePonctuel(ficheHorairePonctuelle.getIdCategorie()));
                    //spinnerCategorie.setSelection(accesHoraires.getPositionByIdCategorieHorairePonctuel(ficheHorairePonctuelle.getIdCategorie()));
                    //spinnerCategorie.setSelection(1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View boiteSaisie = getLayoutInflater().inflate(R.layout.image_view, null);

                AlertDialog dialog = new AlertDialog.Builder(HorairePonctuelActivity.this)
                        .setView(boiteSaisie)
                        .setPositiveButton(getResources().getString(R.string.bouton_positif), null)
                        .create();

                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        ImageView dialogImageView = dialog.findViewById(R.id.dialog_imageview);
                        dialogImageView.setImageURI(Uri.parse(imagePath));
                    }
                });
                dialog.show();
            }
        });

        // On ajoute un bouton flotant
        FloatingActionButton fab = findViewById(R.id.fab_ajout_fiche);
        if (modification) {
            fab.setImageResource(R.drawable.ic_baseline_save_alt_24);
            Categorie categorie = accesHoraires.getCategorieById(ficheHorairePonctuelle.getIdCategorie());
            spinnerLocalisation.setSelection(accesHoraires.getPositionByIdLocalisation(categorie.getIdLocalisation()));
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextNom.getText().toString().equals("")) {
                    if (spinnerCategorie.getSelectedItem() != null) {
                        ficheHorairePonctuelle.setNom(editTextNom.getText().toString());
                        ficheHorairePonctuelle.setInformation(editTextInformation.getText().toString());
                        ficheHorairePonctuelle.setIdCategorie(spinnerCategorie.getSelectedItemId() + "");
                        ficheHorairePonctuelle.setCheminPhoto(imagePath);
                        accesHoraires.updateFicheHorairePonctuelle(ficheHorairePonctuelle, ficheHorairePonctuelle.getId());
                        retour();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.toast_pas_de_categorie, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.toast_pas_de_nom, Toast.LENGTH_LONG).show();
                }
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
                        if (!checkBoxAjoutFin.isChecked()) {
                            if (!editTextHeure.getText().toString().equals("")) {
                                horairePonctuelle = new HorairePonctuelle(editTextHeure.getText().toString(),
                                        spinnerJour.getSelectedItemId() + "",
                                        ficheHorairePonctuelle.getId());
                                accesHoraires.addHorairePonctuelle(horairePonctuelle);
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.toast_champs_vides, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            if (editTextHeure.getText().toString().equals("")
                                    || editTextHeureFin.getText().toString().equals("")) {
                                Toast.makeText(getApplicationContext(), R.string.toast_champs_vides, Toast.LENGTH_LONG).show();
                            } else {
                                horairePonctuelle = new HorairePonctuelle(editTextHeure.getText().toString(),
                                        editTextHeureFin.getText().toString(),
                                        spinnerJour.getSelectedItemId() + "",
                                        ficheHorairePonctuelle.getId());
                                accesHoraires.addHorairePonctuelle(horairePonctuelle);
                            }
                        }

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

                rowHeureFin.setVisibility(horairePonctuelle.getHoraireFin() != null ? View.VISIBLE : View.INVISIBLE);
                checkBoxAjoutFin.setChecked(horairePonctuelle.getHoraireFin() != null ? true : false);

                editTextHeure.setText(horairePonctuelle.getHoraireDebut());
                spinnerJour.setSelection(Integer.parseInt(horairePonctuelle.getIdJour()) - 1);

                System.out.println("Heure de fin : " + horairePonctuelle.getHoraireFin());
                if (horairePonctuelle.getHoraireFin() != null) {

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
                        if (!checkBoxAjoutFin.isChecked()) {
                            horairePonctuelle = new HorairePonctuelle(editTextHeure.getText().toString(),
                                    spinnerJour.getSelectedItemId() + "",
                                    ficheHorairePonctuelle.getId());
                        } else {
                            horairePonctuelle = new HorairePonctuelle(editTextHeure.getText().toString(),
                                    editTextHeureFin.getText().toString(),
                                    spinnerJour.getSelectedItemId() + "",
                                    ficheHorairePonctuelle.getId());
                        }
                        System.out.println(editTextHeureFin.getText().toString());
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

    /**
     * Efface la photo selectionner par l'utilisateur
     */
    public void onClickEffacerP(View view) {
        imagePath = "";
        imageView.setImageURI(Uri.parse(""));
    }

    /**
     * Active la camera de l'appareil android pour prendre une photo
     */
    public void onClickCameraP(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFileP();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("ERREUR", ex +"");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, TAKE_PICTURE);
            }
        }
    }

    /**
     * Créer l'image
     * @return
     * @throws IOException
     */
    private File createImageFileP() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        galleryAddPicP(image.getAbsolutePath());
        return image;
    }

    /**
     * Ajoute l'image à la gallerie
     */
    private void galleryAddPicP(String path) {
        System.out.println(path);
        imagePath = path;
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    /**
     * Ouvre la gallery afin de selectionner une photo
     */
    public void onClickGalleryP(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    /**
     * Appelé automatiquement lorsque une activité fille se termine
     * @param requestCode
     * @param resultCode
     * @param returnedIntent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent returnedIntent) {
        super.onActivityResult(requestCode, resultCode, returnedIntent);
        if (resultCode == RESULT_OK) {
            switch(requestCode) {
                case TAKE_PICTURE:
                    if (resultCode == Activity.RESULT_OK) {
                        imageView.setImageURI(Uri.parse(imagePath));
                    }
                    break;
                case PICK_IMAGE:
                    if (resultCode == Activity.RESULT_OK) {
                        imageView.setImageURI(returnedIntent.getData());
                        imagePath = returnedIntent.getData().toString();
                    }
                    break;
                default:
                    break;
            }
        }

    }
}
