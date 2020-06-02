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
 * Creates a request to the api and notifies observer with the Cocktail-list result.
 * The request is only sent when send() is called, then the request is added to
 * the request queue and dispatched immediately.
 * Observers must be attached before calling send().
 */
public class CocktailListRequest implements ObservableRequest<List<Cocktail>> {

    private static final String URL_BY_ID_BASE = "https://www.thecocktaildb.com/api/json/v1/1/lookup.php?i=";
    private List<Observer<List<Cocktail>>> observers = new ArrayList<>();
    private List<Cocktail> cocktails;
    private final String url;
    private final RequestQueue requestQueue;

    /**
     * Instantiates a new Cocktail list request.
     *
     * @param url          the url
     * @param requestQueue the request queue
     */
    public CocktailListRequest(String url, RequestQueue requestQueue) {
        this.url = url;
        this.requestQueue = requestQueue;
        this.cocktails = new ArrayList<>();
    }

    @Override
    public void send() {
        requestQueue.add(new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        try {
                            final List<String> ids = JsonResponses.cocktailIdSequenceFrom(response);
                            for (final String id : ids) {
                                new CocktailRequest(URL_BY_ID_BASE + id, requestQueue)
                                    .observe(new Observer<Cocktail>() {
                                        @Override
                                        public void onResult(Cocktail result) {
                                            cocktails.add(result);
                                            if (cocktails.size() == ids.size()) {
                                                notifyResultToObservers(cocktails);
                                            }
                                        }
                                        @Override
                                        public void onError(Exception exception) {
                                            notifyErrorToObservers(exception);
                                            exception.printStackTrace();
                                        }
                                    });
                            }
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
                        error.printStackTrace();
                    }
                }));
    }

    @Override
    public ObservableRequest<List<Cocktail>> observe(Observer<List<Cocktail>> observer) {
        observers.add(observer);
        return this;
    }

    @Override
    public ObservableRequest<List<Cocktail>> detach(Observer<List<Cocktail>> observer) {
        observers.remove(observer);
        return this;
    }

    @Override
    public void notifyResultToObservers(List<Cocktail> cocktailList) {
        for (Observer<List<Cocktail>> observer : observers) {
            observer.onResult(cocktailList);
        }
    }

    @Override
    public void notifyErrorToObservers(Exception e) {
        for (Observer<List<Cocktail>> observer : observers) {
            observer.onError(e);
        }
    }
}
