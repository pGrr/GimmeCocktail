package com.gimmecocktail.activities;

import android.os.Bundle;
import com.gimmecocktail.R;
import com.gimmecocktail.http.ByNameRequest;

/**
 * Activity that takes user's input from a search bar, queries the API
 * using a search-by-name request and shows the result as cocktail cards.
 */
public class SearchByNameActivity extends AbstractSearchCocktailsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.search_cocktail_by_name_title));
    }

    /**
     * Adds a search-by-name request to the API request queue.
     * The request will be sent asynchronously to the api:
     * the passed mutable live data of this activity will be updated with the results.
     *
     * @param name the query parameter
     */
    @Override
    protected void searchCocktails(String name) {
        getRequestQueue().add(new ByNameRequest(name, getModel().getCocktails(), this));
    }

}
