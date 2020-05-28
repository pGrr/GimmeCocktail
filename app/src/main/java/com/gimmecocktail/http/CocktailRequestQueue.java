package com.gimmecocktail.http;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

public class CocktailRequestQueue<T> extends RequestQueue {

    private static final int MAX_CACHE_SIZE_IN_BYTES = 10 * 1024 * 1024; // 10MB

    public Context getContext() {
        return context;
    }

    private final Context context;
    private final MutableLiveData<T> mutableLiveData;

    public CocktailRequestQueue(Context context, MutableLiveData<T> mutableLiveData) {
        super(new DiskBasedCache(context.getCacheDir(), MAX_CACHE_SIZE_IN_BYTES), new BasicNetwork(new HurlStack()));
        this.context = context;
        this.mutableLiveData = mutableLiveData;
        this.start();
    }

    private MutableLiveData<T> getMutableLiveData() {
        return mutableLiveData;
    }
}
