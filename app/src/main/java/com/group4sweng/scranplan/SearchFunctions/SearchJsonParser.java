package com.group4sweng.scranplan.SearchFunctions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class SearchJsonParser {

    private JSONObject mJsonObject;

    SearchJsonParser(JSONObject object) {
        this.mJsonObject = object;
    }

    /***
     * Will parse and return the object ID as a string list
     * @return List of the ObjectID
     */
    List<String> ParseAndReturnObjectId(){
        if(mJsonObject == null) return null;

        List<String> results = new ArrayList<>();
        JSONArray hits = mJsonObject.optJSONArray("hits");
        if (hits == null) return  null;

        for (int i = 0; i < hits.length(); ++i){
            JSONObject hit = hits.optJSONObject(i);
            if (hit == null) continue;

            String objectID = hit.optString("objectID");

            results.add(objectID);


        }

        return Collections.synchronizedList(results);
    }




}
