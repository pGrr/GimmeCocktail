package com.gimmecocktail.http;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gimmecocktail.Cocktail;
import com.gimmecocktail.activities.AbstractSearchCocktailsActivity;
import com.gimmecocktail.activities.ShowCocktailActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

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
                            Cocktail cocktail = CocktailRequests.cocktailSequenceFrom(response).get(0);
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
