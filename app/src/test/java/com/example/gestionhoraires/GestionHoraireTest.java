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

    @Before
    public  void setUp() {

        accesHoraire = new HoraireDAO(ApplicationProvider.getApplicationContext());
        accesHoraire.open();
    }

    @Test
    public void testAddCategorieWithWrongIdLocalisation() {
        Categorie categorie = new Categorie("nom", "100", 0);
        accesHoraire.addCategorie(categorie);
        Cursor cursor = accesHoraire.getCursorAllCategoriePlageHoraire();
        cursor.moveToLast();
        assertNotEquals("nom", cursor.getString(accesHoraire.CATEGORIE_NUM_COLONNE_NOM));
    }

    @Test
    public void testAddCategorieWithCorrectLocalisation() {
        Categorie categorie = new Categorie("nom", "1", 0);
        accesHoraire.addCategorie(categorie);
        Cursor cursor = accesHoraire.getCursorAllCategoriePlageHoraire();
        cursor.moveToLast();
        assertEquals("nom", cursor.getString(accesHoraire.CATEGORIE_NUM_COLONNE_NOM));
    }

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

    @Test
    public void testNoConflictBetweenCategorieAndLocalisation() {
        Localisation localisation = new Localisation("AAAAA");
        accesHoraire.addLocalisation(localisation);
        Cursor cursor = accesHoraire.getCursorAllLocalisation();
        cursor.moveToLast();
        assertEquals(false, accesHoraire.conflictWithCategorie(cursor.getString(0)));
    }

    @Test
    public void testAddCategorieWithWrongIsPonctuel() {
        Categorie categorie = new Categorie("aNewOne", "1", 2);
        accesHoraire.addCategorie(categorie);
        Cursor cursor = accesHoraire.getCursorAllCategorie();
        cursor.moveToLast();
        assertNotEquals("aNewOne", cursor.getString(2));
    }

    @Test
    public void testAddCategorieWithCorrectIsPonctuel() {
        Categorie categorie = new Categorie("aNewOne", "1", 0);
        accesHoraire.addCategorie(categorie);
        Cursor cursor = accesHoraire.getCursorAllCategorie();
        cursor.moveToLast();
        assertEquals("aNewOne", cursor.getString(2));
    }
}
