package com.example.a5236;

import android.graphics.Bitmap;
import android.os.Bundle;

import com.example.a5236.data.model.LoggedInUser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    public static Landmark currentLandmark;
    public static Bitmap currentBitmap;
    public static LoggedInUser loggedInUser;

    public static void setLoggedInUser(LoggedInUser user){ loggedInUser = user;}
    public static LoggedInUser getLoggedInUser() { return loggedInUser;}

    public static void setCurrentBitmap(Bitmap bitmap){ currentBitmap = bitmap;}
    public static Bitmap getCurrentBitmap() { return currentBitmap;}

    public static void setCurrentLandmark(Landmark landmark){
        currentLandmark = landmark;
    }
    public static Landmark getCurrentLandmark() {
        return currentLandmark;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");

    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG, "onPause()");
    }
    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "onStart()");
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d(TAG, "onStop()");
    }

    @Override
    public void onDestroy(){
        Log.d(TAG, "onDestroy()");
        super.onDestroy();

    }
}