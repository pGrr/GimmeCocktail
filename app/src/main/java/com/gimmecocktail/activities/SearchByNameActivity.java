package com.gimmecocktail.activities;

import android.os.Bundle;
import com.gimmecocktail.R;
import com.gimmecocktail.http.ByNameRequest;

public class SearchByNameActivity extends AbstractSearchCocktailsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.search_cocktail_by_name_title));
    }

    @Override
    protected void searchCocktails(String name) {
        getRequestQueue().add(new ByNameRequest(name, getModel().getCocktails()));
    }

}
