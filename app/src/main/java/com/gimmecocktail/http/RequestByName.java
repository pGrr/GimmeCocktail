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

public final class RequestByName extends CocktailRequestQueue {

    public RequestByName(Context context, MutableLiveData<List<Cocktail>> cocktails, String name) {
        super(context, cocktails);
        JsonObjectRequest request = searchByName(name);
        this.add(request);
    }

    private JsonObjectRequest searchByName(String name) {
        final MutableLiveData<List<Cocktail>> mutableLiveData = getMutableLiveData();
        return new JsonObjectRequest(
                Request.Method.GET,
                this.urlByName(name),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject cocktailResponse) {
                        try {
                            List<Cocktail> cocktails = cocktailSequenceFrom(cocktailResponse);
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

    private String urlByName(String name) {
        return "https://www.thecocktaildb.com/api/json/v1/1/search.php?s=" + name;
    }
}
