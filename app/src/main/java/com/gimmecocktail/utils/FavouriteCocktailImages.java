package com.gimmecocktail.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
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
            String fileName, Bitmap bitmapImage, Activity activity){
        ContextWrapper cw = new ContextWrapper(activity);
        File directory = cw.getDir(FAVOURITES_THUMBNAILS_DIR, Context.MODE_PRIVATE);
        File mypath = new File(directory,fileName);
        try (FileOutputStream fos = new FileOutputStream(mypath)) {
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
            String fileName, Activity activity, int imageViewId) {
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
     * Deletes a favourite thumbnail image with the given name from internal storage.
     *
     * @param fileName the file name
     * @param activity the activity
     */
    public static void delete(String fileName, Activity activity){
            ContextWrapper cw = new ContextWrapper(activity);
            File directory = cw.getDir(FAVOURITES_THUMBNAILS_DIR, Context.MODE_PRIVATE);
            File mypath = new File(directory,fileName);
            mypath.delete();
    }

    /**
     * Checks if a favourite thumbnail image with the given name exists in the internal storage
     *
     * @param fileName the file name
     * @param activity the activity
     * @return true if the file exists, false otherwise
     */
    public static boolean exists(String fileName, Activity activity) {
        ContextWrapper cw = new ContextWrapper(activity);
        File directory = cw.getDir(FAVOURITES_THUMBNAILS_DIR, Context.MODE_PRIVATE);
        File mypath = new File(directory,fileName);
        return mypath.exists();
    }

}
