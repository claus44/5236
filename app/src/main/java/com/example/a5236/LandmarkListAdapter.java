package com.example.a5236;

import android.content.Context;
import android.graphics.Typeface;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LandmarkListAdapter extends BaseExpandableListAdapter {

    private static final String TAG = "LandmarkListAdapter";
    private Context mContext;
    private List<String> mLandmarkGroupTitleList;
    private HashMap<String, List<Landmark>> mLandmarkListGroupedItems;
    private StorageReference mStorageRef;
    private HashMap<String, Pair<Integer,Integer>> childrenCountsForGroups = new HashMap<>();
    private int endOfChildren;

    public LandmarkListAdapter(Context mContext, List<String> mLandmarkGroupTitleList, HashMap<String, List<Landmark>> mLandmarkListGroupedItems) {
        this.mContext = mContext;
        this.mLandmarkGroupTitleList = mLandmarkGroupTitleList;
        this.mLandmarkListGroupedItems = mLandmarkListGroupedItems;
        for(Map.Entry pair: mLandmarkListGroupedItems.entrySet()){
            int maxChildren = ((List<Landmark>) pair.getValue()).size();
            if(maxChildren < 5){
                childrenCountsForGroups.put(((String) pair.getKey()),new Pair(maxChildren,maxChildren));
            }else{
                childrenCountsForGroups.put(((String) pair.getKey()),new Pair(5,maxChildren));
            }
        }
    }

    @Override
    public int getGroupCount() {
        return this.mLandmarkGroupTitleList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        String groupKey = mLandmarkGroupTitleList.get(groupPosition);
        Pair count = (Pair) childrenCountsForGroups.get(groupKey);
        return (Integer)count.first;
//        return this.mLandmarkListGroupedItems.get(this.mLandmarkGroupTitleList.get(groupPosition)).size();
    }
    public void addToChildrenCount(int groupPosition){
        String groupKey = mLandmarkGroupTitleList.get(groupPosition);
        Pair count = (Pair) childrenCountsForGroups.get(groupKey);
        int childrenLeft = (Integer) count.second - (Integer)count.first;
        if(childrenLeft <= 5 ){
            endOfChildren = 1;
            Pair newCount = new Pair((Integer) count.second,(Integer) count.second);
            childrenCountsForGroups.put(groupKey, newCount);
        }else{
            endOfChildren = 0;
            Pair newCount = new Pair((Integer) count.first + 5,(Integer) count.second);
            childrenCountsForGroups.put(groupKey, newCount);
        }
    }
    public boolean endOfChildren(int groupPosition){
        String groupKey = mLandmarkGroupTitleList.get(groupPosition);
        Pair count = (Pair) childrenCountsForGroups.get(groupKey);
        Boolean check = (Integer) count.second == (Integer)count.first;
        return check;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mLandmarkGroupTitleList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.mLandmarkListGroupedItems.get(mLandmarkGroupTitleList.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String landmarkGroupTitle = (String) getGroup(groupPosition);
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.landmark_list_group, parent,false);
        }
        TextView vwLandmarkGroup = (TextView) convertView.findViewById(R.id.landmarkGroupTitle);
        vwLandmarkGroup.setTypeface(null, Typeface.BOLD);
        vwLandmarkGroup.setText(landmarkGroupTitle);
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.landmark_list_item,  parent,false);
        }
        if(childPosition < this.mLandmarkListGroupedItems.get(mLandmarkGroupTitleList.get(groupPosition)).size()){
            final Landmark landmarkItem = (Landmark) getChild(groupPosition,childPosition);
            TextView tvLandmarkItemTitle = (TextView) convertView.findViewById(R.id.landmarkListItemTitle);
            tvLandmarkItemTitle.setText(landmarkItem.getTitle());
            TextView tvLandmarkItemDesc = (TextView) convertView.findViewById(R.id.landmarkListItemDescription);
            if(landmarkItem.getDescription().length() > 60){
                tvLandmarkItemDesc.setText(landmarkItem.getDescription().substring(0,60) + "...");
            }else{
                tvLandmarkItemDesc.setText(landmarkItem.getDescription());
            }
            ImageView ivLandmarkItemImage = (ImageView) convertView.findViewById(R.id.landmarkItemImage);
            if(LoginActivity.isConnectedToInternet(mContext)){
                mStorageRef = FirebaseStorage.getInstance().getReference().child(landmarkItem.getImage());
                GlideApp.with(mContext).load(mStorageRef).into(ivLandmarkItemImage);
            }else{
                Toast.makeText(mContext,  "No Internet", Toast.LENGTH_SHORT).show();
            }
            Button loadMoreBtn = (Button) convertView.findViewById(R.id.load_more_button);
            loadMoreBtn.setVisibility(View.GONE);
            if(isLastChild ){
                if(!endOfChildren(groupPosition)){
                    loadMoreBtn.setVisibility(View.VISIBLE);
                    loadMoreBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addToChildrenCount(groupPosition);
                            notifyDataSetChanged();
                        }
                    });
                }
            }
        }else{
            TextView tvLandmarkItemTitle = (TextView) convertView.findViewById(R.id.landmarkListItemTitle);
            tvLandmarkItemTitle.setVisibility(View.GONE);
            TextView tvLandmarkItemDesc = (TextView) convertView.findViewById(R.id.landmarkListItemDescription);
            tvLandmarkItemDesc.setVisibility(View.GONE);
            ImageView ivLandmarkItemImage = (ImageView) convertView.findViewById(R.id.landmarkItemImage);
            ivLandmarkItemImage.setVisibility(View.GONE);
            Button loadMoreBtn = (Button) convertView.findViewById(R.id.load_more_button);
            loadMoreBtn.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
