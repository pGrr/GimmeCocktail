package com.gimmecocktail.http;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gimmecocktail.model.Cocktail;

import org.json.JSONException;
import org.json.JSONObject;

public class OneRandomRequest extends JsonObjectRequest {
    public OneRandomRequest(final MutableLiveData<Cocktail> mutableLiveData) {
        super(
                Method.GET,
                "https://www.thecocktaildb.com/api/json/v1/1/random.php",
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Cocktail cocktail = JsonResponses.cocktailSequenceFrom(response).get(0);
                            mutableLiveData.setValue(cocktail);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
    }
}
