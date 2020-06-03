package com.group4sweng.scranplan.RecipeCreation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.group4sweng.scranplan.Administration.LoadingDialog;
import com.group4sweng.scranplan.Drawing.LayoutCreator;
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.RecipeCreation.RecipeStepsRecycler.StepData;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;
import com.group4sweng.scranplan.Xml.XmlParser;
import com.group4sweng.scranplan.Xml.XmlSerializar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RecipeSteps extends Fragment {

    private RecyclerView mStep;
    private Button mButtonDefaults;
    private Button mButtonAdd;
    private Button mButtonSubmit;

    private Integer mBackgroundColor;
    private String mFont;
    private String mFontSize;
    private Integer mFontSizeInt;
    private Integer mFontColor;
    private Integer mFontBackground;

    private Long recipeNum;

    private UserInfoPrivate mUser;
    private StorageReference mStorageRef;
    private ArrayList<StorageReference> mMediaRefs;
    private ArrayList<StorageReference> mAudioRefs;

    private ArrayList<XmlParser.Slide> slides;

    private LoadingDialog mLoadingDialog;

    private ArrayList<Uri> mMediaUris;
    private ArrayList<Uri> mAudioUris;

    private List<StepData> mStepList;
    private LinearLayoutManager mManager;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.SmoothScroller smoothScroller;
    private Integer mAdapterPos;

    private ArrayList<Boolean> imagePresent;
    private ArrayList<Boolean> videoPresent;

    private Integer mediaRequestCode = 1;
    private Integer audioRequestCode = 2;
    private Integer graphicsRequestCode = 3;
    private Integer defaultsRequestCode = 4;

    RecipeSteps(UserInfoPrivate user, Long recipeNum) {
        mUser = user;
        this.recipeNum = recipeNum;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_recipe_steps, container, false);

        initPageItems(view);
        initPageListeners(view);
        addSlide();

        return view;
    }

    // Initialise all items on page
    private void initPageItems(View view) {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAudioUris = new ArrayList<>();
        mAudioRefs = new ArrayList<>();
        mMediaUris = new ArrayList<>();
        mMediaRefs = new ArrayList<>();

        // Page elements
        mStep = view.findViewById(R.id.recipeStepRecyler);
        mButtonDefaults = view.findViewById(R.id.recipeStepDefaults);
        mButtonAdd = view.findViewById(R.id.recipeStepAdd);
        mButtonSubmit = view.findViewById(R.id.recipeStepSubmit);

        // App defaults
        mBackgroundColor = Color.WHITE;
        mFont = "Robotica";
        mFontSize = "Medium";
        mFontSizeInt = 18;
        mFontColor = Color.BLACK;
        mFontBackground = Color.WHITE;

        slides = new ArrayList<>();
        mLoadingDialog = new LoadingDialog(getActivity());

        mStepList = new ArrayList<>();
        mManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        mStep.setLayoutManager(mManager);
        mAdapter = new RecipeStepsRecycler(this, mStepList);
        mStep.setAdapter(mAdapter);

        smoothScroller = new LinearSmoothScroller(getContext()) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };

        imagePresent = new ArrayList<>();
        videoPresent = new ArrayList<>();
    }

    private void initPageListeners(View view) {

        // Store current default settings and load defaults dialog
        mButtonDefaults.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("backColour", mBackgroundColor);
            bundle.putString("font", mFont);
            bundle.putString("size", mFontSize);
            bundle.putInt("fontColour", mFontColor);
            bundle.putInt("fontBackground", mFontBackground);

            RecipeDefaultsCreation defaultsCreation = new RecipeDefaultsCreation();
            defaultsCreation.setArguments(bundle);
            defaultsCreation.setTargetFragment(this, defaultsRequestCode);
            defaultsCreation.show(getFragmentManager(), "Show defaults fragment");
        });

        // Add slide
        mButtonAdd.setOnClickListener(v -> addSlide());

        // Create recipe
        mButtonSubmit.setOnClickListener(v -> {
            mLoadingDialog.startLoadingDialog();
            new CreateSlide().execute();
        });
    }

    // Add slide to presentation
    private void addSlide() {
        mStepList.add(new StepData(null, null, null, false, null));
        mAdapter.notifyDataSetChanged();

        mMediaUris.add(null);
        mMediaRefs.add(null);
        mAudioUris.add(null);
        mAudioRefs.add(null);

        imagePresent.add(false);
        videoPresent.add(false);

        // Scroll to new slide
        smoothScroller.setTargetPosition(mAdapter.getItemCount() - 1);
        mManager.startSmoothScroll(smoothScroller);
    }

    // Add media element to slide
    void addMedia(int position) {
        mAdapterPos = position;
        Intent mediaSelect = new Intent(Intent.ACTION_GET_CONTENT);
        mediaSelect.setType("image/* video/*");
        startActivityForResult(mediaSelect, mediaRequestCode);
    }

    // Remove media element from slide
    void removeMedia(int position) {
        mStepList.get(position).removeMedia();
        mMediaRefs.remove(position);
        mMediaUris.remove(position);
        imagePresent.set(position, false);
        videoPresent.set(position, false);
        mAdapter.notifyDataSetChanged();
    }

    // Add audio element to slide
    void addAudio(int position) {
        mAdapterPos = position;
        Intent audioSelect = new Intent(Intent.ACTION_GET_CONTENT);
        audioSelect.setType("audio/*");
        startActivityForResult(audioSelect, audioRequestCode);
    }

    // Remove audio element from slide
    void removeAudio(int position) {
        mStepList.get(position).removeAudio();
        mAudioRefs.remove(position);
        mAudioUris.remove(position);
        mAdapter.notifyDataSetChanged();
    }

    // Add timer to slide
    void addTimer(int position) {
        mStepList.get(position).showTimer();
        mAdapter.notifyDataSetChanged();
    }

    // Remove timer from slide
    void removeTimer(int position) {
        mStepList.get(position).removeTimer();
        mAdapter.notifyDataSetChanged();
    }

    // Add user created graphics to slide
    void addGraphics(int position) {
        mAdapterPos = position;
        Intent graphicsView = new Intent(getContext(), LayoutCreator.class);
        startActivityForResult(graphicsView, graphicsRequestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            // Returned on media requests
            if (requestCode == mediaRequestCode) {
                Uri mMediaUri = data.getData();
                mMediaUris.add(mAdapterPos, mMediaUri);

                if (mMediaUri != null) {
                    // Handler for images
                    if (mMediaUri.toString().contains("image")) {
                        imagePresent.set(mAdapterPos, true);

                        // Create unique storage reference
                        mMediaRefs.add(mAdapterPos, mStorageRef.child("presentations/images/"
                                + mUser.getUID() + "_" + recipeNum + "_step" + mAdapterPos));
                        mStepList.get(mAdapterPos).setMedia(mMediaUri);

                        mAdapter.notifyDataSetChanged();

                    // Handler for videos
                    } else if (mMediaUri.toString().contains("video")) {
                        videoPresent.set(mAdapterPos, true);

                        // Create unique storage reference
                        mMediaRefs.add(mAdapterPos, mStorageRef.child("presentations/videos/"
                                + mUser.getUID() + "_" + recipeNum + "_step" + mAdapterPos));
                        mStepList.get(mAdapterPos).setMedia(mMediaUri);
                        mAdapter.notifyDataSetChanged();
                    }
                }

            // Returned on audio requests
            } else if (requestCode == audioRequestCode) {
                Uri mAudioUri = data.getData();
                mAudioUris.add(mAdapterPos, mAudioUri);

                if (mAudioUri != null) {
                    mStepList.get(mAdapterPos).setAudio(mAudioUri);
                    mAdapter.notifyDataSetChanged();

                    // Create unique storage reference
                    mAudioRefs.add(mAdapterPos, mStorageRef.child("presentations/audio/"
                            + mUser.getUID() + "_" + recipeNum + "_step" + mAdapterPos));
                }
            // Returned on user create graphics requests
            } else if (requestCode == graphicsRequestCode) {
                Bundle graphics = data.getExtras();
                ArrayList<XmlParser.Shape> shapes = (ArrayList<XmlParser.Shape>) graphics.getSerializable("shapes");
                ArrayList<XmlParser.Triangle> triangles = (ArrayList<XmlParser.Triangle>) graphics.getSerializable("triangles");

                mStepList.get(mAdapterPos).setGraphics(shapes, triangles);

            // Returned after setting defaults
            } else if (requestCode == defaultsRequestCode) {
                Bundle bundle = data.getExtras();
                mBackgroundColor = bundle.getInt("backColour");
                mFont =  bundle.getString("font");
                mFontSize = bundle.getString("size");
                mFontColor = bundle.getInt("fontColour");
                mFontBackground = bundle.getInt("fontBackground");

                // Convert font size from string to int
                switch (mFontSize) {
                    case "Small":
                        mFontSizeInt = 14;
                        break;
                    case "Large":
                        mFontSizeInt = 22;
                        break;
                    default:
                        mFontSizeInt = 18;
                        break;
                }
            }
        }
    }

    // Compiles all data for slide creation
    @SuppressLint("StaticFieldLeak")
    private class CreateSlide extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            buildXML();
            return null;
        }

        // Create all the elements for XML creation
        private void buildXML() {
            ArrayList<XmlParser.Audio> audios = new ArrayList<>();
            ArrayList<XmlParser.Image> images = new ArrayList<>();
            ArrayList<XmlParser.Video> videos = new ArrayList<>();
            ArrayList<Uri> mediaDownloads = new ArrayList<>();

            // Keep track of uploads and downloads for synchronous finish
            List<UploadTask> uploadTasks = new ArrayList<>();
            List<Task<Uri>> downloadTasks = new ArrayList<>();

            for (int i = 0; i < mAdapter.getItemCount(); i++) {
                int pos = i;

                // Make sure arrays aren't empty for each slide
                audios.add(null);
                images.add(null);
                videos.add(null);
                mediaDownloads.add(null);

                // Upload audio file
                try {
                    UploadTask audioTask = mAudioRefs.get(pos).putFile(mAudioUris.get(pos));
                    uploadTasks.add(audioTask); // Keeps track of ongoing tasks

                    audioTask.addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Failed to upload audio", Toast.LENGTH_SHORT).show())
                            .addOnSuccessListener(audioSnapshot -> {
                                Task<Uri> downloadTask = mAudioRefs.get(pos).getDownloadUrl();
                                downloadTasks.add(downloadTask); //Keeps track of ongoing tasks
                                downloadTask.addOnSuccessListener(audioUri -> {
                                    downloadTasks.add(mAudioRefs.get(pos).getDownloadUrl()); // Retrieve download link

                                    boolean looping = mStepList.get(pos).isLooping();
                                    Integer startTime = mStepList.get(pos).getStartTime();
                                    if(startTime == null){
                                        audios.set(pos, new XmlParser.Audio(audioUri.toString(), 0, looping));
                                    } else {
                                        audios.set(pos, new XmlParser.Audio(audioUri.toString(), startTime, looping));
                                    }
                                });
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Upload media
                try {
                    UploadTask mediaTask = mMediaRefs.get(i).putFile(mMediaUris.get(pos));
                    uploadTasks.add(mediaTask); // Keeps track of ongoing tasks
                    int finalI = i; // Needed for access within class
                    mediaTask.addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Failed to upload media", Toast.LENGTH_SHORT).show())
                            .addOnSuccessListener(mediaSnapshot -> {
                                Task<Uri> downloadTask = mMediaRefs.get(pos).getDownloadUrl();
                                downloadTasks.add(downloadTask); // Keeps track of ongoing tasks
                                downloadTask.addOnSuccessListener(mediaUri -> {
                                    mediaDownloads.set(finalI, mediaUri); // Retrieve download link
                                });
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // When all uploads are completed
                Tasks.whenAllSuccess(uploadTasks).addOnCompleteListener(uTask ->
                        // When all downloads are completed
                        Tasks.whenAllSuccess(downloadTasks).addOnCompleteListener(dTask -> {

                    XmlParser.Text text = null;

                    // If text is present on slide...
                    if (!mStepList.get(pos).getDescription().equals("")) {
                        if (imagePresent.get(pos) || videoPresent.get(pos)) {
                            // Create appropriate media element with correct sizing for text
                            if (imagePresent.get(pos)) {
                                images.set(pos, new XmlParser.Image(mediaDownloads.get(pos).toString(), 10f, 10f, 80f, 40f, 0, 0));
                            } else if (videoPresent.get(pos)) {
                                videos.set(pos, new XmlParser.Video(mediaDownloads.get(pos).toString(), 0, false, 0f, 10f, 100f, 40f));
                            }
                            text = new XmlParser.Text(mStepList.get(pos).getDescription(), "#" + Integer.toHexString(mFontBackground),
                                    mFont, mFontSizeInt, "#" + Integer.toHexString(mFontColor), 800,
                                    10f, 60f, 30f, 80f, 0, 0, "", "");
                        } else {
                            // Create larger text box if no media present
                            text = new XmlParser.Text(mStepList.get(pos).getDescription(), "#" + Integer.toHexString(mFontBackground),
                                    mFont, mFontSizeInt, "#" + Integer.toHexString(mFontColor), 800,
                                    10f, 10f, 80f, 80f, 0, 0, "", "");
                        }
                    }
                    // If no text present
                    else {
                        // Create larger appropriate media element
                        if (imagePresent.get(pos)) {
                            images.set(pos, new XmlParser.Image(mediaDownloads.get(pos).toString(), 10f, 30f, 80f, 80f, 0, 0));
                        } else if (videoPresent.get(pos)) {
                            videos.set(pos, new XmlParser.Video(mediaDownloads.get(pos).toString(), 0, false, 0f, 10f, 100f, 80f));
                        }
                    }

                    // Lines cannot be added
                    ArrayList<XmlParser.Line> lines = new ArrayList<>();
                    // Get user shape locations
                    ArrayList<XmlParser.Shape> shapes = mStepList.get(pos).getShapes();
                    ArrayList<XmlParser.Triangle> triangles = mStepList.get(pos).getTriangles();

                    // Convert timer from minutes into milliseconds
                    Float timer = null;
                    if (mStepList.get(pos).getTimer() != null) {
                        timer = mStepList.get(pos).getTimer() * 60000f;
                    }

                    // Create slide element
                    slides.add(new XmlParser.Slide("Step " + (pos + 1), -1, text, lines, shapes, triangles, audios.get(pos), images.get(pos), videos.get(pos), timer));

                    // Once all slides are created, compile into XML
                    if (slides.size() == mAdapter.getItemCount())
                        createXML();
                }));
            }
        }


        // Creates XML file
        private void createXML() {
            XmlSerializar xmlSerializar = new XmlSerializar();

            // Creates document info and defaults for XML file
            XmlParser.DocumentInfo documentInfo = new XmlParser.DocumentInfo(mUser.getUID(),
                    Calendar.getInstance().getTime().toString(), 1.0f, mAdapter.getItemCount(), "User recipe " + recipeNum);
            XmlParser.Defaults defaults = new XmlParser.Defaults("#" + Integer.toHexString(mBackgroundColor), mFont, "#" + Integer.toHexString(mFontBackground),
                    mFontSizeInt, "#" + Integer.toHexString(mFontColor), "#000000", "#000000", -1, -1);

            try {
                // Create new file in phone storage
                File recipeXML = new File(getContext().getFilesDir().getPath() + "recipe.xml");
                recipeXML.createNewFile();

                // Compile XML
                FileOutputStream fileOutputStream = new FileOutputStream(recipeXML);
                xmlSerializar.compile(fileOutputStream, documentInfo, defaults, slides);

                // Upload XML file to unique location and return to creation screen
                StorageReference xmlRef = mStorageRef.child("presentations/xml/" + mUser.getUID() + "_" + recipeNum);
                UploadTask xmlTask = xmlRef.putFile(Uri.fromFile(recipeXML));
                xmlTask.addOnSuccessListener(taskSnapshot -> xmlRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("xml_url", uri.toString());

                    mLoadingDialog.dismissDialog();
                    ((RecipeCreation) requireActivity()).stepComplete(2, bundle);
                }));
            } catch (IOException e) {
                Toast.makeText(getContext(), "Something went wrong, please try again", Toast.LENGTH_SHORT);
                e.printStackTrace();
            }
        }
    }
}
