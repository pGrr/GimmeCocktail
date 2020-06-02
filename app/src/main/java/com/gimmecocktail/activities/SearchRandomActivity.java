package com.gimmecocktail.activities;

import androidx.core.content.ContextCompat;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import com.gimmecocktail.http.RequestFactory;
import com.gimmecocktail.model.Cocktail;
import com.gimmecocktail.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Activity that queries the API with for a random-cocktail and shows the result.
 * Whenever the refresh button is pressed, a new random cocktail is requested and showed.
 */
public class SearchRandomActivity extends ShowCocktailActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRefreshButtonObserver();
        // if there was no cocktail passed as extras, request a random cocktail to the API
        if (savedInstanceState == null) {
            setRandomCocktail();
        }
    }

    /**
     * Sends an asynchronous request to the API for a random cocktail,
     * once the result is ready, updates the cocktail mutable live data.
     * If an error occurs, it alerts the user.
     */
    private void setRandomCocktail() {
        RequestFactory.random(getRequestQueue()).observe(new com.gimmecocktail.Observer<Cocktail>() {
            @Override
            public void onResult(Cocktail result) {
                // once the cocktail is ready, update the cocktail mutable live data of model
                getModel().getCocktail().setValue(result);
            }
            @Override
            public void onError(Exception exception) {
                // if an error occurs, alert the user
                Activities.alert(
                        getString(R.string.connection_failed_title),
                        getString(R.string.connection_failed_message),
                        SearchRandomActivity.this,
                        true
                );
            }
        }).send();
    }

    private void setRefreshButtonObserver() {
        final FloatingActionButton button = findViewById(R.id.refresh_random_cocktail_button);
        button.setColorFilter(ContextCompat.getColor(
                SearchRandomActivity.this,
                R.color.colorPrimary),
                PorterDuff.Mode.MULTIPLY);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRandomCocktail();
            }
        });
    }

}
