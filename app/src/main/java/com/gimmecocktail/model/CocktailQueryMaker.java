package com.gimmecocktail.model;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class CocktailQueryMaker {

    private final static String NAME = "cocktails";
    private CocktailDAO daoInstance;
    private ForkJoinPool forkJoinPool;

    public CocktailQueryMaker(Context context) {
        this.forkJoinPool = new ForkJoinPool();
        this.daoInstance = Room.databaseBuilder(context, CocktailDatabase.class, NAME).build().getCocktailDAO();
    }

    public void getAll(final MutableLiveData<List<Cocktail>> result) {
        forkJoinPool.execute(new Runnable() {
            @Override
            public void run() {
                result.postValue(daoInstance.getAll());
            }
        });
    }

    public void exists(final String id, final MutableLiveData<Boolean> result) {
        forkJoinPool.submit(new Runnable() {
            @Override
            public void run() {
                result.postValue(daoInstance.idExists(id));
            }
        });
    }

    public void getById(final String id, final MutableLiveData<Cocktail> result) {
        forkJoinPool.submit(new Runnable() {
            @Override
            public void run() {
                result.postValue(daoInstance.getById(id));
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
