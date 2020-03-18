package com.group4sweng.scranplan.Presentation;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.group4sweng.scranplan.Home;
import com.group4sweng.scranplan.MainActivity;
import com.group4sweng.scranplan.ProfileSettings;
import com.group4sweng.scranplan.PublicProfile;
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.SearchFunctions.SearchListFragment;
import com.group4sweng.scranplan.SearchFunctions.SearchRecyclerAdapter;
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

import static androidx.test.InstrumentationRegistry.getContext;

/**
 *  All parts of the presentation, taking the XML document and separating it out into its slide that
 *  are then conveniently displayed for the user. User can go forward and backwards along with
 *  navigating to any particular slide. User can also add comments and view existing comments to
 *  any particular slide.
 */
public class Presentation extends AppCompatActivity {

    FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private ProgressBar spinner;
    private XmlParser.DocumentInfo documentInfo;
    private DisplayMetrics displayMetrics = new DisplayMetrics();
    final static String TAG = "PRES";

    // Comment additions
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
            //TODO old comments section where we used XML, for James Crawley to delete where he sees fit
//            if (slide.comments != null) {
//                slideLayout.addView(addComments(slide.comments, defaults, defaultTypeFace[0], slideWidth, slideHeight));
//            }
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
        comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandableLayout.toggle();
            }
        });

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
}