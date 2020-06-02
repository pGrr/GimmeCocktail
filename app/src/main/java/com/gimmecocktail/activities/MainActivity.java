package com.gimmecocktail.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.gimmecocktail.R;

/**
 * The Main activity.
 */
public final class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRoutes();
    }

    private void setRoutes() {
        Button byName = findViewById(R.id.button_by_name);
        Button byIngredient = findViewById(R.id.button_by_ingredient);
        Button random = findViewById(R.id.button_discover_random);
        Button favourites = findViewById(R.id.button_favourites);
        startOnClick(byName, SearchByNameActivity.class, R.layout.activity_search_cocktails);
        startOnClick(byIngredient, SearchByIngredientActivity.class, R.layout.activity_search_cocktails);
        startOnClick(random, SearchRandomActivity.class, R.layout.activity_search_random);
        startOnClick(favourites, FavouritesActivity.class, R.layout.activity_favourites);
    }

    private <T> void startOnClick(final Button button, final Class<T> activityClass, final int layoutId) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), activityClass);
                intent.putExtra("layoutId", layoutId);
                startActivity(intent);
            }
        });
    }
}
