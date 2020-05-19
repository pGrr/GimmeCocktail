package com.gimmecocktail.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.gimmecocktail.Cocktail;
import com.gimmecocktail.http.RequestByIngredient;
import com.gimmecocktail.http.RequestByName;

import java.util.ArrayList;
import java.util.List;

public final class SearchViewModel extends AndroidViewModel {

    private MutableLiveData<List<Cocktail>> cocktails;

    public SearchViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<Cocktail>> getCocktails() {
        if (cocktails == null) {
            cocktails = new MutableLiveData<List<Cocktail>>();
            cocktails.setValue(new ArrayList<Cocktail>());
        }
        return cocktails;
    }

    public void searchCocktailsByName(String name) {
        new RequestByName(this.getApplication(), getCocktails(), name);
    }

    public void searchCocktailsByIngredient(String ingredient) {
        new RequestByIngredient(this.getApplication(), getCocktails(), ingredient);
    }
}
