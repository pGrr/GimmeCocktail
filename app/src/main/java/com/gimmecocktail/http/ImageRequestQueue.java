package com.gimmecocktail.http;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageRequest;
import com.gimmecocktail.Cocktail;

import java.util.List;

public class ImageRequestQueue extends RequestQueue {

    private static final int MAX_CACHE_SIZE_IN_BYTES = 10 * 1024 * 1024; // 10MB
    private static final int MAX_WIDTH = 300;
    private static final int MAX_HEIGHT = 300;

    public ImageRequestQueue(Context context, final ImageView imageView, String url) {
        super(
                new DiskBasedCache(context.getCacheDir(), MAX_CACHE_SIZE_IN_BYTES),
                new BasicNetwork(new HurlStack()));
        this.start();
        ImageRequest thumbnailRequest = new ImageRequest(
                url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap thumbnail) {
                        imageView.setImageBitmap(thumbnail);
                    }
                },
                MAX_WIDTH,
                MAX_HEIGHT,
                ImageView.ScaleType.FIT_CENTER,
                Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        this.add(thumbnailRequest);
    }
}
