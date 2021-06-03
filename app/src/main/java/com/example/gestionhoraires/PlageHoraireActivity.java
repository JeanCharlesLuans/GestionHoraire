package com.example.gestionhoraires;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.icu.util.Currency;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.emergency.EmergencyNumber;
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
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.example.gestionhoraires.beans.Categorie;
import com.example.gestionhoraires.beans.EnsemblePlageHoraire;
import com.example.gestionhoraires.beans.FichePlageHoraire;
import com.example.gestionhoraires.beans.Jour;
import com.example.gestionhoraires.beans.PlageHoraire;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.transform.Result;

public class PlageHoraireActivity extends AppCompatActivity {

    /** Identifiant de l'intention pour l'acces a la camera */
    private static final int TAKE_PICTURE = 1;

    /** Identifiant de l'intention pour l'acces a la gallerie d'image */
    private static final int PICK_IMAGE = 2;

    /** Identifiant de modification */
    private final static String CODE_MODIFICATION = "MODIFIER";

    /** Code identifiant modification */
    private static final String CODE_IDENTIFICATION = "IDENTIFIANT_MODIFICATION";

    /** Path de l'image */
    private String imagePath;

    /** Objet destiné à faciliter l'accès à la table des horaires */
    private HoraireDAO accesHoraires;

    /** barre d'outils de l'applications */
    private Toolbar maBarreOutil;

    /** EditText contenant le nom */
    private EditText editTextNom;

    /** Spinner contenant les localisations */
    private Spinner spinnerLocalisation;

    /** Spinner contenant les Catégories */
    private Spinner spinnerCategorie;

    /** EditText contenant les informations complémentaires */
    private EditText editTextInformation;

    /** EditText contenant les horaire du matin du jour courant */
    private EditText editTextMatin;

    /** EditText contenant les horaire de l'apres-midi du jour courant */
    private EditText editTextAprem;

    /** La photo de la fiche */
    private ImageView imageView;

    /** La fiche plage horaire */
    private static FichePlageHoraire fichePlageHoraire;

    /** Le tableau contenant les 7 plages horaires */
    private EnsemblePlageHoraire[] ensemblesPlagesHoraire;

    /** Indicateur de modification */
    private boolean modification;

    /** Identifiant de la fiche à modifier */
    private String idFichePlageHoraire;

    /** Indicateur de premier passage dans la modification */
    private boolean indicateurPremierPassage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plage_horaire);

        modification = getIntent().getBooleanExtra(CODE_MODIFICATION, false);
        if (modification) {
            idFichePlageHoraire = getIntent().getStringExtra(CODE_IDENTIFICATION) + "";
        }

        indicateurPremierPassage = true;

        // accès au DAO
        accesHoraires = new HoraireDAO(this);
        accesHoraires.open();

        maBarreOutil = findViewById(R.id.plage_horaire_tool_bar);
        setSupportActionBar(maBarreOutil);
        maBarreOutil.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        maBarreOutil.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!modification) {
                    for (EnsemblePlageHoraire ensemblePlageHoraire : ensemblesPlagesHoraire) {
                        if (ensemblePlageHoraire != null) {
                            accesHoraires.deleteEnsemblePlageHoraire(ensemblePlageHoraire.getId());
                        }
                    }
                    accesHoraires.deleteFichePlageHoraire(fichePlageHoraire.getId());
                }
                retour();
            }
        });

        if (!modification) {
            fichePlageHoraire = new FichePlageHoraire();
            accesHoraires.addFichePlageHoraire(fichePlageHoraire);
            Cursor cursor = accesHoraires.getCursorAllFichePlageHoraire();
            cursor.moveToLast();
            fichePlageHoraire.setId(cursor.getString(0));
        } else {
            System.out.println(idFichePlageHoraire);
            fichePlageHoraire = accesHoraires.getFichePlageHoraireById(idFichePlageHoraire);
        }

        /* Initialisation du tableau */
        ensemblesPlagesHoraire = new EnsemblePlageHoraire[7];

        // on récupere toutes les widgets
        editTextNom = findViewById(R.id.editText_nom);
        spinnerLocalisation = findViewById(R.id.spinner_localisation);
        spinnerCategorie = findViewById(R.id.spinner_categorie);
        editTextInformation = findViewById(R.id.editText_info);
        // edit Text pour présenter les horaire du jour courant
        editTextMatin = findViewById(R.id.editText_matin);
        editTextAprem = findViewById(R.id.editText_aprem);
        imageView = findViewById(R.id.imageView);

        // On récupère les button des jours
        Button btnLundi = findViewById(R.id.btn_lundi);
        Button btnMardi = findViewById(R.id.btn_mardi);
        Button btnMercredi = findViewById(R.id.btn_mercredi);
        Button btnJeudi = findViewById(R.id.btn_jeudi);
        Button btnVendredi = findViewById(R.id.btn_vendredi);
        Button btnSamedi = findViewById(R.id.btn_samedi);
        Button btnDimanche = findViewById(R.id.btn_dimanche);

        SimpleCursorAdapter adapterLocalisation = getAdapterLocalisation();
        adapterLocalisation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocalisation.setAdapter(adapterLocalisation);

        spinnerLocalisation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SimpleCursorAdapter adapterCategorie = getAdapterCategorieByLocalisation(spinnerLocalisation.getSelectedItemId() + "");
                adapterCategorie.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategorie.setAdapter(adapterCategorie);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // On ajoute un bouton flotant
        FloatingActionButton fab = findViewById(R.id.fab_ajouter);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (!editTextNom.getText().toString().equals("")) {
                        if (spinnerCategorie.getSelectedItem() == null) {
                            Toast.makeText(getApplicationContext(), R.string.toast_pas_de_categorie, Toast.LENGTH_LONG).show();
                        } else {
                            ajouterFichePlageHoraire();
                            retour();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.toast_pas_de_nom, Toast.LENGTH_LONG).show();
                    }
                }
            }
        );

        if (modification) {
            fab.setImageResource(R.drawable.ic_baseline_save_alt_24);
            editTextNom.setText(fichePlageHoraire.getNom());
            editTextInformation.setText(fichePlageHoraire.getInformation());
            if (fichePlageHoraire.getCheminPhoto() != null) {
                imagePath = fichePlageHoraire.getCheminPhoto();
                imageView.setImageURI(Uri.parse(imagePath));
            }
            System.out.println(fichePlageHoraire.getIdCategorie());
            Categorie categorie = accesHoraires.getCategorieById(fichePlageHoraire.getIdCategorie());
            spinnerLocalisation.setSelection(accesHoraires.getPositionByIdLocalisation(categorie.getIdLocalisation()));

            ArrayList<EnsemblePlageHoraire> ensembles = accesHoraires.getEnsembleHoraireByFiche(idFichePlageHoraire);
            for (EnsemblePlageHoraire ensemble : ensembles) {
                if (ensemble != null) {
                    switch (Integer.parseInt(ensemble.getIdJour())) {
                        case 1:
                            ensemblesPlagesHoraire[0] = ensemble;
                            toogleStyleBoutonSemaine(btnLundi, true);
                            break;
                        case 2:
                            ensemblesPlagesHoraire[1] = ensemble;
                            toogleStyleBoutonSemaine(btnMardi, true);
                            break;
                        case 3:
                            ensemblesPlagesHoraire[2] = ensemble;
                            toogleStyleBoutonSemaine(btnMercredi, true);
                            break;
                        case 4:
                            ensemblesPlagesHoraire[3] = ensemble;
                            toogleStyleBoutonSemaine(btnJeudi, true);
                            break;
                        case 5:
                            ensemblesPlagesHoraire[4] = ensemble;
                            toogleStyleBoutonSemaine(btnVendredi, true);
                            break;
                        case 6:
                            ensemblesPlagesHoraire[5] = ensemble;
                            toogleStyleBoutonSemaine(btnSamedi, true);
                            break;
                        case 7:
                            ensemblesPlagesHoraire[6] = ensemble;
                            toogleStyleBoutonSemaine(btnDimanche, true);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    /**
     * Méthode invoquée à la première activation du menu d'options
     * @param menuActivite menu d'option activé
     * @return un booléen égal à vrai si le menu a pu être créé
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menuActivite) {
        getMenuInflater().inflate(R.menu.menu_ponctuel_tool_bar, menuActivite);
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
            case R.id.retour :
                for (EnsemblePlageHoraire ensemblePlageHoraire : ensemblesPlagesHoraire) {
                    if (ensemblePlageHoraire != null) {
                        accesHoraires.deleteEnsemblePlageHoraire(ensemblePlageHoraire.getId());
                    }
                }
                accesHoraires.deleteFichePlageHoraire(fichePlageHoraire.getId());
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
     * @return un Adapter contenant l'ensemble des catégories
     */
    private SimpleCursorAdapter getAdapterCategorie() {
        return new SimpleCursorAdapter(this,
                android.R.layout.simple_spinner_item,
                accesHoraires.getCursorAllCategorie(),
                new String[] {HelperBDHoraire.LOCALISATION_NOM},
                new int[] {android.R.id.text1,}, 0);
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
     * Efface la photo selectionner par l'utilisateur
     */
    public void onClickEffacer(View view) {
        imageView.setImageURI(Uri.parse(""));
    }

    /**
     * Active la camera de l'appareil android pour prendre une photo
     */
    public void onClickCamera(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
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
    private File createImageFile() throws IOException {
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
        galleryAddPic(image.getAbsolutePath());
        return image;
    }

    /**
     * Ajoute l'image à la gallerie
     */
    private void galleryAddPic(String path) {
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
    public void onClickGallery(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    /**
     * Ajoute la plage horaire a la base de donner et retourne a l'activité principale
     */
    public void ajouterFichePlageHoraire() {
        String identifiant = fichePlageHoraire.getId();
        fichePlageHoraire = new FichePlageHoraire(editTextNom.getText().toString(),
                accesHoraires.getCategorieById(spinnerCategorie.getSelectedItemId() + "").getId(),
                editTextInformation.getText().toString(),
                imagePath);
        accesHoraires.updateFichePlageHoraire(fichePlageHoraire, identifiant);
    }

    /**
     * permet la selection des horaire d'un jour
     */
    public void onClickjour(View view){
        // on recupere le bouton qui a été cliquer
        Button btn = view.findViewById(view.getId());

        //EnsemblePlageHoraire ensemblePlageHoraire = accesHoraires.getEnsemblePlageHoraireById()

        // on créer la boite de dialogue
        final View boiteSaisie = getLayoutInflater().inflate(R.layout.saisie_horaire_jour, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(btn.getText())
                .setView(boiteSaisie)
                .setPositiveButton(getResources().getString(R.string.bouton_positif), null)
                .setNegativeButton(getResources().getString(R.string.bouton_negatif), null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialoginterface) {
                RadioGroup radioGroup = dialog.findViewById(R.id.groupe_horaire);
                TextView textViewMatin = dialog.findViewById(R.id.textView_matin);
                TextView textViewAprem = dialog.findViewById(R.id.textView_aprem);
                CheckBox chbFermeMatin = dialog.findViewById(R.id.chb_ferme_matin);
                TimePicker tpMatinDebut = dialog.findViewById(R.id.tp_matin_debut);
                TimePicker tpMatinFin = dialog.findViewById(R.id.tp_matin_fin);
                CheckBox chbFermeAprem = dialog.findViewById(R.id.chb_ferme_aprem);
                TimePicker tpApremDebut = dialog.findViewById(R.id.tp_aprem_debut);
                TimePicker tpApremFin = dialog.findViewById(R.id.tp_aprem_fin);
                Button buttonEffacer = dialog.findViewById(R.id.btn_effacer);

                tpMatinDebut.setIs24HourView(true);
                tpMatinFin.setIs24HourView(true);
                tpApremDebut.setIs24HourView(true);
                tpApremFin.setIs24HourView(true);

                PlageHoraire plageHoraireMatin;
                PlageHoraire plageHoraireSoir;
                Jour jour = accesHoraires.getJourByNom(btn.getText().toString());
                int position = Integer.parseInt(jour.getId()) - 1;

                boolean dejaEnregistre = ensemblesPlagesHoraire[position] == null ? false : true;
                ensemblesPlagesHoraire[position] = dejaEnregistre ? accesHoraires.getEnsemblePlageHoraireByIdFicheAndJour(getIdLastFiche(),
                        jour.getId()) : null;

                if (dejaEnregistre) {
                    EnsemblePlageHoraire ensemblePlageHoraire = ensemblesPlagesHoraire[position];
                    plageHoraireMatin = accesHoraires.getPlageHoraireById(ensemblePlageHoraire.getIdPlageHoraireMatin());
                    plageHoraireSoir = accesHoraires.getPlageHoraireById(ensemblePlageHoraire.getIdPlageHoraireSoir());
                    String[] tabHoraireMatinDebut = plageHoraireMatin.getHoraireOuverture().split(":");
                    String[] tabHoraireMatinFin = plageHoraireMatin.getHoraireFermeture().split(":");
                    int heureHoraireMatinDebut = Integer.parseInt(tabHoraireMatinDebut[0]);
                    int minuteHoraireMatinDebut = Integer.parseInt(tabHoraireMatinDebut[1]);
                    int heureHoraireMatinFin = Integer.parseInt(tabHoraireMatinFin[0]);
                    int minuteHoraireMatinFin = Integer.parseInt(tabHoraireMatinFin[1]);
                    tpMatinDebut.setHour(heureHoraireMatinDebut);
                    tpMatinDebut.setMinute(minuteHoraireMatinDebut);
                    tpMatinFin.setHour(heureHoraireMatinFin);
                    tpMatinFin.setMinute(minuteHoraireMatinFin);
                    chbFermeMatin.setChecked(plageHoraireMatin.getEstFerme() == 0 ? false : true);
                    if (plageHoraireSoir.getHoraireFermeture() != null) {
                        radioGroup.check(R.id.option_2_plage_h);
                        textViewMatin.setText(getString(R.string.horaire_matin));
                        textViewAprem.setVisibility(View.VISIBLE);
                        tpApremDebut.setVisibility(View.VISIBLE);
                        tpApremFin.setVisibility(View.VISIBLE);
                        chbFermeAprem.setVisibility(View.VISIBLE);
                        chbFermeAprem.setChecked(plageHoraireSoir.getEstFerme() == 0 ? false : true);

                        String[] tabHoraireSoirDebut = plageHoraireSoir.getHoraireOuverture().split(":");
                        String[] tabHoraireSoirFin = plageHoraireSoir.getHoraireFermeture().split(":");
                        int heureHoraireSoirDebut = Integer.parseInt(tabHoraireSoirDebut[0]);
                        int minuteHoraireSoirDebut = Integer.parseInt(tabHoraireSoirDebut[1]);
                        int heureHoraireSoirFin = Integer.parseInt(tabHoraireSoirFin[0]);
                        int minuteHoraireSoirFin = Integer.parseInt(tabHoraireSoirFin[1]);
                        tpApremDebut.setHour(heureHoraireSoirDebut);
                        tpApremDebut.setMinute(minuteHoraireSoirDebut);
                        tpApremFin.setHour(heureHoraireSoirFin);
                        tpApremFin.setMinute(minuteHoraireSoirFin);
                    }
                } else {
                    System.out.println("Nouvelle initialisation");
                }

                buttonEffacer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dejaEnregistre) {
                            System.out.println("Id de l'ensemble : " + ensemblesPlagesHoraire[position].getId());
                            accesHoraires.deleteEnsemblePlageHoraire(ensemblesPlagesHoraire[position].getId());
                            ensemblesPlagesHoraire[position] = null;
                        }

                        // when everything is ok
                        toogleStyleBoutonSemaine(btn, false);
                        dialog.dismiss();
                    }
                });

                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (radioGroup.getCheckedRadioButtonId()) {
                            case R.id.option_jour_entier:
                                textViewMatin.setText(getString(R.string.colonne_horaire));
                                textViewAprem.setVisibility(View.INVISIBLE);
                                tpApremDebut.setVisibility(View.INVISIBLE);
                                tpApremFin.setVisibility(View.INVISIBLE);
                                chbFermeAprem.setVisibility(View.INVISIBLE);
                                if (dejaEnregistre) {
                                    EnsemblePlageHoraire ensemblePlageHoraire = ensemblesPlagesHoraire[position];
                                    PlageHoraire plageHoraire = accesHoraires.getPlageHoraireById(ensemblePlageHoraire.getIdPlageHoraireSoir());
                                    plageHoraire.setHoraireOuverture(null);
                                    plageHoraire.setHoraireFermeture(null);
                                    accesHoraires.updatePlageHoraire(plageHoraire, ensemblePlageHoraire.getIdPlageHoraireSoir());
                                }
                                break;
                            case R.id.option_2_plage_h:
                                textViewMatin.setText(getString(R.string.horaire_matin));
                                textViewAprem.setVisibility(View.VISIBLE);
                                tpApremDebut.setVisibility(View.VISIBLE);
                                tpApremFin.setVisibility(View.VISIBLE);
                                chbFermeAprem.setVisibility(View.VISIBLE);
                                break;
                        }
                    }
                });


                // on ajout un écouteur sur le boutons OK
                Button boutonPositif = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                boutonPositif.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // on change le style du bouton pour qu'il soit actif
                        toogleStyleBoutonSemaine(btn, true);
                        PlageHoraire plageHoraireMatin;
                        PlageHoraire plageHoraireSoir;
                        Jour jour = accesHoraires.getJourByNom(btn.getText().toString());
                        int position = Integer.parseInt(jour.getId()) - 1;

                        switch (radioGroup.getCheckedRadioButtonId()) {

                            case R.id.option_jour_entier:
                                String horaireDebut = tpMatinDebut.getHour() + ":" + tpMatinDebut.getMinute();
                                String horaireFin = tpMatinFin.getHour() + ":" + tpMatinFin.getMinute();
                                int estFerme = chbFermeMatin.isChecked() ? 1 : 0;
                                plageHoraireMatin = new PlageHoraire(horaireDebut, horaireFin, estFerme);

                                if (!dejaEnregistre) {
                                    System.out.println("Ajout plage horaire");
                                    accesHoraires.addPlageHoraire(plageHoraireMatin);
                                    Cursor cursor = accesHoraires.getCursorAllPlageHoraire();
                                    cursor.moveToLast();
                                    plageHoraireMatin = accesHoraires.getPlageHoraireById(cursor.getString(0));
                                    ensemblesPlagesHoraire[position] = new EnsemblePlageHoraire(plageHoraireMatin.getId(),
                                            jour.getId(),
                                            getIdLastFiche());
                                    accesHoraires.addEnsemblePlageHoraire(ensemblesPlagesHoraire[position]);
                                } else {
                                    accesHoraires.updatePlageHoraire(plageHoraireMatin,
                                            ensemblesPlagesHoraire[position].getIdPlageHoraireMatin());

                                    accesHoraires.updateEnsemblePlageHoraire(ensemblesPlagesHoraire[position],
                                            ensemblesPlagesHoraire[position].getId());

                                }

                                break;
                            case R.id.option_2_plage_h:
                                String horaireMatinDebut = tpMatinDebut.getHour() + ":" + tpMatinDebut.getMinute();
                                String horaireMatinFin = tpMatinFin.getHour() + ":" + tpMatinFin.getMinute();
                                String horaireSoirDebut = tpApremDebut.getHour() + ":" + tpApremDebut.getMinute();
                                String horaireSoirFin = tpApremFin.getHour() + ":" + tpApremFin.getMinute();
                                int estFermeMatin = chbFermeMatin.isChecked() ? 1 : 0;
                                int estFermeSoir = chbFermeAprem.isChecked() ? 1 : 0;
                                plageHoraireMatin = new PlageHoraire(horaireMatinDebut, horaireMatinFin, estFermeMatin);
                                plageHoraireSoir = new PlageHoraire(horaireSoirDebut, horaireSoirFin, estFermeSoir);

                                if (!dejaEnregistre) {
                                    accesHoraires.addPlageHoraire(plageHoraireMatin);
                                    Cursor cursor = accesHoraires.getCursorAllPlageHoraire();
                                    cursor.moveToLast();
                                    plageHoraireMatin = accesHoraires.getPlageHoraireById(cursor.getString(0));

                                    accesHoraires.addPlageHoraire(plageHoraireSoir);
                                    cursor = accesHoraires.getCursorAllPlageHoraire();
                                    cursor.moveToLast();
                                    plageHoraireSoir = accesHoraires.getPlageHoraireById(cursor.getString(0));
                                    ensemblesPlagesHoraire[position] = new EnsemblePlageHoraire(plageHoraireMatin.getId(),
                                            plageHoraireSoir.getId(),
                                            jour.getId(),
                                            getIdLastFiche());
                                    accesHoraires.addEnsemblePlageHoraire(ensemblesPlagesHoraire[position]);

                                } else {
                                    accesHoraires.updatePlageHoraire(plageHoraireMatin,
                                            ensemblesPlagesHoraire[position].getIdPlageHoraireMatin());
                                    if (ensemblesPlagesHoraire[position].getIdPlageHoraireSoir() == null) {
                                        accesHoraires.addPlageHoraire(plageHoraireSoir);
                                        Cursor cursor = accesHoraires.getCursorAllPlageHoraire();
                                        cursor.moveToLast();
                                        plageHoraireSoir.setId(cursor.getString(0));
                                        ensemblesPlagesHoraire[position].setIdPlageHoraireSoir(plageHoraireSoir.getId());
                                    } else {
                                        accesHoraires.updatePlageHoraire(plageHoraireSoir,
                                                ensemblesPlagesHoraire[position].getIdPlageHoraireSoir());
                                    }
                                    accesHoraires.updateEnsemblePlageHoraire(ensemblesPlagesHoraire[position],
                                            ensemblesPlagesHoraire[position].getId());
                                }

                                break;
                        }

                        //when everything is ok
                        dialog.dismiss();
                    }
                });
            }
        });

        dialog.show();
    }

    /**
     * change le style du bouton selectionné
     * @param button
     */
    private void toogleStyleBoutonSemaine(Button button, boolean isActive) {
        if (button.getCurrentTextColor() == getColor(R.color.primary) && isActive) {
            button.setTextColor(getColor(R.color.white));
            button.setBackgroundColor(getColor(R.color.secondary));
        } else if (button.getCurrentTextColor() == getColor(R.color.white) && !isActive) {
            button.setTextColor(getColor(R.color.primary));
            button.setBackgroundColor(getColor(R.color.white));
        }
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

    /**
     * Retourne l'identifiant de la derniere fiche créée
     * @return l'identifiant
     */
    private String getIdLastFiche() {
        Cursor cursor = accesHoraires.getCursorAllFichePlageHoraire();
        cursor.moveToLast();
        return accesHoraires.getFichePlageHoraireById(cursor.getString(0)).getId();
    }
}
