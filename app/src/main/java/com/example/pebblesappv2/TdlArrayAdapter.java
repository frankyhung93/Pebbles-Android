package com.example.pebblesappv2;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.ArraySwipeAdapter;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by ChunFaiHung on 2017/2/12.
 */

public class TdlArrayAdapter extends BaseSwipeAdapter {

    private final Context context;
    private ArrayList<ToDoItem> tdl_data = new ArrayList<ToDoItem>();
    public PebblesTDLSource tdl_source;

    // TUTORIAL / GUIDE:
    // The three parameters for the custom array adapter must be present
    // So as to provide a complete set of parameters for the super constructor
    // No warnings/errors will occur if you for example do this: super(context);
    public TdlArrayAdapter(Context context, ArrayList<ToDoItem> tdl_data) {
        this.context = context;
        this.tdl_data = tdl_data;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.inflate_td_swipe_layout, null);
    }

    @Override
    public void fillValues(final int position, View rowView) {
        SwipeLayout swipeLayout = (SwipeLayout)rowView.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                Log.d("DEBUG", "Position Swiped Open: "+position);
            }
        });
        swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                Log.d("DEBUG", "Position Double Clicked: "+position);
            }
        });
        swipeLayout.findViewById(R.id.td_item_del_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context, "click delete", Toast.LENGTH_SHORT).show();
                tdl_source = new PebblesTDLSource(context);
                tdl_source.open();
                tdl_source.DeleteTDItem(tdl_data.get(position).getId());
                tdl_data.remove(position);

                // Update the list view
                closeAllItems();
                notifyDataSetChanged();
            }
        });

        ToDoItem this_row;
        this_row = tdl_data.get(position);

        TextView tdl_dateView = (TextView) swipeLayout.findViewById(R.id.td_date);
        TextView tdl_timeView = (TextView) swipeLayout.findViewById(R.id.td_time);
        TextView tdl_descView = (TextView) swipeLayout.findViewById(R.id.td_desc);
        tdl_dateView.setText(this_row.getToDoDate());
        tdl_timeView.setText(this_row.getToDoTime());
        tdl_descView.setText(this_row.getToDoDesc());
    }
    @Override
    public void notifyDataSetChanged() {
        Collections.sort(tdl_data, new Comparator<ToDoItem>() {
            public int compare(ToDoItem o1, ToDoItem o2) {
                if (o1.getDateFromString() == null || o2.getDateFromString() == null)
                    return 0;
                return o1.getDateFromString().compareTo(o2.getDateFromString());
            }
        });
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return tdl_data.size();
    }

    @Override
    public Object getItem(int position) {
        return tdl_data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public boolean removeID(long targetID) {
        if (targetID == 1000000) return false;
        for (int i = 0; i < tdl_data.size(); i++) {
            if (tdl_data.get(i).getId() == targetID) {
                tdl_data.remove(i);
                return true;
            }
        }
        return false;
    }

}
