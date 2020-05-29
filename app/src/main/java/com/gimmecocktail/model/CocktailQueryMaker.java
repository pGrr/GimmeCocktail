package com.gimmecocktail.model;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

/**
 * Provides access to a CocktailDAO instance and handles requests to the database asynchronously.
 */
public class CocktailQueryMaker {

    private final static String NAME = "cocktails";
    private final CocktailDAO daoInstance;
    private final ForkJoinPool forkJoinPool;

    /**
     * Instantiates a new Cocktail query maker.
     *
     * @param context the context
     */
    public CocktailQueryMaker(Context context) {
        this.forkJoinPool = new ForkJoinPool();
        this.daoInstance = Room.databaseBuilder(context, CocktailDatabase.class, NAME).build().getCocktailDAO();
    }

    /**
     * Makes an async query to the database, which will fill the given
     * cocktail-list mutable live data with all cocktails in the database (i.e. the favourites).
     *
     * @param result the cocktail-list mutable live data to be updated with the query-result
     */
    public void getAll(final MutableLiveData<List<Cocktail>> result) {
        forkJoinPool.execute(new Runnable() {
            @Override
            public void run() {
                result.postValue(daoInstance.getAll());
            }
        });
    }

    /**
     * Makes an async query to the database for the cocktail with the given id,
     * and sets the given boolean mutable live data as true if the cocktail was found
     * (e.g. is favourite)
     *
     * @param id     the id of the cocktail
     * @param result the boolean mutable live data reflecting the is-favourite status
     */
    public void exists(final String id, final MutableLiveData<Boolean> result) {
        forkJoinPool.submit(new Runnable() {
            @Override
            public void run() {
                result.postValue(daoInstance.idExists(id));
            }
        });
    }

    /**
     * Makes an async query to the database for the cocktail with the given id,
     * and puts the result in the given cocktail mutable live data.
     *
     * @param id     the id
     * @param result the result
     */
    public void getById(final String id, final MutableLiveData<Cocktail> result) {
        forkJoinPool.submit(new Runnable() {
            @Override
            public void run() {
                result.postValue(daoInstance.getById(id));
            }
        });
    }

    /**
     * Insert all the given cocktails in the database (i.e. sets them as favourite).
     *
     * @param cocktails the cocktails
     */
    public void insertAll(final Cocktail ...cocktails) {
        forkJoinPool.submit(new Runnable() {
            @Override
            public void run() {
                daoInstance.insertAll(cocktails);
            }
        });
    }

    /**
     * Delete the given cocktail from the database (i.e. sets it as non-favourite).
     *
     * @param cocktail the cocktail
     */
    public void delete(final Cocktail cocktail) {
        forkJoinPool.submit(new Runnable() {
            @Override
            public void run() {
                daoInstance.delete(cocktail);
            }
        });
    }

}
