package com.group4sweng.scranplan;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageButton;

import java.io.InputStream;
import java.net.URL;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap>{

    ImageButton imageButton;

    public DownloadImageTask(ImageButton imageButton) {
        this.imageButton = imageButton;
    }

    protected Bitmap doInBackground(String... urls) {
        String url = urls[0];
        Bitmap logo = null;
        try {
            InputStream is = new URL(url).openStream();
            logo = BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return logo;
    }

    protected void onPostExecute(Bitmap result) {
        imageButton.setImageBitmap(result);
    }

}
