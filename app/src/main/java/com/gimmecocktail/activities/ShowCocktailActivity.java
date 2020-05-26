package com.gimmecocktail.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.gimmecocktail.Cocktail;
import com.gimmecocktail.R;
import com.gimmecocktail.databinding.ActivityShowCocktailBinding;
import com.gimmecocktail.http.ImageRequestQueue;
import com.gimmecocktail.viewmodels.SearchViewModel;
import com.gimmecocktail.viewmodels.ShowCocktailViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class ShowCocktailActivity extends AppCompatActivity {

    private ActivityShowCocktailBinding binding;
    private ShowCocktailViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_cocktail);
        // create and bind model
        final ShowCocktailViewModel model = new ViewModelProvider(this).get(ShowCocktailViewModel.class);
        this.model = model;
        // create and set data-binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_cocktail);
        binding.setLifecycleOwner(this);
        if (getIntent().hasExtra("cocktail")) {
            Cocktail cocktail = getIntent().getExtras().getParcelable("cocktail");
            model.getCocktail().setValue(cocktail);
        }
        binding.setCocktail(model.getCocktail().getValue());
        setFavouriteButtonBehaviour();
        ImageView imageView = findViewById(R.id.cocktail_thumbnail);
        new ImageRequestQueue(this, imageView, model.getCocktail().getValue().getThumbnailUrl());
    }


    private void setFavouriteButtonBehaviour() {
        final FloatingActionButton button = (FloatingActionButton)findViewById(R.id.button_favourites);
        button.setColorFilter(getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model.isFavourite()) {
                    model.setFavourite(false);
                    button.setImageDrawable(getDrawable(R.drawable.ic_favorite_border_white_24dp));
                } else {
                    model.setFavourite(true);
                    button.setImageDrawable(getDrawable(R.drawable.ic_favorite_white_24dp));
                }
                button.setColorFilter(getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
            }
        });
    }

}
