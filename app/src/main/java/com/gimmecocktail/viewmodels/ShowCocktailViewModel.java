package com.gimmecocktail.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.gimmecocktail.Cocktail;
import com.gimmecocktail.http.CocktailRequestQueue;
import com.gimmecocktail.http.OneRandomRequest;

public class ShowCocktailViewModel extends AndroidViewModel {

    private MutableLiveData<Cocktail> cocktail = new MutableLiveData<>();
    private boolean isFavourite = false;
    private CocktailRequestQueue<Cocktail> requestQueue;

    public ShowCocktailViewModel(@NonNull Application application) {
        super(application);
    }

    public void setCocktail(Cocktail cocktail) {
        this.cocktail.setValue(cocktail);
    }

    public void setRandomCocktail() {
        getRequestQueue().add(new OneRandomRequest(getCocktail()));
    }

    public CocktailRequestQueue getRequestQueue() {
        if (requestQueue == null) {

            requestQueue = new CocktailRequestQueue(getApplication().getApplicationContext(), getCocktail());
        }
        return requestQueue;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean isFavourite) {
        this.isFavourite = isFavourite;
    }

    public MutableLiveData<Cocktail> getCocktail() {
        return cocktail;
    }
}
