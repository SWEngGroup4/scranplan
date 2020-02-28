package com.group4sweng.scranplan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.provider.FontRequest;
import androidx.core.provider.FontsContractCompat;

import com.squareup.picasso.Picasso;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Presentation extends AppCompatActivity {

    private ProgressBar spinner;
    private XmlParser.DocumentInfo documentInfo;
    private DisplayMetrics displayMetrics = new DisplayMetrics();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.presentation);

        // Fullscreen the presentation
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        String xml_URL = intent.getStringExtra("xml_URL");
        DownloadXmlTask xmlTask = new DownloadXmlTask(this);

        spinner = findViewById(R.id.presentationLoad);
        xmlTask.execute(xml_URL);
    }

    private void presentation (Map<String, Object> xml) {
        documentInfo = (XmlParser.DocumentInfo) xml.get("documentInfo");
        final List<RelativeLayout> slideLayouts = new ArrayList<>();
        List<XmlParser.Slide> xmlSlides = (List<XmlParser.Slide>) xml.get("slides");
        RelativeLayout presentationContainer = findViewById(R.id.presentationContainer);

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        final Integer[] currentSlide = {0};
        final Typeface[] defaultTypeFace = new Typeface[1];

        XmlParser.Defaults defaults = (XmlParser.Defaults) xml.get("defaults");
        Integer slideHeight = defaults.slideHeight;
        Integer slideWidth = defaults.slideWidth;

        if (slideHeight == -1) {
            slideHeight = displayMetrics.heightPixels;
        }
        if (slideWidth == -1) {
            slideWidth = displayMetrics.widthPixels;
        }

        FontRequest request = new FontRequest("com.google.android.gms.fonts",
                "com.google.android.gms",
                defaults.font,
                R.array.com_google_android_gms_fonts_certs);
        FontsContractCompat.FontRequestCallback callback = new FontsContractCompat.FontRequestCallback() {
            @Override
            public void onTypefaceRetrieved(Typeface typeface) {
                defaultTypeFace[0] = typeface;
            }
        };

        for (final XmlParser.Slide slide : xmlSlides) {
            RelativeLayout slideLayout = new RelativeLayout(getApplicationContext());
            RelativeLayout.LayoutParams slideParams = new RelativeLayout.LayoutParams(slideWidth, slideHeight);
            slideParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            slideLayout.setLayoutParams(slideParams);
            slideLayout.setBackgroundColor(Color.parseColor(defaults.backgroundColor));

            XmlParser.Text id = new XmlParser.Text(slide.id, defaults);
            slideLayout.addView(addText(id, defaults, defaultTypeFace[0], slideWidth, slideHeight));

            if (slide.duration != -1) {
                Thread thread = new Thread(){
                    @Override
                    public void run() {
                        try {
                            synchronized (this) {
                                wait (slide.duration);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        newSlide(slideLayouts, currentSlide[0], "next");
                                    }
                                });
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();
            }

            if (slide.text != null) {
                slideLayout.addView(addText(slide.text, defaults, defaultTypeFace[0], slideWidth, slideHeight));
            }
            if (slide.line != null) {
                //TODO - Generate line graphic
            }
            if (slide.shape != null) {
                //TODO - Generate shape graphic
            }
            if (slide.audio != null) {
                //TODO - Generate audio
            }
            if (slide.image != null) {
                slideLayout.addView(addImage(slide.image, defaults, slideWidth, slideHeight));
            }
            if (slide.video != null) {
                //TODO - Generate video
            }
            if (slide.comments != null) {
                slideLayout.addView(addComments(slide.comments, defaults, defaultTypeFace[0], slideWidth, slideHeight));
            }
            if (slide.timer != null) {
                slideLayout.addView(addTimer(slide.timer));
            }

            Button prevSlide = findViewById(R.id.prevButton);
            Button nextSlide = findViewById(R.id.nextButton);

            nextSlide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentSlide[0] = newSlide(slideLayouts, currentSlide[0], "next");
                }
            });

            prevSlide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentSlide[0] =  newSlide(slideLayouts, currentSlide[0], "prev");
                }
            });

            slideLayout.setVisibility(View.GONE);
            presentationContainer.addView(slideLayout);
            slideLayouts.add(slideLayout);
        }

        slideLayouts.get(currentSlide[0]).setVisibility(View.VISIBLE);

        spinner.setVisibility(View.GONE);
    }

    private TextView addText(final XmlParser.Text text, XmlParser.Defaults defaults, Typeface defaultTypeFace,
                             Integer slideWidth, Integer slideHeight) {
        final TextView textView = new TextView(getApplicationContext());

        RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        textParams.setMargins(Math.round(slideWidth* (text.xPos /  100)),
                Math.round(slideHeight * (text.yPos / 100)), 0, 0);
        textView.setLayoutParams(textParams);

        textView.setText(text.text);
        if (!text.font.equals(defaults.font)) {
            FontRequest request = new FontRequest("com.google.android.gms.fonts",
                    "com.google.android.gms",
                    defaults.font,
                    R.array.com_google_android_gms_fonts_certs);
            FontsContractCompat.FontRequestCallback callback = new FontsContractCompat.FontRequestCallback() {
                @Override
                public void onTypefaceRetrieved(Typeface typeface) {
                    textView.setTypeface(typeface);
                }
            };
        } else {
            textView.setTypeface(defaultTypeFace);
        }
        textView.setTextSize(text.fontSize);
        textView.setTextColor(Color.parseColor(text.fontColor));

        if (text.startTime > 0) {
            textView.setVisibility(View.GONE);
            Thread thread = new Thread(){
                @Override
                public void run() {
                    try {
                        synchronized (this) {
                            wait (text.startTime);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textView.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }
        if (text.endTime > text.startTime) {
            Thread thread = new Thread(){
                @Override
                public void run() {
                    try {
                        synchronized (this) {
                            wait (text.endTime);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textView.setVisibility(View.GONE);
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }
        return textView;
    }

    private ImageView addImage(final XmlParser.Image image, XmlParser.Defaults defaults,
                               Integer slideWidth, Integer slideHeight) {
        final ImageView imageView = new ImageView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                Math.round(slideWidth * (image.width / 100)), Math.round(slideHeight * (image.height / 100)));
        layoutParams.setMargins(Math.round(slideWidth * (image.xStart / 100)),
                Math.round(slideHeight * (image.yStart / 100)), 0, 0);
        imageView.setLayoutParams(layoutParams);

        if (image.startTime > 0) {
            imageView.setVisibility(View.GONE);
            Thread thread = new Thread(){
                @Override
                public void run() {
                    try {
                        synchronized (this) {
                            wait (image.startTime);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageView.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }

        if (image.endTime > image.startTime) {
            Thread thread = new Thread(){
                @Override
                public void run() {
                    try {
                        synchronized (this) {
                            wait (image.endTime);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageView.setVisibility(View.GONE);
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }

        Picasso.get().load(image.urlName).into(imageView);
        return imageView;
    }

    private RelativeLayout addComments(List<XmlParser.Comment> comments,
                             XmlParser.Defaults defaults, Typeface defaultTypeFace, Integer slideWidth, Integer slideHeight) {
        RelativeLayout commentLayout = new RelativeLayout(getApplicationContext());
        RelativeLayout.LayoutParams commentListParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        commentListParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        commentLayout.setLayoutParams(commentListParams);

        int prevCommentId = 0;
        for (XmlParser.Comment comment : comments) {
            comment.text.text = comment.userID + ": " + comment.text.text;
            TextView commentText = addText(comment.text, defaults, defaultTypeFace, slideWidth, slideHeight);

            commentText.setId(prevCommentId + 1);
            RelativeLayout.LayoutParams commentParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            commentParams.addRule(RelativeLayout.BELOW, prevCommentId);
            commentText.setLayoutParams(commentParams);

            prevCommentId += 1;
            commentLayout.addView(commentText);
        }

        return commentLayout;
    }

    private TextView addTimer(final Float timer) {
        final TextView timerView = new TextView(getApplicationContext());
        timerView.setText("Timer: " + timer);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
        timerView.setLayoutParams(layoutParams);

        return timerView;
    }

    private Integer newSlide(List<RelativeLayout> slides, Integer currentSlide, String direction) {
        try {
            switch (direction) {
                case "next":
                    if (currentSlide < documentInfo.totalSlides - 1) {
                        slides.get(currentSlide + 1).setVisibility(View.VISIBLE);
                        slides.get(currentSlide).setVisibility(View.GONE);
                        currentSlide += 1;
                        Log.d("Test", "" + currentSlide);
                    }
                    break;
                case "prev":
                    if (currentSlide > 0) {
                        slides.get(currentSlide - 1).setVisibility(View.VISIBLE);
                        slides.get(currentSlide).setVisibility(View.GONE);
                        currentSlide -= 1;
                    }
                    break;
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return currentSlide;
    }

    private static class DownloadXmlTask extends AsyncTask<String, Void, Map<String, Object>> {
        @SuppressLint("StaticFieldLeak")
        Presentation presentation;

        DownloadXmlTask(Presentation p) {
            this.presentation = p;
        }

        @Override
        protected Map<String, Object> doInBackground(String... urls) {
            try {
                return loadXML(urls[0]);
            } catch (IOException | XmlPullParserException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Map<String, Object> result) {
            presentation.presentation(result);
        }

        private Map<String, Object> loadXML(String url) throws XmlPullParserException, IOException {

            XmlParser xmlParser = new XmlParser();
            Map<String, Object> xml;
            try (InputStream stream = downloadXML(url)) {
                xml = xmlParser.parse(stream);
            }
            return xml;
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