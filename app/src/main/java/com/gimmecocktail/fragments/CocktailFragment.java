package com.gimmecocktail.fragments;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.graphics.PorterDuff;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.gimmecocktail.databinding.CocktailFragmentBinding;
import com.gimmecocktail.http.ApiRequestQueue;
import com.gimmecocktail.http.ThumbnailRequest;
import com.gimmecocktail.model.Cocktail;
import com.gimmecocktail.model.CocktailQueryMaker;
import com.gimmecocktail.utils.FavouriteCocktailImages;
import com.gimmecocktail.viewmodels.CocktailViewModel;
import com.gimmecocktail.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class CocktailFragment extends Fragment {

    private CocktailViewModel model;
    private CocktailFragmentBinding binding;
    private ApiRequestQueue requestQueue;
    private CocktailQueryMaker queryMaker;

    public static CocktailFragment newInstance() {
        return new CocktailFragment();
    }

    public CocktailViewModel getModel() {
        return model;
    }

    public CocktailQueryMaker getQueryMaker() {
        if (queryMaker == null) {
            queryMaker = new CocktailQueryMaker(getActivity());
        }
        return queryMaker;
    }

    public ApiRequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = new ApiRequestQueue(getActivity());
        }
        return requestQueue;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cocktail_fragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        model = ViewModelProviders.of(this).get(CocktailViewModel.class);
        this.binding = DataBindingUtil.setContentView(getActivity(), R.layout.cocktail_fragment);
        this.binding.setLifecycleOwner(this);
        if (savedInstanceState == null) {
            if (getActivity().getIntent().hasExtra("cocktail")) {
                Cocktail cocktail = (Cocktail)Objects.requireNonNull(getActivity().getIntent().getExtras())
                        .getParcelable("cocktail");
                model.getCocktail().setValue(cocktail);

                binding.setCocktail(cocktail);
                binding.executePendingBindings();
            }
        }
        // TODO: Use the ViewModel
        setModelObserver();
        setOnFavouriteObserver();
        setFavouriteButtonBehaviour();
    }

    private void setModelObserver() {
        // Set cocktail mutable live data observer
        model.getCocktail().observe(getActivity(), new Observer<com.gimmecocktail.model.Cocktail>() {
            @Override
            public void onChanged(com.gimmecocktail.model.Cocktail cocktail) { // on cocktail change
                binding.setCocktail(cocktail);
                // query the db: is cocktail favourite?
                // on response, update isFavourite mutable live data
                getQueryMaker().exists(cocktail.getId(), model.isFavourite());
                // if image is in the favourites directory, load it from there
                boolean savedImageExist = FavouriteCocktailImages.exists(cocktail.getId(), getActivity());
                if (savedImageExist) {
                    FavouriteCocktailImages.load(cocktail.getId(), getActivity(), R.id.cocktail_thumbnail);
                } else {
                    // else if has been downloaded and set, inject it in the image view
                    if (cocktail.getThumbnailBitmap() != null) {
                        ImageView image = getActivity().findViewById(R.id.cocktail_thumbnail);
                        image.setImageBitmap(cocktail.getThumbnailBitmap());
                    } else {
                        // else get it via http
                        getRequestQueue().add(new ThumbnailRequest(
                                cocktail.getThumbnailUrl(),
                                model.getCocktail(),
                                getActivity()));
                    }
                }
                binding.executePendingBindings();
            }
        });
    }

    private void setOnFavouriteObserver() {
        model.isFavourite().observe(getActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isFavourite) {
                // Update the database
                if (isFavourite) {
                    getQueryMaker().insertAll(model.getCocktail().getValue());
                } else {
                    getQueryMaker().delete(model.getCocktail().getValue());
                }
                // Update the favourites button in the UI
                final FloatingActionButton button = getActivity().findViewById(R.id.button_favourites);
                if (isFavourite) {
                    button.setImageDrawable(getActivity()
                            .getDrawable(R.drawable.ic_favorite_white_24dp));
                } else {
                    button.setImageDrawable(getActivity()
                            .getDrawable(R.drawable.ic_favorite_border_white_24dp));
                }
                // if the cocktail is set favourite and the thumbnail is downloaded and set,
                // save it in internal memory storage
                com.gimmecocktail.model.Cocktail cocktail = model.getCocktail().getValue();
                if (isFavourite && cocktail.getThumbnailBitmap() != null) {
                    FavouriteCocktailImages.save(
                            cocktail.getId(),
                            cocktail.getThumbnailBitmap(),
                            getActivity());
                } else if (!isFavourite) {
                    // else, if not favourite, delete the saved image from internal memory
                    FavouriteCocktailImages.delete(cocktail.getId(), getActivity());
                }
            }
        });
    }

    private void setFavouriteButtonBehaviour() {
        FloatingActionButton button = getActivity().findViewById(R.id.button_favourites);
        button.setColorFilter(
                ContextCompat.getColor(
                        getActivity(),
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
