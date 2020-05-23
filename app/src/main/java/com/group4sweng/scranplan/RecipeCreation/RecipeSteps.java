package com.group4sweng.scranplan.RecipeCreation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import com.group4sweng.scranplan.Drawing.LayoutCreator;
import com.group4sweng.scranplan.Drawing.Rectangle;
import com.group4sweng.scranplan.Drawing.Triangle;
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

    private UserInfoPrivate mUser;
    private StorageReference mStorageRef;
    private ArrayList<StorageReference> mMediaRefs;
    private ArrayList<StorageReference> mAudioRefs;

    private ArrayList<XmlParser.Slide> slides;

    private ArrayList<Uri> mMediaUris;
    private ArrayList<Uri> mAudioUris;

    private List<StepData> mStepList;
    private LinearLayoutManager mManager;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.SmoothScroller smoothScroller;
    private Integer mAdapterPos;

    private Boolean imagePresent;
    private Boolean videoPresent;

    private Integer mediaRequestCode = 1;
    private Integer audioRequestCode = 2;
    private Integer graphicsRequestCode = 3;
    private Integer defaultsRequestCode = 4;

    RecipeSteps(UserInfoPrivate user) {
        mUser = user;
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

    private void initPageItems(View view) {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAudioUris = new ArrayList<>();
        mAudioRefs = new ArrayList<>();
        mMediaUris = new ArrayList<>();
        mMediaRefs = new ArrayList<>();

        mStep = view.findViewById(R.id.recipeStepRecyler);
        mButtonDefaults = view.findViewById(R.id.recipeStepDefaults);
        mButtonAdd = view.findViewById(R.id.recipeStepAdd);
        mButtonSubmit = view.findViewById(R.id.recipeStepSubmit);

        mBackgroundColor = Color.WHITE;
        mFont = "Robotica";
        mFontSize = "Medium";
        mFontSizeInt = 18;
        mFontColor = Color.BLACK;

        slides = new ArrayList<>();

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

        imagePresent = false;
        videoPresent = false;
    }

    private void initPageListeners(View view) {

        mButtonDefaults.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("backColour", mBackgroundColor);
            bundle.putString("font", mFont);
            bundle.putString("size", mFontSize);
            bundle.putInt("fontColour", mFontColor);

            RecipeDefaultsCreation defaultsCreation = new RecipeDefaultsCreation();
            defaultsCreation.setArguments(bundle);
            defaultsCreation.setTargetFragment(this, defaultsRequestCode);
            defaultsCreation.show(getFragmentManager(), "Show defaults fragment");
        });

        mButtonAdd.setOnClickListener(v -> addSlide());

        mButtonSubmit.setOnClickListener(v -> new CreateSlide().execute());
    }

    private void addSlide() {
        mStepList.add(new StepData(null, null, null));
        mAdapter.notifyDataSetChanged();

        mMediaUris.add(null);
        mMediaRefs.add(null);
        mAudioUris.add(null);
        mAudioRefs.add(null);

        smoothScroller.setTargetPosition(mAdapter.getItemCount() - 1);
        mManager.startSmoothScroll(smoothScroller);
    }

    void addMedia(int position) {
        mAdapterPos = position;
        Intent mediaSelect = new Intent(Intent.ACTION_PICK);
        mediaSelect.setType("image/* video/*");
        startActivityForResult(mediaSelect, mediaRequestCode);
    }

    void removeMedia(int position) {
        mStepList.get(position).removeMedia();
        mMediaRefs.remove(position);
        mMediaUris.remove(position);
        imagePresent = false;
        videoPresent = false;
        mAdapter.notifyDataSetChanged();
    }

    void addAudio(int position) {
        mAdapterPos = position;
        Intent audioSelect = new Intent(Intent.ACTION_PICK);
        audioSelect.setType("audio/*");
        startActivityForResult(audioSelect, audioRequestCode);
    }

    void removeAudio(int position) {
        mStepList.get(position).removeAudio();
        mAudioRefs.remove(position);
        mAudioUris.remove(position);
        mAdapter.notifyDataSetChanged();
    }

    void addTimer(int position) {
        mStepList.get(position).showTimer();
        mAdapter.notifyDataSetChanged();
    }

    void removeTimer(int position) {
        mStepList.get(position).removeTimer();
        mAdapter.notifyDataSetChanged();
    }

    void addGraphics(int position) {
        mAdapterPos = position;
        Intent graphicsView = new Intent(getContext(), LayoutCreator.class);
        startActivityForResult(graphicsView, graphicsRequestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Test", "Activity result");
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == mediaRequestCode) {
                Uri mMediaUri = data.getData();
                mMediaUris.add(mAdapterPos, mMediaUri);

                if (mMediaUri != null) {
                    if (mMediaUri.toString().contains("image")) {
                        imagePresent = true;
                        mMediaRefs.add(mAdapterPos, mStorageRef.child("presentations/images/"
                                + mUser.getUID() + "_" + mUser.getRecipes() + "_step" + mAdapterPos));
                        mStepList.get(mAdapterPos).setMedia(mMediaUri);
                        mAdapter.notifyDataSetChanged();
                    } else if (mMediaUri.toString().contains("video")) {
                        videoPresent = true;
                        mMediaRefs.add(mAdapterPos, mStorageRef.child("presentations/videos/"
                                + mUser.getUID() + "_" + mUser.getRecipes() + "_step" + mAdapterPos));
                        mStepList.get(mAdapterPos).setMedia(mMediaUri);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            } else if (requestCode == audioRequestCode) {
                Uri mAudioUri = data.getData();
                mAudioUris.add(mAdapterPos, mAudioUri);

                if (mAudioUri != null) {
                    mStepList.get(mAdapterPos).setAudio(mAudioUri);
                    mAdapter.notifyDataSetChanged();

                    mAudioRefs.add(mAdapterPos, mStorageRef.child("presentations/audio/"
                            + mUser.getUID() + "_" + mUser.getRecipes() + "_step" + mAdapterPos));
                }
            } else if (requestCode == graphicsRequestCode) {
                Bundle graphics = data.getExtras();
                Log.d("Test", "Worked");
                ArrayList<XmlParser.Shape> shapes = (ArrayList<XmlParser.Shape>) graphics.getSerializable("shapes");
                ArrayList<XmlParser.Triangle> triangles = (ArrayList<XmlParser.Triangle>) graphics.getSerializable("triangles");

                mStepList.get(mAdapterPos).setGraphics(shapes, triangles);
            } else if (requestCode == defaultsRequestCode) {
                Bundle bundle = data.getExtras();
                mBackgroundColor = bundle.getInt("backColour");
                mFont =  bundle.getString("font");
                mFontSize = bundle.getString("size");
                mFontColor = bundle.getInt("fontColour");

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

    private class CreateSlide extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            buildXML();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d("Test", "POST EXECUTE");
        }

        private void buildXML() {
            ArrayList<XmlParser.Audio> audios = new ArrayList<>();
            ArrayList<XmlParser.Image> images = new ArrayList<>();
            ArrayList<XmlParser.Video> videos = new ArrayList<>();

            List<UploadTask> uploadTasks = new ArrayList<>();
            List<Task<Uri>> downloadTasks = new ArrayList<>();

            for (int i = 0; i < mAdapter.getItemCount(); i++) {
                int pos = i;

                audios.add(null);
                images.add(null);
                videos.add(null);

                try {
                    UploadTask audioTask = mAudioRefs.get(pos).putFile(mAudioUris.get(pos));
                    uploadTasks.add(audioTask);
                    Log.d("Test", "Audio upload starting");

                    audioTask.addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show())
                            .addOnSuccessListener(audioSnapshot -> {
                                Log.d("Test", "Audio upload done");
                                Task<Uri> downloadTask = mAudioRefs.get(pos).getDownloadUrl();
                                downloadTasks.add(downloadTask);
                                downloadTask.addOnSuccessListener(audioUri -> {
                                    Log.d("Test", "Audio task complete");
                                    downloadTasks.add(mAudioRefs.get(pos).getDownloadUrl());
                                    audios.set(pos, new XmlParser.Audio(audioUri.toString(), 0, false));
                                });
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    UploadTask mediaTask = mMediaRefs.get(pos).putFile(mMediaUris.get(pos));
                    uploadTasks.add(mediaTask);
                    Log.d("Test", "Media[" + i + "] upload starting");

                    int finalI = i;
                    mediaTask.addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Failed to upload media", Toast.LENGTH_SHORT).show())
                            .addOnSuccessListener(mediaSnapshot -> {
                                Log.d("Test", "Media[" + finalI + "] upload done");
                                Task<Uri> downloadTask = mMediaRefs.get(pos).getDownloadUrl();
                                downloadTasks.add(downloadTask);
                                downloadTask.addOnSuccessListener(mediaUri -> {
                                    Log.d("Test", "Media[" + finalI + " task complete");
                                    if (imagePresent) {
                                        images.set(pos, new XmlParser.Image(mediaUri.toString(), 30f, 10f, 40f, 40f, 0, 0));
                                    } else if (videoPresent) {
                                        videos.set(pos, new XmlParser.Video(mediaUri.toString(), 0, false, 30f, 10f));
                                    }
                                });
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }

                int finalI1 = i;
                Tasks.whenAllSuccess(uploadTasks).addOnCompleteListener(uTask -> {
                    Log.d("Test", "All uploads done for " + finalI1);

                    Tasks.whenAllSuccess(downloadTasks).addOnCompleteListener(dTask -> {
                        Log.d("Test", "All tasks complete for " + finalI1);

                        XmlParser.Text text = new XmlParser.Text(mStepList.get(pos).getDescription(), mFont, mFontSizeInt, "#" + Integer.toHexString(mFontColor), 800,
                                30f, 50f, 30f, 40f, 0, 0, "", "");

                        ArrayList<XmlParser.Line> lines = new ArrayList<>();
                        ArrayList<XmlParser.Shape> shapes = mStepList.get(pos).getShapes();
                        ArrayList<XmlParser.Triangle> triangles = mStepList.get(pos).getTriangles();

                        slides.add(new XmlParser.Slide("Step " + pos + 1, -1, text, lines, shapes, triangles, audios.get(pos),
                                null, images.get(pos), videos.get(pos), mStepList.get(pos).getimer()));
                        if (slides.size() == mAdapter.getItemCount())
                            createXML();
                    });
                });
            }
        }

        private void createXML() {
            XmlSerializar xmlSerializar = new XmlSerializar();
            XmlParser.DocumentInfo documentInfo = new XmlParser.DocumentInfo(mUser.getUID(),
                    Calendar.getInstance().getTime().toString(), 1.0f, mAdapter.getItemCount(), "User recipe " + mUser.getRecipes());
            XmlParser.Defaults defaults = new XmlParser.Defaults("#" + Integer.toHexString(mBackgroundColor), mFont, mFontSizeInt,
                    "#" + Integer.toHexString(mFontColor), "#000000", "#000000", -1, -1);
            Log.d("Test", "Building XML");
            try {
                File recipeXML = new File(getContext().getFilesDir().getPath() + "recipe.xml");
                recipeXML.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(recipeXML);
                xmlSerializar.compile(fileOutputStream, documentInfo, defaults, slides);
                Log.d("Test", "XML written");

                StorageReference xmlRef = mStorageRef.child("presentations/xml/" + mUser.getUID() + "_" + mUser.getRecipes());
                UploadTask xmlTask = xmlRef.putFile(Uri.fromFile(recipeXML));
                xmlTask.addOnSuccessListener(taskSnapshot -> xmlRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("xml_url", uri.toString());

                    ((RecipeCreation) requireActivity()).stepComplete(2, bundle);
                }));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
