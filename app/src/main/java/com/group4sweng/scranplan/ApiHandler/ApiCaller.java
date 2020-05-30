package com.group4sweng.scranplan.ApiHandler;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * API Caller Author: NBillis
 * (c) CoDev 2020
 * https://square.github.io/okhttp/
 */
class APICaller {

    final OkHttpClient mHttpClient = new OkHttpClient();
    private String mURL;
    private String mHeaderKey;
    private String mHeaderValue;
    private String mQueryKey;
    private String mQueryValue;


    public Response sendGet() throws Exception{
        Response mResponse;
        Request mRequest = new Request.Builder()
                .url(mURL)
                .addHeader(mHeaderKey, mHeaderValue)
                .build();
        mResponse = mHttpClient.newCall(mRequest).execute();

        return mResponse;

    }

    public Response sendPost() throws Exception{
        RequestBody formBody = new FormBody.Builder()
                .add(mQueryKey, mQueryValue)
                .build();

        Request request = new Request.Builder()
                .url(mURL)
                .addHeader(mHeaderKey, mHeaderValue)
                .post(formBody)
                .build();

        Response mResponse = mHttpClient.newCall(request).execute();
        return mResponse;
    }

    public void setmHeaderKey(String mHeaderKey) {
        this.mHeaderKey = mHeaderKey;
    }

    public void setmHeaderValue(String mHeaderValue) {
        this.mHeaderValue = mHeaderValue;
    }

    public void setmQueryValue(String mQueryValue) {
        this.mQueryValue = mQueryValue;
    }

    public void setmQueryKey(String mQueryKey) {
        this.mQueryKey = mQueryKey;
    }

    public void setmURL(String mURL) {
        this.mURL = mURL;
    }

    public String getmHeaderKey() {
        return mHeaderKey;
    }

    public String getmHeaderValue() {
        return mHeaderValue;
    }

    public String getmQueryValue() {
        return mQueryValue;
    }

    public String getmQueryKey() {
        return mQueryKey;
    }

    public String getmURL() {
        return mURL;
    }
}