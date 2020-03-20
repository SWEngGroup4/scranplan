package com.group4sweng.scranplan.APIHandler;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;

import io.sentry.core.Sentry;

/**
 * Grocery searcher, searches API with the given ingredients and shop
 */
public class GrocerySearcher {
    enum Shop {
        Tesco;
    }

    private static final String TESCOGROCERY = "http://dev.tescolabs.com/grocery/products/?";
    private static final String GROCERYKEYTESCO = "292c39f29323450b9c921b0976b9c4b3";
    private APICaller mApiCaller;
    private Shop mShop;

    /**
     * Initalise the grocery searcher with the correct shop
     *
     * @param shop Shop being searched
     */
    public GrocerySearcher(Shop shop) throws IOException {
        mShop = shop;
        if (shop == Shop.Tesco) {
            mApiCaller = new APICaller(TESCOGROCERY);
        }
        else{
            Sentry.captureMessage("No Shop Selected");
        }
    }

    public int SearchForIngredient(String ingredient) throws IOException, JSONException {
        switch (mShop){
            case Tesco:
                HashMap<String, String> mQuery = new HashMap<String, String>();
                mQuery.put(("query"),ingredient);
                mQuery.put(("offset"),"0");
                mQuery.put(("limit"),"10");

                int i = new APICaller(TESCOGROCERY).withHeaders("Ocp-Apim-Subscription-Key :" + GROCERYKEYTESCO).withData(mQuery).send();
                return i;
            default:
                return -1;
        }


    }
}
