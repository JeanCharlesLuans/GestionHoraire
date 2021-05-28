package com.example.gestionhoraires;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PlageHoraireActivity extends AppCompatActivity {

    /** Identifiant de l'intention pour l'acces a la camera */
    private static final int TAKE_PICTURE = 1;

    /** Identifiant de l'intention pour l'acces a la gallerie d'image */
    private static final int PICK_IMAGE = 2;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plage_horaire);

        // accès au DAO
        accesHoraires = new HoraireDAO(this);
        accesHoraires.open();

        maBarreOutil = findViewById(R.id.plage_horaire_tool_bar);
        setSupportActionBar(maBarreOutil);
        maBarreOutil.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        maBarreOutil.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retour();
            }
        });

        // on récupere toutes les widgets
        editTextNom = findViewById(R.id.editText_nom);
        spinnerLocalisation = findViewById(R.id.spinner_localisation);
        spinnerCategorie = findViewById(R.id.spinner_categorie);
        editTextInformation = findViewById(R.id.editText_info);
        // edit Text pour présenter les horaire du jour courant
        editTextMatin = findViewById(R.id.editText_matin);
        editTextAprem = findViewById(R.id.editText_aprem);
        imageView = findViewById(R.id.imageView);

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
                    ajouterPlageHoraire();
                }
            }
        );
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
     * ajoute la plage horaire a la base de donner et retourne a l'activité principale
     */
    public void ajouterPlageHoraire() {
        // TODO ajout plage horaire
        // when everything is ok
        retour();
    }

    /**
     * permet la selection des horaire d'un jour
     */
    public void onClickjour(View view){
        // on recupere le bouton qui a été cliquer
        Button btn = view.findViewById(view.getId());

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

                buttonEffacer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO effacer

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
                        // TODO Action ajout horaire jour semaine

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
        }
    }
}
