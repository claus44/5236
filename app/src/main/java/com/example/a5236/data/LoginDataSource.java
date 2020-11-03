package com.example.a5236.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.a5236.Account;
import com.example.a5236.data.model.LoggedInUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    private LoggedInUser user;
    private boolean creation;
    private static final String TAG = "LoginDataSource";




    public Result<LoggedInUser> login(String username, String password) {
        try {
            mDatabase = FirebaseDatabase.getInstance().getReference();

            mDatabase.child("Accounts").push();
            DatabaseReference ref = mDatabase.child("Accounts").push();

            Account account = new Account("abc", "123456", 0);
            ref.setValue(account);

            // TODO: handle loggedInUser authentication
            LoggedInUser fakeUser =
                    new LoggedInUser(username);
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public  Result<LoggedInUser> register(final String username, final String password) {
        // TODO: handle adding user - mostly works

        mDatabase = FirebaseDatabase.getInstance().getReference();
        try {
            user = new LoggedInUser(username);
            //registerFirebase(username, password);
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    checkUsernameExists(snapshot, username, password);
                    Log.d(TAG, "#########after check##########");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            Thread.sleep(1000);
//TODO: find a better solution/make it work
            if (creation) {Log.d(TAG, "#########after listener##########");
                return new Result.Success<>(user);
            } else {Log.d(TAG, "#########after exists##########");
                Exception e = null;
                return new Result.Error(new IOException("Account already exists", e));
            }
        } catch (Exception e) {Log.d(TAG, "#########after exception##########");
            return new Result.Error(new IOException("Error Signing up", e));
        }
    }

    private void registerFirebase(final String username, final String password) {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                checkUsernameExists(snapshot, username, password);
                Log.d(TAG, "#########after check##########");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//    public Result<LoggedInUser> deleteUser(String Username, String password) {
//        //TODO: Authenticate username, password, and delete
//    }

    public void logout() {
        // TODO: revoke authentication
    }

    private void checkUsernameExists(DataSnapshot dataSnapshot, String username, String password) {
        boolean userExists = false;
        for (DataSnapshot ds : dataSnapshot.getChildren()){
            if (ds.getKey().equals("Accounts")){
                HashMap<String, Object> hm = (HashMap<String, Object>) ds.getValue();
                userExists = hm.containsKey(username);
            }
        }
        Log.d(TAG, String.valueOf(userExists));
        if (!userExists) {

            Account account = new Account(username, password, 0);
            mDatabase.child("Accounts").child(account.getUsername()).setValue(account);

        }
        creation = !userExists;
    }

    public void updatePassword(String username, String password) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Account account = new Account(username, password, 0);
        mDatabase.child("Accounts").child(username).setValue(account);
    }

    public void deleteUser(String username, String password) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Accounts").child(username).removeValue();
    }
}