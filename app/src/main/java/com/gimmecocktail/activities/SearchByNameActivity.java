package com.gimmecocktail.activities;

import android.os.Bundle;

import com.gimmecocktail.Observer;
import com.gimmecocktail.R;
import com.gimmecocktail.http.CocktailListRequest;
import com.gimmecocktail.model.Cocktail;

import java.util.List;

/**
 * Activity that takes user's input from a search bar, queries the API
 * using a search-by-name request and shows the result as cocktail cards.
 */
public class SearchByNameActivity extends AbstractSearchCocktailsActivity {

    private static final String BASE_REQUEST_URL = ;

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
        CocktailListRequest request = new CocktailListRequest(BASE_REQUEST_URL + name);
        request.observe(new Observer<List<Cocktail>>() {
            @Override
            public void onResult(List<Cocktail> result) {
                getModel().getCocktails().setValue(result);
            }

            @Override
            public void onError(Exception exception) {
                Activities.alert(
                        getString(R.string.connection_failed_title),
                        getString(R.string.connection_failed_message),
                        SearchByNameActivity.this,
                        true
                );
            }
        });
        request.execute();
    }

}
