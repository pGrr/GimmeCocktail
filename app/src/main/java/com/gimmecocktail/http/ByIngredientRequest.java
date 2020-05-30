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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

/**
 * Provides a request initialized to send a search-cocktail-by-ingredient request
 * to the Cocktail-DB API https://www.thecocktaildb.com/.
 * The initialized request is ready to be added to the RequestQueue.
 * When added, it will send the API-request and update
 * the given cocktail-list mutable live data with the results.
 */
public class ByIngredientRequest extends JsonObjectRequest {

    /**
     * Instantiates a new ByIngredientRequest.
     *
     * @param ingredient           the ingredient to be queried
     * @param mutableLiveData the cocktail-list mutable live data to be updated with the result
     * @param apiRequestQueue the ApiRequestQueue, necessary to send a second api-request to
     *                        retrieve a cocktail details once its id is known
     * @param activity        the activity that instantiated the request (used for alerting errors)
     */
    public ByIngredientRequest(final String ingredient,
                               final MutableLiveData<List<Cocktail>> mutableLiveData,
                               final ApiRequestQueue apiRequestQueue,
                               final AppCompatActivity activity) {
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
                                apiRequestQueue.add(new ByIdRequest(id, mutableLiveData, activity));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Activities.alert(
                                activity.getString(R.string.connection_failed_title),
                                activity.getString(R.string.connection_failed_message),
                                activity);
                    }
                });
    }
}
