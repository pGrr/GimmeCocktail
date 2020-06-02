package com.gimmecocktail.http;

import android.graphics.Bitmap;
import android.widget.ImageView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.gimmecocktail.ObservableRequest;
import com.gimmecocktail.Observer;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates a request to the api and notifies observer with the Image result.
 * The request is only sent when send() is called, then the request is added to
 * the request queue and dispatched immediately.
 * Observers must be attached before calling send().
 */
public class BitMapRequest implements ObservableRequest<Bitmap> {

    private static final int MAX_WIDTH = 300;
    private static final int MAX_HEIGHT = 300;
    private List<Observer<Bitmap>> observers = new ArrayList<>();
    private final RequestQueue requestQueue;
    private final String url;


    /**
     * Instantiates a new Thumbnail request.
     *
     * @param url          the url
     * @param requestQueue the request queue
     */
    public BitMapRequest(String url, RequestQueue requestQueue) {
        this.url = url;
        this.requestQueue = requestQueue;
    }

    @Override
    public ObservableRequest<Bitmap> observe(Observer<Bitmap> observer) {
        observers.add(observer);
        return this;
    }

    @Override
    public ObservableRequest<Bitmap> detach(Observer<Bitmap> observer) {
        observers.remove(observer);
        return this;
    }

    @Override
    public void send() {
        requestQueue.add(new ImageRequest(
                url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap image) {
                        notifyResultToObservers(image);
                    }
                },
                MAX_WIDTH,
                MAX_HEIGHT,
                ImageView.ScaleType.FIT_CENTER,
                Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        notifyErrorToObservers(error);
                        error.printStackTrace();
                    }
                }));
    }

    @Override
    public void notifyResultToObservers(Bitmap result) {
        for (Observer<Bitmap> observer: observers) {
            observer.onResult(result);
        }
    }

    @Override
    public void notifyErrorToObservers(Exception e) {
        for (Observer<Bitmap> observer: observers) {
            observer.onError(e);
        }
    }

}
