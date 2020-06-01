package com.gimmecocktail.http;

import android.graphics.Bitmap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gimmecocktail.Observable;
import com.gimmecocktail.Observer;
import com.gimmecocktail.R;
import com.gimmecocktail.activities.Activities;
import com.gimmecocktail.model.Cocktail;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a request initialized to send a get-random-cocktail request
 * to the Cocktail-DB API https://www.thecocktaildb.com/.
 * The initialized request is ready to be added to the RequestQueue.
 * When added, it will send the API-request and update
 * the given cocktail mutable live data with the result.
 */
public class OneRandomRequest implements Observable<Cocktail> {

    private List<Observer<Cocktail>> observers = new ArrayList<>();
    private Cocktail cocktail;
    private RequestQueue requestQueue = new ApiRequestQueue();
    private JsonObjectRequest cocktailRequest;
    private ThumbnailRequest imageRequest;

    public OneRandomRequest(RequestQueue requestQueue) {
        this(null, null);
    }

    /**
     * Instantiates a new OneRandomRequest.
     *
     * @param mutableLiveData the mutable live data to be updated with the result
     * @param activity        the activity that instantiated the request (used for alerting errors)
     */
    public OneRandomRequest(final MutableLiveData<Cocktail> mutableLiveData,
                            final AppCompatActivity activity) {
        cocktailRequest = new JsonObjectRequest(
                Request.Method.GET,
                "https://www.thecocktaildb.com/api/json/v1/1/random.php",
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            final Cocktail cocktail = JsonResponses.cocktailSequenceFrom(response).get(0);
                            OneRandomRequest.this.cocktail = cocktail;
                            OneRandomRequest.this.imageRequest = new ThumbnailRequest(
                                    cocktail.getThumbnailUrl());
                            OneRandomRequest.this.imageRequest.observe(new Observer<Bitmap>() {
                                @Override
                                public void onResult(Bitmap result) {
                                    cocktail.setThumbnailBitmap(result);
                                    notifyResultToObservers();
                                }
                            });
                            notifyResultToObservers();
                            if (mutableLiveData != null) {
                                mutableLiveData.setValue(cocktail);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (activity != null) {
                            Activities.alert(
                                    activity.getString(R.string.connection_failed_title),
                                    activity.getString(R.string.connection_failed_message),
                                    activity,
                                    true);
                        }
                    }
                });
        requestQueue.add(cocktailRequest);
        requestQueue.add(imageRequest.getRequest());
    }

    @Override
    public void observe(Observer<Cocktail> observer) {
        observers.add(observer);
    }

    @Override
    public void detach(Observer<Cocktail> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyResultToObservers() {
        for (Observer<Cocktail> observer : observers) {
            observer.onResult(cocktail);
        }
    }
}
