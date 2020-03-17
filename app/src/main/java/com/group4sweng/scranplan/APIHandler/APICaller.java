package com.group4sweng.scranplan.APIHandler;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import io.sentry.core.Sentry;

public class APICaller {
    private Context mContext;
    private String mUrl = null;
    private Cache mCache;
    private Network mNetwork = new BasicNetwork(new HurlStack());
    private RequestQueue mRequestQueue;



    /**
     * Setup the caller
     */
    public void setUpAPICaller(Context context){
        setmContext(context);
        mCache = new DiskBasedCache(mContext.getCacheDir(), 1024*1024);
        mRequestQueue = new RequestQueue(mCache, mNetwork);
        mRequestQueue.start();

        StringRequest mStringRequest = new StringRequest(Request.Method.GET, mUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
            }
        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Sentry.captureException(error);
                    }
                });
    }

    public RequestQueue getmRequestQueue() {
        if(mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> mRequest){
        getmRequestQueue().add(mRequest);
    }


    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmNetwork(Network mNetwork) {
        this.mNetwork = mNetwork;
    }

    public Network getmNetwork() {
        return mNetwork;
    }
}
