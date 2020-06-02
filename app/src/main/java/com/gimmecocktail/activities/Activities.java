package com.gimmecocktail.activities;

import android.app.Activity;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.MutableLiveData;
import com.gimmecocktail.R;
import com.gimmecocktail.model.Cocktail;
import com.gimmecocktail.model.CocktailQueryMaker;

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

}
