package com.gimmecocktail.http;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gimmecocktail.model.Cocktail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ByIngredientRequest extends JsonObjectRequest {
    public ByIngredientRequest(final String ingredient, final MutableLiveData<List<Cocktail>> mutableLiveData, final CocktailRequestQueue cocktailRequestQueue) {
        super(
                Request.Method.GET,
                "https://www.thecocktaildb.com/api/json/v1/1/filter.php?i=" + ingredient,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject cocktailResponse) {
                        try {
                            JSONArray drinks = cocktailResponse.getJSONArray("drinks");
                            for (int i=0; i<drinks.length(); i++) {
                                JSONObject jsonCocktail = drinks.getJSONObject(i);
                                String id = jsonCocktail.getString("idDrink");
                                cocktailRequestQueue.add(new ByIdRequest(id, mutableLiveData));
                            }
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
}
