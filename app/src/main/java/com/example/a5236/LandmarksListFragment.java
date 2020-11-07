package com.example.a5236;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LandmarksListFragment extends Fragment {

    private static final String TAG = "LandmarksListFragment";
    private static final String notFound = "Not Found";
    private static final String Found = "Found";
    Context mContext;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> landmarkGroupList;
    HashMap<String, List<Landmark>> landmarkItemList;

    private static final int Image_Capture_Code = 1;
    ImageView mImageView;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_landmarks_list, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        final Button profileButton = view.findViewById(R.id.profile);
        final Button cameraButton = view.findViewById(R.id.camera);
        super.onViewCreated(view, savedInstanceState);
        expListView = (ExpandableListView) view.findViewById(R.id.landmarksExpList);
        // method to replace later with Firebase linked data
        prepareLandmarkData();

        final LoginActivity mContext = (LoginActivity) getActivity();
        listAdapter = new LandmarkListAdapter(mContext, landmarkGroupList, landmarkItemList);
        expListView.setAdapter(listAdapter);
        // Click Listeners
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                Log.d(TAG, "GROUP CLICK LISTENER");
                return false;
            }
        });
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                Log.d(TAG, "GROUP EXPAND LISTENER");
            }
        });
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                Log.d(TAG, "GROUP COLLAPSE LISTENER");
            }
        });
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                LoginActivity.setCurrentLandmark((Landmark) listAdapter.getChild(groupPosition, childPosition));

                NavHostFragment.findNavController(LandmarksListFragment.this)
                        .navigate(R.id.action_landmarksListFragment_to_landmarkFragment);
                //TODO: pass landmark clicked on
                Log.d(TAG, "CHILD CLICK LISTENER TODO");
                return false;
            }
        });

        //TODO: Update navigate action to profile page.
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(LandmarksListFragment.this)
                        .navigate(R.id.action_landmarksListFragment_to_landmarkFragment);

            }
        });
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cInt,Image_Capture_Code);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Capture_Code) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                LoginActivity.setCurrentBitmap(bitmap);
                NavHostFragment.findNavController(LandmarksListFragment.this)
                        .navigate(R.id.action_landmarksListFragment_to_addLandmarkFragment);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // method to initialize landmark list
    private void prepareLandmarkData(){
        landmarkGroupList = new ArrayList<String>();
        landmarkItemList = new HashMap<String, List<Landmark>>();

        landmarkGroupList.add("Not Found");
        landmarkGroupList.add("Found");

        Landmark dummy1 = new Landmark("Dummy1", 2,"2","imageUrl", "Here is a small description. it can be longer or shorter", null, null);
        Landmark dummy3 = new Landmark("Dummy3", 2,"2","imageUrl", "Here is a small description. it can be longer or shorter. Here is a small description. it can be longer or shorter", null, null);
        Landmark dummy2 = new Landmark("Dummy2", 2,"2","imageUrl", "Here is a small description", null, null);

        List<Landmark> notFound = new ArrayList<Landmark>();
        notFound.add(dummy1);
        notFound.add(dummy3);
        notFound.add(dummy1);
        notFound.add(dummy1);
        notFound.add(dummy1);
        notFound.add(dummy1);
        notFound.add(dummy1);
        notFound.add(dummy1);
        notFound.add(dummy1);
        notFound.add(dummy3);
        notFound.add(dummy3);
        notFound.add(dummy3);
        notFound.add(dummy3);
        notFound.add(dummy3);
        notFound.add(dummy3);
        notFound.add(dummy3);
        notFound.add(dummy1);
        notFound.add(dummy1);
        notFound.add(dummy1);
        notFound.add(dummy1);
        notFound.add(dummy1);
        notFound.add(dummy1);
        notFound.add(dummy1);
        notFound.add(dummy1);
        notFound.add(dummy1);
        notFound.add(dummy1);

        List<Landmark> found = new ArrayList<Landmark>();
        found.add(dummy2);

        landmarkItemList.put(landmarkGroupList.get(0), notFound);
        landmarkItemList.put(landmarkGroupList.get(1), found);

    }

}