package com.gimmecocktail.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gimmecocktail.model.Cocktail;
import com.gimmecocktail.databinding.CocktailCardBinding;

import java.util.List;

public class CocktailsAdapter extends RecyclerView.Adapter<CocktailsAdapter.CocktailsViewHolder> {
    private final List<Cocktail> cocktails;
    private static CocktailsViewHolder.ClickListener clickListener;
    public CocktailsAdapter(List<Cocktail> cocktails) {
        this.cocktails = cocktails;
    }

    @Override
    public int getItemCount() {
        return cocktails.size();
    }

    public void setOnItemClickListener(CocktailsViewHolder.ClickListener clickListener) {
        CocktailsAdapter.clickListener = clickListener;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public CocktailsAdapter.CocktailsViewHolder onCreateViewHolder(ViewGroup parent,
                                                                   int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CocktailCardBinding itemBinding = CocktailCardBinding.inflate(
                layoutInflater,
                parent,
                false);
        return new CocktailsViewHolder(itemBinding);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull CocktailsViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.bind(cocktails.get(position));
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class CocktailsViewHolder extends RecyclerView.ViewHolder {

        // If your layout file is something_awesome.xml then your binding class will be SomethingAwesomeBinding
        // Since our layout file is item_movie.xml, our auto generated binding class is ItemMovieBinding
        private final CocktailCardBinding binding;


        //Define a constructor taking a ItemMovieBinding as its parameter
        CocktailsViewHolder(CocktailCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(getAdapterPosition());
                }
            });
        }

        /**
         * We will use this function to bind instance of Movie to the row
         */
        void bind(Cocktail cocktail) {
            binding.setCocktail(cocktail);
            binding.executePendingBindings();
        }

        public interface ClickListener {
            void onItemClick(int position);
        }
    }
}
