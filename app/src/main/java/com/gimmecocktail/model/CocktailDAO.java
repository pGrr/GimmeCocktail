package com.gimmecocktail.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

/**
 * The Cocktail DAO.
 */
@Dao
public interface CocktailDAO {

    /**
     * Gets all the cocktails in the database.
     *
     * @return the list of all cocktails
     */
    @Query("SELECT * FROM cocktail")
    List<Cocktail> getAll();

    /**
     * Gets a Cocktail by its id.
     *
     * @param id the id
     * @return the by id
     */
    @Query("SELECT * FROM Cocktail WHERE id = :id")
    Cocktail getById(String id);

    /**
     * Check a cocktail is saved in the database (i.e. is favourite).
     *
     * @param id the id
     * @return the boolean
     */
    @Query("SELECT COUNT(id) FROM Cocktail WHERE id = :id")
    Boolean idExists(String id);

    /**
     * Insert all given cocktails in the database (i.e. sets them as favourites).
     *
     * @param cocktails the cocktails
     */
    @Insert
    void insertAll(Cocktail... cocktails);

    /**
     * Delete the given Cocktail from the database (i.e. sets it as non-favourite).
     *
     * @param cocktail the cocktail
     */
    @Delete
    void delete(Cocktail cocktail);

}
