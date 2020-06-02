package com.gimmecocktail.http;

import com.android.volley.RequestQueue;

/**
 * A Factory for Requests to the CocktailDb Api
 */
public class RequestFactory {

    /**
     * Random cocktail request.
     *
     * @param requestQueue the request queue
     * @return the cocktail request
     */
    public static CocktailRequest random(RequestQueue requestQueue) {
        return new CocktailRequest(
                "https://www.thecocktaildb.com/api/json/v1/1/random.php",
                requestQueue);
    }

    /**
     * By id cocktail request.
     *
     * @param id           the id
     * @param requestQueue the request queue
     * @return the cocktail request
     */
    public static CocktailRequest byId(String id, RequestQueue requestQueue) {
        return new CocktailRequest(
                "https://www.thecocktaildb.com/api/json/v1/1/lookup.php?i=" + id,
                requestQueue);
    }

    /**
     * By name cocktail list request.
     *
     * @param name         the name
     * @param requestQueue the request queue
     * @return the cocktail list request
     */
    public static CocktailListRequest byName(String name, RequestQueue requestQueue) {
        return new CocktailListRequest(
                "https://www.thecocktaildb.com/api/json/v1/1/search.php?s=" + name,
                requestQueue);
    }

    /**
     * By ingredient cocktail list request.
     *
     * @param ingredient   the ingredient
     * @param requestQueue the request queue
     * @return the cocktail list request
     */
    public static CocktailListRequest byIngredient(String ingredient, RequestQueue requestQueue) {
        return new CocktailListRequest(
                "https://www.thecocktaildb.com/api/json/v1/1/filter.php?i=" + ingredient,
                requestQueue);
    }


    /**
     * Bit map request.
     *
     * @param url          the url
     * @param requestQueue the request queue
     * @return the bit map request
     */
    public static BitMapRequest bitMap(String url, RequestQueue requestQueue) {
        return new BitMapRequest(url, requestQueue);
    }

}
