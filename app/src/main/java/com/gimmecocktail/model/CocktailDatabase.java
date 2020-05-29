package com.gimmecocktail.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * The Cocktail database class, providing access to the Cocktail DAO.
 */
@Database(entities = {Cocktail.class}, version = 1, exportSchema = false)
public abstract class CocktailDatabase extends RoomDatabase {

    /**
     * Gets the cocktail DAO.
     *
     * @return the cocktail dao
     */
    abstract public CocktailDAO getCocktailDAO();

}
