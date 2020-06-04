package com.gimmecocktail.http;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gimmecocktail.R;
import com.gimmecocktail.activities.Activities;
import com.gimmecocktail.model.Cocktail;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import java.util.Objects;

/**
 * Provides a request initialized to send a search-cocktail-by-id request
 * to the Cocktail-DB API https://www.thecocktaildb.com/.
 * The initialized request is ready to be added to the RequestQueue.
 * When added, it will send the API-request and update
 * the given cocktail-list mutable live data with the results.
 */
class ByIdRequest extends JsonObjectRequest {

    /**
     * Instantiates a new ByIdRequest.
     *
     * @param id              the id of the cocktail to be retrieved
     * @param mutableLiveData the cocktail-list mutable live data to be updated with the result
     * @param activity        the activity that instantiated the request (used for alerting errors)
     */
    ByIdRequest(final String id,
                final MutableLiveData<List<Cocktail>> mutableLiveData,
                final AppCompatActivity activity) {
        super(
                Request.Method.GET,
                "https://www.thecocktaildb.com/api/json/v1/1/lookup.php?i=" + id,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject cocktailResponse) {
                        try {
                            List<Cocktail> cocktails = JsonResponses.cocktailSequenceFrom(cocktailResponse);
                            Objects.requireNonNull(mutableLiveData.getValue()).addAll(cocktails);
                            mutableLiveData.setValue(mutableLiveData.getValue());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Activities.alert(
                                activity.getString(R.string.connection_failed_title), 
                                activity.getString(R.string.connection_failed_message),
                                activity,
                                true);
                    }
                });
    }

}
