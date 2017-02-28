package com.example.pebblesappv2;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by ChunFaiHung on 2017/2/26.
 */

public class WeeklyRtAdapter extends ArrayAdapter<String> {
    private final Context mContext;
    private String[] daysList;
    private PebblesTDLSource rt_source;

    public WeeklyRtAdapter(Context context, String[] values) {
        super(context, -1, values);
        rt_source = new PebblesTDLSource(context);
        rt_source.open();
        this.daysList = values;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(100, 100);
        View rowView = inflater.inflate(R.layout.inflate_weekly_day, parent, false);
        TextView dayLabel = (TextView) rowView.findViewById(R.id.weekday_label);
        LinearLayout weekdayll = (LinearLayout) rowView.findViewById(R.id.linearLayout_weekday);
        dayLabel.setText(daysList[position]);

        // Fetch data - iconId from SharedPreferences for each day
        SharedPreferences wkdayPref = mContext.getSharedPreferences(mContext.getString(R.string.WeekPreferenceKey), Context.MODE_PRIVATE);
        String iconIdList = wkdayPref.getString(daysList[position], "nil");

        if (iconIdList != "nil" && iconIdList != "") {
            String[] iconStrIdList = iconIdList.split(",");
            for (int strlistCounter = 0; strlistCounter < iconStrIdList.length; strlistCounter++) {
                Integer recordId = Integer.parseInt(iconStrIdList[strlistCounter]);
                Integer resId = rt_source.getResIdFromRoutineId(recordId);
                ImageView imIcon = new ImageView(mContext);
                imIcon.setImageResource(resId);
                weekdayll.addView(imIcon,layoutParams);
            }
        } else {
            Log.d("DEBUG", "GET NO SharedPref FROM "+daysList[position]);
        }

        return rowView;
    }

}
