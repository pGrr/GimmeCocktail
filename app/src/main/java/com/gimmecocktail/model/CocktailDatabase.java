package com.gimmecocktail.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Cocktail.class}, version = 1, exportSchema = false)
public abstract class CocktailDatabase extends RoomDatabase {

    abstract public CocktailDAO getCocktailDAO();

}
