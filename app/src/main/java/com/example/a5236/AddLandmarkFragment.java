package com.example.a5236;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AddLandmarkFragment extends Fragment {
    private static final String TAG = "AddLandmarkFragment";
    private static final int Image_Capture_Code = 1;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    ImageView mImageView;
    EditText titleEditText,descriptionEditText, hintEditText, difficultyEditText;
    Button createLandmarkBtn, retryBtn, backBtn;
    ProgressBar loadingProgressBar;
    LoginActivity mContext;

    FusedLocationProviderClient fusedLocationProviderClient;
    private String coordinates;

    public AddLandmarkFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_landmark, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mImageView = view.findViewById(R.id.imageView);
        titleEditText = view.findViewById(R.id.title);
        descriptionEditText = view.findViewById(R.id.description);
        hintEditText = view.findViewById(R.id.hint);
        difficultyEditText = view.findViewById(R.id.difficulty);
        createLandmarkBtn = view.findViewById(R.id.createLandmark);
        retryBtn = view.findViewById(R.id.retry);
        backBtn = view.findViewById(R.id.backButton);
        loadingProgressBar = view.findViewById(R.id.loading);

        mContext = (LoginActivity) getActivity();

        //Initialize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);

        titleEditText.addTextChangedListener(txtWatcher);
        descriptionEditText.addTextChangedListener(txtWatcher);
        hintEditText.addTextChangedListener(txtWatcher);
        difficultyEditText.addTextChangedListener(txtWatcher);


        mImageView.setImageBitmap(LoginActivity.getCurrentBitmap());

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(AddLandmarkFragment.this)
                        .navigate(R.id.action_addLandmarkFragment_to_landmarksListFragment);

            }
        });

        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cInt,Image_Capture_Code);
            }
        });

        createLandmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                loadingProgressBar.setVisibility(View.VISIBLE);
            final String title = titleEditText.getText().toString();
            final String desc = descriptionEditText.getText().toString();
            final String hint = hintEditText.getText().toString();
            final int difficulty = Integer.parseInt(difficultyEditText.getText().toString());
            if(ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED){
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if(location != null){
                            Double latitude = location.getLatitude();
                            Double longtitude = location.getLongitude();
                            coordinates = longtitude + "," + latitude;
                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    tryCreateLandmark(snapshot, title, difficulty, desc, hint);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) { }
                            });
                        }
                    }
                });
            }else{
                // permission denied
                ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION} , 44);
            }
            }
        });


    }

    private final TextWatcher txtWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {}
        @Override
        public void afterTextChanged(Editable s) {
            int checksPassed = 0;
            if(titleEditText.getText().toString().length()!=0){
                checksPassed++;
            }else{
                titleEditText.setError(mContext.getResources().getString(R.string.title_error));
            }
            if(descriptionEditText.getText().toString().length()!=0 ){
                checksPassed++;
            }else{
                descriptionEditText.setError(mContext.getResources().getString(R.string.desc_error));
            }
            if(hintEditText.getText().toString().length() != 0 ){
                checksPassed++;
            }else{
                hintEditText.setError(mContext.getResources().getString(R.string.hint_error));
            }
            if(difficultyEditText.getText().toString().length() != 0 &&
                    Integer.parseInt(difficultyEditText.getText().toString()) > 0 &&
                    Integer.parseInt(difficultyEditText.getText().toString()) < 11){
                checksPassed++;
            }else{
                difficultyEditText.setError(mContext.getResources().getString(R.string.diff_error));
            }
            if(checksPassed == 4){
                createLandmarkBtn.setEnabled(true);
            }else{
                createLandmarkBtn.setEnabled(false);
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Capture_Code) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                mImageView.setImageBitmap(bitmap);
                LoginActivity.setCurrentBitmap(bitmap);

            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(AddLandmarkFragment.this)
                        .navigate(R.id.action_addLandmarkFragment_to_landmarksListFragment);
            }
        }
    }

    public void tryCreateLandmark(DataSnapshot dataSnapshot, final String title, final int diff, final String desc, final String hint){
        boolean landmarkExists = checkTitleExists(dataSnapshot,title);
        if (!landmarkExists) {
            byte[] data = covertBitmapToStoreInFirebase(LoginActivity.getCurrentBitmap());
            mStorageRef = FirebaseStorage.getInstance().getReference();
            StorageReference fileStorageRef = mStorageRef.child("images/" + title);
            UploadTask uploadTask = fileStorageRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {

                @Override
                public void onFailure(@NonNull Exception exception) {
                    loadingProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext().getApplicationContext(), "Failed to create landmark", Toast.LENGTH_LONG).show();

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String imageRef = "images/" + title;
                    List<String> foundByUsers = new ArrayList<String>();
                    foundByUsers.add(LoginActivity.getLoggedInUser().getUserId());
                    Landmark landmark = new Landmark(title, diff, coordinates, imageRef, desc, hint, LoginActivity.getLoggedInUser().getUserId(), foundByUsers);
                    mDatabase.child("Landmarks").child(title).setValue(landmark);
                    updateLandmarkList(landmark);
                    Toast.makeText(getContext().getApplicationContext(), "Landmark Successfully Created", Toast.LENGTH_LONG).show();
                    NavHostFragment.findNavController(AddLandmarkFragment.this)
                                    .navigate(R.id.action_addLandmarkFragment_to_landmarksListFragment);
                }
            });

        }else{
            loadingProgressBar.setVisibility(View.GONE);
            Toast.makeText(getContext().getApplicationContext(), "Landmark Title Already Exists", Toast.LENGTH_LONG).show();
        }
    }
    private boolean checkTitleExists(DataSnapshot dataSnapshot, final String title){
        boolean landmarkExists = false;
        for (DataSnapshot ds : dataSnapshot.getChildren()){
            if (ds.getKey().equals("Landmarks")){
                HashMap<String, Object> hm = (HashMap<String, Object>) ds.getValue();
                landmarkExists = hm.containsKey(title);
            }
        }
        return landmarkExists;
    }

    private void updateLandmarkList(Landmark landmark){
        HashMap<String, List<Landmark>> currentLandmarks = LoginActivity.getLandmarkItemList();
        List<Landmark> currentFound = currentLandmarks.get("Found");
        currentFound.add(landmark);
        currentLandmarks.put("Found", currentFound);
        LoginActivity.setLandmarkItemList(currentLandmarks);
    }

    public byte[] covertBitmapToStoreInFirebase(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        return data;
    }
}