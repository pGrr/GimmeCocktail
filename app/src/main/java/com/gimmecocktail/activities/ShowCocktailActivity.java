package com.gimmecocktail.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;

import com.gimmecocktail.R;
import com.gimmecocktail.fragments.CocktailFragment;
import com.gimmecocktail.model.Cocktail;
import com.gimmecocktail.viewmodels.CocktailViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.Objects;

/**
 * Activity that shows a Cocktail, provided by explicit intent extras.
 */
public class ShowCocktailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_cocktail);
    }

}
