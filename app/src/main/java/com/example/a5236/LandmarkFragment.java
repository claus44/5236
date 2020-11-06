package com.example.a5236;

import android.content.Context;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.service.autofill.Dataset;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a5236.ui.login.LoginFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

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

    private static Landmark landmark = LandmarkActivity.getCurrentLandmark();
    //final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


    public LandmarkFragment() {
        // Required empty public constructor
    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LandmarkFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LandmarkFragment newInstance(String param1, String param2) {
        LandmarkFragment fragment = new LandmarkFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


//        final Button foundButton = getView().findViewById(R.id.landmark_found);
//        final Button hintButton = getView().findViewById(R.id.landmark_hint);
//        final ImageView landmarkImg = getView().findViewById(R.id.landmark_img);
//        final TextView landmarkTitle = getView().findViewById(R.id.landmark_title);
//        final TextView landmarkDescription = getView().findViewById(R.id.landmark_description);
//
//        //landmarkImg.setImageBitmap((Uri) LandmarkActivity.currentLandmark.getImage());
//        // TODO: figure out how to handle images
//        landmarkTitle.setText(landmark.getTitle());
//        landmarkDescription.setText(landmark.getDescription());

//        foundButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//        hintButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_landmark, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        final Button foundButton = getView().findViewById(R.id.landmark_found);
        final Button hintButton = getView().findViewById(R.id.landmark_hint);
        final ImageView landmarkImg = getView().findViewById(R.id.landmark_img);
        final TextView landmarkTitle = getView().findViewById(R.id.landmark_title);
        final TextView landmarkDescription = getView().findViewById(R.id.landmark_description);

        //landmarkImg.setImageBitmap((Uri) LandmarkActivity.currentLandmark.getImage());
        // TODO: figure out how to handle images
        landmarkTitle.setText(landmark.getTitle());
        landmarkDescription.setText(landmark.getDescription());

    }


}