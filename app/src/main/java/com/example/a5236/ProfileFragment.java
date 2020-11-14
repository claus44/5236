package com.example.a5236;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.a5236.data.model.LoggedInUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private TextView scoreTextView, userNameTextView;
    private RecyclerView leaderboardRecyclerView;
    private LoginActivity mContext;
    private String username;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        mContext = (LoginActivity) getActivity();
        username = LoggedInUser.getUserId();

        scoreTextView = view.findViewById(R.id.score_textview);
        userNameTextView = view.findViewById(R.id.username_textview);

        leaderboardRecyclerView = view.findViewById(R.id.recycler_view);
        ArrayList<String> usernames = new ArrayList<>(LoginActivity.getLeaderboard().keySet());
        ArrayList<Integer> scores = new ArrayList<>(LoginActivity.getLeaderboard().values());
        LeaderboardRecyclerViewAdapter adapter = new LeaderboardRecyclerViewAdapter(mContext,usernames, scores);
        leaderboardRecyclerView.setAdapter(adapter);
        leaderboardRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        Switch friendFilter = (Switch) view.findViewById(R.id.friend_filter);
        friendFilter.setChecked(false);
        friendFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                intializeLeaderboard(isChecked);
            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot accounts = snapshot.child("Accounts");
                userNameTextView.setText(username);
                String score = accounts.child(LoggedInUser.getUserId()).child("score").getValue().toString();
                scoreTextView.setText("score: " + score);
                LoginActivity.retrieveLeaderboardData(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void intializeLeaderboard(boolean friendsOnly){
        ArrayList<String> usernames = new ArrayList<>(LoginActivity.getLeaderboard().keySet());
        ArrayList<Integer> scores = new ArrayList<>(LoginActivity.getLeaderboard().values());
        if(friendsOnly){
            //reset usernames and scores
        }
        LeaderboardRecyclerViewAdapter adapter = new LeaderboardRecyclerViewAdapter(mContext,usernames, scores);
        leaderboardRecyclerView.setAdapter(adapter);
        leaderboardRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    }
}