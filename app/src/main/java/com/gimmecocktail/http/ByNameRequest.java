package com.gimmecocktail.http;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gimmecocktail.R;
import com.gimmecocktail.activities.Activities;
import com.gimmecocktail.model.Cocktail;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a request initialized to send a search-cocktail-by-name request
 * to the Cocktail-DB API https://www.thecocktaildb.com/.
 * The initialized request is ready to be added to the RequestQueue.
 * When added, it will send the API-request and update
 * the given cocktail-list mutable live data with the results.
 */
public class ByNameRequest extends JsonObjectRequest {

    /**
     * Instantiates a new ByNameRequest.
     *
     * @param name            the name of the cocktail to be queried
     * @param mutableLiveData the mutable live data to be updated with the results
     * @param activity        the activity that instantiated the request (used for alerting errors)
     */
    public ByNameRequest(final String name,
                         final MutableLiveData<List<Cocktail>> mutableLiveData,
                         final AppCompatActivity activity) {
        super(
                Request.Method.GET,
                "https://www.thecocktaildb.com/api/json/v1/1/search.php?s=" + name,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            mutableLiveData.setValue(new ArrayList<Cocktail>());
                            if (!(name == null) && ! name.isEmpty()) {
                                List<Cocktail> cocktails = JsonResponses.cocktailSequenceFrom(response);
                                mutableLiveData.setValue(cocktails);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NetworkError) {
                            Activities.alert(
                                    activity.getString(R.string.connection_failed_title),
                                    activity.getString(R.string.connection_failed_message),
                                    activity,
                                    true);
                        }
                    }
                });
    }
}
