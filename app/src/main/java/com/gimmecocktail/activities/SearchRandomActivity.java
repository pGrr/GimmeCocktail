package com.gimmecocktail.activities;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.gimmecocktail.http.CocktailRequestQueue;
import com.gimmecocktail.http.OneRandomRequest;
import com.gimmecocktail.model.Cocktail;
import com.gimmecocktail.R;
import com.gimmecocktail.databinding.ActivitySearchRandomBinding;
import com.gimmecocktail.http.ThumbnailRequest;
import com.gimmecocktail.model.CocktailQueryMaker;
import com.gimmecocktail.viewmodels.ShowCocktailViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class SearchRandomActivity extends ShowCocktailActivity {

    private ActivitySearchRandomBinding binding;
    private ShowCocktailViewModel model;
    private CocktailRequestQueue requestQueue;
    private CocktailQueryMaker queryMaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setModel();
        setModelObserver();
        setRandomCocktail();
        setFavouriteButtonBehaviour();
        setRefreshButtonBehaviour();
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
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_search_random);
        this.binding.setLifecycleOwner(this);
        if (getIntent().hasExtra("cocktail")) {
            Cocktail cocktail = Objects.requireNonNull(getIntent().getExtras()).getParcelable("cocktail");
            model.getCocktail().setValue(cocktail);
            checkIsFavourite(cocktail);
            setThumbnail();
        }
        binding.setCocktail(model.getCocktail().getValue());
        binding.executePendingBindings();
    }

    protected void setModelObserver() {
        model.getCocktail().observe(this, new Observer<Cocktail>() {
            @Override
            public void onChanged(Cocktail cocktail) {
                binding.setCocktail(cocktail);
                checkIsFavourite(cocktail);
                setThumbnail();
                binding.executePendingBindings();
            }
        });
        model.isFavourite().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isFavourite) {
                setFavourite(isFavourite);
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
                checkIsFavourite(getModel().getCocktail().getValue());
            }
        });
    }

    protected void checkIsFavourite(Cocktail cocktail) {
        getQueryMaker().exists(cocktail.getId(), model.isFavourite());
    }

    protected void setFavourite(boolean isFavourite) {
        final FloatingActionButton button = findViewById(R.id.button_favourites);
        if (isFavourite) {
            getQueryMaker().insertAll(getModel().getCocktail().getValue());
            button.setImageDrawable(getDrawable(R.drawable.ic_favorite_white_24dp));
        } else {
            getQueryMaker().delete(model.getCocktail().getValue());
            button.setImageDrawable(getDrawable(R.drawable.ic_favorite_border_white_24dp));
        }
    }

    protected void setButtonColor(FloatingActionButton button, int colorId) {
        button.setColorFilter(
                ContextCompat.getColor(
                        SearchRandomActivity.this,
                        colorId),
                PorterDuff.Mode.MULTIPLY);
    }

    protected void setThumbnail() {
        ImageView imageView = findViewById(R.id.cocktail_thumbnail);
        getRequestQueue().add(new ThumbnailRequest(
                getModel().getCocktail().getValue().getThumbnailUrl(),
                this,
                imageView));
    }
    private void setRandomCocktail() {
        getRequestQueue().add(new OneRandomRequest(getModel().getCocktail()));
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
                setRandomCocktail();
            }
        });
    }

}
