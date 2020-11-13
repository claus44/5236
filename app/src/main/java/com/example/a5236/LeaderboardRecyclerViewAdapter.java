package com.example.a5236;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class LeaderboardRecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder> {

    private static final String TAG = "LeaderboardRecyclerView";
    private ArrayList<String> mUsernames;
    private ArrayList<Integer> mScores;
    private Context mContext;


    public LeaderboardRecyclerViewAdapter(Context mContext, ArrayList<String> mUsernames, ArrayList<Integer> mScores ) {
        this.mUsernames = mUsernames;
        this.mScores = mScores;
        this.mContext = mContext;
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
        holder.usernameandscore.setText(mUsernames.get(position) + ":" + mScores.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return mUsernames.size();
    }
}

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView usernameandscore;
        LinearLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameandscore = itemView.findViewById(R.id.username_and_score);
            parentLayout = itemView.findViewById(R.id.parent_layout);
    }
}
