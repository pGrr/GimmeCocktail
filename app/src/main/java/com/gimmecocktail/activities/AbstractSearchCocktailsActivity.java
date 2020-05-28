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
import com.gimmecocktail.Cocktail;
import com.gimmecocktail.R;
import com.gimmecocktail.adapters.CocktailsAdapter;
import com.gimmecocktail.databinding.ActivitySearchByNameBinding;
import com.gimmecocktail.viewmodels.SearchViewModel;
import java.util.List;
import java.util.Objects;

public abstract class AbstractSearchCocktailsActivity extends AppCompatActivity {

    private SearchViewModel model;
    private ActivitySearchByNameBinding binding;

    public SearchViewModel getModel() {
        return model;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setModel();
        setUpRecyclerView();
        setModelObserver();
        setOnQueryTextListener();
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
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_search_by_name);
        this.binding.setLifecycleOwner(this);
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

    protected void setTitle(String title) {
        TextView textView = findViewById(R.id.search_cocktails_title);
        textView.setText(title);
    }

    protected abstract void searchCocktails(String query);
}