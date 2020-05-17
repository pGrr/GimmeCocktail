package com.gimmecocktail;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.gimmecocktail.http.RequestByIngredient;

import java.util.List;

public final class SearchViewModel extends AndroidViewModel {

    private MutableLiveData<List<Cocktail>> cocktails;

    public SearchViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<Cocktail>> getCocktails() {
        if (cocktails == null) {
            cocktails = new MutableLiveData<List<Cocktail>>();
        }
        return cocktails;
    }

    private void searchCocktailsByName(String name) {
        new RequestByIngredient(this.getApplication(), getCocktails(), name);
    }

    private void searchCocktailsByIngredient(String ingredient) {
        new RequestByIngredient(this.getApplication(), getCocktails(), ingredient);
    }
}
