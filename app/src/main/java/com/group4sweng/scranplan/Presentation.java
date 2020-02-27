package com.group4sweng.scranplan;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class Presentation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String xml_URL = intent.getStringExtra("xml_URL");
        DownloadXmlTask xmlTask = new DownloadXmlTask();

        xmlTask.execute(xml_URL);
    }

    private class DownloadXmlTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                return loadXML(urls[0]);
            } catch (IOException | XmlPullParserException e) {
                return e.toString();
            }
        }

        private String loadXML(String url) throws XmlPullParserException, IOException {
            InputStream stream = null;
            XmlParser xmlParser = new XmlParser();
            Map<String, Object> xml = null;

            try {
                stream = downloadXML(url);
                xml = xmlParser.parse(stream);
                presentation(xml);
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
            return "Test";
        }

        private void presentation (Map<String, Object> xml) {
//            XmlParser.Defaults defaults =
            Log.d("Test", xml.get("defaults").toString());
        }

        private InputStream downloadXML(String xml_URL) throws IOException {
            URL url = new URL(xml_URL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();
            return urlConnection.getInputStream();
        }
    }
}
