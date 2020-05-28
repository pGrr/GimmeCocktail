package com.gimmecocktail.http;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gimmecocktail.model.Cocktail;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ByNameRequest extends JsonObjectRequest {

    public ByNameRequest(final String name, final MutableLiveData<List<Cocktail>> mutableLiveData) {
        super(
                Request.Method.GET,
                "https://www.thecocktaildb.com/api/json/v1/1/search.php?s=" + name,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject cocktailResponse) {
                        try {
                            List<Cocktail> cocktails = JsonResponses.cocktailSequenceFrom(cocktailResponse);
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
}
