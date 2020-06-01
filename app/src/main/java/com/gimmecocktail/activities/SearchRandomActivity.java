package com.gimmecocktail.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.gimmecocktail.fragments.CocktailFragment;
import com.gimmecocktail.http.ApiRequestQueue;
import com.gimmecocktail.http.OneRandomRequest;
import com.gimmecocktail.model.Cocktail;
import com.gimmecocktail.R;
import com.gimmecocktail.databinding.ActivitySearchRandomBinding;
import com.gimmecocktail.http.ThumbnailRequest;
import com.gimmecocktail.model.CocktailQueryMaker;
import com.gimmecocktail.utils.FavouriteCocktailImages;
import com.gimmecocktail.viewmodels.CocktailViewModel2;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

/**
 * Activity that queries the API with a get-random-cocktail request
 * and shows the result.
 */
public class SearchRandomActivity extends AppCompatActivity {

    private boolean isSavedInstanceStateNull;
    private CocktailFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_random);
        setRefreshButtonBehaviour();
        isSavedInstanceStateNull = savedInstanceState == null;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        this.fragment = new CocktailFragment();
        fragmentTransaction.add(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isSavedInstanceStateNull) {
            setRandomCocktail();
        }
    }

    private void setRandomCocktail() {
        fragment.getRequestQueue().add(
                new OneRandomRequest(
                        fragment.getModel().getCocktail(),
                        this));
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
