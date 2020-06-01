package com.gimmecocktail.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.gimmecocktail.model.Cocktail;

/**
 * A view model that manages a single Cocktail.
 */
public class CocktailViewModel2 extends ViewModel {

    private MutableLiveData<Cocktail> cocktail;
    private MutableLiveData<Boolean> isFavourite;

    /**
     * Gets the cocktail mutable live data.
     *
     * @return the cocktail mutable live data
     */
    public MutableLiveData<Cocktail> getCocktail() {
        if (cocktail == null) {
            cocktail = new MutableLiveData<>();
        }
        return cocktail;
    }

    /**
     * Returns a boolean mutable live data representing the is-favourite status of the cocktail.
     *
     * @return the boolean is-favourite mutable live data
     */
    public MutableLiveData<Boolean> isFavourite() {
        if (isFavourite == null) {
            isFavourite = new MutableLiveData<>();
        }
        return isFavourite;
    }

}
