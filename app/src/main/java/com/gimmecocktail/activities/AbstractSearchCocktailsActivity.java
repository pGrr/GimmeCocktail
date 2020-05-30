package com.gimmecocktail.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.gimmecocktail.http.ApiRequestQueue;
import com.gimmecocktail.model.Cocktail;
import com.gimmecocktail.R;
import com.gimmecocktail.adapters.CocktailCardsAdapter;
import com.gimmecocktail.databinding.ActivitySearchCocktailsBinding;
import com.gimmecocktail.viewmodels.CocktailListViewModel;
import java.util.List;
import java.util.Objects;

/**
 * Abstract class providing base implementation for all activities that query the API
 * for cocktails and show the results as cards.
 * @see SearchByNameActivity
 * @see SearchByIngredientActivity
 */
public abstract class AbstractSearchCocktailsActivity extends AppCompatActivity {

    private CocktailListViewModel model;
    private ActivitySearchCocktailsBinding binding;
    private ApiRequestQueue requestQueue;

    /**
     * Gets the cocktail-list view model.
     *
     * @return the cocktail-list view model
     */
    CocktailListViewModel getModel() {
        return model;
    }

    /**
     * Gets the api request queue to be used to add API requests.
     *
     * @return the activity request queue
     */
    ApiRequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = new ApiRequestQueue(getApplication().getApplicationContext());
        }
        return requestQueue;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setModel();
        setRequestQueue();
        setUpRecyclerView();
        setModelObserver();
        setOnQueryTextListener();
    }

    /**
     * Sets the title of the activity in the view.
     *
     * @param title the title
     */
    void setTitle(String title) {
        TextView textView = findViewById(R.id.search_cocktails_title);
        textView.setText(title);
    }

    /**
     * Abstract search cocktails method to be implemented by sub-classes.
     *
     * @param query the query parameter
     */
    protected abstract void searchCocktails(String query);

    private void setModel() {
        this.model = new ViewModelProvider(this).get(CocktailListViewModel.class);
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_search_cocktails);
        this.binding.setLifecycleOwner(this);
    }

    private void setRequestQueue() {
        this.requestQueue = new ApiRequestQueue(this);
    }

    private void setUpRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        getRecyclerView().setLayoutManager(layoutManager);
    }

    private RecyclerView getRecyclerView() {
        return AbstractSearchCocktailsActivity.this.binding.cocktailsRecyclerView;
    }

    private void setModelObserver() {
        this.model.getCocktails().observe(this, new Observer<List<Cocktail>>() {
            @Override
            public void onChanged(List<Cocktail> cocktails) {
                CocktailCardsAdapter adapter = createCocktailsAdapter(cocktails);
                AbstractSearchCocktailsActivity.this.getRecyclerView().setAdapter(adapter);
            }
        });
    }

    private void setOnQueryTextListener() {
        final SearchView searchView = findViewById(R.id.search_bar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    searchCocktails(query);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // searchCocktails(newText);
                return true;
            }
        });
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

    private CocktailCardsAdapter createCocktailsAdapter(List<Cocktail> cocktails) {
        CocktailCardsAdapter adapter = new CocktailCardsAdapter(cocktails);
        setOnItemClickListener(adapter);
        return adapter;
    }

    private void startShowCocktailActivity(Cocktail cocktail) {
        Intent intent = new Intent(AbstractSearchCocktailsActivity.this, ShowCocktailActivity.class)
                .putExtra("cocktail", cocktail);
        startActivity(intent);
    }

}
