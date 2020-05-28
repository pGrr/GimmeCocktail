package com.gimmecocktail.model;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.gimmecocktail.activities.FavouritesActivity;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

public class CocktailQueryMaker {

    private final static String NAME = "cocktails";
    private CocktailDAO daoInstance;
    private ForkJoinPool forkJoinPool;

    public CocktailQueryMaker(Context context) {
        this.forkJoinPool = new ForkJoinPool();
        this.daoInstance = Room.databaseBuilder(context, CocktailDatabase.class, NAME).build().getCocktailDAO();
    }

    public void getAll(final MutableLiveData<List<Cocktail>> result) {
        forkJoinPool.submit(new Runnable() {
            @Override
            public void run() {
                result.setValue(daoInstance.getAll());
            }
        });
    }

    public void exists(final String id, final MutableLiveData<Boolean> result) {
        forkJoinPool.submit(new Runnable() {
            @Override
            public void run() {
                result.setValue(daoInstance.idExists(id));
            }
        });
    }

    public void getById(final String id, final MutableLiveData<Cocktail> result) {
        forkJoinPool.submit(new Runnable() {
            @Override
            public void run() {
                result.setValue(daoInstance.getById(id));
            }
        });
    }

    public void insertAll(final Cocktail ...cocktails) {
        forkJoinPool.submit(new Runnable() {
            @Override
            public void run() {
                daoInstance.insertAll(cocktails);
            }
        });
    }

    public void delete(final Cocktail cocktail) {
        forkJoinPool.submit(new Runnable() {
            @Override
            public void run() {
                daoInstance.delete(cocktail);
            }
        });
    }

}
