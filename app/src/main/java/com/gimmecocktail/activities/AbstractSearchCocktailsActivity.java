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
import com.gimmecocktail.http.CocktailRequestQueue;
import com.gimmecocktail.model.Cocktail;
import com.gimmecocktail.R;
import com.gimmecocktail.adapters.CocktailsAdapter;
import com.gimmecocktail.databinding.ActivitySearchCocktailsBinding;
import com.gimmecocktail.model.CocktailQueryMaker;
import com.gimmecocktail.viewmodels.SearchViewModel;
import java.util.List;
import java.util.Objects;

public abstract class AbstractSearchCocktailsActivity extends AppCompatActivity {

    private SearchViewModel model;
    private ActivitySearchCocktailsBinding binding;
    private CocktailRequestQueue requestQueue;
    private CocktailQueryMaker queryMaker;

    SearchViewModel getModel() {
        return model;
    }

    CocktailRequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = new CocktailRequestQueue<>(getApplication().getApplicationContext(), model.getCocktails());
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
        return AbstractSearchCocktailsActivity.this.binding.cocktailsRecyclerView;
    }

    private void setModel() {
        this.model = new ViewModelProvider(this).get(SearchViewModel.class);
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_search_cocktails);
        this.binding.setLifecycleOwner(this);
    }

    private void setRequestQueue() {
        this.requestQueue = new CocktailRequestQueue<>(this, model.getCocktails());
    }

    private void setModelObserver() {
        this.model.getCocktails().observe(this, new Observer<List<Cocktail>>() {
            @Override
            public void onChanged(List<Cocktail> cocktails) {
                CocktailsAdapter adapter = createCocktailsAdapter(cocktails);
                AbstractSearchCocktailsActivity.this.getRecyclerView().setAdapter(adapter);
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
        Intent intent = new Intent(AbstractSearchCocktailsActivity.this, ShowCocktailActivity.class)
                .putExtra("cocktail", cocktail);
        startActivity(intent);
    }

    private void setOnQueryTextListener() {
        final SearchView searchView = findViewById(R.id.search_bar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    searchCocktails(query);
                } catch (Exception e) {
                    Log.d(SearchByNameActivity.class.getName(), Objects.requireNonNull(e.getMessage()));
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchCocktails(newText);
                return true;
            }
        });
    }

    void setTitle(String title) {
        TextView textView = findViewById(R.id.search_cocktails_title);
        textView.setText(title);
    }

    protected abstract void searchCocktails(String query);

}
