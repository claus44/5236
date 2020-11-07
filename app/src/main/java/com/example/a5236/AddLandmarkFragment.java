package com.example.a5236;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

import com.bumptech.glide.Glide;
import com.example.a5236.ui.login.SignUpFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class AddLandmarkFragment extends Fragment {
    private static final String TAG = "AddLandmarkFragment";
    private static final int Image_Capture_Code = 1;
    private DatabaseReference mDatabase;
    ImageView mImageView;
    EditText titleEditText,descriptionEditText, hintEditText, difficultyEditText;
    Button createLandmarkBtn, retryBtn, backBtn;
    ProgressBar loadingProgressBar;
    LoginActivity mContext;

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
                mDatabase = FirebaseDatabase.getInstance().getReference();
                // add get GPS coordinates
//                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        boolean landmarkCreated = tryCreateLandmark(snapshot, title, desc, hint);
//                        if(!landmarkCreated){
//
//                            NavHostFragment.findNavController(AddLandmarkFragment.this)
//                                    .navigate(R.id.action_addLandmarkFragment_to_landmarksListFragment);
//                        }else{
//                            loadingProgressBar.setVisibility(View.GONE);
//                        }
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) { }
//                });



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

    public boolean tryCreateLandmark(DataSnapshot dataSnapshot, String title, String desc, String hint){
        boolean landmarkExists = false;
        for (DataSnapshot ds : dataSnapshot.getChildren()){
            if (ds.getKey().equals("Landmarks")){
                HashMap<String, Object> hm = (HashMap<String, Object>) ds.getValue();
                landmarkExists = hm.containsKey(title);
            }
        }
        if (!landmarkExists) {
//            Landmark landmark = new Landmark()
//            mDatabase.child("Landmarks").child(account.getUsername()).setValue(account);
        }
        return landmarkExists;
    }

}