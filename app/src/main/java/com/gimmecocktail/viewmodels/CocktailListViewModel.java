package com.gimmecocktail.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.gimmecocktail.model.Cocktail;
import java.util.ArrayList;
import java.util.List;

/**
 * A view model that manages a list of Cocktails.
 */
public final class CocktailListViewModel extends ViewModel {

    private MutableLiveData<List<Cocktail>> cocktails;

    /**
     * Gets the cocktail list mutable live data.
     *
     * @return the cocktails
     */
    public MutableLiveData<List<Cocktail>> getCocktails() {
        if (cocktails == null) {
            cocktails = new MutableLiveData<>();
            cocktails.setValue(new ArrayList<Cocktail>());
        }
        return cocktails;
    }
    
}
