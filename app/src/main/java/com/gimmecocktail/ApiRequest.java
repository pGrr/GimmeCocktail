package com.gimmecocktail;

import android.content.Context;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ApiRequest {

    private static final int MAX_CACHE_SIZE_IN_BYTES = 10 * 1024 * 1024; // 10MB

    private final Context context;

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    private final RequestQueue requestQueue;

    public ApiRequest(Context context) {
        this.context = context;
        this.requestQueue = this.createRequestQueue();
        this.requestQueue.start();
    }

    private final RequestQueue createRequestQueue() {
        // Instantiate the cache
        //Cache cache = new DiskBasedCache(this.context.getCacheDir(), MAX_CACHE_SIZE_IN_BYTES);
        // Set up the network to use HttpURLConnection as the HTTP client.
        //Network network = new BasicNetwork(new HurlStack());
        // Instantiate the RequestQueue with the cache and network.
        //return new RequestQueue(cache, network);
        return Volley.newRequestQueue(this.context);
    }

    public final JsonObjectRequest searchByName(String name) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                this.urlByName(name),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("ApiTest", "Response: " + cocktailSequenceFrom(response).toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ApiTest", "Response: " + error.getMessage());
                        error.printStackTrace();
                    }
                });
        this.requestQueue.add(jsonObjectRequest);
        return jsonObjectRequest;
    }

    private final List<Cocktail> cocktailSequenceFrom(JSONObject jsonCocktails) throws JSONException {
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

    private final Cocktail cocktailFrom(JSONObject jsonCocktail) throws JSONException {
        Map<String,String> ingredients = new LinkedHashMap<>();
        for (int i=1; i<Cocktail.N_MAX_INGREDIENTS; i++) {
            String ingredient = jsonCocktail.getString("strIngredient" + i);
            String measure = jsonCocktail.getString("strMeasure" + i);
            if (ingredient != "null" && !ingredient.isEmpty()) {
                ingredients.put(ingredient, measure);
            }
        }
        return new Cocktail(
                jsonCocktail.getString("strDrink"),
                jsonCocktail.getString("strAlcoholic"),
                jsonCocktail.getString("strCategory"),
                ingredients,
                null,
                jsonCocktail.getString("strGlass"),
                jsonCocktail.getString("strInstructions"));
    }

    private final String urlByName(String name) {
        return "https://www.thecocktaildb.com/api/json/v1/1/search.php?s=" + name;
    }

    private final String urlByIngredient(String ingredient) {
        return "https://www.thecocktaildb.com/api/json/v1/1/search.php?s=" + ingredient;
    }

}
