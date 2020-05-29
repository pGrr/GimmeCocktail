package com.gimmecocktail.http;

import androidx.lifecycle.MutableLiveData;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gimmecocktail.model.Cocktail;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Provides a request initialized to send a get-random-cocktail request
 * to the Cocktail-DB API https://www.thecocktaildb.com/.
 * The initialized request is ready to be added to the RequestQueue.
 * When added, it will send the API-request and update
 * the given cocktail mutable live data with the result.
 */
public class OneRandomRequest extends JsonObjectRequest {

    /**
     * Instantiates a new OneRandomRequest.
     *
     * @param mutableLiveData the mutable live data to be updated with the result
     */
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
