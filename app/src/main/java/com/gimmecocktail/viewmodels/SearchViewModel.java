package com.gimmecocktail.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.gimmecocktail.Cocktail;
import com.gimmecocktail.http.ByIngredientRequest;
import com.gimmecocktail.http.ByNameRequest;
import com.gimmecocktail.http.CocktailRequestQueue;
import java.util.ArrayList;
import java.util.List;

public final class SearchViewModel extends AndroidViewModel {

    private MutableLiveData<List<Cocktail>> cocktails;
    private CocktailRequestQueue requestQueue;

    public SearchViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<Cocktail>> getCocktails() {
        if (cocktails == null) {
            cocktails = new MutableLiveData<>();
            cocktails.setValue(new ArrayList<Cocktail>());
        }
        return cocktails;
    }

    public CocktailRequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = new CocktailRequestQueue(getApplication().getApplicationContext(), getCocktails());
        }
        return requestQueue;
    }

    public void searchCocktailsByName(String name) {
        getRequestQueue().add(new ByNameRequest(name, this.cocktails));
    }

    public void searchCocktailsByIngredient(String ingredient) {
        this.cocktails.setValue(new ArrayList<Cocktail>());
        getRequestQueue().add(new ByIngredientRequest(ingredient, this.cocktails, this.requestQueue));
    }
}
