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
        setCocktailObserver();
        setOnFavouriteObserver();
        setFavouriteButtonBehaviour();
        setRefreshButtonBehaviour();
        if (savedInstanceState == null) {
            if (getIntent().hasExtra("cocktail")) {
                Cocktail cocktail = Objects.requireNonNull(getIntent().getExtras()).getParcelable("cocktail");
                model.getCocktail().setValue(cocktail);
            } else {
                setRandomCocktail();
            }
        }
    }

    private CocktailQueryMaker getQueryMaker() {
        if (queryMaker == null) {
            queryMaker = new CocktailQueryMaker(this, CocktailQueryMaker.DbName.FAVOURITES);
        }
        return queryMaker;
    }

    private ApiRequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = new ApiRequestQueue(this);
        }
        return requestQueue;
    }

    private void setModel() {
        this.model = new ViewModelProvider(this).get(CocktailViewModel.class);
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_search_random);
        this.binding.setLifecycleOwner(this);
    }

    private void setCocktailObserver() {
        // Set cocktail mutable live data observer
        model.getCocktail().observe(this, new Observer<Cocktail>() {
            AppCompatActivity context = SearchRandomActivity.this;
            @Override
            public void onChanged(Cocktail cocktail) { // on cocktail change
                binding.setCocktail(cocktail);
                // query the db: is cocktail favourite?
                // on response, update isFavourite mutable live data
                getQueryMaker().exists(cocktail.getId(), model.isFavourite());
                // if image is in the favourites directory, load it from there
                boolean savedImageExist = FavouriteCocktailImages.exists(cocktail.getId(), context);
                if (savedImageExist) {
                    FavouriteCocktailImages.load(cocktail.getId(), context, R.id.cocktail_thumbnail);
                } else {
                    // else if has been downloaded and set, inject it in the image view
                    if (cocktail.getThumbnailBitmap() != null) {
                        ImageView image = findViewById(R.id.cocktail_thumbnail);
                        image.setImageBitmap(cocktail.getThumbnailBitmap());
                    } else {
                        // else get it via http
                        getRequestQueue().add(new ThumbnailRequest(
                                cocktail.getThumbnailUrl(),
                                model.getCocktail(),
                                context));
                    }
                }
                binding.executePendingBindings();
            }
        });
    }

    private void setOnFavouriteObserver() {
        model.isFavourite().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isFavourite) {
                // Update the database
                if (isFavourite) {
                    getQueryMaker().insertAll(model.getCocktail().getValue());
                } else {
                    getQueryMaker().delete(model.getCocktail().getValue());
                }
                // Update the favourites button in the UI
                final FloatingActionButton button = findViewById(R.id.button_favourites);
                if (isFavourite) {
                    button.setImageDrawable(getDrawable(R.drawable.ic_favorite_white_24dp));
                } else {
                    button.setImageDrawable(getDrawable(R.drawable.ic_favorite_border_white_24dp));
                }
                // if the cocktail is set favourite and the thumbnail is downloaded and set,
                // save it in internal memory storage
                Cocktail cocktail = model.getCocktail().getValue();
                AppCompatActivity context = SearchRandomActivity.this;
                if (isFavourite && cocktail.getThumbnailBitmap() != null) {
                    FavouriteCocktailImages.save(
                            cocktail.getId(),
                            cocktail.getThumbnailBitmap(),
                            context);
                } else if (!isFavourite) {
                    // else, if not favourite, delete the saved image from internal memory
                    FavouriteCocktailImages.delete(cocktail.getId(), context);
                }
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
                boolean isFavourite = Objects.requireNonNull(model.isFavourite().getValue());
                model.isFavourite().setValue(!isFavourite);
            }
        });
    }

    private void setRandomCocktail() {
        getRequestQueue().add(new OneRandomRequest(model.getCocktail(), this));
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
