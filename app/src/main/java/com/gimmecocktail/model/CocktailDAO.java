package com.gimmecocktail.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface CocktailDAO {

    @Query("SELECT * FROM Cocktail")
    List<Cocktail> getAll();

    @Query("SELECT * FROM Cocktail WHERE id = :id")
    Cocktail getById(String id);

    @Query("SELECT COUNT(id) FROM Cocktail WHERE id = :id")
    Boolean idExists(String id);

    @Insert
    void insertAll(Cocktail... cocktails);

    @Delete
    void delete(Cocktail cocktail);

}

