package com.group4sweng.scranplan.RecipeCreation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.drawable.shapes.OvalShape;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.group4sweng.scranplan.Drawing.GraphicsView;
import com.group4sweng.scranplan.Drawing.Line;
import com.group4sweng.scranplan.Drawing.Rectangle;
import com.group4sweng.scranplan.Drawing.Shape;
import com.group4sweng.scranplan.Drawing.Triangle;
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.Xml.XmlParser;
import com.group4sweng.scranplan.Xml.XmlSerializar;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class RecipeSteps extends Fragment {

    private TextView mStep;
    private GraphicsView mGraphics;
    private ImageButton mButtonMedia;
    private EditText mRecipeText;
    private Switch mTimerSwitch;
    private EditText mTimerValue;
    private Button mButtonDefaults;
    private Button mButtonAudio;
    private Button mButtonGraphics;
    private Button mButtonAdd;
    private Button mButtonSubmit;

    private String mBackgroundColor;
    private String mFont;
    private Integer mFontSize;
    private String mFontColor;
    private String mLineColor;
    private String mFillColor;
    private Integer mSlideWidth;
    private Integer mSlideHeight;

    private String xmlURL;

    private UploadTask uploadTask;
    private StorageReference mStorageRef;
    private StorageReference mMediaRef = null;
    private StorageReference mAudioRef = null;
    private StorageReference mXmlRef;

    private XmlParser.Defaults defaults;
    private ArrayList<XmlParser.Slide> slides;

    private Uri mMediaUri;
    private Uri mAudioUri;

    private Boolean imagePresent;
    private Boolean videoPresent;

    private Integer mediaRequestCode = 1;
    private Integer audioRequestCode = 2;

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

        return view;
    }

    private void initPageItems(View view) {
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mStep = view.findViewById(R.id.recipeStepID);
        mGraphics = view.findViewById(R.id.recipeStepGraphicsView);
        mButtonMedia = view.findViewById(R.id.recipeStepMedia);
        mRecipeText = view.findViewById(R.id.recipeStepText);
        mTimerSwitch = view.findViewById(R.id.recipeStepTimer);
        mTimerValue = view.findViewById(R.id.recipeStepTimerValue);
        mButtonDefaults = view.findViewById(R.id.recipeStepDefaults);
        mButtonAudio = view.findViewById(R.id.recipeStepAudio);
        mButtonGraphics = view.findViewById(R.id.recipeStepGraphics);
        mButtonAdd = view.findViewById(R.id.recipeStepAdd);
        mButtonSubmit = view.findViewById(R.id.recipeStepSubmit);

        defaults = new XmlParser.Defaults("#FFFFFF", "Robotic", 18,
                "#000000", "#000000", "#000000", -1, -1);
        slides = new ArrayList<>();

        imagePresent = false;
        videoPresent = false;
    }

    private void initPageListeners(View view) {
        mButtonMedia.setOnClickListener(v -> {
            Intent mediaSelect = new Intent(Intent.ACTION_PICK);
            mediaSelect.setType("image/* video/*");
            startActivityForResult(mediaSelect, mediaRequestCode);
        });

        mTimerSwitch.setOnClickListener(v -> {
            mTimerValue.setVisibility(View.VISIBLE);
        });

        mButtonDefaults.setOnClickListener(v -> {

        });

        mButtonAudio.setOnClickListener(v -> {
            Intent audioSelect = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(audioSelect, audioRequestCode);
        });

        mButtonGraphics.setOnClickListener(v -> {

        });

        mButtonAdd.setOnClickListener(v -> addSlide());
        mButtonSubmit.setOnClickListener(v -> buildXML());
    }

    private void addSlide() {
        String mediaUrl = null;
        String audioUrl = null;

        if (mMediaUri != null)
            uploadFile(mMediaUri, mMediaRef);
        if (mAudioUri != null)
            uploadFile(mAudioUri, mAudioRef);

        XmlParser.Text text = new XmlParser.Text(mRecipeText.getText().toString(), defaults);

        ArrayList<XmlParser.Line> lines = new ArrayList<>();
        for (Line line : mGraphics.getLines()) {
            lines.add(new XmlParser.Line(line.getxStart().floatValue(), line.getyStart().floatValue(),
                    line.getxEnd().floatValue(), line.getyEnd().floatValue(),
                    Integer.toHexString(line.getColour()), 0, 0));
        }

        ArrayList<XmlParser.Shape> shapes = new ArrayList<>();
        for (Rectangle rect : mGraphics.getOvals())
            shapes.add(new XmlParser.Shape("oval", rect.getXStart(), rect.getYStart(),
                    rect.getWidth(), rect.getHeight(), Integer.toHexString(rect.getColour()),
                    0, 0, null));
        for (Rectangle rect : mGraphics.getRectangles())
            shapes.add(new XmlParser.Shape("rectangle", rect.getXStart(), rect.getYStart(),
                    rect.getWidth(), rect.getHeight(), Integer.toHexString(rect.getColour()),
                    0, 0, null));

        ArrayList<XmlParser.Triangle> triangles = new ArrayList<>();
        for (Triangle triangle : mGraphics.getTriangles()) {
            triangles.add(new XmlParser.Triangle(triangle.getxPos1().floatValue(), triangle.getyPos1().floatValue(),
                    triangle.getxPos2().floatValue(), triangle.getyPos2().floatValue(),
                    triangle.getxPos3().floatValue(), triangle.getyPos3().floatValue(),
                    Integer.toHexString(triangle.getColour()), 0, 0, null));
        }


        XmlParser.Audio audio = new XmlParser.Audio(audioUrl, 0, false);
        XmlParser.Image image = null;
        XmlParser.Video video = null;
        Float timer = null;

        if (imagePresent)
            image = new XmlParser.Image(mediaUrl, 35f, 10f, 30f, 30f, 0, 0);
        if (videoPresent)
            video = new XmlParser.Video(mediaUrl, 0, false, 35f, 10f);
        if (mTimerSwitch.isChecked())
            timer = Float.valueOf(mTimerValue.getText().toString());

        slides.add(new XmlParser.Slide("Step " + (slides.size() + 1), -1,
                text, lines, shapes, triangles, audio,
                null, image, video, timer));
    }

    private void buildXML() {
        XmlParser.DocumentInfo documentInfo = new XmlParser.DocumentInfo("author",
                Calendar.getInstance().getTime().toString(), 1.0f, 1, "");
        XmlSerializar xmlSerializar = new XmlSerializar();

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == mediaRequestCode) {
                mMediaUri = data.getData();

                if (mMediaUri != null) {
                    if (mMediaUri.toString().contains("image")) {
                        imagePresent = true;
                        Picasso.get().load(mMediaUri).fit().centerCrop().into(mButtonMedia);

                        mMediaRef = mStorageRef.child("presentations/images/"
                                + mMediaUri.getLastPathSegment());
                    } else if (mMediaUri.toString().contains("video")) {
                        videoPresent = true;
                        mMediaRef = mStorageRef.child("presentations/videos/"
                                + mMediaUri.getLastPathSegment());
                    }
                }
            } else if (requestCode == audioRequestCode) {
                mAudioUri = data.getData();

                if (mAudioUri != null) {
                    mAudioRef = mStorageRef.child("presentations/audio/"
                            + mAudioUri.getLastPathSegment());
                }
            }
        }
    }
}
