package com.gimmecocktail.activities;

import android.os.Bundle;

import com.gimmecocktail.Observer;
import com.gimmecocktail.R;
import com.gimmecocktail.http.CocktailListRequest;
import com.gimmecocktail.model.Cocktail;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity that takes user's input from a search bar, queries the API
 * using a search-by-ingredient request and shows the result as cocktail cards.
 */
public class SearchByIngredientActivity extends AbstractSearchCocktailsActivity {

    private final static String BASE_REQUEST_URL = ;

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
        CocktailListRequest request = new CocktailListRequest(BASE_REQUEST_URL + ingredient);
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
                        SearchByIngredientActivity.this,
                        true
                );
            }
        });
        request.execute();
    }

}
