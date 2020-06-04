package com.gimmecocktail.widgets;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.gimmecocktail.R;
import com.gimmecocktail.http.ApiRequestQueue;
import com.gimmecocktail.http.JsonResponses;
import com.gimmecocktail.model.Cocktail;
import com.gimmecocktail.model.CocktailQueryMaker;
import com.gimmecocktail.utils.FavouriteCocktailImages;

import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A worker that queries the API for a random cocktail and its image using WorkManager.
 * On success, it saves the cocktail in the database, the image in the favourites directory,
 * and sends an update broadcast to the widget holding the cocktail as extras.
 * On failure, e.g. for a lack of connectivity or for other reasons,
 * WorkManager will take care of retrying at the most appropriate time until it succeeds.
 */
public class RandomCocktailWorker extends Worker {

    /**
     * Instantiates a new Random cocktail worker.
     *
     * @param context      the context
     * @param workerParams the worker params
     */
    public RandomCocktailWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(getClass().getName(), "Work starting...");
        // creates the volley requestQueue
        final ApiRequestQueue requestQueue = new ApiRequestQueue(getApplicationContext());
        // create the query-maker to query the database
        final CocktailQueryMaker queryMaker = new CocktailQueryMaker(
                getApplicationContext(),
                CocktailQueryMaker.DbName.COCKTAIL_OF_THE_DAY_WIDGET
        );
        // Setup a RequestFuture object for a synchronous volley request for the cocktail
        RequestFuture<JSONObject> futureCocktail = RequestFuture.newFuture();
        // Create the volley request using the future object
        JsonObjectRequest cocktailRequest = new JsonObjectRequest(
                Request.Method.GET,
                "https://www.thecocktaildb.com/api/json/v1/1/random.php",
                null,
                futureCocktail,
                futureCocktail);
        // add the volley request to the request queue
        requestQueue.add(cocktailRequest);
        try {
            // wait for the response (blocking, timeout 10 seconds)
            JSONObject response = futureCocktail.get(10, TimeUnit.SECONDS);
            // on successful response, convert the json into a Cocktail
            List<Cocktail> cocktails = JsonResponses.cocktailSequenceFrom(response);
            Cocktail cocktail;
            if (!cocktails.isEmpty()) {
                cocktail = cocktails.get(0);
                Log.d(getClass().getName(), "Cocktail received from the api.");
            } else {
                Log.d(getClass().getName(), "No cocktail received from the api! Work will return Retry.");
                return Result.retry();
            }
            // fetch the previous cocktail (in order to delete its saved image in favourites folder)
            Log.d(getClass().getName(), "Fetching previous cocktail from the database...");
            cocktails = queryMaker.getAll().get();
            Cocktail previousCocktail = null;
            if (!cocktails.isEmpty()) {
                previousCocktail = cocktails.get(0);
                Log.d(getClass().getName(), "Previous cocktail fetched.");
                // remove previous cocktail saved from the database
                Log.d(getClass().getName(), "Deleting previous cocktail from the database...");
                queryMaker.clear().get();
            } else {
                Log.d(getClass().getName(), "No previous cocktail was found in the database.");
            }
            // when done, save the new one
            Log.d(getClass().getName(), "Saving the new cocktail in the database...");
            queryMaker.insertAll(cocktail).get();
            // Setup a RequestFuture object for a synchronous volley request for the image
            RequestFuture<Bitmap> futureImage = RequestFuture.newFuture();
            // Create the volley request using the future object
            ImageRequest imageRequest = new ImageRequest(
                    cocktail.getThumbnailUrl(),
                    futureImage,
                    300,
                    300,
                    ImageView.ScaleType.FIT_CENTER,
                    Bitmap.Config.RGB_565,
                    futureImage);
            // add the volley request to the request queue
            Log.d(getClass().getName(), "Fetching the image from the api...");
            requestQueue.add(imageRequest);
            // wait for the response (blocking, timeout 10 seconds)
            Bitmap image = futureImage.get(10, TimeUnit.SECONDS);
            // on result, save the image in the favourite directory
            Log.d(getClass().getName(), "Image received from the api, saving into favourite directory...");
            FavouriteCocktailImages.save(cocktail.getId(), image, getApplicationContext());
            if (previousCocktail != null) {
                // delete the previous cocktail image from the favourite directory
                Log.d(getClass().getName(), "Deleting the previous cocktail image from the favourite directory...");
                FavouriteCocktailImages.delete(previousCocktail.getId(), getApplicationContext());
            }
            // send an update intent for the widget holding the cocktail and the image as extras
            Log.d(getClass().getName(), "Send update intent to the widget holding cocktail and image as extras...");
            Intent widgetIntent = new Intent(getApplicationContext(), CocktailOfTheDayAppWidget.class);
            widgetIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            final int[] appWidgetIds = AppWidgetManager.getInstance(getApplicationContext())
                    .getAppWidgetIds(
                            new ComponentName(getApplicationContext(),
                                    CocktailOfTheDayAppWidget.class));
            widgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            widgetIntent.putExtra("cocktail", cocktail);
            widgetIntent.putExtra("image", image);
            getApplicationContext().sendBroadcast(widgetIntent);
            // return success for the WorkManager to know that this work doesn't need to be retried.
            // the work will be repeated after the configured time by the WorkManager
            Log.d(getClass().getName(), "Done. Work returns Success.");
            return Result.success();
        } catch (Exception e) {
            // on failure (e.g. lack of connectivity, volley exception, etc, return Retry
            // for the WorkManager to know that this work must be retried as soon as the
            // specified constraints are met (e.g. connectivity restored)
            Log.d(getClass().getName(), "Error! Work failed and returned Retry.");
            Log.e(getClass().getName(), Log.getStackTraceString(e));
            return Result.retry();
        }
    }
}
