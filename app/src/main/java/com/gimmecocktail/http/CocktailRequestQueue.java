package com.gimmecocktail.http;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gimmecocktail.Cocktail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
