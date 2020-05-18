package com.gimmecocktail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.gimmecocktail.databinding.ActivitySearchByNameBinding;
import com.gimmecocktail.http.ImageRequestQueue;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchByNameActivity extends AppCompatActivity {
    private RecyclerView resultsRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ActivitySearchByNameBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // create and bind model
        final SearchViewModel model = new ViewModelProvider(this).get(SearchViewModel.class);
        // create and set data-binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_by_name);
        binding.setLifecycleOwner(this);
        // set up recycler view's adapter on model change
        final Context context = this;
        model.getCocktails().observe(this, new Observer<List<Cocktail>>() {
            @Override
            public void onChanged(List<Cocktail> cocktails) {
                RecyclerView recyclerView = binding.cocktailsRecyclerView; // In xml we have given id cocktails_recycler_view to RecyclerView
                LinearLayoutManager layoutManager = new LinearLayoutManager(context); // you can use getContext() instead of "this"
                recyclerView.setLayoutManager(layoutManager);
                CocktailsAdapter adapter = new CocktailsAdapter(cocktails);
                recyclerView.setAdapter(adapter);
            }
        });
        // query for cocktails on search input
        SearchView searchView = findViewById(R.id.search_bar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    model.searchCocktailsByName(query);
                } catch (Exception e) {
                    Log.d("SearchByName", e.getMessage());
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                model.searchCocktailsByName(newText);
                return true;
            }
        });
    }

}
