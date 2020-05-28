package com.gimmecocktail.activities;

import android.os.Bundle;
import androidx.lifecycle.MutableLiveData;
import com.gimmecocktail.viewmodels.SearchViewModel;

public class FavouritesActivity extends AbstractSearchCocktailsActivity {

    private MutableLiveData<Boolean> isFavourite;
    private SearchViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchCocktails(null);
    }

    @Override
    protected void searchCocktails(String query) {
        getQueryMaker().getAll(model.getCocktails());
    }

}
