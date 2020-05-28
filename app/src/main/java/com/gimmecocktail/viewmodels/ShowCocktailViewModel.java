package com.gimmecocktail.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.gimmecocktail.model.Cocktail;

public class ShowCocktailViewModel extends ViewModel {

    private MutableLiveData<Cocktail> cocktail;
    private MutableLiveData<Boolean> isFavourite;

    public ShowCocktailViewModel() {
        super();
    }

    public MutableLiveData<Cocktail> getCocktail() {
        if (cocktail == null) {
            cocktail = new MutableLiveData<>();
        }
        return cocktail;
    }

    public void setCocktail(Cocktail cocktail) {
        this.cocktail.setValue(cocktail);
    }

    public MutableLiveData<Boolean> isFavourite() {
        if (isFavourite == null) {
            isFavourite = new MutableLiveData<>();
            isFavourite.setValue(false);
        }
        return isFavourite;
    }

    public void setIsFavourite(boolean isFavourite) {
        this.isFavourite.setValue(isFavourite);
    }

}
