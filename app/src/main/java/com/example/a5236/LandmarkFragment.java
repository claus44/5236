package com.example.a5236;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.service.autofill.Dataset;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LandmarkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LandmarkFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final int PERMISSION_REQUEST_CODE_LOCATION = 3;
    private static Landmark landmark;
    private StorageReference mStorageRef;
    private Context mContext;
    private String coordinates;
    FusedLocationProviderClient fusedLocationProviderClient;
    private DatabaseReference mDatabase;

    static Button foundButton;
    static Button hintButton;

    //final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


    public LandmarkFragment() {
        // Required empty public constructor
    }


//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment LandmarkFragment.
//     */
//    // TODO: Rename and change types and number of parameters
    public static LandmarkFragment newInstance(String param1, String param2) {
        LandmarkFragment fragment = new LandmarkFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
        final TextView landmarkDescription = getView().findViewById(R.id.landmark_description);
        final TextView landmarkHint = getView().findViewById(R.id.landmark_hint_text);
        //disable found and hint button if user already found landmark
        if (landmark.getFoundByUsers().contains(LoggedInUser.getUserId())) {
            foundButton.setEnabled(false);
            hintButton.setEnabled(false);
            landmarkHint.setText(landmark.getHint());
        }


        landmarkTitle.setText(landmark.getTitle());
        landmarkDescription.setText(landmark.getDescription());
        mStorageRef = FirebaseStorage.getInstance().getReference().child(landmark.getImage());
        GlideApp.with(mContext).load(mStorageRef).into(landmarkImg);

        foundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION} , PERMISSION_REQUEST_CODE_LOCATION );
            }
        });

        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        int score = Integer.parseInt(snapshot.child("Accounts")
                                .child(LoggedInUser.getUserId()).child("score").getValue().toString());
                        mDatabase.child("Accounts").child(LoggedInUser.getUserId())
                                .child("score").setValue(score - 1);

                        landmarkHint.setText(landmark.getHint());
                        hintButton.setEnabled(false);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

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
                    Toast.makeText(getActivity(), "Need Location Access", Toast.LENGTH_SHORT).show();
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }

    @SuppressLint("MissingPermission")
    // only called if permission is granted
    private void checkFoundLandmark() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
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

                        mDatabase = FirebaseDatabase.getInstance().getReference();
                        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                int score = Integer.parseInt(snapshot.child("Accounts")
                                        .child(LoggedInUser.getUserId()).child("score").getValue().toString());
                                mDatabase.child("Accounts").child(LoggedInUser.getUserId())
                                        .child("score").setValue(landmark.getDifficulty() + score);

                                ArrayList<String> foundByUsersAL = (ArrayList<String>)
                                        snapshot.child("Landmarks").child(landmark.getTitle())
                                                .child("foundByUsers").getValue();

                                foundByUsersAL.add(LoggedInUser.getUserId());
                                mDatabase.child("Landmarks").child(landmark.getTitle())
                                        .child("foundByUsers").setValue(foundByUsersAL);

                                Toast.makeText(getActivity(), "Location Found! Score: " +
                                        (score + landmark.getDifficulty()), Toast.LENGTH_SHORT).show();

                                updateLandmarkList(landmark);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });

                        NavHostFragment.findNavController(LandmarkFragment.this)
                                .navigate(R.id.action_landmarkFragment_to_landmarksListFragment);
                    } else {
                        Toast.makeText(getActivity(), "Wrong Location!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    //gets distance between guess and actual location of landmark
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
        List<Landmark> currentFound = currentLandmarks.get("Found");
        List<Landmark> currentNotFound = currentLandmarks.get("Not Found");
        currentFound.add(landmark);
        currentNotFound.remove(landmark);
        currentLandmarks.put("Found", currentFound);
        currentLandmarks.put("Not Found", currentNotFound);
        LoginActivity.setLandmarkItemList(currentLandmarks);
    }

}