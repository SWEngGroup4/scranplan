package com.group4sweng.scranplan.RecipeCreation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.group4sweng.scranplan.Drawing.GraphicsView;
import com.group4sweng.scranplan.Drawing.Line;
import com.group4sweng.scranplan.Drawing.Rectangle;
import com.group4sweng.scranplan.Drawing.Triangle;
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.RecipeCreation.RecipeStepsRecycler.StepData;
import com.group4sweng.scranplan.Xml.XmlParser;
import com.group4sweng.scranplan.Xml.XmlSerializar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class RecipeSteps extends Fragment {

    private RecyclerView mStep;
    private Button mButtonDefaults;
    private Button mButtonAdd;
    private Button mButtonSubmit;

    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;

    private Integer mBackgroundColor;
    private String mFont;
    private String mFontSize;
    private Integer mFontColor;

    private String xmlURL;

    private UploadTask uploadTask;
    private StorageReference mStorageRef;
    private StorageReference mMediaRef = null;
    private ArrayList<StorageReference> mMediaRefs;
    private StorageReference mAudioRef = null;
    private ArrayList<StorageReference> mAudioRefs;
    private StorageReference mXmlRef;

    private XmlParser.Defaults defaults;
    private ArrayList<XmlParser.Slide> slides;

    private Uri mMediaUri;
    private ArrayList<Uri> mMediaUris;
    private Uri mAudioUri;
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
    private Integer defaultsRequestCode = 3;

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
        mMediaUris = new ArrayList<>();
        mMediaRefs = new ArrayList<>();

        mStep = view.findViewById(R.id.recipeStepRecyler);
        mButtonDefaults = view.findViewById(R.id.recipeStepDefaults);
        mButtonAdd = view.findViewById(R.id.recipeStepAdd);
        mButtonSubmit = view.findViewById(R.id.recipeStepSubmit);

        mBackgroundColor = Color.WHITE;
        mFont = "Robotica";
        mFontSize = "Medium";
        mFontColor = Color.BLACK;

        defaults = new XmlParser.Defaults("#FFFFFF", "Robotic", 18,
                "#000000", "#000000", "#000000", -1, -1);
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

        mButtonSubmit.setOnClickListener(v -> buildXML());
    }

    private void addSlide() {
        mStepList.add(new StepData(null, null, null));
        mAdapter.notifyDataSetChanged();

        smoothScroller.setTargetPosition(mAdapter.getItemCount() - 1);
        mManager.startSmoothScroll(smoothScroller);
    }

    private void buildXML() {
        XmlParser.DocumentInfo documentInfo = new XmlParser.DocumentInfo("author",
                Calendar.getInstance().getTime().toString(), 1.0f, 1, "");
        XmlSerializar xmlSerializar = new XmlSerializar();

        Boolean UPLOADING = true;
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            int pos = i;
            while (UPLOADING) {
                mAudioRefs.get(i).putFile(mAudioUris.get(i)).addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show())
                        .addOnSuccessListener(audioSnapshot -> mAudioRefs.get(pos).getDownloadUrl().addOnSuccessListener(audioUri -> {
                            XmlParser.Audio audio = new XmlParser.Audio(audioUri.toString(), 0, false);

                            StorageReference reference = mStorageRef.child("presentations/images/" + mMediaUris.get(pos).getLastPathSegment());
                            reference.putFile(mMediaUris.get(pos)).addOnFailureListener(e ->
                                    Toast.makeText(getContext(), "Failed to upload media", Toast.LENGTH_SHORT).show())
                                    .addOnSuccessListener(mediaSnapshot -> mMediaRefs.get(pos).getDownloadUrl().addOnSuccessListener(mediaUri -> {
                                        XmlParser.Image image = null;
                                        XmlParser.Video video = null;
                                        if (imagePresent) {
                                            image = new XmlParser.Image(mediaUri.toString(), 30f, 10f, 40f, 40f, 0, 0);
                                        } else if (videoPresent) {
                                            video = new XmlParser.Video(mediaUri.toString(), 0, false, 30f, 10f);
                                        }

                                        Integer fontSize = 18;
                                        switch (mFontSize) {
                                            case "Small":
                                                fontSize = 14;
                                                break;
                                            case "Medium":
                                                fontSize = 18;
                                                break;
                                            case "Large":
                                                fontSize = 22;
                                                break;
                                        }

                                        XmlParser.Text text = new XmlParser.Text(mStepList.get(pos).getDescription(), mFont, fontSize, mFontColor.toString(), 800,
                                                30f, 50f, 30f, 40f, 0, 0, "", "");
                                        ArrayList<XmlParser.Line> lines = new ArrayList<>();
                                        ArrayList<XmlParser.Shape> shapes = new ArrayList<>();
                                        ArrayList<XmlParser.Triangle> triangles = new ArrayList<>();

                                        slides.add(new XmlParser.Slide("Step: pos", -1, text, lines, shapes, triangles, audio, null, image, video, null));
                                    }));
                        }).addOnFailureListener(e -> {
                            throw new RuntimeException("Unable to grab image URL from Firebase for image URL being uploaded currently. This shouldn't happen.");
                        }));
            }
        }

        xmlSerializar.compile(documentInfo, defaults, slides);

        Bundle bundle = new Bundle();
        bundle.putString("xmlURL", xmlURL);

        ((RecipeCreation) requireActivity()).stepComplete(2, bundle);
    }

    private void uploadFile(Uri file, StorageReference ref) {
//        UploadTask uploadTask = ref.putFile(file);
//
//        Task<Uri> uriTask = uploadTask.continueWithTask(task -> {
//            if (!task.isSuccessful()) {
//                throw task.getException();
//            }
//            return ref.getDownloadUrl();
//        }).addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                Uri downloadUri = task.getResult();
//                Log.d("Test", String.valueOf(downloadUri));
//            }
//        });
        ref.putFile(file).addOnFailureListener(e ->
                Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show())
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(locationUri -> {
                        String imageUrl = locationUri.toString();
                    }).addOnFailureListener(e -> {
                        throw new RuntimeException("Unable to grab image URL from Firebase for image URL being uploaded currently. This shouldn't happen.");
                    });
                }
            });
    }

    void addMedia(int position) {
        mAdapterPos = position;
        Intent mediaSelect = new Intent(Intent.ACTION_GET_CONTENT);
        mediaSelect.setType("image/* video/*");
        startActivityForResult(mediaSelect, mediaRequestCode);
    }

    void removeMedia(int position) {
        mStepList.get(position).removeMedia();
        mAdapter.notifyDataSetChanged();
    }

    void addAudio(int position) {
        mAdapterPos = position;
        Intent audioSelect = new Intent(Intent.ACTION_GET_CONTENT);
        audioSelect.setType("audio/*");
        startActivityForResult(audioSelect, audioRequestCode);
    }

    void removeAudio(int position) {
        mStepList.get(position).removeAudio();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == mediaRequestCode) {
                mMediaUri = data.getData();
                mMediaUris.add(mAdapterPos, mMediaUri);

                if (mMediaUri != null) {
                    if (mMediaUri.toString().contains("image")) {
                        imagePresent = true;

                        mStepList.get(mAdapterPos).setMedia(mMediaUri);
                        mAdapter.notifyDataSetChanged();
                    } else if (mMediaUri.toString().contains("video")) {
                        videoPresent = true;
                    }
                }
            } else if (requestCode == audioRequestCode) {
                mAudioUri = data.getData();
                mAudioUris.add(mAdapterPos, mAudioUri);

                if (mAudioUri != null) {
                    mStepList.get(mAdapterPos).setAudio(mAudioUri);
                    mAdapter.notifyDataSetChanged();

                    mAudioRefs.add(mAdapterPos, mStorageRef.child("presentations/audio/"
                            + mAudioUri.getLastPathSegment()));
                }
            } else if (requestCode == defaultsRequestCode) {
                Bundle bundle = data.getExtras();
                mBackgroundColor = bundle.getInt("backColour");
                mFont =  bundle.getString("font");
                mFontSize = bundle.getString("size");
                mFontColor = bundle.getInt("fontColour");
            }
        }
    }
}
