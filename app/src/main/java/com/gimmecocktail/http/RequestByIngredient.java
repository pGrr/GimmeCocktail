package com.gimmecocktail.http;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gimmecocktail.Cocktail;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public final class RequestByIngredient extends CocktailRequestQueue {

    public RequestByIngredient(Context context, MutableLiveData<List<Cocktail>> cocktails, String ingredient) {
        super(context, cocktails);
        JsonObjectRequest request = searchByIngredient(ingredient);
        this.add(request);
    }

    private JsonObjectRequest searchByIngredient(String ingredient) {
        final MutableLiveData<List<Cocktail>> mutableLiveData = getMutableLiveData();
        return new JsonObjectRequest(
                Request.Method.GET,
                this.urlByIngredient(ingredient),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject cocktailResponse) {
                        try {
                            List<Cocktail> cocktails = cocktailSequenceFrom(cocktailResponse);
                            Log.d("ApiTest", "Response: " + cocktails.toString());
                            mutableLiveData.setValue(cocktails);
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
    }

    private String urlByIngredient(String ingredient) {
        return "https://www.thecocktaildb.com/api/json/v1/1/search.php?s=" + ingredient;
    }
}
