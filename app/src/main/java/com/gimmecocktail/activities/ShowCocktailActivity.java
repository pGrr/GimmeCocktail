package com.gimmecocktail.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.gimmecocktail.Cocktail;
import com.gimmecocktail.R;
import com.gimmecocktail.databinding.ActivityShowCocktailBinding;
import com.gimmecocktail.http.ThumbnailRequest;
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
        model.getRequestQueue().add(new ThumbnailRequest(
                model.getCocktail().getValue().getThumbnailUrl(),
                this,
                imageView));
    }


    private void setFavouriteButtonBehaviour() {
        final FloatingActionButton button = findViewById(R.id.button_favourites);
        button.setColorFilter(
                ContextCompat.getColor(
                        ShowCocktailActivity.this,
                        R.color.colorPrimary),
                PorterDuff.Mode.MULTIPLY);        button.setOnClickListener(new View.OnClickListener() {
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
                                ShowCocktailActivity.this,
                                R.color.colorPrimary),
                        PorterDuff.Mode.MULTIPLY);
            }
        });
    }

}
