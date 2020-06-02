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
import com.gimmecocktail.model.Cocktail;
import com.gimmecocktail.R;
import com.gimmecocktail.databinding.ActivityShowCocktailBinding;
import com.gimmecocktail.http.BitMapRequest;
import com.gimmecocktail.model.CocktailQueryMaker;
import com.gimmecocktail.utils.FavouriteCocktailImages;
import com.gimmecocktail.viewmodels.CocktailViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.Objects;

/**
 * Activity that shows a Cocktail, provided by explicit intent extras.
 */
public class ShowCocktailActivity extends AppCompatActivity {

    private ActivityShowCocktailBinding binding;
    private CocktailViewModel model;
    private ApiRequestQueue requestQueue;
    private CocktailQueryMaker queryMaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setModel();
        setModelObserver();
        setOnFavouriteObserver();
        setFavouriteButtonBehaviour();
        if (savedInstanceState == null) {
            if (getIntent().hasExtra("cocktail")) {
                Cocktail cocktail = Objects.requireNonNull(getIntent().getExtras()).getParcelable("cocktail");
                model.getCocktail().setValue(cocktail);
            }
        }
    }

    private CocktailQueryMaker getQueryMaker() {
        if (queryMaker == null) {
            queryMaker = new CocktailQueryMaker(this);
        }
        return queryMaker;
    }

    private ApiRequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = new ApiRequestQueue();
        }
        return requestQueue;
    }

    private void setModel() {
        this.model = new ViewModelProvider(this).get(CocktailViewModel.class);
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_show_cocktail);
        this.binding.setLifecycleOwner(this);
    }

    private void setModelObserver() {
        // Set cocktail mutable live data observer
        model.getCocktail().observe(this, new Observer<Cocktail>() {
            AppCompatActivity context = ShowCocktailActivity.this;
            @Override
            public void onChanged(Cocktail cocktail) { // on cocktail change
                binding.setCocktail(cocktail);
                // query the db: is cocktail favourite?
                // on response, update isFavourite mutable live data
                getQueryMaker().isFavourite(cocktail, model.isFavourite());
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
                        getRequestQueue().add(new BitMapRequest(
                                cocktail.getThumbnailUrl(),
                                model.getCocktail(),
                                context).getRequest());
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
                AppCompatActivity context = ShowCocktailActivity.this;
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
                        ShowCocktailActivity.this,
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

}
