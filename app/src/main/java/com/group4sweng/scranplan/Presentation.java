package com.group4sweng.scranplan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.provider.FontRequest;
import androidx.core.provider.FontsContractCompat;
import androidx.core.view.MotionEventCompat;

import com.github.aakira.expandablelayout.ExpandableLayoutListener;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.squareup.picasso.Picasso;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Presentation extends AppCompatActivity {

    private ProgressBar spinner;
    private XmlParser.DocumentInfo documentInfo;
    private DisplayMetrics displayMetrics = new DisplayMetrics();
    ExpandableRelativeLayout expandableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.presentation);
        expandableLayout = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout);

        Log.d("Test", "Presentation launched");

        // Fullscreen the presentation
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        String xml_URL = intent.getStringExtra("xml_URL");
        Log.d("Test", xml_URL);
        DownloadXmlTask xmlTask = new DownloadXmlTask(this);

        spinner = findViewById(R.id.presentationLoad);
        xmlTask.execute(xml_URL);
        expandableLayout.bringToFront();
    }

    private void presentation (Map<String, Object> xml) {
        documentInfo = (XmlParser.DocumentInfo) xml.get("documentInfo");
        final List<RelativeLayout> slideLayouts = new ArrayList<>();
        List<String> dropdownItems = new ArrayList<>();
        final List<XmlParser.Slide> xmlSlides = (List<XmlParser.Slide>) xml.get("slides");
        CardView presentationContainer = findViewById(R.id.presentationContainer);

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        final Integer[] currentSlide = {0};
        final Typeface[] defaultTypeFace = new Typeface[1];

        XmlParser.Defaults defaults = (XmlParser.Defaults) xml.get("defaults");
        Integer slideHeight = defaults.slideHeight;
        Integer slideWidth = defaults.slideWidth;

        if (slideHeight == -1) {
            slideHeight = Math.round(displayMetrics.heightPixels * 0.8f);
            Log.d("Test", String.valueOf(slideHeight));
        }
        if (slideWidth == -1) {
            slideWidth = Math.round(displayMetrics.widthPixels * 0.8f);
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
            slideLayout.addView(addText(id, slideWidth, slideHeight));
            dropdownItems.add(id.text);

            if (slide.text != null) {
                slideLayout.addView(addText(slide.text, slideWidth, slideHeight));
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
                Log.e("Test", "Text element added");
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

            Spinner dropdown = findViewById(R.id.presentationSpinner);
            dropdown.setVisibility(View.VISIBLE);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, dropdownItems);
            dropdown.setAdapter(adapter);

            dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("Test", String.valueOf(position));
                    currentSlide[0] = toSlide(slideLayouts, currentSlide[0], position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            Button prevSlide = findViewById(R.id.prevButton);
            prevSlide.setVisibility(View.VISIBLE);
            Button nextSlide = findViewById(R.id.nextButton);
            nextSlide.setVisibility(View.VISIBLE);
            Button comments = findViewById(R.id.comments);
            comments.setVisibility(View.VISIBLE);
//            expandableLayout.bringToFront();

            comments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // toggle expand, collapse
//                    expandableLayout.bringToFront();
//                    expandableLayout.bringChildToFront(findViewById(R.id.textviewexpand));
                    expandableLayout.toggle();

                }
            });

            nextSlide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentSlide[0] = toSlide(slideLayouts, currentSlide[0], currentSlide[0] + 1);
                }
            });

            prevSlide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentSlide[0] = toSlide(slideLayouts, currentSlide[0], currentSlide[0] - 1);
                }
            });

            slideLayout.setVisibility(View.GONE);
            presentationContainer.addView(slideLayout);
            slideLayouts.add(slideLayout);
            expandableLayout.bringToFront();
        }

        slideLayouts.get(currentSlide[0]).setVisibility(View.VISIBLE);
        spinner.setVisibility(View.GONE);
        expandableLayout.bringToFront();
    }

    private PresentationTextView addText(final XmlParser.Text text, Integer slideWidth, Integer slideHeight) {

        PresentationTextView textView = new PresentationTextView(getApplicationContext(), slideHeight, slideWidth);
        textView.setDims(text.width, text.height);
        textView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        textView.setText(text.text);
        textView.setFont(text.font, text.fontWeight);
        textView.setTextSize(text.fontSize);
        textView.setTextColour(text.fontColor);
        textView.setPos(text.xPos, text.yPos);
        if (text.startTime > 0) {
            textView.setStartTime(this, text.startTime);
        }
        if (text.endTime > text.startTime) {
            textView.setEndTime(this, text.endTime);
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
            PresentationTextView commentText = addText(comment.text, slideWidth, slideHeight);

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

    private Integer toSlide(List<RelativeLayout> slides, Integer currentSlide, Integer slideNumber) {
        if (slideNumber > slides.size() - 1 || slideNumber < 0) {
            Toast.makeText(getApplicationContext(), "Slide does not exist", Toast.LENGTH_SHORT).show();
        } else {
            slides.get(slideNumber).setVisibility(View.VISIBLE);
            slides.get(currentSlide).setVisibility(View.GONE);
            currentSlide = slideNumber;
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

    private void addFirestoreComments(){
        ExpandableRelativeLayout expandableLayout
                = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout);

        // toggle expand, collapse
        expandableLayout.toggle();
        // expand
        expandableLayout.expand();
        // collapse
        expandableLayout.collapse();

        // move position of child view
        expandableLayout.moveChild(0);
        // move optional position
        expandableLayout.move(500);

        // set base position which is close position
        expandableLayout.setClosePosition(500);

        expandableLayout.setListener(new ExpandableLayoutListener() {
            @Override
            public void onAnimationStart() {
            }

            @Override
            public void onAnimationEnd() {
            }

            // You can get notification that your expandable layout is going to open or close.
            // So, you can set the animation synchronized with expanding animation.
            @Override
            public void onPreOpen() {
            }

            @Override
            public void onPreClose() {
            }

            @Override
            public void onOpened() {
            }

            @Override
            public void onClosed() {
            }
        });
    }
}