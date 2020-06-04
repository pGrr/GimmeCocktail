package com.gimmecocktail.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.RemoteViews;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.gimmecocktail.R;
import com.gimmecocktail.activities.SearchRandomActivity;
import com.gimmecocktail.model.Cocktail;
import com.gimmecocktail.model.CocktailQueryMaker;
import com.gimmecocktail.utils.FavouriteCocktailImages;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * App widget that displays one random cocktail a day and opens an activity showing the
 * cocktail details on widget-click.
 * A unique periodic work is scheduled on enabling the widget to fetch a random cocktail from the API.
 * Until the connectivity is unavailable, a connection-error message is shown in the widget, or,
 * if a random cocktail was already fetched and saved into the database, that one will be shown.
 * As soon as the connectivity becomes available and the scheduled work completes, the widget
 * receives an intent and updates itself.
 */
public class CocktailOfTheDayAppWidget extends AppWidgetProvider {

    /**
     * On enabling the widget a unique periodic work is scheduled using WorkManager.
     * The work will try to fetch a random cocktail to the api, save it into the database and
     * finally send an update intent to this widget holding the cocktail as extras.
     * If a work with the same name was present (thus the work is already scheduled),
     * the new work is ignored.
     * On failure, the WorkManager will take care of retrying when the specified constraints
     * are met (e.g. the connectivity available).
     * On success, the WorkManager will repeat the work approximately after one day.
     *
     * @param context the context
     */
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetId = appWidgetManager.getAppWidgetIds(
                new ComponentName(context, CocktailOfTheDayAppWidget.class));
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                RandomCocktailWorker.class, 1, TimeUnit.DAYS)
                .setConstraints(new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED).build())
                .build();
        Log.d(getClass().getName(), "Creating new work (if one exists, this will be ignored)");
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "fetchRandomCocktail",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest);
    }

    /**
     * On disabling the widget, the scheduled UniquePeriodicWork is cancelled.
     *
     * @param context the context
     */
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        WorkManager.getInstance(context).cancelUniqueWork("fetchRandomCocktail");
        Log.d(getClass().getName(), "Existing works are now deleted.");
    }

    /**
     * onReceive() is used instead of onUpdate() in order to use the intent extras, which may contain
     * a new cocktail sent from the worker.
     * Only ACTION_APPWIDGET_UPDATE is considered, any other action won't produce any effect.
     * When an ACTION_APPWIDGET_UPDATE intent is received, if the intent holds a cocktail (and thus
     * it was sent by the worker) the cocktail is loaded from there.
     * Else (e.g. on device restart), the widget will attempt to load the cocktail info from the
     * database (where the worker should have saved it if its api request was successful).
     * If neither intent extras nor the database contain the cocktail (e.g. if the first ever widget
     * of this kind was added without network connectivity), then the default error message
     * is shown to the user until the worker will complete its task and finally update the widget.
     *
     * @param context the context
     * @param intent the intent
     */
    @Override
    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context, intent);
        // when the work has fetched the cocktail an its image, it will send an intent
        // using AppWidgetManager.ACTION_APPWIDGET_UPDATE, which will be received here;
        if (Objects.requireNonNull(intent.getAction()).equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            Log.d(getClass().getName(),"update intent received");
            // if the intent holds a cocktail and its image, then update the widget with them
            if (intent.hasExtra("cocktail") && intent.hasExtra("image")) {
                Log.d(getClass().getName(), "loading cocktail from extras");
                Cocktail cocktail = Objects.requireNonNull(intent.getExtras()).getParcelable("cocktail");
                Bitmap image = Objects.requireNonNull(intent.getExtras()).getParcelable("image");
                updateCocktailWidgets(context, cocktail, image);
            } else {
                // else attempt to load the cocktail from the database, if present
                CocktailQueryMaker queryMaker = new CocktailQueryMaker(
                        context, CocktailQueryMaker.DbName.COCKTAIL_OF_THE_DAY_WIDGET);
                Future<List<Cocktail>> futureCocktails = queryMaker.getAll();
                try {
                    if (!futureCocktails.get().isEmpty()) {
                        Log.d(getClass().getName(), "loading cocktail from database...");
                        Cocktail cocktail = futureCocktails.get().get(0);
                        updateCocktailWidgets(context, cocktail, null);
                        Log.d(getClass().getName(), "done.");
                    } else {
                        Log.d(getClass().getName(), "Database is empty: " + futureCocktails.get().isEmpty());
                    }
                } catch (Exception e) {
                    Log.d(getClass().getName(), "Database query failed!");
                    Log.e(getClass().getName(), Log.getStackTraceString(e));
                }
            }
        }
    }

    /**
     * Updates the widget UI using the given cocktail and image and sets a pending intent
     * that, on click, will open the current cocktail inside an activity to see its full details.
     *
     * @param context the context
     * @param cocktail the cocktail
     * @param image the image
     */
    private void updateCocktailWidgets(final Context context, final Cocktail cocktail, Bitmap image) {
        Log.d(getClass().getName(), "Updating the widget UI with the cocktail " + cocktail.getName() + "...");
        // the widgets may have many instances that must be updated, fetch their ids
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        final int[] appWidgetId = appWidgetManager.getAppWidgetIds(
                new ComponentName(context, CocktailOfTheDayAppWidget.class));
        // configure a pending intent that will start SearchRandomActivity
        // passing the cocktail as extra whenever the user clicks on the widget
        Intent configIntent = new Intent(context, SearchRandomActivity.class);
        configIntent.putExtra("cocktail", cocktail);
        PendingIntent configPendingIntent = PendingIntent.getActivity(
                context, 0, configIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // create the remote views, to be used to update the widget ui
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.cocktail_of_the_day_app_widget);
        // set onclick pending intent
        views.setOnClickPendingIntent(R.id.cocktail_widget, configPendingIntent);
        // update cocktail name
        views.setTextViewText(R.id.widget_cocktail_name, cocktail.getName());
        // inject the image into the image-view
        if (image != null) {
            // which is either passed as extra from the worker
            Log.d(getClass().getName(), "Loading the image from intent extras...");
            views.setImageViewBitmap(R.id.widget_cocktail_thumbnail, image);
        } else {
            // or saved in the favourites directory
            Log.d(getClass().getName(), "Loading the image from favourites directory...");
            FavouriteCocktailImages.load(cocktail.getId(), context, views, R.id.widget_cocktail_thumbnail);
        }
        // finally update the widget UI with the setup remote views object
        Log.d(getClass().getName(), "Done, the widget UI will now be updated.");
        AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, views);
    }

}

