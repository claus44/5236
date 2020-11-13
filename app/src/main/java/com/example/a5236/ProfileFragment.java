package com.example.a5236;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.a5236.data.model.LoggedInUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private TextView scoreTextView, userNameTextView;

    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    private static final String USERS = "Accounts";
    private String username;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        //get username from data/model/LoggedInUser
        username = LoggedInUser.getUserId();

        scoreTextView = getView().findViewById(R.id.score_textview);
        userNameTextView = getView().findViewById(R.id.username_textview);
        //leaderboardRecyclerView = getView().findViewById(R.id.leaderboard_recyclerview);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = rootRef.child(USERS);
        Log.v("USERID", userRef.getKey());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //set the profile page's textboxes from
                userNameTextView.setText(username);
                String score = snapshot.child(LoggedInUser.getUserId()).child("score").getValue().toString();
                scoreTextView.setText("score: " + score);


                HashMap<String, Object> hm = (HashMap<String, Object>) snapshot.getValue();
                for (String key: hm.keySet()) {
                    HashMap<String, Object> value = (HashMap<String, Object>) hm.get(key);
                    String scoreValue = value.get("score").toString();
                    HashMap<String, String> x = LoginActivity.getLeaderboard();
                    x.put(key, scoreValue);
                    LoginActivity.setLeaderboard(x);
                }

//                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                    if (ds.getKey().equals("Accounts")) {
//                        HashMap<String, Object> hm = (HashMap<String, Object>) ds.getValue();
//                        if(hm.containsKey(username)){
//                            Account updatedAccount = new Account(username, password,  Integer.parseInt(((HashMap<String, Object>) hm.get(username)).get("score").toString()));
//                            mDatabase.child("Accounts").child(username).setValue(updatedAccount);
//                            updatedPassword = true;
//                        }
//                    }
//                }
//                for (DataSnapshot ds : snapshot.getChildren()) {
//                    if (ds.child("username").getValue().equals(username)) {
//                        userNameTextView.setText(username);
//                        scoreTextView.setText((Integer) ds.child("score").getValue());
//                    }
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}