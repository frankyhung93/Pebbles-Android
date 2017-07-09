package com.example.pebblesappv2;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

class WklyDialogGridAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<RoutineItem> rt_data;
    private ArrayList<Integer> recordIdList;

    WklyDialogGridAdapter(Context c) {
        PebblesTDLSource rt_source = new PebblesTDLSource(c);
        rt_source.open();
        rt_data = rt_source.getRoutines();
        mContext = c;
        recordIdList = new ArrayList<>();
    }

    void setRecordIdList(ArrayList<Integer> newIdList) {
        this.recordIdList = newIdList;
    }

    boolean inRecordIdList(Integer testId) {
        return this.recordIdList.contains(testId);
    }

    void addInRecordIdList(Integer addId) {
        this.recordIdList.add(addId);
    }

    void remInRecordIdList(Integer remId) {
        this.recordIdList.remove(this.recordIdList.indexOf(remId));
    }

    ArrayList<Integer> getRecordIdList() {
        return this.recordIdList;
    }

    public int getCount() {
        return rt_data.size();
    }

    public Object getItem(int position) {
        return rt_data.get(position);
    }

    public long getItemId(int position) {
        return rt_data.get(position).getRtId();
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemGrid;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            itemGrid = LayoutInflater.from(mContext).inflate(R.layout.inflate_wkly_routines_dialog_griditem, parent, false);
        } else {
            itemGrid = convertView;
        }
        ImageView routine_iconView = (ImageView) itemGrid.findViewById(R.id.wkly_dialog_routine_icon);
        TextView routine_textView = (TextView) itemGrid.findViewById(R.id.wkly_dialog_routine_title);
        View routine_wrapView = itemGrid.findViewById(R.id.wkly_dialog_item_wrap);
        routine_iconView.setImageResource((int)rt_data.get(position).getRtIconId());
        routine_textView.setText(rt_data.get(position).getRtIconName());
        routine_textView.setTextColor((int)rt_data.get(position).getRtTxColor());
        // Setting the background (With Border as Selected) of
        if (recordIdList.contains((int)rt_data.get(position).getRtId())) {
            Log.d("DEBUG", "BINGO within getView" + rt_data.get(position).getRtId());
            // use a GradientDrawable with only one color set, to make it a solid color
            GradientDrawable border = new GradientDrawable();
            border.setColor((int)rt_data.get(position).getRtBgColor()); // Original BG color
            border.setStroke(10, 0xFF000000); //black border with full opacity
            routine_wrapView.setBackground(border);
        } else {
            Log.d("DEBUG", "getting-rt_data within getView: " + rt_data.get(position).getRtId());
            routine_wrapView.setBackgroundColor((int) rt_data.get(position).getRtBgColor());
        }
        return itemGrid;
    }
}
