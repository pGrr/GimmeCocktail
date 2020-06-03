package com.gimmecocktail.widgets;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gimmecocktail.http.ApiRequestQueue;
import com.gimmecocktail.http.JsonResponses;
import com.gimmecocktail.model.Cocktail;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Objects;

/**
 * A worker that queries the API for a random cocktail.
 * On success, it sends a broadcast to the widget holding the cocktail as extras.
 * On failure, the worker will retry.
 */
public class RandomCocktailWorker extends Worker {

    private ListenableWorker.Result result = Result.retry();
    private int appWidgetId[];

    public RandomCocktailWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        appWidgetId = workerParams.getInputData().getIntArray("widgetId");
    }

    @NonNull
    @Override
    public Result doWork() {
        final ApiRequestQueue requestQueue = new ApiRequestQueue(getApplicationContext());
        requestQueue.add(new JsonObjectRequest(
                Request.Method.GET,
                "https://www.thecocktaildb.com/api/json/v1/1/random.php",
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Cocktail cocktail = null;
                        try {
                            cocktail = JsonResponses.cocktailSequenceFrom(response).get(0);
                        } catch (JSONException e) {
                            Log.e(getClass().getName(), Log.getStackTraceString(e));
                            RandomCocktailWorker.this.result = Result.retry();
                        }
                        final Cocktail finalCocktail = cocktail;
                        requestQueue.add(new ImageRequest(
                                Objects.requireNonNull(cocktail).getThumbnailUrl(),
                                new Response.Listener<Bitmap>() {
                                    @Override
                                    public void onResponse(Bitmap image) {
                                        RandomCocktailWorker.this.result = Result.success();
                                        Intent widgetIntent = new Intent();
                                        widgetIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                                        widgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                                        widgetIntent.putExtra("image", image);
                                        widgetIntent.putExtra("cocktail", finalCocktail);
                                        getApplicationContext().sendBroadcast(widgetIntent);
                                        LocalBroadcastManager.getInstance(getApplicationContext())
                                                .sendBroadcast(widgetIntent);
                                    }
                                },
                                300,
                                300,
                                ImageView.ScaleType.FIT_CENTER,
                                Bitmap.Config.RGB_565,
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.e(getClass().getName(), Log.getStackTraceString(error));
                                        RandomCocktailWorker.this.result = Result.retry();
                                    }
                                }
                        ));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(getClass().getName(), Log.getStackTraceString(error));
                        RandomCocktailWorker.this.result = Result.retry();
                    }
                }));
        return result;
    }
}
