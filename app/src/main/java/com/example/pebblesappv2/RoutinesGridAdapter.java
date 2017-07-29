package com.example.pebblesappv2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

class RoutinesGridAdapter extends ArrayAdapter<RoutineItem> {
    private Context mContext;
    private ArrayList<RoutineItem> rt_data = new ArrayList<>();

    RoutinesGridAdapter(Context c, ArrayList<RoutineItem> rt_data) {
        super(c, 0, rt_data);
        this.mContext = c;
        this.rt_data = rt_data;
        Log.d("CONSTRUCTOR", getCount()+" ");
    }

    @Override
    public int getCount() {
        return rt_data.size();
    }

    @Override
    public RoutineItem getItem(int position) {
        return rt_data.get(position);
    }

    @Override
    public long getItemId(int position) {
//        return position;
        return rt_data.get(position).getRtId();
    }

    // create a new ImageView for each item referenced by the Adapter
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View itemGrid;
        RoutineItem this_rt_item;
        this_rt_item = rt_data.get(position);

        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            itemGrid = LayoutInflater.from(mContext).inflate(R.layout.inflate_routines_griditem, parent, false);
//            itemGrid.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, 90dp));
        } else {
            itemGrid = convertView;
        }
        Log.d("DEBUG","Position: "+position+", iconid: "+this_rt_item.getRtIconId());
        ImageView routine_iconView = (ImageView) itemGrid.findViewById(R.id.routine_icon);
        TextView routine_titleView = (TextView) itemGrid.findViewById(R.id.routine_title);
        View rt_item_wrap = itemGrid.findViewById(R.id.rt_item_wrap);
        routine_titleView.setText(this_rt_item.getRtIconName());
        routine_titleView.setTextColor((int)this_rt_item.getRtTxColor());
        rt_item_wrap.setBackgroundColor((int)this_rt_item.getRtBgColor());
        routine_iconView.setImageResource((int)this_rt_item.getRtIconId());
        return itemGrid;
    }

}
