package com.example.pebblesappv2;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

class WeeklyRtAdapter extends ArrayAdapter<String> {
    private final Context mContext;
    private String[] daysList;
    private PebblesTDLSource rt_source;

    WeeklyRtAdapter(Context context, String[] values) {
        super(context, -1, values);
        rt_source = new PebblesTDLSource(context);
        rt_source.open();
        this.daysList = values;
        this.mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(100, 100);
        View rowView = inflater.inflate(R.layout.inflate_weekly_day, parent, false);
        TextView dayLabel = (TextView) rowView.findViewById(R.id.weekday_label);
        LinearLayout weekdayll = (LinearLayout) rowView.findViewById(R.id.linearLayout_weekday);
        dayLabel.setText(daysList[position]);

        // Fetch data - iconId from SharedPreferences for each day
        SharedPreferences wkdayPref = mContext.getSharedPreferences(mContext.getString(R.string.WeekPreferenceKey), Context.MODE_PRIVATE);
        String iconIdList = wkdayPref.getString(daysList[position], "nil");

        if (!iconIdList.equals("nil") && !iconIdList.equals("")) {
            String[] iconStrIdList = iconIdList.split(",");
            for (String value: iconStrIdList) {
                Integer recordId = Integer.parseInt(value);
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
