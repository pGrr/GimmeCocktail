package com.gimmecocktail.activities;

import android.os.Bundle;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.gimmecocktail.Observer;
import com.gimmecocktail.R;
import com.gimmecocktail.http.ApiRequestQueue;
import com.gimmecocktail.http.RequestFactory;
import com.gimmecocktail.model.Cocktail;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity that takes user's input from a search bar, queries the API for cocktails
 * using a search-by-ingredient request and shows the result as cards.
 */
public class SearchByIngredientActivity extends AbstractSearchCocktailsActivity {

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestQueue = new ApiRequestQueue(this);
        // set the title, shown in the top bar
        TextView textView = findViewById(R.id.search_cocktails_title);
        textView.setText(getString(R.string.search_cocktail_by_ingredient_title));
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
        // clear the mutable live data
        getModel().getCocktails().setValue(new ArrayList<Cocktail>());
        // send the request
        RequestFactory.byIngredient(ingredient, requestQueue).observe(new Observer<List<Cocktail>>() {
            @Override
            public void onResult(List<Cocktail> result) {
                // when the cocktail list is ready, update the mutable live data
                getModel().getCocktails().setValue(result);
            }
            @Override
            public void onError(Exception exception) {
                // if an error occurs, alert the user
                Activities.alert(
                        getString(R.string.connection_failed_title),
                        getString(R.string.connection_failed_message),
                        SearchByIngredientActivity.this,
                        true
                );
            }
        }).send();
    }

}
