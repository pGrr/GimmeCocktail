package com.gimmecocktail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import java.util.List;

public class SearchByNameActivity extends AppCompatActivity {
    private RecyclerView resultsRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_name);
//        this.resultsRecyclerView = (RecyclerView) findViewById(R.id.results_recycler_view);
//        this.resultsRecyclerView.setHasFixedSize(true);
//        this.layoutManager = new LinearLayoutManager(this);
//        this.resultsRecyclerView.setLayoutManager(layoutManager);
        //this.mAdapter = new MyAdapter(myDataset);
        //resultsRecyclerView.setAdapter(mAdapter);

        SearchViewModel model = new ViewModelProvider(this).get(SearchViewModel.class);
        model.getCocktails().observe(this, new Observer<List<Cocktail>>() {
            @Override
            public void onChanged(List<Cocktail> cocktails) {
                for (Cocktail cocktail: cocktails) {
                    Log.d("ViewModel", cocktails.toString());
                }
            }
        });
    }


}
