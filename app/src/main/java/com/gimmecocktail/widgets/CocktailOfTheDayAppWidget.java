package com.gimmecocktail.widgets;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.widget.RemoteViews;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import com.gimmecocktail.R;
import com.gimmecocktail.activities.SearchRandomActivity;
import com.gimmecocktail.model.Cocktail;
import java.util.Date;
import java.util.Objects;

/**
 * App widget that displays one random cocktail a day.
 * On widget enabling and then onwards once a day,
 * a work is scheduled to fetch a random cocktail from the API.
 * Until the connectivity is unavailable, a connection-error message is shown in the widget.
 * As soon as the connectivity becomes available and the scheduled work completes,
 * the cocktail info is updated in the widget.
 * Clicking on a cocktail displayed on the widget will open a SearchRandomCocktail activity
 * showing that specific cocktail info.
 */
public class CocktailOfTheDayAppWidget extends AppWidgetProvider {

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        saveImageSetTime(context, 0); // reset saved image time on widget enabling
    }

    @Override
    public void onUpdate(final Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // this logic prevents infinite loop which is caused by
        // the broadcast of ACTION_PACKAGE_CHANGED in the process of
        // idle resource cleanup by the work manager.
        // Also, it prevents the cocktail to be set more frequently than once a day.
        // The work will be scheduled only if not called recently and if the image wasn't set recently
        if(!hasWorkCalledRecently(context.getApplicationContext())
                && !hasCocktailSetRecently(context.getApplicationContext())){
            //schedule the fetch-random-cocktail work
            scheduleGetRandomCocktailWork(appWidgetIds, context);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        // when the work has fetched the cocktail an its image, it will send an intent
        // using AppWidgetManager.ACTION_APPWIDGET_UPDATE, which will be received here.
        if (!hasCocktailSetRecently(context) && intent.hasExtra("cocktail") && intent.hasExtra("image")) {
            // if the intent contains the work's data, update the widget's UI
            Cocktail cocktail = Objects.requireNonNull(intent.getExtras()).getParcelable("cocktail");
            Bitmap image = Objects.requireNonNull(intent.getExtras()).getParcelable("image");
            updateCocktailWidgets(context, cocktail, image);
        }
    }

    /**
     * Updates the widget UI with the cocktail data and its image,
     * and sets a pending intent that will start a SearchRandomActivity
     * passing the cocktail as extra whenever the user clicks on the widget.
     *
     * @param context the context
     * @param cocktail the cocktail
     * @param image the cocktail's image
     */
    private static void updateCocktailWidgets(Context context, Cocktail cocktail, Bitmap image) {
        // the widgets may have many instances that must be updated, fetch their ids
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetId = appWidgetManager.getAppWidgetIds(
                new ComponentName(context, CocktailOfTheDayAppWidget.class));
        // configure a pending intent that will start SearchRandomActivity
        // passing the cocktail as extra whenever the user clicks on the widget
        Intent configIntent = new Intent(context, SearchRandomActivity.class);
        configIntent.putExtra("cocktail", cocktail);
        PendingIntent configPendingIntent = PendingIntent.getActivity(
                context, 0, configIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // update the widget UI
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.cocktail_of_the_day_app_widget);
        views.setOnClickPendingIntent(R.id.cocktail_widget, configPendingIntent);
        views.setImageViewBitmap(R.id.widget_cocktail_thumbnail, image);
        views.setTextViewText(R.id.widget_cocktail_name, cocktail.getName());
        appWidgetManager.updateAppWidget(appWidgetId, views);
        // save the time the cocktail was set
        saveImageSetTime(context, new Date().getTime());
    }

    /**
     * Configures and enqueues a work to fetch a random cocktail from the API,
     * using WorkManager. WorkManager will take care of retrying the request if it fails,
     * e.g. because of a lack of connectivity.
     *
     * @param appWidgetId the app widget id array
     * @param context the context
     */
    public static void scheduleGetRandomCocktailWork(int[] appWidgetId, Context context) {
        Data source = new Data.Builder()
                .putIntArray("widgetId", appWidgetId)
                .build();
        OneTimeWorkRequest workRequest =
                new OneTimeWorkRequest.Builder(RandomCocktailWorker.class)
                        .setInputData(source)
                        .build();
        WorkManager.getInstance(context).cancelUniqueWork("fetchRandomCocktail");
        WorkManager.getInstance(context).enqueueUniqueWork(
                "fetchRandomCocktail",
                ExistingWorkPolicy.KEEP,
                workRequest);
    }

    /**
     * This is used to prevent loop due to work manager ACTION_PACKAGE_CHANGED broadcast
     * that will results in a call to onUpdate method and thus to an infinite loop.
     * Unchecked scheduling of works in onUpdate are to be avoided for this reason.
     * This method use shared preferences to save the last time a work was enqueued,
     * and when onUpdate is called checks that more than 30 seconds have passed.
     * Only if more than 30 seconds have passed the work is enqueued.
     *
     * @param context the context
     * @return true if a work was scheduled in the last 30 seconds, false otherwise
     */
    @SuppressLint("ApplySharedPref")
    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    private static boolean hasWorkCalledRecently(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.location_pref), Context.MODE_PRIVATE);

        long lastApiCallTime = sharedPref.getLong(
                context.getString(R.string.last_api_call), 0);

        if(lastApiCallTime != 0){
            long currentTime = new Date().getTime();
            double seconds = (currentTime - lastApiCallTime)/1000;
            if(seconds > 30){
                sharedPref.edit().putLong(context.getString(R.string.last_api_call), currentTime).commit();
                return false;
            }
        }else{
            sharedPref.edit().putLong(context.getString(R.string.last_api_call), new Date().getTime()).commit();
        }
        return true;
    }

    /**
     * This is used to perform a check against the last time a cocktail was set for this widget.
     * A cocktail can be updated only once a day.
     *
     * @param context the context
     * @return true if the cocktail was set in the last day, false otherwise
     */
    @SuppressWarnings({"IntegerDivisionInFloatingPointContext", "BooleanMethodIsAlwaysInverted"})
    private static boolean hasCocktailSetRecently(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.location_pref), Context.MODE_PRIVATE);

        long lastImageSetMillis = sharedPref.getLong(
                context.getString(R.string.last_cocktail_set), 0);

        if(lastImageSetMillis != 0){
            long currentTime = new Date().getTime();
            double seconds = (currentTime - lastImageSetMillis)/1000;
            if(seconds > 86400000){
                return false;
            }
        }
        return lastImageSetMillis != 0;
    }

    /**
     * Saves in the shared preferences the time of setting the cocktail, in order to perform checks.
     * @param context the context
     * @param time the time to be set
     */
    private static void saveImageSetTime(Context context, long time) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.location_pref), Context.MODE_PRIVATE);
        sharedPref.edit().putLong(context.getString(R.string.last_cocktail_set), time).apply();
    }

}

