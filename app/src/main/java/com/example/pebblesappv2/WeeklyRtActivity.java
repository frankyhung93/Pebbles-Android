package com.example.pebblesappv2;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import java.util.ArrayList;

public class WeeklyRtActivity extends BaseACA{
    public final String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    private WklyDialogGridAdapter wklyDialogGridAdapter;
    private String currentEditingDay = "";

    @SuppressWarnings("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weeklyrt);
        // Setting toolbar interface
        Toolbar toolbar = (Toolbar) findViewById(R.id.general_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Testing add IconIDs to two separate days
//        SharedPreferences wkdayPref = this.getSharedPreferences(this.getString(R.string.WeekPreferenceKey), Context.MODE_PRIVATE);
//        SharedPreferences.Editor prefEditor = wkdayPref.edit();
//        prefEditor.putString("Monday", "2,6");
//        prefEditor.putString("Wednesday", "3");
//        prefEditor.commit();

        // Setting custom adapter
        ListView weekList = (ListView) findViewById(R.id.weeklyrt_listview);
        final ArrayAdapter<String> weekListAdapter = new WeeklyRtAdapter(this, daysOfWeek);
        weekList.setAdapter(weekListAdapter);

        LinearLayout wkly_dialog_ll = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.wkly_dialog_routines_grid, null);
        GridView wkly_dialog_gridv = (GridView) wkly_dialog_ll.findViewById(R.id.wkly_routine_dialog_gridview);
        wklyDialogGridAdapter = new WklyDialogGridAdapter(WeeklyRtActivity.this);
        wkly_dialog_gridv.setAdapter(wklyDialogGridAdapter);
        // Set the Routine Grid Dialog up
        final AlertDialog.Builder builder = new AlertDialog.Builder(WeeklyRtActivity.this);
        // Inflate (LINEAR_LAYOUT) View (containing current ROUTINES) into dialog...
        builder.setView(wkly_dialog_ll);
        builder.setTitle("Choose Routine");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences wkdayPref = WeeklyRtActivity.this.getSharedPreferences(WeeklyRtActivity.this.getString(R.string.WeekPreferenceKey), Context.MODE_PRIVATE);
                        SharedPreferences.Editor prefEditor = wkdayPref.edit();
                        ArrayList<Integer> getIdList = wklyDialogGridAdapter.getRecordIdList();
                        String idList_str = "";
                        for (Integer i = 0; i < getIdList.size(); i++) {
                            idList_str += getIdList.get(i)+",";
                        }
                        if (getIdList.size()>=1) {
                            idList_str = idList_str.substring(0, idList_str.lastIndexOf(','));
                        }
                        Log.d("DEBUG-FREAK", currentEditingDay+" - "+idList_str);
                        prefEditor.putString(currentEditingDay, idList_str);
                        prefEditor.apply();
                        weekListAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        final Dialog routineDialog = builder.create();

        // INFLATED ROUTINES VIEW ON CLICK
        wkly_dialog_gridv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Fetch adapter item original bg color
                RoutineItem rt_item = (RoutineItem) wklyDialogGridAdapter.getItem(i);
                Integer itemBgColor = (int)rt_item.getRtBgColor();
                // Fetch adapter item id
                Integer clickedItemId = (int)wklyDialogGridAdapter.getItemId(i);

                if (wklyDialogGridAdapter.inRecordIdList(clickedItemId)) {
                    view.setBackgroundColor(itemBgColor);
                    wklyDialogGridAdapter.remInRecordIdList(clickedItemId);
                } else {
                    // use a GradientDrawable with only one color set, to make it a solid color
                    GradientDrawable border = new GradientDrawable();
                    border.setColor(itemBgColor); // Original BG color
                    border.setStroke(10, 0xFF000000); //black border with full opacity
                    view.setBackground(border);
                    wklyDialogGridAdapter.addInRecordIdList(clickedItemId);
                }
            }
        });

        // WEEK DAY ON CLICK
        weekList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

        // WEEK DAY ON LOOOOOOONNNG CLICK
        weekList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                currentEditingDay = daysOfWeek[i];
                Log.d("DEBUG", i+" ");
                // Fetch data - iconId from SharedPreferences for each day
                SharedPreferences wkdayPref = WeeklyRtActivity.this.getSharedPreferences(WeeklyRtActivity.this.getString(R.string.WeekPreferenceKey), Context.MODE_PRIVATE);
                String iconIdList = wkdayPref.getString(daysOfWeek[i], "nil");
                ArrayList<Integer> listToPass = new ArrayList<>();
                if (!iconIdList.equals("nil") && !iconIdList.equals("")) {
                    String[] iconStrIdList = iconIdList.split(",");
                    for (String value: iconStrIdList) {
                        listToPass.add(Integer.parseInt(value));
                    }
                }

                wklyDialogGridAdapter.setRecordIdList(listToPass);
                wklyDialogGridAdapter.notifyDataSetChanged();
                routineDialog.show();
                // True event receiver thus no further event propagation up the view hierarchy
                return true;
            }
        });
    }

}
