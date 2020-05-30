package com.gimmecocktail.activities;

import android.os.Bundle;
import com.gimmecocktail.R;
import com.gimmecocktail.http.ByIngredientRequest;
import com.gimmecocktail.model.Cocktail;
import java.util.ArrayList;

/**
 * Activity that takes user's input from a search bar, queries the API
 * using a search-by-ingredient request and shows the result as cocktail cards.
 */
public class SearchByIngredientActivity extends AbstractSearchCocktailsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.search_cocktail_by_ingredient_title));
    }

    /**
     * Adds a search-by-ingredient request to the API request queue.
     * The request will be sent asynchronously to the api:
     * the passed mutable live data of this activity will be updated with the results.
     *
     * @param ingredient the query parameter
     */
    @Override
    protected void searchCocktails(String ingredient) {
        getModel().getCocktails().setValue(new ArrayList<Cocktail>());
        getRequestQueue().add(new ByIngredientRequest(
                ingredient,
                getModel().getCocktails(),
                getRequestQueue(), this));
    }

}
