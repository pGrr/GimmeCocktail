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
import com.gimmecocktail.adapters.CocktailCardsAdapter;
import com.gimmecocktail.databinding.ActivityFavouritesBinding;
import com.gimmecocktail.model.Cocktail;
import com.gimmecocktail.model.CocktailQueryMaker;
import com.gimmecocktail.viewmodels.CocktailListViewModel;
import java.util.List;
import java.util.Objects;

/**
 * Activity that shows favourite cocktails as cards.
 */
public class FavouritesActivity extends AppCompatActivity {

    private CocktailListViewModel model;
    private ActivityFavouritesBinding binding;
    private CocktailQueryMaker queryMaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setModel();
        setUpRecyclerView();
        setModelObserver();
        if (savedInstanceState == null) {
            searchCocktails();
            binding.executePendingBindings();
        }
    }

    private void setModel() {
        this.model = new ViewModelProvider(this).get(CocktailListViewModel.class);
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_favourites);
        this.binding.setLifecycleOwner(this);
    }

    private void setUpRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        getRecyclerView().setLayoutManager(layoutManager);
    }

    private void setModelObserver() {
        model.getCocktails().observe(this, new Observer<List<Cocktail>>() {
            @Override
            public void onChanged(List<Cocktail> cocktails) {
                CocktailCardsAdapter adapter = createCocktailsAdapter(cocktails);
                FavouritesActivity.this.getRecyclerView().setAdapter(adapter);
                binding.executePendingBindings();
            }
        });
    }

    private RecyclerView getRecyclerView() {
        return FavouritesActivity.this.binding.cocktailsRecyclerView;
    }

    private void searchCocktails() {
        getQueryMaker().getAll(model.getCocktails());
    }

    private CocktailQueryMaker getQueryMaker() {
        if (this.queryMaker == null) {
            this.queryMaker = new CocktailQueryMaker(this);
        }
        return this.queryMaker;
    }

    private CocktailCardsAdapter createCocktailsAdapter(List<Cocktail> cocktails) {
        CocktailCardsAdapter adapter = new CocktailCardsAdapter(cocktails);
        setOnItemClickListener(adapter);
        return adapter;
    }

    private void setOnItemClickListener(CocktailCardsAdapter adapter) {
        adapter.setOnItemClickListener(new CocktailCardsAdapter.CocktailsViewHolder.ClickListener() {
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

}
