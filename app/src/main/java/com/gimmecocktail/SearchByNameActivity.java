package com.gimmecocktail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

public class SearchByNameActivity extends AppCompatActivity {
    private RecyclerView resultsRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_name);
        this.resultsRecyclerView = (RecyclerView) findViewById(R.id.results_recycler_view);
        this.resultsRecyclerView.setHasFixedSize(true);
        this.layoutManager = new LinearLayoutManager(this);
        this.resultsRecyclerView.setLayoutManager(layoutManager);
        //this.mAdapter = new MyAdapter(myDataset);
        resultsRecyclerView.setAdapter(mAdapter);
    }


}
