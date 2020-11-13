package com.example.a5236;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import android.content.Intent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.widget.TextView;
import android.view.View;

public class ProfileActivity extends AppCompatActivity {

    private TextView scoreTextView, userNameTextView;

    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    private static final String USERS = "Accounts";
    private String username;


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //get username information from login activity
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        //end

        scoreTextView = findViewById(R.id.score_textview);
        userNameTextView = findViewById(R.id.username_textview);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = rootRef.child(USERS);
        Log.v("USERID", userRef.getKey());

//        database = FirebaseDatabase.getInstance();
//        userRef = database.getReference(USERS);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.child("username").getValue().equals(username)) {
                        userNameTextView.setText(ds.child("username").getValue(String.class));
                        scoreTextView.setText(ds.child("score").getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        })
    }
}
