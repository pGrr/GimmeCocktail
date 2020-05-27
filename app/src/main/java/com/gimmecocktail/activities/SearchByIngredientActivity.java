package com.gimmecocktail.activities;

import android.os.Bundle;

import com.gimmecocktail.R;

public class SearchByIngredientActivity extends AbstractSearchCocktailsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.search_cocktail_by_ingredient_title));
    }

    @Override
    protected void searchCocktails(String ingredient) {
        getModel().searchCocktailsByIngredient(ingredient);
    }

}
