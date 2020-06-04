package com.gimmecocktail.http;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.gimmecocktail.R;
import com.gimmecocktail.activities.Activities;
import com.gimmecocktail.model.Cocktail;
import java.util.Observable;
import java.util.Observer;

/**
 * Provides a request initialized to send an image request to a given url.
 * The initialized request is ready to be added to the RequestQueue.
 * When added, it will send get-image request immediately.
 */
public class ThumbnailRequest extends ImageRequest {

    private static final int MAX_WIDTH = 300;
    private static final int MAX_HEIGHT = 300;

    /**
     * Instantiates a new ThumbnailRequest, and on result sets the retrieved image
     * as the Cocktail thumbnail Bitmap of cocktail mutable live data.
     *
     * @param url             the url of the image to be retrieved
     * @param mutableLiveData the mutable live data holding the cocktail
     * @param activity        the activity that instantiated the request (used for alerting errors)
     */
    public ThumbnailRequest(
            String url,
            final MutableLiveData<Cocktail> mutableLiveData,
            final AppCompatActivity activity) {
        super(
                url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap thumbnail) {
                        Cocktail cocktail = mutableLiveData.getValue();
                        cocktail.setThumbnailBitmap(thumbnail);
                        mutableLiveData.setValue(cocktail);
                    }
                },
                MAX_WIDTH,
                MAX_HEIGHT,
                ImageView.ScaleType.FIT_CENTER,
                Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Activities.alert(
                                activity.getString(R.string.connection_failed_title),
                                activity.getString(R.string.connection_failed_image_message),
                                activity,
                                false);                    }
                });
    }

}
