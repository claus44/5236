package com.example.a5236;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LandmarksListFragment extends Fragment {

    private static final String TAG = "LandmarksListFragment";
    private static final String notFound = "Not Found";
    private static final String Found = "Found";
    Context mContext;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> landmarkGroupList;
    HashMap<String, List<Landmark>> landmarkItemList;

    private DatabaseReference mDatabase;

    private static final int Image_Capture_Code = 1;
    private final int PERMISSION_REQUEST_CODE_CAMERA = 2;
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
        LoginActivity.myMenu.findItem(R.id.option_add_friend).setVisible(true);
        LoginActivity.myMenu.findItem(R.id.option_remove_friend).setVisible(true);
        LoginActivity.myMenu.findItem(R.id.option_delete_account).setVisible(true);
        LoginActivity.myMenu.findItem(R.id.option_update_password).setVisible(true);

        prepareLandmarkInfo();

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
        
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(LandmarksListFragment.this)
                        .navigate(R.id.action_landmarksListFragment_to_profileFragment);

            }
        });
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE_CAMERA );
//                Uri uri = FileProvider.getUriForFile(getActivity(), "com.example.a5236.fileprovider", LoginActivity.getPhoto());
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//                startActivityForResult(intent,Image_Capture_Code);
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_CAMERA:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Uri uri = FileProvider.getUriForFile(getActivity(), "com.example.a5236.fileprovider", LoginActivity.getPhoto());
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(intent,Image_Capture_Code);
                }else {
                    Toast.makeText(getActivity(), "Need Camera Access", Toast.LENGTH_SHORT).show();
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Capture_Code) {
            if (resultCode == Activity.RESULT_OK) {
               Uri uri = FileProvider.getUriForFile(getActivity(), "com.example.a5236.fileprovider", LoginActivity.getPhoto());
               NavHostFragment.findNavController(LandmarksListFragment.this)
                        .navigate(R.id.action_landmarksListFragment_to_addLandmarkFragment);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void prepareLandmarkInfo(){
        landmarkGroupList = new ArrayList<String>();
        landmarkGroupList.add("Not Found");
        landmarkGroupList.add("Found");
        landmarkItemList = LoginActivity.getLandmarkItemList();
    }

}