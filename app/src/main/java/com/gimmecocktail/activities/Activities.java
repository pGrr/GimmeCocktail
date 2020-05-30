package com.gimmecocktail.activities;

import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


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
    public static void alert(String title, String message, final AppCompatActivity activity) {
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(
                        android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                activity.finish();
                            }
                        }).show();
    }

}
