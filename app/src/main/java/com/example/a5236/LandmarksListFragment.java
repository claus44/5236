package com.example.a5236;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LandmarksListFragment extends Fragment {

    private static final String TAG = "LandmarksListFragment";
    Context mContext;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> landmarkGroupList;
    HashMap<String, List<Landmark>> landmarkItemList;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_landmarks_list, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        expListView = (ExpandableListView) view.findViewById(R.id.landmarksExpList);
        // method to replace later with Firebase linked data
        prepareLandmarkData();

        final LandmarkActivity mContext = (LandmarkActivity) getActivity();
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
                Log.d(TAG, "CHILD CLICK LISTENER TODO");
                return false;
            }
        });
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