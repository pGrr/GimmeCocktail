package com.gimmecocktail.http;

import android.graphics.Bitmap;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gimmecocktail.Observable;
import com.gimmecocktail.Observer;
import com.gimmecocktail.model.Cocktail;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CocktailListRequest implements Observable<List<Cocktail>> {

    private List<Observer<List<Cocktail>>> observers = new ArrayList<>();
    private List<Cocktail> cocktails;
    private RequestQueue requestQueue = new ApiRequestQueue();
    private JsonObjectRequest cocktailsRequest;
    private ThumbnailRequest imageRequest;
    private Exception exception;
    private int requestCounter = 0;

    public CocktailListRequest(String url) {
        cocktailsRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            final List<Cocktail> cocktails = JsonResponses.cocktailSequenceFrom(response);
                            for (final Cocktail cocktail : cocktails) {
                                imageRequest = new ThumbnailRequest(cocktail.getThumbnailUrl());
                                imageRequest.observe(new Observer<Bitmap>() {
                                    @Override
                                    public void onResult(Bitmap result) {
                                        cocktail.setThumbnailBitmap(result);
                                        cocktails.add(cocktail);
                                        requestCounter--;
                                        if (requestCounter == 0) {
                                            notifyResultToObservers();
                                        }
                                    }

                                    @Override
                                    public void onError(Exception exception) {
                                        CocktailListRequest.this.exception = exception;
                                        notifyErrorToObservers();
                                    }
                                });
                                requestQueue.add(imageRequest.getRequest());
                                requestCounter++;
                            }
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
    }

    @Override
    public void observe(Observer<List<Cocktail>> observer) {
        observers.add(observer);
    }

    @Override
    public void detach(Observer<List<Cocktail>> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyResultToObservers() {
        for (Observer<List<Cocktail>> observer : observers) {
            observer.onResult(cocktails);
        }
    }

    @Override
    public void notifyErrorToObservers() {
        for (Observer<List<Cocktail>> observer : observers) {
            observer.onError(exception);
        }
    }
}
