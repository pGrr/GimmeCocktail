package com.example.gimmecocktail;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public final class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRoutes();
    }

    private void setRoutes() {
        Button byName = (Button) findViewById(R.id.button_by_name);
        Button byIngredient = (Button) findViewById(R.id.button_by_ingredient);
        Button random = (Button) findViewById(R.id.button_discover_random);
        Button favourites = (Button) findViewById(R.id.button_favourites);
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
