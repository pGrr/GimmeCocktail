package com.gimmecocktail.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;

import com.gimmecocktail.Cocktail;
import com.gimmecocktail.adapters.CocktailsAdapter;
import com.gimmecocktail.R;
import com.gimmecocktail.viewmodels.SearchViewModel;
import com.gimmecocktail.databinding.ActivitySearchByNameBinding;

import java.util.ArrayList;
import java.util.List;

public class SearchByNameActivity extends AppCompatActivity {
    private RecyclerView resultsRecyclerView;
    private CocktailsAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ActivitySearchByNameBinding binding;
    private SearchViewModel model;
    private CocktailsAdapter.CocktailsViewHolder.ClickListener onItemClick = new CocktailsAdapter.CocktailsViewHolder.ClickListener() {
        @Override
        public void onItemClick(int position) {
            Cocktail cocktail = model.getCocktails().getValue().get(position);
            Intent intent = new Intent(
                    SearchByNameActivity.this,
                    ShowCocktailActivity.class)
                    .putExtra("cocktail", cocktail);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new CocktailsAdapter(new ArrayList<Cocktail>());
        // create and bind model
        final SearchViewModel model = new ViewModelProvider(this).get(SearchViewModel.class);
        this.model = model;
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
                adapter.setOnItemClickListener(onItemClick);
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
        mAdapter.setOnItemClickListener(onItemClick);
    }


}
