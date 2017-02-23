package com.example.pebblesappv2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ChunFaiHung on 2017/2/23.
 */

public class RoutinesGridAdapter extends BaseAdapter {
    private Context mContext;

    public RoutinesGridAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mIcons.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemGrid;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            itemGrid = LayoutInflater.from(mContext).inflate(R.layout.inflate_routines_griditem, parent, false);
//            itemGrid.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, 90dp));
            ImageView routine_iconView = (ImageView) itemGrid.findViewById(R.id.routine_icon);
            TextView routine_titleView = (TextView) itemGrid.findViewById(R.id.routine_title);
            routine_titleView.setText(mTitles[position]);
            routine_iconView.setImageResource(mIcons[position]);
        } else {
            itemGrid = convertView;
        }
        return itemGrid;
    }

    // references to our images
    private Integer[] mIcons = {
            R.drawable.tool, R.drawable.chat,
            R.drawable.chef, R.drawable.summer,
            R.drawable.weight, R.drawable.keyboard,
            R.drawable.acoustic_guitar, R.drawable.camping,
            R.drawable.bicycle, R.drawable.learning
    };
    private String[] mTitles = {
            "Tool", "Chat",
            "Chef", "Summer",
            "Weight", "Keyboard",
            "Acoustic guitar", "Camping",
            "Bicycle", "Learning"
    };

}
