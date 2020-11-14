package com.example.a5236;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.example.a5236.data.model.LoggedInUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    public static Landmark currentLandmark;
    public static Bitmap currentBitmap;
    public static LoggedInUser loggedInUser;
    public static HashMap<String, List<Landmark>> landmarkItemList;
    public static File photo;
    public static HashMap<String, Integer> leaderboard  = new HashMap<>();
    public static ArrayList<String> friends = new ArrayList<>();
    public static Account user;
    private String response;
    public static Menu myMenu;
    private DatabaseReference mDatabase;


    public static Account getUser() {
        return user;
    }

    public static void setUser(Account user) {
        LoginActivity.user = user;
    }

    public static ArrayList<String> getFriends() {
        return friends;
    }

    public static void setFriends(ArrayList<String> friends) {
        LoginActivity.friends = friends;
    }
    public static HashMap<String, Integer> getLeaderboard() {
        return sortByScore(leaderboard);
    }

    public static void setLeaderboard(HashMap<String, Integer> leaderboard) {
        LoginActivity.leaderboard = sortByScore(leaderboard);
    }

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

    public static void retrieveLeaderboardData(DataSnapshot dataSnapshot){
        DataSnapshot accounts = dataSnapshot.child("Accounts");
        HashMap<String, Object> hm = (HashMap<String, Object>) accounts.getValue();
        HashMap<String, Integer> currentLeaderboard = getLeaderboard();
        for (String key: hm.keySet()) {
            HashMap<String, Object> value = (HashMap<String, Object>) hm.get(key);
            int scoreValue = Integer.parseInt(value.get("score").toString());
            currentLeaderboard.put(key, scoreValue);
        }
        setLeaderboard(currentLeaderboard);
    }

    public static void retrieveFriendData(DataSnapshot dataSnapshot, String username){
        DataSnapshot friends = dataSnapshot.child("Friends").child(username);
        ArrayList<String> friendList = (ArrayList<String>) friends.getValue();
        setFriends(friendList);
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
        myMenu.findItem(R.id.option_update_password).setVisible(false);
        myMenu.findItem(R.id.option_logout_account).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        showAddItemDialog(id);
        return super.onOptionsItemSelected(item);
    }

    private void showAddItemDialog(final int id) {
        final EditText taskEditText = new EditText(this);
        String title = "";
        String message = "";
        if (id == R.id.option_add_friend) {
            title = "Add Friend";
            message = "Enter desired Username";
        } else if (id == R.id.option_remove_friend) {
            title = "Remove Friend";
            message = "Enter desired Username";
        } else if (id == R.id.option_delete_account) {
            title = "Delete Account";
            message = "Are you sure? Enter your username to confirm";
        } else if (id == R.id.option_update_password) {
            title = "Update Password";
            message = "Enter your new password";
        }else if(id == R.id.option_logout_account){
            title = "Logout";
            message = "Sure you want to logout?";
        }
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setView(taskEditText)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        response = String.valueOf(taskEditText.getText());

                        if (id == R.id.option_add_friend) {
                            if (!response.equals(LoggedInUser.getUserId())) {
                                mDatabase = FirebaseDatabase.getInstance().getReference();
                                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        addFriend(response, snapshot);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            }

                        } else if (id == R.id.option_remove_friend) {

                            if (!response.equals(LoggedInUser.getUserId())) {
                                mDatabase = FirebaseDatabase.getInstance().getReference();
                                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        removeFriend(response, snapshot);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            }

                        } else if (id == R.id.option_update_password) {

                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    updatePassword(snapshot, response);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });

                        } else if (id == R.id.option_delete_account) {
                            if (response.equals(LoggedInUser.getUserId())) {
                                mDatabase = FirebaseDatabase.getInstance().getReference();
                                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        deleteUser(snapshot, response);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });


                            }
                        }else if (id == R.id.option_logout_account) {
                            if (response.equals(LoggedInUser.getUserId())) {
                                setLoggedInUser(null);
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                            }
                        }

                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();

    }

    private void deleteUser(DataSnapshot dataSnapshot, String username){
        boolean userDeleted = false;
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (ds.getKey().equals("Accounts")) {
                HashMap<String, Object> hm = (HashMap<String, Object>) ds.getValue();
                if (hm.containsKey(username)) {
                    mDatabase.child("Accounts").child(username).removeValue();
                    if(mDatabase.child("Friends").child(username)!=null){
                        mDatabase.child("Friends").child(username).removeValue();
                    }
                    userDeleted = true;
                    Toast.makeText(this, "Successfully Deleted", Toast.LENGTH_SHORT).show();
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            }
        }
        if(!userDeleted){
            Toast.makeText(this, "Failed to Delete", Toast.LENGTH_SHORT).show();
        }
    }


    private void addFriend (String friend, DataSnapshot snapshot){
        if (snapshot.child("Accounts").child(friend).getValue() != null) {
            ArrayList<String> friendsList = (ArrayList<String>) snapshot.child("Friends")
                    .child(LoggedInUser.getUserId()).getValue();
            if(!friendsList.contains(friend)){
                friendsList.add(friend);
                friends.add(friend);
                Toast.makeText(this, "Added "+ friend+ " as a friend", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,  friend+ " is already a friend", Toast.LENGTH_SHORT).show();
            }
            mDatabase.child("Friends").child(LoggedInUser.getUserId()).setValue(friendsList);
        }else{
            Toast.makeText(this,  friend+ " does not exist", Toast.LENGTH_SHORT).show();
        }

    }

    private void removeFriend (String friend, DataSnapshot snapshot){
        if (snapshot.child("Accounts").child(friend).getValue() != null) {
            ArrayList<String> friendsList = (ArrayList<String>) snapshot.child("Friends")
                    .child(LoggedInUser.getUserId()).getValue();
            if(friendsList.contains(friend)){
                friendsList.remove(friend);
                friends.remove(friend);
                Toast.makeText(this, "Removed "+ friend+ " from friends", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, friend+ " is not a friend", Toast.LENGTH_SHORT).show();
            }
            mDatabase.child("Friends").child(LoggedInUser.getUserId()).setValue(friendsList);
        }else{
            Toast.makeText(this,  friend+ " does not exist", Toast.LENGTH_SHORT).show();
        }

    }

    private void updatePassword(DataSnapshot dataSnapshot, String password) {
        if(mDatabase.child("Accounts").child(LoggedInUser.getUserId()) != null){
            mDatabase.child("Accounts").child(LoggedInUser.getUserId()).child("password").setValue(password);
            Toast.makeText(this,  "Successfully updated password ", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,  "Failed to update password", Toast.LENGTH_SHORT).show();
        }
    }


    public static HashMap<String, Integer> sortByScore(HashMap<String, Integer> hm) {
        List<Map.Entry<String, Integer> > list = new LinkedList<Map.Entry<String, Integer> >(hm.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() {
            public int compare(Map.Entry<String, Integer> n1, Map.Entry<String, Integer> n2) {
                return (n2.getValue()).compareTo(n1.getValue());
            }
        });
        HashMap<String, Integer> sorted = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> pair : list) {
            sorted.put(pair.getKey(), pair.getValue());
        }
        return sorted;
    }

}