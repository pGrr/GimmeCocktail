package com.gimmecocktail.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.gimmecocktail.model.Cocktail;
import com.gimmecocktail.R;
import com.gimmecocktail.adapters.CocktailCardsAdapter;
import com.gimmecocktail.databinding.ActivitySearchCocktailsBinding;
import com.gimmecocktail.viewmodels.CocktailListViewModel;
import java.util.List;
import java.util.Objects;

/**
 * Abstract class providing base implementation for all activities
 * that query the API for a cocktail list and show the results as cards.
 * @see SearchByNameActivity
 * @see SearchByIngredientActivity
 */
public abstract class AbstractSearchCocktailsActivity extends AppCompatActivity {

    private CocktailListViewModel model;
    private ActivitySearchCocktailsBinding binding;
    // to prevent onQueryTextChange event caused by a configuration change
    private boolean queryTextConfigChangeFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the view-model
        this.model = new ViewModelProvider(this).get(CocktailListViewModel.class);
        // set the data-binding
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_search_cocktails);
        this.binding.setLifecycleOwner(this);
        // set the recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        AbstractSearchCocktailsActivity.this.binding.cocktailsRecyclerView.setLayoutManager(layoutManager);
        // set the view-model observer
        this.model.getCocktails().observe(this, new Observer<List<Cocktail>>() {
            @Override
            public void onChanged(List<Cocktail> cocktails) {
                CocktailCardsAdapter adapter = new CocktailCardsAdapter(cocktails);
                setOnItemClickListener(adapter);
                AbstractSearchCocktailsActivity.this.binding.cocktailsRecyclerView.setAdapter(adapter);
            }
        });
        // set the onQueryText listener
        queryTextConfigChangeFlag = savedInstanceState != null;
        setOnQueryTextListener();
    }

    /**
     * Gets the view-model of the activity
     *
     * @return the view-model
     */
    public CocktailListViewModel getModel() {
        return model;
    }

    /**
     * Abstract search cocktails method to be implemented by sub-classes.
     *
     * @param query the query parameter
     */
    protected abstract void searchCocktails(String query);

    /**
     * Whenever the query text is changed by the user, a new request for cocktails is sent
     */
    private void setOnQueryTextListener() {
        final SearchView searchView = findViewById(R.id.search_bar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchCocktails(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                // prevent a search caused by a onQueryTextChange event due to a configuration change
                if (queryTextConfigChangeFlag) {
                    queryTextConfigChangeFlag = false;
                } else {
                    searchCocktails(newText);
                }
                return true;
            }
        });
    }

    /**
     * When a card is clicked, a ShowCocktailActivity is started, passing the cocktail as extras
     * @see ShowCocktailActivity
     */
    private void setOnItemClickListener(CocktailCardsAdapter adapter) {
        adapter.setOnItemClickListener(new CocktailCardsAdapter.CocktailsViewHolder.ClickListener() {
            @Override
            public void onItemClick(int position) {
                Cocktail cocktail = Objects.requireNonNull(model.getCocktails().getValue()).get(position);
                Intent intent = new Intent(
                        AbstractSearchCocktailsActivity.this,
                        ShowCocktailActivity.class);
                intent.putExtra("layoutId", R.layout.activity_show_cocktail);
                intent.putExtra("cocktail", cocktail);
                startActivity(intent);
            }
        });
    }

}
