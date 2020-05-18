package com.gimmecocktail.http;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.gimmecocktail.Cocktail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CocktailRequestQueue extends RequestQueue {

    private static final int MAX_CACHE_SIZE_IN_BYTES = 10 * 1024 * 1024; // 10MB
    private final Context context;
    private final MutableLiveData<List<Cocktail>> mutableLiveData;

    public CocktailRequestQueue(Context context, MutableLiveData<List<Cocktail>> mutableLiveData) {
        super(
                new DiskBasedCache(context.getCacheDir(), MAX_CACHE_SIZE_IN_BYTES),
                new BasicNetwork(new HurlStack()));
        this.context = context;
        this.mutableLiveData = mutableLiveData;
        this.start();
    }

    protected final List<Cocktail> cocktailSequenceFrom(JSONObject jsonCocktails) throws JSONException {
        List<Cocktail> cocktails = new ArrayList<>();
        JSONArray drinksArray = jsonCocktails.getJSONArray("drinks");
        for (int i=0; i<drinksArray.length(); i++) {
            JSONObject jsonCocktail = drinksArray.getJSONObject(i);
            Cocktail cocktail = this.cocktailFrom(jsonCocktail);
            cocktails.add(cocktail);
            Log.d("ApiTest", cocktail.toString());
        }
        return cocktails;
    }

    protected final Cocktail cocktailFrom(JSONObject jsonCocktail) throws JSONException {
        Map<String,String> ingredients = new LinkedHashMap<>();
        for (int i=1; i<Cocktail.N_MAX_INGREDIENTS; i++) {
            String ingredient = jsonCocktail.getString("strIngredient" + i);
            String measure = jsonCocktail.getString("strMeasure" + i);
            if (ingredient != "null" && !ingredient.isEmpty()) {
                ingredients.put(ingredient, measure);
            }
        }
        String imageUrl = jsonCocktail.getString("strDrinkThumb");
        //fetchImage(imageUrl);
        return new Cocktail(
                jsonCocktail.getString("strDrink"),
                jsonCocktail.getString("strAlcoholic"),
                jsonCocktail.getString("strCategory"),
                ingredients,
                imageUrl,
                jsonCocktail.getString("strGlass"),
                jsonCocktail.getString("strInstructions"));
    }

    private void fetchImage(String url) {
        new ImageRequestQueue(context, mutableLiveData, url);
    }

    protected MutableLiveData<List<Cocktail>> getMutableLiveData() {
        return mutableLiveData;
    }
}
