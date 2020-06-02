package com.gimmecocktail.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.MutableLiveData;

import com.gimmecocktail.Observer;
import com.gimmecocktail.R;
import com.gimmecocktail.http.BitMapRequest;
import com.gimmecocktail.model.Cocktail;
import com.gimmecocktail.model.CocktailQueryMaker;
import com.gimmecocktail.utils.FavouriteCocktailImages;


/**
 * Provides static helper methods for activities.
 */
public class Activities {

    /**
     * Shows an alert dialog with the given title and message.
     *
     * @param title    the title
     * @param message  the message
     * @param activity the activity
     */
    public static void alert(String title,
                             String message,
                             final Activity activity,
                             final boolean shouldActivityBeClosed) {
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(
                        android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                if (shouldActivityBeClosed) {
                                    activity.finish();
                                }
                            }
                        }).show();
    }

    /**
     * Queries asynchronously the database to know if the cocktail was saved as favourite,
     * and updates the boolean mutable live data on result.
     * If an error occurs during the query, an alert modal is shown to the user.
     * @param cocktail the cocktail
     * @param isFavourite the mutable live data
     * @param queryMaker the query maker
     * @param activity the activity
     */
    public static void checkIsFavourite(
            final Cocktail cocktail,
            final MutableLiveData<Boolean> isFavourite,
            final CocktailQueryMaker queryMaker,
            final Activity activity) {
        queryMaker.isFavourite(cocktail, new com.gimmecocktail.Observer<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                isFavourite.setValue(result);
            }
            @Override
            public void onError(Exception exception) {
                Activities.alert(
                        activity.getString(R.string.database_connection_error_title),
                        activity.getString(R.string.database_connection_error_message),
                        activity,
                        true
                );
                exception.printStackTrace();
            }
        });
    }

    /**
     * Loads the thumbnail of a cocktail and injects it in the image view.
     * If the thumbnail was saved in memory it loads it from there, else it sends
     * an asynchronous request to the api.
     * If an error occurs, a modal is shown to the user.
     * @param cocktail the cocktail
     * @param imageViewId the id of the image view where to inject the thumbnail
     * @param activity the activity
     */
    public static void loadImage(final Cocktail cocktail, final int imageViewId, final Activity activity) {
        // if the image is present in memory, load it from there
        if (FavouriteCocktailImages.exists(cocktail.getId(), activity)) {
            FavouriteCocktailImages.load(cocktail.getId(), activity, imageViewId);
        } else {
            // else, query the api
            BitMapRequest request = new BitMapRequest(cocktail.getThumbnailUrl());
            request.observe(new Observer<Bitmap>() {
                @Override
                public void onResult(Bitmap result) {
                    cocktail.setThumbnailBitmap(result);
                    ImageView image = activity.findViewById(imageViewId);
                    image.setImageBitmap(result);
                }
                @Override
                public void onError(Exception exception) {
                    Activities.alert(
                            activity.getString(R.string.database_connection_error_title),
                            activity.getString(R.string.database_connection_error_message),
                            activity,
                            true
                    );
                }
            });
            request.execute();
        }
    }

}
