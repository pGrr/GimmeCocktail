package com.gimmecocktail.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.gimmecocktail.Observer;
import com.gimmecocktail.R;
import com.gimmecocktail.databinding.RandomCocktailAppWidgetBinding;
import com.gimmecocktail.http.OneRandomRequest;
import com.gimmecocktail.model.Cocktail;

import java.util.Objects;

/**
 * Implementation of App Widget functionality.
 */
public class RandomCocktailAppWidget extends AppWidgetProvider {

    RandomCocktailAppWidgetBinding binding;

    static void updateAppWidget(final Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.random_cocktail_app_widget);
        new OneRandomRequest().observe(new Observer<Cocktail>() {

            @Override
            public void onResult(Cocktail cocktail) {
                views.setImageViewBitmap(R.id.widget_cocktail_thumbnail, cocktail.getThumbnailBitmap());
                views.setTextViewText(R.id.widget_cocktail_name_text, cocktail.getName());
                views.setTextViewText(R.id.widget_cocktail_ingredients_text, cocktail.getIngredients());
            }
        });
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.hasExtra("cocktail")) {
            Cocktail cocktail = Objects.requireNonNull(intent.getExtras()).getParcelable("cocktail");

        }

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple com.gimmecocktail.widgets.widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.random_cocktail_app_widget);
            remoteViews.setTextViewText(R.id.ingredients_text_widget, cock);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

