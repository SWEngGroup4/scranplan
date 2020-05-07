package com.group4sweng.scranplan.ApiHandler;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Grocery searcher, searches API with the given ingredients and shop
 */
public class GrocerySearcher extends APICaller{


    private static final String TESCOGROCERY = "http://dev.tescolabs.com/grocery/products/";
    private static final String GROCERYKEYTESCO = System.getenv("TESCO_API_KEY");

    private String mOffsetValue;
    private String mQueryValue;
    private String mLimitValue;

    GrocerySearcher(String query, String offset, String limit){
        this.mQueryValue = query;
        this.mOffsetValue = offset;
        this.mLimitValue = limit;
    }

    @Override
    public Response sendPost() throws Exception {
        RequestBody formBody = new FormBody.Builder()
                .add("offset", mOffsetValue)
                .add("query", mQueryValue)
                .add("limit", mLimitValue)
                .build();

        assert GROCERYKEYTESCO != null;
        Request request = new Request.Builder()
                .header("Ocp-Apim-Subscription-Key", GROCERYKEYTESCO)
                .url(TESCOGROCERY)
                .post(formBody)
                .build();

        return mHttpClient.newCall(request).execute();}
}
