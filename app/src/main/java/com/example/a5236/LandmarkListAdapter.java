package com.example.a5236;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;

public class LandmarkListAdapter extends BaseExpandableListAdapter {

    private static final String TAG = "LandmarkListAdapter";
    private Context mContext;
    private List<String> mLandmarkGroupTitleList;
    private HashMap<String, List<Landmark>> mLandmarkListGroupedItems;

    public LandmarkListAdapter(Context mContext, List<String> mLandmarkGroupTitleList, HashMap<String, List<Landmark>> mLandmarkListGroupedItems) {
        this.mContext = mContext;
        this.mLandmarkGroupTitleList = mLandmarkGroupTitleList;
        this.mLandmarkListGroupedItems = mLandmarkListGroupedItems;
    }

    @Override
    public int getGroupCount() {
        return this.mLandmarkGroupTitleList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.mLandmarkListGroupedItems.get(this.mLandmarkGroupTitleList.get(groupPosition)).size();
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
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final Landmark landmarkItem = (Landmark) getChild(groupPosition,childPosition);
        if(convertView == null){

            LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.landmark_list_item,  parent,false);
        }
        //Change later with Firebase linked
        TextView tvLandmarkItemTitle = (TextView) convertView.findViewById(R.id.landmarkListItemTitle);
        tvLandmarkItemTitle.setText(landmarkItem.getTitle());
        TextView tvLandmarkItemDesc = (TextView) convertView.findViewById(R.id.landmarkListItemDescription);
        tvLandmarkItemDesc.setText(landmarkItem.getDescription());
        ImageView ivLandmarkItemImage = (ImageView) convertView.findViewById(R.id.landmarkItemImage);

        //Change later with Firebase linked
        Glide.with(mContext).load("https://icons-for-free.com/iconfiles/png/512/file-131964752888364301.png").into(ivLandmarkItemImage);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
