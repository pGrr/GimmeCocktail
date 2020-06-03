package com.gimmecocktail.activities;

import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.gimmecocktail.R;
import com.gimmecocktail.databinding.ActivityShowCocktailBinding;
import com.gimmecocktail.http.ApiRequestQueue;
import com.gimmecocktail.http.RequestFactory;
import com.gimmecocktail.model.Cocktail;
import com.gimmecocktail.model.CocktailQueryMaker;
import com.gimmecocktail.utils.FavouriteCocktailImages;
import com.gimmecocktail.viewmodels.CocktailViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.Objects;

public class ShowCocktailActivity extends AppCompatActivity {

    private ActivityShowCocktailBinding binding;
    private CocktailViewModel model;
    private ApiRequestQueue requestQueue;
    private CocktailQueryMaker queryMaker;

    /**
     * Gets the view-model
     * @return the view-model
     */
    public CocktailViewModel getModel() {
        return model;
    }

    /**
     * Gets the request queue
     *
     * @return the request queue
     */
    public ApiRequestQueue getRequestQueue() {
        return requestQueue;
    }

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
        int layoutId = Objects.requireNonNull(getIntent().getExtras()).getInt("layoutId");
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_show_cocktail);
        setContentView(layoutId);
        this.binding.setLifecycleOwner(this);
        // set mutable live data observers
        setCocktailObserver();
        setIsFavouriteObserver();
        // set button observers
        setFavouriteButtonObserver();
        // if a cocktail was passed as extras, set it in the mutable live data
        if (getIntent().hasExtra("cocktail")) {
            Cocktail cocktail = Objects.requireNonNull(getIntent().getExtras())
                    .getParcelable("cocktail");
            model.getCocktail().setValue(cocktail);
        }
    }

    /**
     * Called whenever a cocktail is set (mutable live data of model)
     */
    protected void setCocktailObserver() {
        model.getCocktail().observe(this, new Observer<Cocktail>() {
            @Override
            public void onChanged(Cocktail cocktail) {
                // update the ui bindings
                binding.setCocktail(cocktail);
                binding.executePendingBindings();
                // query asynchronously the database to know if this cocktail was saved as favourite
                // (isFavourite mutable live data will be updated on result)
                Activities.checkIsFavourite(
                        cocktail, model.isFavourite(), queryMaker, ShowCocktailActivity.this);
                // load the image (from memory if present, else query the api)
                loadImage(cocktail, R.id.cocktail_thumbnail);
            }
        });
    }

    /**
     * Loads the thumbnail of a cocktail and injects it in the image view.
     * If the thumbnail was saved in memory it loads it from there, else it sends
     * an asynchronous request to the api.
     * If an error occurs, a modal is shown to the user.
     *
     * @param cocktail the cocktail
     * @param imageViewId the id of the image view where to inject the thumbnail
     */
    public void loadImage(final Cocktail cocktail, final int imageViewId) {
        // if the image is present in memory, load it from there
        if (FavouriteCocktailImages.exists(cocktail.getId(), this)) {
            FavouriteCocktailImages.load(cocktail.getId(), this, imageViewId);
        } else {
            // else, query the api
            RequestFactory.bitMap(cocktail.getThumbnailUrl(), requestQueue)
                    .observe(new com.gimmecocktail.Observer<Bitmap>() {
                @Override
                public void onResult(Bitmap result) {
                    // when the image is downloaded, set it in the cocktail and in the image-view
                    cocktail.setThumbnailBitmap(result);
                    ImageView image = findViewById(imageViewId);
                    image.setImageBitmap(result);
                }
                @Override
                public void onError(Exception exception) {
                    // if an error occurs, alert the user
                    Activities.alert(
                            getString(R.string.database_connection_error_title),
                            getString(R.string.database_connection_error_message),
                            ShowCocktailActivity.this,
                            true
                    );
                }
            }).send();
        }
    }

    /**
     * Called when isFavourite (mutable live data of model) is changed
     */
    protected void setIsFavouriteObserver() {
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
                            ShowCocktailActivity.this);
                } else if (getModel().getCocktail().getValue() != null) {
                    // else if is set not-favourite, delete the saved image
                    FavouriteCocktailImages.delete(
                            Objects.requireNonNull(model.getCocktail().getValue()).getId(),
                            ShowCocktailActivity.this);
                }
            }
        });
    }

    protected void setFavouriteButtonObserver() {
        final FloatingActionButton button = findViewById(R.id.button_favourites);
        setButtonColor(button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isFavourite = Objects.requireNonNull(model.isFavourite().getValue());
                ShowCocktailActivity.this.model.isFavourite().setValue(!isFavourite);
                //Activities.checkIsFavourite(model.getCocktail().getValue(), model.isFavourite(),
                  //      queryMaker, ShowCocktailActivity.this);
            }
        });
    }

    protected void setButtonColor(FloatingActionButton button) {
        button.setColorFilter(
                ContextCompat.getColor(
                        ShowCocktailActivity.this,
                        R.color.colorPrimary),
                PorterDuff.Mode.MULTIPLY);
    }

}
