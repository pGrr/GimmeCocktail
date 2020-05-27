package com.gimmecocktail.http;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

public class ThumbnailRequest extends ImageRequest {

    private static final int MAX_WIDTH = 300;
    private static final int MAX_HEIGHT = 300;

    public ThumbnailRequest(String url, Context context, final ImageView imageView) {
        super(
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
    }
}
