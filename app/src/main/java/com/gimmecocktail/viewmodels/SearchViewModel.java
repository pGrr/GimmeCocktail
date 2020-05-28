package com.gimmecocktail.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.gimmecocktail.model.Cocktail;
import java.util.ArrayList;
import java.util.List;

public final class SearchViewModel extends AndroidViewModel {

    private MutableLiveData<List<Cocktail>> cocktails;

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

}
