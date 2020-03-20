package com.group4sweng.scranplan.APIHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import io.sentry.core.Sentry;

/**
 * API Caller Author: NBillis
 * (c) CoDev 2020
 * <p>
 * READ ME How to send a request see final line
 * <p>
 * Http request and send to "http://host:port/path"
 * with name=test and age=30 and read server's response as JSONObject
 * <p>
 * HashMap<String, String>params=new HashMap<>();
 * params.put("name", "test");
 * params.put("age", "30");
 * <p>
 * req.preparePost().withData(params).sendAndReadJSON();
 */
class APICaller {

    private HttpURLConnection mConnection;

    /**
     * Other API methods allowing expansion further down the line
     */
    public static enum Method {
        POST, PUT, DELETE, GET;
    }


    /**
     * Setup the caller with URL
     */
    APICaller(String url) throws IOException {
        try {
            URL mUrl = new URL(url);
            mConnection = (HttpURLConnection) mUrl.openConnection();
            mConnection.setDoInput(true);
            mConnection.setRequestMethod(Method.GET.name());
        } catch (MalformedURLException e) {
            Sentry.captureException(e);
        }
    }

    /**
     * @param headers headers to be passed on the API usually holding they key
     * @return APICaller with the headers added to the call
     */
    APICaller withHeaders(String... headers) {
        for (String header : headers) {
            String[] h = header.split("[:]");
            mConnection.setRequestProperty(h[0], h[1]);
        }
        return this;
    }

    /**
     * @param query the data which is being queried
     * @return itself
     * @throws IOException .
     */
    APICaller withData(String query) throws IOException {
        mConnection.setDoOutput(true);
        OutputStream mOutputStream = mConnection.getOutputStream();
        Writer writer = new BufferedWriter(new OutputStreamWriter(mOutputStream, StandardCharsets.UTF_8));
        writer.write(query);
        writer.close();
        return this;
    }


    /**
     * @param params data in the form of a hashMap
     * @return itself
     * @throws IOException .
     */
    APICaller withData(HashMap<String, String> params) throws IOException {
        StringBuilder result = new StringBuilder();
        // Appends: key=value (for first param) OR &key=value(second and more)
        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(result.length() > 0 ? "&" : "").append(entry.getKey()).append("=").append(entry.getValue());
        }
        withData(result.toString());
        return this;
    }

    /**
     * Gets the HTTP status code to indicate whether it successfully sent
     *
     * @return Code from the server on sucess and -1 on error
     * @throws IOException .
     */
    int send() throws IOException {
        int result = -1;
        try {
            result = mConnection.getResponseCode();
            mConnection.disconnect();
        } catch (NullPointerException e) {
            Sentry.captureException(e);
        }
        return result;
    }

    /**
     * Function to send the request and read the responce
     *
     * @return String of the response from the server
     * @throws IOException .
     */
    String sendAndReadString() throws IOException {
        InputStream mInputStream = mConnection.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(mInputStream));
        StringBuilder response = new StringBuilder();
        for (String line; (line = br.readLine()) != null; ) response.append(line + "\n");
        return response.toString();
    }

    /**
     * Sends the request to the server and reads the JSON into the log
     *
     * @return JSON Response
     * @throws JSONException .
     * @throws IOException   .
     */
    JSONObject sendAndReadJSON() throws JSONException, IOException {
        return new JSONObject(sendAndReadString());
    }


}
