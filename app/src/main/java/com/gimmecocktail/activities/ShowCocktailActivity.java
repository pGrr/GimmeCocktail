package com.gimmecocktail.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gimmecocktail.Cocktail;
import com.gimmecocktail.R;
import com.gimmecocktail.databinding.ActivityShowCocktailBinding;
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
    }


    private void setFavouriteButtonBehaviour() {
        final FloatingActionButton button = (FloatingActionButton) findViewById(R.id.button_favourites);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model.isFavourite()) {
                    model.setFavourite(false);
                    button.setImageDrawable(getDrawable(android.R.drawable.btn_star_big_off));
                } else {
                    model.setFavourite(true);
                    button.setImageDrawable(getDrawable(android.R.drawable.btn_star_big_on));
                }
            }
        });
    }

}
