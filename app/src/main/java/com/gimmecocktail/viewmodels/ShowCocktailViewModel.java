package com.gimmecocktail.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gimmecocktail.Cocktail;

public class ShowCocktailViewModel extends ViewModel {

    private MutableLiveData<Cocktail> cocktail = new MutableLiveData<>();
    private boolean isFavourite = false;

    public void setCocktail(Cocktail cocktail) {
        this.cocktail.setValue(cocktail);
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
