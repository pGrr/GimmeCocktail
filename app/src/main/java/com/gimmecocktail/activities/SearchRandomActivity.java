package com.gimmecocktail.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.gimmecocktail.Cocktail;
import com.gimmecocktail.R;
import com.gimmecocktail.adapters.CocktailsAdapter;
import com.gimmecocktail.databinding.ActivitySearchRandomBinding;
import com.gimmecocktail.databinding.ActivityShowCocktailBinding;
import com.gimmecocktail.http.ThumbnailRequest;
import com.gimmecocktail.viewmodels.SearchViewModel;
import com.gimmecocktail.viewmodels.ShowCocktailViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class SearchRandomActivity extends AppCompatActivity {

    private ActivitySearchRandomBinding binding;
    private ShowCocktailViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_cocktail);
        // create and bind model
        setModel();
        setModelObserver();
        setRefreshButtonBehaviour();
        setFavouriteButtonBehaviour();
        model.setRandomCocktail();
    }

    private void setModel() {
        this.model = new ViewModelProvider(this).get(ShowCocktailViewModel.class);
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_search_random);
        this.binding.setLifecycleOwner(this);
        this.model.setRandomCocktail();
    }

    private void setModelObserver() {
        this.model.getCocktail().observe(this, new Observer<Cocktail>() {
            @Override
            public void onChanged(Cocktail cocktail) {
                binding.setCocktail(cocktail);
                ImageView imageView = findViewById(R.id.cocktail_thumbnail);
                model.getRequestQueue().add(new ThumbnailRequest(
                        model.getCocktail().getValue().getThumbnailUrl(),
                        SearchRandomActivity.this,
                        imageView));
                binding.executePendingBindings();
            }
        });
    }

    private void setRefreshButtonBehaviour() {
        final FloatingActionButton button = findViewById(R.id.refresh_random_cocktail_button);
        button.setColorFilter(
                ContextCompat.getColor(
                        SearchRandomActivity.this,
                        R.color.colorPrimary),
                PorterDuff.Mode.MULTIPLY);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.setRandomCocktail();
            }
        });

    }

    private void setFavouriteButtonBehaviour() {
        final FloatingActionButton button = findViewById(R.id.button_favourites);
        button.setColorFilter(
                ContextCompat.getColor(
                        SearchRandomActivity.this,
                        R.color.colorPrimary),
                PorterDuff.Mode.MULTIPLY);
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
                button.setColorFilter(
                        ContextCompat.getColor(
                                SearchRandomActivity.this,
                                R.color.colorPrimary),
                        PorterDuff.Mode.MULTIPLY);
            }
        });
    }
}
