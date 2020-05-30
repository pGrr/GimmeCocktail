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
import com.gimmecocktail.http.ThumbnailRequest;
import com.gimmecocktail.model.CocktailQueryMaker;
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
        setFavouriteButtonBehaviour();
        if (savedInstanceState == null) {
            if (getIntent().hasExtra("cocktail")) {
                Cocktail cocktail = Objects.requireNonNull(getIntent().getExtras()).getParcelable("cocktail");
                model.getCocktail().setValue(cocktail);
                setThumbnail();
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
            requestQueue = new ApiRequestQueue(this);
        }
        return requestQueue;
    }

    private void setModel() {
        this.model = new ViewModelProvider(this).get(CocktailViewModel.class);
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_show_cocktail);
        this.binding.setLifecycleOwner(this);
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
                setFavourite(isFavourite);
            }
        });
    }

    private void setFavouriteButtonBehaviour() {
        final FloatingActionButton button = findViewById(R.id.button_favourites);
        setButtonColor(button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isFavourite = Objects.requireNonNull(model.isFavourite().getValue());
                setFavourite(!isFavourite);
                checkIsFavourite(Objects.requireNonNull(model.getCocktail().getValue()));
            }
        });
    }

    private void checkIsFavourite(Cocktail cocktail) {
        getQueryMaker().exists(cocktail.getId(), model.isFavourite());
    }

    private void setFavourite(boolean isFavourite) {
        final FloatingActionButton button = findViewById(R.id.button_favourites);
        if (isFavourite) {
            getQueryMaker().insertAll(model.getCocktail().getValue());
            button.setImageDrawable(getDrawable(R.drawable.ic_favorite_white_24dp));
        } else {
            getQueryMaker().delete(model.getCocktail().getValue());
            button.setImageDrawable(getDrawable(R.drawable.ic_favorite_border_white_24dp));
        }
    }

    private void setButtonColor(FloatingActionButton button) {
        button.setColorFilter(
                ContextCompat.getColor(
                        ShowCocktailActivity.this,
                        R.color.colorPrimary),
                PorterDuff.Mode.MULTIPLY);
    }

    private void setThumbnail() {
        ImageView imageView = findViewById(R.id.cocktail_thumbnail);
        getRequestQueue().add(new ThumbnailRequest(
                Objects.requireNonNull(model.getCocktail().getValue()).getThumbnailUrl(),
                imageView, this));
    }

}
