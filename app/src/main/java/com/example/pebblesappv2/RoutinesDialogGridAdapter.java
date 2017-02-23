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

import java.lang.reflect.Field;


/**
 * Created by ChunFaiHung on 2017/2/23.
 */

public class RoutinesDialogGridAdapter extends BaseAdapter {
    private Context mContext;
    private Integer[] iconIdList;
    private Integer[] mIcons = {
            R.drawable.tool, R.drawable.chat,
            R.drawable.chef, R.drawable.summer,
            R.drawable.weight, R.drawable.keyboard,
            R.drawable.acoustic_guitar, R.drawable.camping,
            R.drawable.bicycle, R.drawable.learning
    };

    public RoutinesDialogGridAdapter(Context c) {
        iconIdList = fetchIconIdList();
        mContext = c;
    }

    public int getCount() {
        return mIcons.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return mIcons[position];
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemGrid;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            itemGrid = LayoutInflater.from(mContext).inflate(R.layout.inflate_routines_dialog_griditem, parent, false);
            ImageView routine_iconView = (ImageView) itemGrid.findViewById(R.id.routine_dialog_icon);
            routine_iconView.setImageResource(mIcons[position]);
        } else {
            itemGrid = convertView;
        }
        return itemGrid;
    }

    private Integer[] fetchIconIdList() {
        final R.drawable drawableResources = new R.drawable();
        final Class<R.drawable> c = R.drawable.class;
        final Field[] fields = c.getDeclaredFields();
        Integer[] tmpList = {};

        for (int i = 0, max = fields.length; i < max; i++) {
            final int resourceId;
            try {
                resourceId = fields[i].getInt(drawableResources);
                tmpList[i] = resourceId;
            } catch (Exception e) {
                continue;
            }
        }
        return tmpList;
    }

}
