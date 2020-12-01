package com.example.a5236;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.service.autofill.Dataset;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.example.a5236.data.model.LoggedInUser;
import com.example.a5236.ui.login.LoginFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static androidx.core.content.ContextCompat.getSystemService;


public class LandmarkFragment extends Fragment {



    private final int PERMISSION_REQUEST_CODE_LOCATION = 3;
    private static Landmark landmark;
    private StorageReference mStorageRef;
    private Context mContext;
    private String coordinates;
    FusedLocationProviderClient fusedLocationProviderClient;
    private DatabaseReference mDatabase;

    static Button foundButton;
    static Button hintButton;

    public LandmarkFragment() {
        // Required empty public constructor
    }

    public static LandmarkFragment newInstance() {
        LandmarkFragment fragment = new LandmarkFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_landmark, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        landmark = LoginActivity.getCurrentLandmark();
        mContext = (LoginActivity) getActivity();
        //Initialize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);

        foundButton = getView().findViewById(R.id.landmark_found);
        hintButton = getView().findViewById(R.id.landmark_hint);
        final ImageView landmarkImg = getView().findViewById(R.id.landmark_img);
        final TextView landmarkTitle = getView().findViewById(R.id.landmark_title);
        final TextView landmarkDifficulty = getView().findViewById(R.id.landmark_difficulty);
        final TextView landmarkDescription = getView().findViewById(R.id.landmark_description);
        final TextView landmarkHint = getView().findViewById(R.id.landmark_hint_text);
        //disable found and hint button if user already found landmark
        if (landmark.getFoundByUsers().contains(LoggedInUser.getUserId())) {
            foundButton.setEnabled(false);
            hintButton.setEnabled(false);
            landmarkHint.setText(landmark.getHint());
        }

        landmarkTitle.setText(landmark.getTitle());
        landmarkDifficulty.setText(getString(R.string.landmark_difficulty)+" " + Integer.toString(landmark.getDifficulty()));
        landmarkDescription.setText(landmark.getDescription());
        if(LoginActivity.isConnectedToInternet(mContext)){
            mStorageRef = FirebaseStorage.getInstance().getReference().child(landmark.getImage());
            GlideApp.with(mContext).load(mStorageRef).into(landmarkImg);
        }else{
            Toast.makeText(mContext,  getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }

        foundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION} , PERMISSION_REQUEST_CODE_LOCATION );
            }
        });

        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginActivity.isConnectedToInternet(mContext)){
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int score = Integer.parseInt(snapshot.child("Accounts")
                                    .child(LoggedInUser.getUserId()).child("score").getValue().toString());
                            mDatabase.child("Accounts").child(LoggedInUser.getUserId())
                                    .child("score").setValue(score - 1);
                            Account account = LoginActivity.getUser();
                            account.setScore(score-1);
                            LoginActivity.setUser(account);
                            landmarkHint.setText(landmark.getHint());
                            hintButton.setEnabled(false);
                            Toast.makeText((LoginActivity) getActivity(),getString(R.string.hint_pt_deduction),Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }else{
                    Toast.makeText(mContext,  getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkFoundLandmark();
                }else {
                    Toast.makeText(getActivity(), getString(R.string.location_access), Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    @SuppressLint("MissingPermission")
    // only called if permission is granted
    private void checkFoundLandmark() {
        fusedLocationProviderClient.getLastLocation()
            .addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        Double latitude = location.getLatitude();
                        Double longitude = location.getLongitude();
                        coordinates = longitude + "," + latitude;
                        if(distance(landmark.getCoordinates(), latitude, longitude) <= 100) {
                            //update buttons and foundByUsers locally
                            LandmarkFragment.foundButton.setEnabled(false);
                            LandmarkFragment.hintButton.setEnabled(false);
                            landmark.getFoundByUsers().add(LoggedInUser.getUserId());
                            if(LoginActivity.isConnectedToInternet(mContext)){
                                mDatabase = FirebaseDatabase.getInstance().getReference();
                                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        int score = Integer.parseInt(snapshot.child("Accounts")
                                                .child(LoggedInUser.getUserId()).child("score").getValue().toString());
                                        mDatabase.child("Accounts").child(LoggedInUser.getUserId())
                                                .child("score").setValue(landmark.getDifficulty() + score);
                                        Account account = LoginActivity.getUser();
                                        account.setScore(landmark.getDifficulty() + score);
                                        LoginActivity.setUser(account);

                                        ArrayList<String> foundByUsersAL = (ArrayList<String>)
                                                snapshot.child("Landmarks").child(landmark.getTitle())
                                                        .child("foundByUsers").getValue();

                                        foundByUsersAL.add(LoggedInUser.getUserId());
                                        mDatabase.child("Landmarks").child(landmark.getTitle())
                                                .child("foundByUsers").setValue(foundByUsersAL);

                                        Toast.makeText(getActivity(), getString(R.string.found_pt_add)+" " +
                                                (score + landmark.getDifficulty()), Toast.LENGTH_SHORT).show();

                                        updateLandmarkList(landmark);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });

                                NavHostFragment.findNavController(LandmarkFragment.this)
                                        .navigate(R.id.action_landmarkFragment_to_landmarksListFragment);
                            }else{
                                Toast.makeText(mContext,  getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.wrong_location), Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getActivity(), getString(R.string.failed_location), Toast.LENGTH_SHORT).show();
                    }
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), getString(R.string.failed_location), Toast.LENGTH_SHORT).show();
                }
            });

    }

    //gets distance between guess and actual location of landmark using the haversine formula.
    public static double distance(String landmarkCoordinates, double latGuess, double lonGuess) {

        final int R = 6371; // Radius of the earth

        double lonLandmark = Double.parseDouble(landmarkCoordinates.substring(0,landmarkCoordinates.indexOf(',')));
        double latLandmark = Double.parseDouble(landmarkCoordinates.substring(landmarkCoordinates.indexOf(',') + 1));

        double latDistance = Math.toRadians(latGuess - latLandmark);
        double lonDistance = Math.toRadians(lonGuess - lonLandmark);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(latLandmark)) * Math.cos(Math.toRadians(latGuess))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c * 1000 * 3.281; // convert to meters and then feet

    }

    private void updateLandmarkList(Landmark landmark){
        HashMap<String, List<Landmark>> currentLandmarks = LoginActivity.getLandmarkItemList();
        List<Landmark> currentFound = currentLandmarks.get(getString(R.string.found_group));
        List<Landmark> currentNotFound = currentLandmarks.get(getString(R.string.not_found_group));
        currentFound.add(landmark);
        currentNotFound.remove(landmark);
        currentLandmarks.put(getString(R.string.found_group), currentFound);
        currentLandmarks.put(getString(R.string.not_found_group), currentNotFound);
        LoginActivity.setLandmarkItemList(currentLandmarks);
    }

}