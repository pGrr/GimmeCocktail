package com.gimmecocktail.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.gimmecocktail.http.CocktailRequestQueue;
import com.gimmecocktail.model.Cocktail;
import com.gimmecocktail.R;
import com.gimmecocktail.databinding.ActivityShowCocktailBinding;
import com.gimmecocktail.http.ThumbnailRequest;
import com.gimmecocktail.model.CocktailQueryMaker;
import com.gimmecocktail.viewmodels.ShowCocktailViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;


public class ShowCocktailActivity extends AppCompatActivity {

    private ActivityShowCocktailBinding binding;
    private ShowCocktailViewModel model;
    private CocktailRequestQueue requestQueue;
    private CocktailQueryMaker queryMaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setModel();
        setModelObserver();
        setFavouriteButtonBehaviour();
        checkIsFavourite();
    }

    public ShowCocktailViewModel getModel() {
        return model;
    }

    public CocktailQueryMaker getQueryMaker() {
        if (queryMaker == null) {
            queryMaker = new CocktailQueryMaker(this);
        }
        return queryMaker;
    }

    public CocktailRequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = new CocktailRequestQueue<>(this, model.getCocktail());
        }
        return requestQueue;
    }

    protected void setModel() {
        this.model = new ViewModelProvider(this).get(ShowCocktailViewModel.class);
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_show_cocktail);
        this.binding.setLifecycleOwner(this);
        if (getIntent().hasExtra("cocktail")) {
            Cocktail cocktail = Objects.requireNonNull(getIntent().getExtras()).getParcelable("cocktail");
            model.getCocktail().setValue(cocktail);
            setThumbnail();
        }
        binding.setCocktail(model.getCocktail().getValue());
    }

    protected void setModelObserver() {
        model.getCocktail().observe(this, new Observer<Cocktail>() {
            @Override
            public void onChanged(Cocktail cocktail) {
                binding.setCocktail(cocktail);
                checkIsFavourite();
                setThumbnail();
                binding.executePendingBindings();
            }
        });
    }

    protected void setFavouriteButtonBehaviour() {
        final FloatingActionButton button = findViewById(R.id.button_favourites);
        setButtonColor(button, R.color.colorPrimary);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFavourite(!model.isFavourite().getValue());
            }
        });
    }

    protected void checkIsFavourite() {
        getQueryMaker().exists(model.getCocktail().getValue().getId(), model.isFavourite());
    }

    protected void setFavourite(boolean isFavourite) {
        final FloatingActionButton button = findViewById(R.id.button_favourites);
        if (isFavourite) {
            getQueryMaker().insertAll(model.getCocktail().getValue());
            button.setImageDrawable(getDrawable(R.drawable.ic_favorite_white_24dp));
        } else if (this.model.isFavourite().getValue()) {
            getQueryMaker().delete(model.getCocktail().getValue());
            button.setImageDrawable(getDrawable(R.drawable.ic_favorite_border_white_24dp));
        }
        this.model.setIsFavourite(isFavourite);
    }

    protected void setButtonColor(FloatingActionButton button, int colorId) {
        button.setColorFilter(
                ContextCompat.getColor(
                        ShowCocktailActivity.this,
                        colorId),
                PorterDuff.Mode.MULTIPLY);
    }

    protected void setThumbnail() {
        ImageView imageView = findViewById(R.id.cocktail_thumbnail);
        getRequestQueue().add(new ThumbnailRequest(
                model.getCocktail().getValue().getThumbnailUrl(),
                this,
                imageView));
    }

}
