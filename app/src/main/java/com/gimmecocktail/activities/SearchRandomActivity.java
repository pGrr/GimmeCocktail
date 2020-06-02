package com.gimmecocktail.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import com.gimmecocktail.http.ApiRequestQueue;
import com.gimmecocktail.http.RequestFactory;
import com.gimmecocktail.model.Cocktail;
import com.gimmecocktail.R;
import com.gimmecocktail.databinding.ActivitySearchRandomBinding;
import com.gimmecocktail.model.CocktailQueryMaker;
import com.gimmecocktail.utils.FavouriteCocktailImages;
import com.gimmecocktail.viewmodels.CocktailViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.Objects;

/**
 * Activity that queries the API with for a random-cocktail and shows the result.
 * Whenever the refresh button is pressed, a new random cocktail is requested and showed.
 */
public class SearchRandomActivity extends AppCompatActivity {

    private ActivitySearchRandomBinding binding;
    private CocktailViewModel model;
    private ApiRequestQueue requestQueue;
    private CocktailQueryMaker queryMaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initialize the request queue, used to send asynchronous requests to the api
        this.requestQueue = new ApiRequestQueue(this);
        // initialize the query maker, used to query the database asynchronously
        this.queryMaker = new CocktailQueryMaker(this);
        // set the model
        this.model = new ViewModelProvider(this).get(CocktailViewModel.class);
        // set the data-binding
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_search_random);
        this.binding.setLifecycleOwner(this);
        // set mutable live data observers
        setCocktailObserver();
        setIsFavouriteObserver();
        // set button observers
        setFavouriteButtonObservers();
        setRefreshButtonObservers();
        // if a cocktail was passed as extras, set it in the mutable live data
        if (savedInstanceState != null && getIntent().hasExtra("cocktail")) {
            Cocktail cocktail = Objects.requireNonNull(getIntent().getExtras())
                    .getParcelable("cocktail");
            model.getCocktail().setValue(cocktail);
        } else {
            // else request a random cocktail to the API
            setRandomCocktail();
        }
    }

    /**
     * Sends an asynchronous request to the API for a random cocktail,
     * once the result is ready, updates the cocktail mutable live data.
     * If an error occurs, it alerts the user.
     */
    private void setRandomCocktail() {
        RequestFactory.random(requestQueue).observe(new com.gimmecocktail.Observer<Cocktail>() {
            @Override
            public void onResult(Cocktail result) {
                // once the cocktail is ready, update the cocktail mutable live data of model
                model.getCocktail().setValue(result);
            }
            @Override
            public void onError(Exception exception) {
                // if an error occurs, alert the user
                Activities.alert(
                        getString(R.string.connection_failed_title),
                        getString(R.string.connection_failed_message),
                        SearchRandomActivity.this,
                        true
                );
            }
        }).send();
    }

    /**
     * Called whenever a cocktail is set (mutable live data of model)
     */
    private void setCocktailObserver() {
        model.getCocktail().observe(this, new Observer<Cocktail>() {
            @Override
            public void onChanged(Cocktail cocktail) {
                // update the ui bindings
                binding.setCocktail(cocktail);
                binding.executePendingBindings();
                // query asynchronously the database to know if this cocktail was saved as favourite
                // (isFavourite mutable live data will be updated on result)
                Activities.checkIsFavourite(
                        cocktail, model.isFavourite(), queryMaker, SearchRandomActivity.this);
                // load the image (from memory if present, else query the api)
                Activities.loadImage(cocktail, R.id.cocktail_thumbnail, SearchRandomActivity.this);
            }
        });
    }

    /**
     * Called when isFavourite (mutable live data of model) is changed
     */
    private void setIsFavouriteObserver() {
        model.isFavourite().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isFavourite) {
                // update button UI
                final FloatingActionButton button = findViewById(R.id.button_favourites);
                if (isFavourite) {
                    button.setImageDrawable(getDrawable(R.drawable.ic_favorite_white_24dp));
                } else {
                    button.setImageDrawable(getDrawable(R.drawable.ic_favorite_border_white_24dp));
                }
                // update the database
                queryMaker.setFavourite(model.getCocktail().getValue(), isFavourite);
                // save/delete thumbnail image from memory
                if (isFavourite
                        && model.getCocktail().getValue() != null
                        && model.getCocktail().getValue().getThumbnailBitmap() != null) {
                    // if is set favourite and image is loaded, save it
                    FavouriteCocktailImages.save(
                            Objects.requireNonNull(model.getCocktail().getValue()).getId(),
                            model.getCocktail().getValue().getThumbnailBitmap(),
                            SearchRandomActivity.this);
                } else {
                    // else if is set not-favourite, delete the saved image
                    FavouriteCocktailImages.delete(
                            Objects.requireNonNull(model.getCocktail().getValue()).getId(),
                            SearchRandomActivity.this);
                }
            }
        });
    }

    private void setFavouriteButtonObservers() {
        final FloatingActionButton button = findViewById(R.id.button_favourites);
        setButtonColor(button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isFavourite = Objects.requireNonNull(model.isFavourite().getValue());
                SearchRandomActivity.this.model.isFavourite().setValue(!isFavourite);
                Activities.checkIsFavourite(model.getCocktail().getValue(), model.isFavourite(),
                        queryMaker, SearchRandomActivity.this);
            }
        });
    }

    private void setRefreshButtonObservers() {
        final FloatingActionButton button = findViewById(R.id.refresh_random_cocktail_button);
        button.setColorFilter(ContextCompat.getColor(
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

    private void setButtonColor(FloatingActionButton button) {
        button.setColorFilter(
                ContextCompat.getColor(
                        SearchRandomActivity.this,
                        R.color.colorPrimary),
                PorterDuff.Mode.MULTIPLY);
    }

}
