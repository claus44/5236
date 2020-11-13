package com.example.a5236;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.example.a5236.data.model.LoggedInUser;
import com.google.firebase.database.DataSnapshot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    public static Landmark currentLandmark;
    public static Bitmap currentBitmap;
    public static LoggedInUser loggedInUser;
    public static HashMap<String, List<Landmark>> landmarkItemList;
    public static File photo;
    private String response;
    public static Menu myMenu;


    public static File getPhoto() {
        return photo;
    }

    public static void setPhoto(File photo) {
        LoginActivity.photo = photo;
    }

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

    public static void setLandmarkItemList(HashMap<String, List<Landmark>> list){
        landmarkItemList = list;
    }
    public static HashMap<String, List<Landmark>> getLandmarkItemList(){return landmarkItemList;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setPhoto(getPhotoFile("landmark"));

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

    public static void retrieveLandmarkData(DataSnapshot dataSnapshot){
        HashMap<String, List<Landmark>> landmarkList = new HashMap<String, List<Landmark>>();
        //initialize group names

        List<Landmark> notFound = new ArrayList<Landmark>();
        List<Landmark> found = new ArrayList<Landmark>();

        DataSnapshot landmarks = dataSnapshot.child("Landmarks");
        HashMap<String, Object> hml = (HashMap<String, Object>) landmarks.getValue();
        for(Map.Entry<String, Object> entry : hml.entrySet()){
            HashMap<String, Object> landmarkObject = (HashMap<String, Object>) entry.getValue();
            ArrayList<String> foundByUsers =  (ArrayList<String>) landmarkObject.get("foundByUsers");
            // create landmark
            Landmark landmark = new Landmark(landmarkObject.get("title").toString(),
                    ((Long)landmarkObject.get("difficulty")).intValue(), landmarkObject.get("coordinates").toString(),
                    landmarkObject.get("image").toString(), landmarkObject.get("description").toString(),
                    landmarkObject.get("hint").toString(), landmarkObject.get("createdBy").toString(),
                    foundByUsers);
            if(foundByUsers.contains(LoginActivity.getLoggedInUser().getUserId())){
                found.add(landmark);
            }else{
                notFound.add(landmark);
            }
        }

        landmarkList.put("Not Found", notFound);
        landmarkList.put("Found", found);
        setLandmarkItemList(landmarkList);
    }


    private String getPhotoFilename(String username){
        return "IMG_" + username + ".jpg";
    }
    private File getPhotoFile(String username){
        File filesDir = getFilesDir();
        File file = new File(filesDir, getPhotoFilename(username));
        return file;
    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.mymenu, menu);
        myMenu = menu;
        myMenu.findItem(R.id.option_add_friend).setVisible(false);
        myMenu.findItem(R.id.option_remove_friend).setVisible(false);
        myMenu.findItem(R.id.option_delete_account).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.option_add_friend) {
            showAddItemDialog("Add Friend");
        }
        else if (id == R.id.option_remove_friend) {
            showAddItemDialog("Remove Friend");
        }
        else if (id == R.id.option_delete_account) {
            showAddItemDialog("Are You sure? Enter your Username to confirm.");
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAddItemDialog(String title) {
        final EditText taskEditText = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage("Enter desired Username")
                .setView(taskEditText)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        response = String.valueOf(taskEditText.getText());
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }
}