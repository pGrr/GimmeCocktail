package com.gimmecocktail;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.widget.RemoteViews;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import com.gimmecocktail.activities.SearchRandomActivity;
import com.gimmecocktail.model.Cocktail;
import com.gimmecocktail.widgets.RandomCocktailWorker;

import java.util.Date;
import java.util.Objects;

/**
 * Implementation of App Widget functionality.
 */
public class CocktailOfTheDayAppWidget extends AppWidgetProvider {

    public static final String SET_COCKTAIL = "com.gimmecocktail.CocktailOfTheDayAppWidget.SET_COCKTAIL";

    @Override
    public void onEnabled(Context context) {
        LocalBroadcastManager.getInstance(context)
                .registerReceiver(this, new IntentFilter(SET_COCKTAIL));
    }

    @Override
    public void onUpdate(final Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //this logic prevents infinite loop which is caused due to
        //the broadcast of ACTION_PACKAGE_CHANGED in the process of
        // idle resource cleanup by the work manager
        if(hasWorkCalledRecently(context.getApplicationContext())){
            return;
        }
        //schedule location fetcher job
        scheduleGetRandomCocktailWork(appWidgetIds, context);
        updateCocktailWidgets(context, "Waiting for network connection...", null);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        //if (intent.getAction().equals(SET_COCKTAIL)) { }
        if (intent.hasExtra("cocktail")) {
            //display place after fetching
            Cocktail cocktail = Objects.requireNonNull(intent.getExtras()).getParcelable("cocktail");
            Bitmap image = Objects.requireNonNull(intent.getExtras()).getParcelable("image");
            updateCocktailWidgets(context, cocktail.getName(), image);
        }
    }

    private static void updateCocktailWidgets(Context context, String name, Bitmap image) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int appWidgetId[] = appWidgetManager
                .getAppWidgetIds(new ComponentName(context, CocktailOfTheDayAppWidget.class));

        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.cocktail_of_the_day_app_widget);
        Intent configIntent = new Intent(context, SearchRandomActivity.class);
        configIntent.putExtra("cocktail", name);
        PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.cocktail_widget, configPendingIntent);
        views.setImageViewBitmap(R.id.widget_cocktail_thumbnail, image);
        views.setTextViewText(R.id.widget_cocktail_name, name);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void scheduleGetRandomCocktailWork(int appWidgetId[], Context context) {
        Data source = new Data.Builder()
                .putIntArray("widgetId", appWidgetId)
                .build();
        OneTimeWorkRequest workRequest =
                new OneTimeWorkRequest.Builder(RandomCocktailWorker.class)
                        .setInputData(source)
                        .build();
        WorkManager.getInstance(context).enqueue(workRequest);
    }

    //this is used to prevent loop
    //due to work manager ACTION_PACKAGE_CHANGED broadcast
    //which results in a call to onUpdate method
    private static boolean hasWorkCalledRecently(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.location_pref), Context.MODE_PRIVATE);

        long lastApiCallTime = sharedPref.getLong(
                context.getString(R.string.last_api_call), 0);

        if(lastApiCallTime != 0){
            long currentTime = new Date().getTime();
            double seconds = (currentTime - lastApiCallTime)/1000;
            if(seconds > 30){ // 5
                sharedPref.edit().putLong(context.getString(R.string.last_api_call),
                        currentTime).commit();
                return false;
            }
        }else{
            sharedPref.edit().putLong(context.getString(R.string.last_api_call),
                    new Date().getTime()).commit();
        }
        return true;
    }

    /*
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        int maxWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        int minWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int maxHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);
        int minHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        Toast.makeText(context, "Max height: " + maxHeight + "\n Min height: " + minHeight, Toast.LENGTH_SHORT).show();
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.cocktail_of_the_day_app_widget);
        if (maxHeight < 300) {
            views.setViewVisibility(R.id.widget_cocktail_ingredients, View.INVISIBLE);
        } else {
            views.setViewVisibility(R.id.widget_cocktail_ingredients, View.VISIBLE);
        }
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }*/

}

