package com.group4sweng.scranplan.Presentation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.provider.FontRequest;
import androidx.core.provider.FontsContractCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.aakira.expandablelayout.ExpandableLayoutListener;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.group4sweng.scranplan.Exceptions.AudioPlaybackError;
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.SoundHandler.AudioURL;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;
import com.squareup.picasso.Picasso;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  All parts of the presentation, taking the XML document and separating it out into its slide that
 *  are then conveniently displayed for the user. User can go forward and backwards along with
 *  navigating to any particular slide. User can also add comments and view existing comments to
 *  any particular slide.
 */
public class Presentation extends AppCompatActivity {

    //  enumerations to define if we should change the devices width or height.
    enum DeviceDisplay {
        WIDTH,
        HEIGHT
    }

    /**  Firebase **/
    FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();

    XmlParser.DocumentInfo documentInfo; //Overall infomation about the current document.
    final static String TAG = "PRES"; //Log tag.

    /**  Device display metrics **/
    private DisplayMetrics displayMetrics = new DisplayMetrics();
    int deviceHeight; //Devices display height (in pixels).
    final static int MIN_HEIGHT_NAV_HIDE = 1750; //Minimum size of device before height of presentation view shrinks.

    /** Audio & Timers (objects/parameters & UI). **/
    private boolean timerIsPlaying = false;
    final private int COUNTDOWN_INTERVAL = 100; //The default interval between timer checks
    private PresentationTimer timer; //Modified countdown timer (includes audio)
    ProgressBar progress; //Current timer progress
    TextView currentDuration, finalDuration; //Current and final timer duration
    ConstraintLayout timerLayout;
    Button playPause;

    /**  Lists containing references to timers (total duration) & audio objects if they exist for each slide by index (corresponding to slide number).
         If a timer or audio object isn't present for the slide a 'null' value is present. **/
    private ArrayList<Float> slideTimers = new ArrayList<>();
    private ArrayList<AudioURL> slideAudioLooping = new ArrayList<>();
    private ArrayList<AudioURL> slideAudio = new ArrayList<>();

    private ProgressBar spinner;

    /**  Comment additions **/
    ExpandableRelativeLayout expandableLayout;
    private boolean isScrolling = false;
    private boolean isLastItemReached = false;
    private DocumentSnapshot lastVisible;
    private String recipeID;
    private Query query;
    com.group4sweng.scranplan.UserInfo.UserInfoPrivate mUser;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.presentation);
        expandableLayout = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout);

        Log.d("Test", "Presentation launched");

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        deviceHeight = displayMetrics.heightPixels;

        Log.d(TAG, "Device display size: " + deviceHeight);

        // Fullscreen the presentation
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        String xml_URL = intent.getStringExtra("xml_URL");
        recipeID = intent.getStringExtra("recipeID");
        mUser = (UserInfoPrivate) intent.getSerializableExtra("user");
        Log.d("Test", xml_URL);
        DownloadXmlTask xmlTask = new DownloadXmlTask(this);

        spinner = findViewById(R.id.presentationLoad);
        xmlTask.execute(xml_URL);
        expandableLayout.bringToFront();

        loadTimerUI();
    }


    /** Provides a value (in pixels) for the presentation size in accordance with the devices width or height as a percentage.
     * @param displayParam - Display parameter to change. (Width/Height)
     * @return - Value of the new width/height. (in pixels)
     */
    private int findNewPresentationSize(DeviceDisplay displayParam) {
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics); //Get the display metrics for the device.

        switch (displayParam) {
            case WIDTH:
                return Math.round(displayMetrics.widthPixels * 0.8f); //Floating point represents the display percentage the presentation should take up.
            case HEIGHT:
                //Shrink the 'Card' presentation display size based upon the height of the device.
                if (deviceHeight < MIN_HEIGHT_NAV_HIDE) {
                    return Math.round(displayMetrics.heightPixels * 0.7f);
                } else {
                    return Math.round(displayMetrics.heightPixels * 0.75f);
                }
        }
        return -1;
    }

    private void presentation (Map<String, Object> xml) {
        documentInfo = (XmlParser.DocumentInfo) xml.get("documentInfo");
        final List<RelativeLayout> slideLayouts = new ArrayList<>();
        List<String> dropdownItems = new ArrayList<>();
        final List<XmlParser.Slide> xmlSlides = (List<XmlParser.Slide>) xml.get("slides");
        CardView presentationContainer = findViewById(R.id.presentationContainer);

        final Integer[] currentSlide = {0};
        final Typeface[] defaultTypeFace = new Typeface[1];

        XmlParser.Defaults defaults = (XmlParser.Defaults) xml.get("defaults");
        Integer slideHeight = defaults.slideHeight;
        Integer slideWidth = defaults.slideWidth;

        if(slideHeight == -1) {
            slideHeight = findNewPresentationSize(DeviceDisplay.HEIGHT);
        }
        if(slideWidth == -1){
            slideWidth = findNewPresentationSize(DeviceDisplay.WIDTH);
        }

        FontRequest request = new FontRequest(
                "com.google.android.gms.fonts",
                "com.google.android.gms",
                defaults.font,
                R.array.com_google_android_gms_fonts_certs);
        FontsContractCompat.FontRequestCallback callback = new FontsContractCompat.FontRequestCallback() {
            @Override
            public void onTypefaceRetrieved(Typeface typeface) {
                defaultTypeFace[0] = typeface;
            }
        };

        int slideCount = 0;

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

            //  Checks if the slide contains a 'non-looping' audio file. Played at end of timer countdown, or as a standalone audio file.
            if (slide.audio != null) { //Check if audio exists within the slide.
                AudioURL audio = new AudioURL();
                audio.storeURL(slide.audio.urlName); //Store our audio for reference.

                slideAudio.add(slideCount, audio); //Add to the index that corresponds to the current slide number.
            } else {
                slideAudio.add(slideCount, null);
            }

            //  Checks if the slide contains a 'looping' audio file. Played whilst the timer is running.
            if (slide.audioLooping != null) {
                AudioURL audio = new AudioURL();
                audio.setLooping(true);
                audio.storeURL(slide.audioLooping.urlName);

                slideAudioLooping.add(slideCount, audio);
            } else {
                slideAudioLooping.add(slideCount, null); //If no audio exists simply set any audioURL objects for the given slide to null.
            }

            if (slide.image != null) {
                Log.e("Test", "Text element added");
                slideLayout.addView(addImage(slide.image, defaults, slideWidth, slideHeight));
            }
            if (slide.video != null) {
                //TODO - Generate video
            }
            //TODO old comments section where we used XML, for James Crawley to delete where he sees fit
//            if (slide.comments != null) {
//                slideLayout.addView(addComments(slide.comments, defaults, defaultTypeFace[0], slideWidth, slideHeight));
//            }
            if (slide.timer != null) {
                slideTimers.add(slideCount, slide.timer);
                slideLayout.addView(addTimer((float) slide.timer));
            } else {
                slideTimers.add(slideCount, -1f);
            }

            Spinner dropdown = findViewById(R.id.presentationSpinner);
            dropdown.setVisibility(View.VISIBLE);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, dropdownItems);
            dropdown.setAdapter(adapter);

            dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("Test", String.valueOf(position));
                    expandableLayout.collapse();
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
            nextSlide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandableLayout.collapse();
                    currentSlide[0] = toSlide(slideLayouts, currentSlide[0], currentSlide[0] + 1);
                }
            });

            prevSlide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandableLayout.collapse();
                    currentSlide[0] = toSlide(slideLayouts, currentSlide[0], currentSlide[0] - 1);
                }
            });

            slideLayout.setVisibility(View.GONE);
            presentationContainer.addView(slideLayout);
            slideLayouts.add(slideLayout);
            expandableLayout.bringToFront();

            slideCount++;
        }

        slideLayouts.get(currentSlide[0]).setVisibility(View.VISIBLE);
        spinner.setVisibility(View.GONE);


        /*
        The following components add the comment capability to each page of the slide show
         */
        Button comments = findViewById(R.id.comments);
        comments.setVisibility(View.VISIBLE);
        ViewCompat.setTranslationZ(expandableLayout, 20);
        expandableLayout.setVisibility(View.VISIBLE);

        /**
         *  Clicking the comments button toggles comments open and closed
         */
        comments.setOnClickListener(v -> expandableLayout.toggle());

        /**
         *  Setting up the expandable comments listeners to download new comments
         *  when the view is reopened
         */
        expandableLayout.setListener(new ExpandableLayoutListener() {
            @Override
            public void onAnimationStart() {

            }
            @Override
            public void onAnimationEnd() {
            }
            @Override
            public void onPreOpen() {
                addFirestoreComments(currentSlide[0].toString());
            }
            @Override
            public void onPreClose() {
            }
            @Override
            public void onOpened() {

            }
            @Override
            public void onClosed() {
                isScrolling = false;
                isLastItemReached = false;
                lastVisible = null;
                query  = null;
            }
        });
        // Adding the functionality for users to add comments
        Button mPostComment = findViewById(R.id.sendCommentButton);
        EditText mInputComment = findViewById(R.id.addCommentEditText);


        /**
         *  Setting up the post comment listener, removing the text from the box and saving
         *  it as a new document in the Firestore, the data is also reloaded
         */
        mPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mInputComment.getText().toString();
                mInputComment.getText().clear();

                CollectionReference ref = mDatabase.collection("recipes").document(recipeID).collection("slide" + currentSlide[0].toString());
                Log.e(TAG, "Added new doc ");
                // Saving the comment as a new document
                HashMap<String, Object> map = new HashMap<>();
                map.put("authorID", mUser.getUID());
                map.put("author", mUser.getDisplayName());
                map.put("comment", content);
                map.put("timestamp", FieldValue.serverTimestamp());
                // Saving default user to Firebase Firestore database
                ref.add(map);
                addFirestoreComments(currentSlide[0].toString());


            }
        });

        /**
         *   Start timer listener that checks for a play/pause button press.
         **/
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //  Reference the current slides corresponding timer float values and audio objects.
                AudioURL audio = slideAudio.get(currentSlide[0]);
                AudioURL audioLooping = slideAudioLooping.get(currentSlide[0]);
                Float slideTimer = slideTimers.get(currentSlide[0]);

                //  Choose how we handle the timer based on what audio files are retrieved from the XML document and if anything is missing or not.
                if(audio == null & audioLooping == null){
                    timerListenerHandler(slideTimer, null, null, false);
                } else if (audioLooping == null || audio == null){
                    timerListenerHandler(slideTimer, audio, null, true);
                } else {
                    timerListenerHandler(slideTimer, audio, audioLooping, true);
                }
            }
        });
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

    //TODO old comments section where we used XML, for James Crawley to delete where he sees fit
//    private RelativeLayout addComments(List<XmlParser.Comment> comments,
//                                       XmlParser.Defaults defaults, Typeface defaultTypeFace, Integer slideWidth, Integer slideHeight) {
//        RelativeLayout commentLayout = new RelativeLayout(getApplicationContext());
//        RelativeLayout.LayoutParams commentListParams = new RelativeLayout.LayoutParams(
//                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT
//        );
//        commentListParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//        commentLayout.setLayoutParams(commentListParams);
//
//        int prevCommentId = 0;
//        for (XmlParser.Comment comment : comments) {
//            comment.text.text = comment.userID + ": " + comment.text.text;
//            PresentationTextView commentText = addText(comment.text, slideWidth, slideHeight);
//
//            commentText.setId(prevCommentId + 1);
//            RelativeLayout.LayoutParams commentParams = new RelativeLayout.LayoutParams(
//                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT
//            );
//            commentParams.addRule(RelativeLayout.BELOW, prevCommentId);
//            commentText.setLayoutParams(commentParams);
//
//            prevCommentId += 1;
//            commentLayout.addView(commentText);
//        }
//
//        return commentLayout;
//    }

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
            timerLayout = findViewById(R.id.timerLayout);
            if(slideTimers.get(slideNumber) != -1){
                timerLayout.setVisibility(View.VISIBLE);
                timerSlideTransition(currentSlide, slideNumber);
            } else {
                timerLayout.setVisibility(View.GONE);
            }
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

    /**
     * This method checks what comment is selected and opens up a menu to either open up another
     * users profile or if the comment was made my this user, user can delete the comment.
     * @param document
     * @param anchor
     * @param sentCurrentSlide
     */
    public void commentSelected(DocumentSnapshot document, View anchor, String sentCurrentSlide){
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(context, anchor);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.menu_comment, popup.getMenu());
        if(document.get("authorID").toString().equals(mUser.getUID())){
            popup.getMenu().getItem(0).setVisible(false);
            popup.getMenu().getItem(1).setVisible(false);
            popup.getMenu().getItem(2).setVisible(true);
        }else{
            popup.getMenu().getItem(0).setVisible(true);
            popup.getMenu().getItem(1).setVisible(true);
            popup.getMenu().getItem(2).setVisible(false);
        }

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(
                    MenuItem item) {
                // Give each item functionality
                switch (item.getItemId()) {
                    case R.id.viewCommentProfile:
                        Log.e(TAG,"Clicked open profile!");
                        //TODO add functionality to open users profile in new fragment
                        break;
                    case R.id.reportComment:
                        Log.e(TAG,"Report comment clicked!");
                        //TODO add functionality to report this comment
                        break;
                    case R.id.deleteComment:
                        Log.e(TAG,"Clicked delete comment!");
                        document.getReference().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                addFirestoreComments(sentCurrentSlide);
                            }
                        });
                        break;
                }
                return true;
            }
        });

        popup.show();//showing popup menu
    }


    /**
     * Function to set up a new recycler view that takes all comments and downloads them from the server
     * when there is more than 5 comments, the data is downloaded 5 items at a time and loads new comments
     * as the user scrolls down through the comments
     *
     * Comments downloaded depend on the slide currently open
     * @param currentSlide
     */
    private void addFirestoreComments(String currentSlide){

        List<CommentRecyclerAdapter.CommentData> data;
        data = new ArrayList<>();

        // Creating a list of the data and building all variables to add to recycler view
        final RecyclerView recyclerView = findViewById(R.id.commentList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager rManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(rManager);
        final RecyclerView.Adapter rAdapter = new CommentRecyclerAdapter(Presentation.this, data, currentSlide);
        recyclerView.setAdapter(rAdapter);
        query = mDatabase.collection("recipes").document(recipeID).collection("slide" + currentSlide).limit(5).orderBy("timestamp");

        // Once the data has been returned, dataset populated and components build
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // For each document a new recipe preview view is generated
                    if(task.getResult() != null)
                    {
                        for (DocumentSnapshot document : task.getResult()) {
                            data.add(new CommentRecyclerAdapter.CommentData(
                                    document,
                                    document.get("authorID").toString(),
                                    document.get("author").toString(),
                                    document.get("comment").toString()
                            ));
                        }
                        rAdapter.notifyDataSetChanged();
                        // Set the last document as last user can see
                        if(task.getResult().size() != 0){
                            lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                        }else{
                            // If no data returned, user notified
                            isLastItemReached = true;
                            data.add(new CommentRecyclerAdapter.CommentData(
                                    null,
                                    null,
                                    "No more results",
                                    "No comments yet for this step, be the first!"
                            ));
                        }
                        // check if user has scrolled through the view
                        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                    isScrolling = true;
                                }
                            }
                            // If user is scrolling and has reached the end, more data is loaded
                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                                // Checking if user is at the end
                                LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                                int visibleItemCount = linearLayoutManager.getChildCount();
                                int totalItemCount = linearLayoutManager.getItemCount();
                                // If found to have reached end, more data is requested from the server in the same manner
                                if (isScrolling && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached) {
                                    isScrolling = false;
                                    Query nextQuery = query.startAfter(lastVisible);
                                    nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> t) {
                                            if (t.isSuccessful()) {
                                                for (DocumentSnapshot d : t.getResult()) {
                                                    data.add(new CommentRecyclerAdapter.CommentData(
                                                            d,
                                                            d.get("authorID").toString(),
                                                            d.get("author").toString(),
                                                            d.get("comment").toString()
                                                    ));
                                                }
                                                if(isLastItemReached){
                                                    // Last comment reached
                                                }
                                                rAdapter.notifyDataSetChanged();
                                                if (t.getResult().size() != 0) {
                                                    lastVisible = t.getResult().getDocuments().get(t.getResult().size() - 1);
                                                }

                                                if (t.getResult().size() < 5) {
                                                    isLastItemReached = true;
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        };
                        recyclerView.addOnScrollListener(onScrollListener);
                    }
                }
            }
        });
    }

    /* ----BEGIN TIMER SECTION---- */

    /** Load associated User interface elements for the timer. */
    private void loadTimerUI() {
        progress = findViewById(R.id.timer_progress);
        currentDuration = findViewById(R.id.current_duration_text);
        finalDuration = findViewById(R.id.final_duration_text);
        playPause = findViewById(R.id.timer_play_pause);
        timerLayout = findViewById(R.id.timerLayout);
    }

    /** Handles the listener on a play/pause button press from within timer layout.
     * @param slideTimer - Time required to count down from.
     * @param audio - Audio that doesn't loop (If XML file contains it). Null otherwise
     * @param audioLooping - Audio that loops (If XML file contains it). Null otherwise
     * @param audioEnabled - Determines if the timer supports audio.
     */
    private void timerListenerHandler(Float slideTimer, @Nullable AudioURL audio, @Nullable  AudioURL audioLooping, boolean audioEnabled){

        if(timerIsPlaying){
            playPause.setCompoundDrawablesWithIntrinsicBounds(R.drawable.exo_icon_play, 0, 0, 0); //  Change left-most icon back to a play button.
            progress.setProgress(0); //Set current duration to 0s.
            if(audioEnabled){
                try {
                    timer.forceStopTimer(); //Attempt to force the timer to stop.
                } catch (AudioPlaybackError audioPlaybackError) {
                    audioPlaybackError.printStackTrace();
                }
            } else {
                timer.cancel(); //Attempt to force the timer to stop.
            }
            timerIsPlaying = false;
        } else {
            playPause.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_stop, 0, 0 , 0); // Change left-most icon to a stop button.
            long slideTimerLong = (long) Math.abs(slideTimer);

            if(audioEnabled && audioLooping != null){
                loadTimer(slideTimerLong, audio, audioLooping);
            } else if (audioEnabled){
                loadTimer(slideTimerLong, audio, null);
            } else {
                loadTimer(slideTimerLong, null, null);
            }
            timer.startTimer();
            timerIsPlaying = true;
        }
    }

    /** Handle slide transitions for the timer.
     * @param slideNumber - Slide numer we are transitioning to.
     */
    @SuppressLint("SetTextI18n")
    private void timerSlideTransition(int currentSlide, int slideNumber) {
        long slideTimer = (long) Math.abs(slideTimers.get(slideNumber)); //Convert float value to long.

        //  Assign the final & current duration for the given timer in a printable format.
        finalDuration.setText(PresentationTimer.printOutputTime(slideTimer));
        playPause.setCompoundDrawablesWithIntrinsicBounds(R.drawable.exo_icon_play, 0, 0, 0); //  Change left-most icon back to a play button.
        currentDuration.setText("00:00");

        progress.setProgress(0); //Force player-head back to 0s.

        if(timer != null){
            try {
                timer.forceStopTimer();
            } catch (AudioPlaybackError e){
                e.printStackTrace();
            }
            timerIsPlaying = false;
        }
    }


    /** Common updates for all timers
     * @param millisUntilFinished - Time until finished. (in milliseconds)
     * @param duration - Total duration of timer. (in milliseconds)
     */
    private void updateOnTick(long millisUntilFinished, long duration) {
        currentDuration.setText(PresentationTimer.printOutputTime(millisUntilFinished)); //Assign the current duration in the format 'mm:ss'.
        progress.setProgress((int) (100 - (millisUntilFinished * 100)/ duration)); //Assign progress of horizontal bar based upon a value between 0 - 100.
    }


    /** Common updates for all timers upon finishing.
     * @param audioEnabled - Determine how we finish based upon if audio is enabled for the timer.
     */
    private void updateOnFinish(boolean audioEnabled){
        if(audioEnabled){
            try {
                timer.stopTimer();
            } catch (AudioPlaybackError audioPlaybackError) {
                audioPlaybackError.printStackTrace();
            }
        }
        playPause.setCompoundDrawablesWithIntrinsicBounds(R.drawable.exo_icon_play, 0, 0, 0); //
        progress.setProgress(100); //Set the playerheader to it's max value '100'.
    }


    /** Function to load a custom presentation timer. Presentation timer is based off
     * the default CountdownTimer but also supports looping and non-looping audio to be played either during the
     * countdown and(or) at the end.
     * @param duration - Time in milliseconds to count down from
     * @param audioURL - (optional) AudioURL object of audio to be played at the end of the timer countdown.
     *                 or as a standalone file.
     * @param audioLoopingURL - (optional) - AudioURL object of audio to be played during timer countdown.
     */
    private void loadTimer(long duration, @Nullable AudioURL audioURL, @Nullable AudioURL audioLoopingURL) {
        //  Check for a valid timer duration & therefore if a timer exists for the given slide (= -1 if it dosen't).
        if(duration > 0){
            timerLayout.setVisibility(View.VISIBLE); //Make the timer visible to the user.

            if(audioURL == null && audioLoopingURL == null){ //Called when a timer with no audio needs to be created
                timer = new PresentationTimer(duration, COUNTDOWN_INTERVAL){
                    @Override
                    public void onTick(long millisUntilFinished) { //Called when the
                        updateOnTick(millisUntilFinished,duration);
                    }

                    @Override
                    public void onFinish() {
                        updateOnFinish(false);
                        timerIsPlaying = false;
                    }
                };
            } else if(audioLoopingURL == null){ //Called when a timer with no looping audio is found
                timer = new PresentationTimer(duration, COUNTDOWN_INTERVAL, audioURL) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        updateOnTick(millisUntilFinished,duration);
                    }

                    @Override
                    public void onFinish() {
                        updateOnFinish(true);
                        timerIsPlaying = false;
                    }
                };
            } else { //Timer with all audio enabled. (looping + non-looping).
                timer = new PresentationTimer(duration, COUNTDOWN_INTERVAL, audioURL, audioLoopingURL) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        updateOnTick(millisUntilFinished,duration);
                    }

                    @Override
                    public void onFinish() {
                        updateOnFinish(true);
                        timerIsPlaying = false;
                    }
                };
            }
        } else {
            timerLayout.setVisibility(View.GONE); //Hide the timer if no timer needs to be loaded.
        }
    }
}