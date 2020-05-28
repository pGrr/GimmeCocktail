package com.gimmecocktail.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.List;

@Database(entities = {Cocktail.class}, version = 1, exportSchema = false)
public abstract class CocktailDatabase extends RoomDatabase {

    abstract public CocktailDAO getCocktailDAO();

}
