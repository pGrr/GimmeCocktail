package com.gimmecocktail.activities;

import android.os.Bundle;
import com.gimmecocktail.R;
import com.gimmecocktail.http.ByIngredientRequest;
import com.gimmecocktail.model.Cocktail;
import java.util.ArrayList;

public class SearchByIngredientActivity extends AbstractSearchCocktailsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.search_cocktail_by_ingredient_title));
    }

    @Override
    protected void searchCocktails(String ingredient) {
        getModel().getCocktails().setValue(new ArrayList<Cocktail>());
        getRequestQueue().add(new ByIngredientRequest(
                ingredient,
                getModel().getCocktails(),
                getRequestQueue()));
    }

}
