package com.gimmecocktail.http;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

/**
 * Provides an initialized request queue which can be used to add requests.
 */
public class ApiRequestQueue extends RequestQueue {

    private static final int MAX_CACHE_SIZE_IN_BYTES = 10 * 1024 * 1024; // Cache size: 10MB

    /**
     * Instantiates a new Api request queue.
     *
     * @param context the context from which the request is to be initialized
     */
    public ApiRequestQueue(Context context) {
        super(new DiskBasedCache(context.getCacheDir(), MAX_CACHE_SIZE_IN_BYTES), new BasicNetwork(new HurlStack()));
        this.start();
    }

}
