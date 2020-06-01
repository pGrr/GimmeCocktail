package com.gimmecocktail.http;

import android.graphics.Bitmap;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gimmecocktail.Observable;
import com.gimmecocktail.Observer;
import com.gimmecocktail.model.Cocktail;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


public class CocktailRequest implements Observable<Cocktail> {

    private List<Observer<Cocktail>> observers = new ArrayList<>();
    private Cocktail cocktail;
    private RequestQueue requestQueue = new ApiRequestQueue();
    private JsonObjectRequest cocktailRequest;
    private ThumbnailRequest imageRequest;
    private Exception exception;

    public CocktailRequest(String url) {
        cocktailRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            final Cocktail cocktail = JsonResponses.cocktailSequenceFrom(response).get(0);
                            CocktailRequest.this.cocktail = cocktail;
                            CocktailRequest.this.imageRequest = new ThumbnailRequest(
                                    cocktail.getThumbnailUrl());
                            CocktailRequest.this.imageRequest.observe(new Observer<Bitmap>() {
                                @Override
                                public void onResult(Bitmap result) {
                                    cocktail.setThumbnailBitmap(result);
                                    notifyResultToObservers();
                                }

                                @Override
                                public void onError(Exception exception) {
                                    CocktailRequest.this.exception = exception;
                                    notifyErrorToObservers();
                                }
                            });
                            requestQueue.add(imageRequest.getRequest());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        exception = error;
                        notifyErrorToObservers();
                    }
                });
        requestQueue.add(cocktailRequest);
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

    @Override
    public void notifyErrorToObservers() {
        for (Observer<Cocktail> observer : observers) {
            observer.onError(exception);
        }
    }
}
