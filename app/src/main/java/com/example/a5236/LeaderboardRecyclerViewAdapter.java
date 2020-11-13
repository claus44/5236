package com.example.a5236;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class LeaderboardRecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder> {

    private static final String TAG = "LeaderboardRecyclerView";

    private ArrayList<String> mUsernameAndScore = new ArrayList<>();
    private Context mContext;

    public LeaderboardRecyclerViewAdapter(ArrayList<String> usernameAndScore, Context context) {
        mUsernameAndScore = usernameAndScore;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        //TODO set the text to the appropriate username and score
        //holder.usernameAndScore.setText();

        HashMap<String, String> list = LoginActivity.getLeaderboard();

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView usernameAndScore;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameAndScore = itemView.findViewById(R.id.username_and_score);
            parentLayout = itemView.findViewById(R.id.parent_layout);
    }
}
