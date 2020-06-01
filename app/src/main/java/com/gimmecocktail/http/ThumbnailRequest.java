package com.gimmecocktail.http;

import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.gimmecocktail.Observable;
import com.gimmecocktail.Observer;
import com.gimmecocktail.R;
import com.gimmecocktail.activities.Activities;
import com.gimmecocktail.model.Cocktail;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a request initialized to send an image request to a given url.
 * The initialized request is ready to be added to the RequestQueue.
 * When added, it will send get-image request and set the retrieved image
 * as the Cocktail thumbnail Bitmap.
 */
public class ThumbnailRequest implements Observable<Bitmap> {

    private static final int MAX_WIDTH = 300;
    private static final int MAX_HEIGHT = 300;
    private List<Observer<Bitmap>> observers = new ArrayList<>();
    private Bitmap bitmap;
    private ImageRequest request;

    public ThumbnailRequest(String url) {
        this(url, null, null);
    }

    /**
     * Instantiates a new ThumbnailRequest.
     *
     * @param url             the url of the image to be retrieved
     * @param mutableLiveData the mutable live data holding the cocktail
     * @param activity        the activity that instantiated the request (used for alerting errors)
     */
    public ThumbnailRequest(
            String url,
            final MutableLiveData<Cocktail> mutableLiveData,
            final AppCompatActivity activity) {
            request = new ImageRequest(
                url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap thumbnail) {
                        bitmap = thumbnail;
                        notifyResultToObservers();
                        if (mutableLiveData != null) {
                            Cocktail cocktail = mutableLiveData.getValue();
                            cocktail.setThumbnailBitmap(thumbnail);
                            mutableLiveData.setValue(cocktail);
                        }
                    }
                },
                MAX_WIDTH,
                MAX_HEIGHT,
                ImageView.ScaleType.FIT_CENTER,
                Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (activity != null) {
                            Activities.alert(
                                    activity.getString(R.string.connection_failed_title),
                                    activity.getString(R.string.connection_failed_image_message),
                                    activity,
                                    false);
                        }
                    }
                });
    }


    public ImageRequest getRequest() {
        return request;
    }

    @Override
    public void observe(Observer<Bitmap> observer) {
        observers.add(observer);
    }

    @Override
    public void detach(Observer<Bitmap> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyResultToObservers() {
        for (Observer<Bitmap> observer: observers) {
            observer.onResult(bitmap);
        }
    }
}
