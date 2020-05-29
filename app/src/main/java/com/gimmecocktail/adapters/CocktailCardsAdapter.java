package com.gimmecocktail.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.gimmecocktail.model.Cocktail;
import com.gimmecocktail.databinding.CocktailCardBinding;
import java.util.List;

/**
 * The adapter to be used together with the recycler view of activities
 * that display a list of cocktail as cards.
 * It creates and inject Cocktail card views into the recycler view.
 */
public class CocktailCardsAdapter extends RecyclerView.Adapter<CocktailCardsAdapter.CocktailsViewHolder> {

    private final List<Cocktail> cocktails;
    private static CocktailsViewHolder.ClickListener clickListener;

    /**
     * Instantiates a new CocktailsCardsAdapter.
     *
     * @param cocktails the cocktails
     */
    public CocktailCardsAdapter(List<Cocktail> cocktails) {
        this.cocktails = cocktails;
    }

    @Override
    public int getItemCount() {
        return cocktails.size();
    }

    /**
     * Sets the card click listener.
     *
     * @param clickListener the click listener
     */
    public void setOnItemClickListener(CocktailsViewHolder.ClickListener clickListener) {
        CocktailCardsAdapter.clickListener = clickListener;
    }

    @NonNull
    @Override
    public CocktailCardsAdapter.CocktailsViewHolder onCreateViewHolder(ViewGroup parent,
                                                                       int viewType) {
        // Create new views (invoked by the layout manager)
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CocktailCardBinding itemBinding = CocktailCardBinding.inflate(
                layoutInflater,
                parent,
                false);
        return new CocktailsViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CocktailsViewHolder holder, int position) {
        // Replace the contents of a view (invoked by the layout manager)
        holder.bind(cocktails.get(position));
    }

    /**
     * The view holder of Cocktail cards.
     * Provides a reference to the views for each data item.
     */
    public static class CocktailsViewHolder extends RecyclerView.ViewHolder {

        // since layout file is cocktail_card.xml,
        // then the generated binding class is CocktailCardBinding
        private final CocktailCardBinding binding;

        /**
         * Instantiates a new Cocktail card view holder.
         *
         * @param binding the binding object
         */
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
         * Performs the binding of the view with the given Cocktail
         *
         * @param cocktail the cocktail to be bound
         */
        void bind(Cocktail cocktail) {
            binding.setCocktail(cocktail);
            binding.executePendingBindings();
        }

        /**
         * The Click listener of a cocktail card.
         */
        public interface ClickListener {

            /**
             * On item click callback.
             *
             * @param position the position (index) of the item (e.g. in the recycler view)
             */
            void onItemClick(int position);

        }

    }

}
