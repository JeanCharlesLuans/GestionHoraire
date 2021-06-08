package com.example.gestionhoraires;

import android.database.Cursor;

import androidx.test.core.app.ApplicationProvider;

import com.example.gestionhoraires.beans.Categorie;
import com.example.gestionhoraires.beans.Localisation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Method;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
public class GestionHoraireTest {

    @Mock
    private HoraireDAO accesHoraire;

    /**
     * Initialisation
     */
    @Before
    public  void setUp() {

        accesHoraire = new HoraireDAO(ApplicationProvider.getApplicationContext());
        accesHoraire.open();
    }

    /**
     * Ajout d'une catégorie avec un identifiant localisation incorrect
     */
    @Test
    public void testAddCategorieWithWrongIdLocalisation() {
        Categorie categorie = new Categorie("nom", "100", 0);
        accesHoraire.addCategorie(categorie);
        Cursor cursor = accesHoraire.getCursorAllCategoriePlageHoraire();
        cursor.moveToLast();
        assertNotEquals("nom", cursor.getString(accesHoraire.CATEGORIE_NUM_COLONNE_NOM));
    }

    /**
     * Ajout d'une catégorie avec un identifiant de localisation correct
     */
    @Test
    public void testAddCategorieWithCorrectLocalisation() {
        Categorie categorie = new Categorie("nom", "1", 0);
        accesHoraire.addCategorie(categorie);
        Cursor cursor = accesHoraire.getCursorAllCategoriePlageHoraire();
        cursor.moveToLast();
        assertEquals("nom", cursor.getString(accesHoraire.CATEGORIE_NUM_COLONNE_NOM));
    }

    /**
     * Conflit entre une catégorie est une localisation, autrement dit qu'il existe une catégorie
     * pour une localisation
     */
    @Test
    public void testConflictBetweenCategorieAndLocalisation() {
        Localisation localisation = new Localisation("France");
        accesHoraire.addLocalisation(localisation);
        Cursor cursor = accesHoraire.getCursorAllLocalisation();
        cursor.moveToLast();
        Categorie categorie = new Categorie("NewCategory", cursor.getString(0), 0);
        accesHoraire.addCategorie(categorie);
        assertEquals(true, accesHoraire.conflictWithCategorie(cursor.getString(0)));
    }

    /**
     * Pas de conflit entre entre une catégorie et une localisation, autrement dit qu'il n'existe
     * pas de catégorie pour une localisation
     */
    @Test
    public void testNoConflictBetweenCategorieAndLocalisation() {
        Localisation localisation = new Localisation("AAAAA");
        accesHoraire.addLocalisation(localisation);
        Cursor cursor = accesHoraire.getCursorAllLocalisation();
        cursor.moveToLast();
        assertEquals(false, accesHoraire.conflictWithCategorie(cursor.getString(0)));
    }

    /**
     * Ajout d'une catégorie avec un identifiant isPonctuel différent de 0 ou 1
     */
    @Test
    public void testAddCategorieWithWrongIsPonctuel() {
        Categorie categorie = new Categorie("aNewOne", "1", 2);
        accesHoraire.addCategorie(categorie);
        Cursor cursor = accesHoraire.getCursorAllCategorie();
        cursor.moveToLast();
        assertNotEquals("aNewOne", cursor.getString(2));
    }

    /**
     * Ajout d'une catégorie avec un identifiant isPonctuel correct
     */
    @Test
    public void testAddCategorieWithCorrectIsPonctuel() {
        Categorie categorie = new Categorie("aNewOne", "1", 0);
        accesHoraire.addCategorie(categorie);
        Cursor cursor = accesHoraire.getCursorAllCategorie();
        cursor.moveToLast();
        assertEquals("aNewOne", cursor.getString(2));
    }
}
