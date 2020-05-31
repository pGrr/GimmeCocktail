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

import com.gimmecocktail.http.ApiRequestQueue;
import com.gimmecocktail.http.OneRandomRequest;
import com.gimmecocktail.model.Cocktail;
import com.gimmecocktail.R;
import com.gimmecocktail.databinding.ActivitySearchRandomBinding;
import com.gimmecocktail.http.ThumbnailRequest;
import com.gimmecocktail.model.CocktailQueryMaker;
import com.gimmecocktail.utils.FavouriteCocktailImages;
import com.gimmecocktail.viewmodels.CocktailViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

/**
 * Activity that queries the API with a get-random-cocktail request
 * and shows the result.
 */
public class SearchRandomActivity extends AppCompatActivity {

    private ActivitySearchRandomBinding binding;
    private CocktailViewModel model;
    private ApiRequestQueue requestQueue;
    private CocktailQueryMaker queryMaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setModel();
        setModelObserver();
        setFavouriteButtonBehaviour();
        setRefreshButtonBehaviour();
        if (savedInstanceState == null) {
            setRandomCocktail();
        }
    }

    private void setModel() {
        this.model = new ViewModelProvider(this).get(CocktailViewModel.class);
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_search_random);
        this.binding.setLifecycleOwner(this);
        if (getIntent().hasExtra("cocktail")) {
            Cocktail cocktail = Objects.requireNonNull(getIntent().getExtras())
                    .getParcelable("cocktail");
            model.getCocktail().setValue(cocktail);
            checkIsFavourite(Objects.requireNonNull(cocktail));
            setThumbnail();
        }
        binding.setCocktail(model.getCocktail().getValue());
        binding.executePendingBindings();
    }

    private void setModelObserver() {
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
                final FloatingActionButton button = findViewById(R.id.button_favourites);
                if (isFavourite) {
                    getQueryMaker().insertAll(model.getCocktail().getValue());
                } else {
                    getQueryMaker().delete(model.getCocktail().getValue());
                }
                if (model.getCocktail().getValue().getThumbnailBitmap() != null && isFavourite) {
                    FavouriteCocktailImages.save(
                            Objects.requireNonNull(model.getCocktail().getValue()).getId(),
                            model.getCocktail().getValue().getThumbnailBitmap(),
                            SearchRandomActivity.this);
                    button.setImageDrawable(getDrawable(R.drawable.ic_favorite_white_24dp));
                } else if (!isFavourite) {
                    FavouriteCocktailImages.delete(
                            Objects.requireNonNull(model.getCocktail().getValue()).getId(),
                            SearchRandomActivity.this);
                    button.setImageDrawable(getDrawable(R.drawable.ic_favorite_border_white_24dp));
                }
            }
        });
    }

    private void setRandomCocktail() {
        getRequestQueue().add(new OneRandomRequest(model.getCocktail(), this));
    }

    private void setFavouriteButtonBehaviour() {
        final FloatingActionButton button = findViewById(R.id.button_favourites);
        setButtonColor(button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isFavourite = Objects.requireNonNull(model.isFavourite().getValue());
                SearchRandomActivity.this.model.isFavourite().setValue(!isFavourite);
                checkIsFavourite(Objects.requireNonNull(model.getCocktail().getValue()));
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
                setRandomCocktail();
            }
        });
    }

    private CocktailQueryMaker getQueryMaker() {
        if (queryMaker == null) {
            queryMaker = new CocktailQueryMaker(this);
        }
        return queryMaker;
    }

    private ApiRequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = new ApiRequestQueue(this);
        }
        return requestQueue;
    }

    private void checkIsFavourite(Cocktail cocktail) {
        getQueryMaker().exists(cocktail.getId(), model.isFavourite());
    }

    private void setButtonColor(FloatingActionButton button) {
        button.setColorFilter(
                ContextCompat.getColor(
                        SearchRandomActivity.this,
                        R.color.colorPrimary),
                PorterDuff.Mode.MULTIPLY);
    }

    private void setThumbnail() {
        if (FavouriteCocktailImages.exists(
                model.getCocktail().getValue().getId(), SearchRandomActivity.this)) {
            FavouriteCocktailImages.load(
                    model.getCocktail().getValue().getId(),
                    SearchRandomActivity.this,
                    R.id.cocktail_thumbnail);
        } else {
            if (model.getCocktail().getValue().getThumbnailBitmap() == null) {
                getRequestQueue().add(new ThumbnailRequest(
                        Objects.requireNonNull(model.getCocktail().getValue()).getThumbnailUrl(),
                        model.getCocktail(),
                        this));
            } else {
                ImageView image = findViewById(R.id.cocktail_thumbnail);
                image.setImageBitmap(model.getCocktail().getValue().getThumbnailBitmap());
            }
        }
    }

}
