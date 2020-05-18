package com.gimmecocktail;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class CocktailResultsAdapter extends RecyclerView.Adapter<CocktailResultsAdapter.CocktailViewHolder> {
    private Cocktail[] cocktails;

    public static class CocktailViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public CocktailViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public CocktailResultsAdapter(Cocktail[] cocktails) {
        this.cocktails = cocktails;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CocktailResultsAdapter.CocktailViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        // create a new view
        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cocktail_card, parent, false);


        // ...
        CocktailViewHolder vh = new CocktailViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CocktailViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //holder.cardView.setText(cocktails[position]);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return cocktails.length;
    }
}
