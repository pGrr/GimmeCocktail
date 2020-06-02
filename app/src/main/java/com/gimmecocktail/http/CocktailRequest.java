package com.gimmecocktail.http;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gimmecocktail.ObservableRequest;
import com.gimmecocktail.Observer;
import com.gimmecocktail.model.Cocktail;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates a request to the api and notifies observer with the Cocktail result.
 * The request is only sent when send() is called, then the request is added to
 * the request queue and dispatched immediately.
 * Observers must be attached before calling send().
 */
public class CocktailRequest implements ObservableRequest<Cocktail> {

    private final List<Observer<Cocktail>> observers = new ArrayList<>();
    private final String url;
    private final RequestQueue requestQueue;

    /**
     * Instantiates a new Cocktail request.
     *
     * @param url          the url
     * @param requestQueue the request queue
     */
    public CocktailRequest(String url, RequestQueue requestQueue) {
        this.url = url;
        this.requestQueue = requestQueue;
    }

    @Override
    public void send() {
        requestQueue.add(new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            final Cocktail cocktail = JsonResponses.cocktailSequenceFrom(response).get(0);
                            notifyResultToObservers(cocktail);
                        } catch (JSONException e) {
                            notifyErrorToObservers(e);
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        notifyErrorToObservers(error);
                    }
                }));
    }

    @Override
    public ObservableRequest<Cocktail> observe(Observer<Cocktail> observer) {
        observers.add(observer);
        return this;
    }

    @Override
    public ObservableRequest<Cocktail> detach(Observer<Cocktail> observer) {
        observers.remove(observer);
        return this;
    }

    @Override
    public void notifyResultToObservers(Cocktail result) {
        for (Observer<Cocktail> observer : observers) {
            observer.onResult(result);
        }
    }

    @Override
    public void notifyErrorToObservers(Exception e) {
        for (Observer<Cocktail> observer : observers) {
            observer.onError(e);
        }
    }
}
