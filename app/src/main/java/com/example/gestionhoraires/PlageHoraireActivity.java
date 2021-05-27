package com.example.gestionhoraires;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

    /**
     * tableaux contenant les différent boutons de la semaine,
     * le lundi commencant a l'index 0 et le dimanche finissant a l'index 6
     * */
    private Button[] boutonSemaine;

    // TODO Add image global

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
        imageView = findViewById(R.id.imageView);
        // boutons de la semaine
        boutonSemaine = new Button[7];
        boutonSemaine[0] = findViewById(R.id.btn_lundi);
        boutonSemaine[1] = findViewById(R.id.btn_mardi);
        boutonSemaine[2] = findViewById(R.id.btn_mercredi);
        boutonSemaine[3] = findViewById(R.id.btn_jeudi);
        boutonSemaine[4] = findViewById(R.id.btn_vendredi);
        boutonSemaine[5] = findViewById(R.id.btn_samedi);
        boutonSemaine[6] = findViewById(R.id.btn_dimanche);
        // edit Text pour présenter les horaire du jour courant
        editTextMatin = findViewById(R.id.editText_matin);
        editTextAprem = findViewById(R.id.editText_aprem);

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
        // TODO Gallery
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
        changerStyleBoutonSemaine(view);
        // TODO methode(s)?
    }

    private void changerStyleBoutonSemaine(View view) {
        Button btn = view.findViewById(view.getId());
        btn.setTextColor(404040);
    }

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
