package com.gimmecocktail.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gimmecocktail.R;

public final class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);
        setRoutes();
    }

    private void setRoutes() {
        Button byName = findViewById(R.id.button_by_name);
        Button byIngredient = findViewById(R.id.button_by_ingredient);
        Button random = findViewById(R.id.button_discover_random);
        Button favourites = findViewById(R.id.button_favourites);
        startOnClick(byName, SearchByNameActivity.class);
        startOnClick(byIngredient, SearchByIngredientActivity.class);
        startOnClick(random, SearchRandomActivity.class);
        startOnClick(favourites, FavouritesActivity.class);
    }

    private <T> void startOnClick(Button button, final Class<T> activityClass) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), activityClass);
                startActivity(intent);
            }
        });
    }
}
