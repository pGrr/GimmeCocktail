package com.gimmecocktail.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.RemoteViews;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Provides static helper methods for reading and writing favourite cocktail images
 */
public class FavouriteCocktailImages {

    /**
     * The directory of internal storage where favourite cocktails' thumbnails are saved
     */
    @SuppressWarnings("WeakerAccess")
    public static final String FAVOURITES_THUMBNAILS_DIR = "cocktail-thumbnails";

    /**
     * Saves a favourite cocktail image to internal storage.
     *
     * @param fileName      the file name
     * @param bitmapImage   the bitmap image
     * @param activity      the activity
     * @return              the absolute path of the saved file
     */
    public static String save(
            String fileName, Bitmap bitmapImage, Context activity){
        ContextWrapper cw = new ContextWrapper(activity);
        File directory = cw.getDir(FAVOURITES_THUMBNAILS_DIR, Context.MODE_PRIVATE);
        File myPath = new File(directory,fileName);
        try (FileOutputStream fos = new FileOutputStream(myPath)) {
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }

    /**
     * Loads a favourite cocktail image from the internal storage
     * and injects it in the View with the given id.
     *
     * @param fileName      the file name
     * @param activity      the activity
     * @param imageViewId   the image view id
     */
    public static void load(
            String fileName, AppCompatActivity activity, int imageViewId) {
        ContextWrapper cw = new ContextWrapper(activity);
        File directory = cw.getDir(FAVOURITES_THUMBNAILS_DIR, Context.MODE_PRIVATE);
        File f = new File(directory, fileName);
        try (FileInputStream fis = new FileInputStream(f)) {
            Bitmap b = BitmapFactory.decodeStream(fis);
            ImageView img = activity.findViewById(imageViewId);
            img.setImageBitmap(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads a favourite cocktail image from the internal storage
     * and injects it in the RemoteView's imageView with the given id.
     *
     * @param fileName      the file name
     * @param context      the activity
     * @param views        the remote views
     * @param imageViewId   the image view id
     */
    public static void load(String fileName, Context context, RemoteViews views, int imageViewId) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir(FAVOURITES_THUMBNAILS_DIR, Context.MODE_PRIVATE);
        File f = new File(directory, fileName);
        try (FileInputStream fis = new FileInputStream(f)) {
            Bitmap b = BitmapFactory.decodeStream(fis);
            views.setImageViewBitmap(imageViewId, b);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a favourite thumbnail image with the given name from internal storage.
     *
     * @param fileName the file name
     * @param activity the activity
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void delete(String fileName, Context activity){
            ContextWrapper cw = new ContextWrapper(activity);
            File directory = cw.getDir(FAVOURITES_THUMBNAILS_DIR, Context.MODE_PRIVATE);
            File myPath = new File(directory,fileName);
            myPath.delete();
    }

    /**
     * Checks if a favourite thumbnail image with the given name exists in the internal storage
     *
     * @param fileName the file name
     * @param context the context
     * @return true if the file exists, false otherwise
     */
    public static boolean exists(String fileName, Context context) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir(FAVOURITES_THUMBNAILS_DIR, Context.MODE_PRIVATE);
        File myPath = new File(directory,fileName);
        return myPath.exists();
    }

}
