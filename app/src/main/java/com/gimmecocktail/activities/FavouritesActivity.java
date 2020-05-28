package com.gimmecocktail.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.gimmecocktail.R;
import com.gimmecocktail.adapters.CocktailsAdapter;
import com.gimmecocktail.databinding.ActivityFavouritesBinding;
import com.gimmecocktail.model.Cocktail;
import com.gimmecocktail.model.CocktailQueryMaker;
import com.gimmecocktail.viewmodels.SearchViewModel;
import java.util.List;
import java.util.Objects;

public class FavouritesActivity extends AppCompatActivity {

    private SearchViewModel model;
    private ActivityFavouritesBinding binding;
    private CocktailQueryMaker queryMaker;

    public SearchViewModel getModel() {
        return model;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setModel();
        setUpRecyclerView();
        setModelObserver();
        searchCocktails();
        binding.executePendingBindings();
    }

    public CocktailQueryMaker getQueryMaker() {
        if (this.queryMaker == null) {
            this.queryMaker = new CocktailQueryMaker(this);
        }
        return this.queryMaker;
    }

    private void setUpRecyclerView() {
        // xml id = cocktails_recycler_view
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        getRecyclerView().setLayoutManager(layoutManager);
    }

    private RecyclerView getRecyclerView() {
        return FavouritesActivity.this.binding.cocktailsRecyclerView;
    }

    private void setModel() {
        this.model = new ViewModelProvider(this).get(SearchViewModel.class);
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_favourites);
        this.binding.setLifecycleOwner(this);
    }

    private void setModelObserver() {
        getModel().getCocktails().observe(this, new Observer<List<Cocktail>>() {
            @Override
            public void onChanged(List<Cocktail> cocktails) {
                CocktailsAdapter adapter = createCocktailsAdapter(cocktails);
                FavouritesActivity.this.getRecyclerView().setAdapter(adapter);
                binding.executePendingBindings();
            }
        });
    }

    private CocktailsAdapter createCocktailsAdapter(List<Cocktail> cocktails) {
        CocktailsAdapter adapter = new CocktailsAdapter(cocktails);
        setOnItemClickListener(adapter);
        return adapter;
    }

    private void setOnItemClickListener(CocktailsAdapter adapter) {
        adapter.setOnItemClickListener(new CocktailsAdapter.CocktailsViewHolder.ClickListener() {
            @Override
            public void onItemClick(int position) {
                Cocktail cocktail = Objects.requireNonNull(model.getCocktails().getValue()).get(position);
                startShowCocktailActivity(cocktail);
            }
        });
    }

    private void startShowCocktailActivity(Cocktail cocktail) {
        Intent intent = new Intent(FavouritesActivity.this, ShowCocktailActivity.class)
                .putExtra("cocktail", cocktail);
        startActivity(intent);
    }

    protected void searchCocktails() {
        getQueryMaker().getAll(getModel().getCocktails());
    }

}
