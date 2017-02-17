package com.example.pebblesappv2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ChunFaiHung on 2017/2/12.
 */

public class TdlArrayAdapter extends ArrayAdapter {

    private final Context context;
    private int inflate_res_id = 0;
    private ArrayList<ToDoItem> tdl_data = new ArrayList<ToDoItem>();

    // TUTORIAL / GUIDE:
    // The three parameters for the custom array adapter must be present
    // So as to provide a complete set of parameters for the super constructor
    // No warnings/errors will occur if you for example do this: super(context);
    public TdlArrayAdapter(Context context, int inflate_res_id, ArrayList<ToDoItem> tdl_data) {
        super(context, inflate_res_id, tdl_data);
        this.context = context;
        this.inflate_res_id = inflate_res_id;
        this.tdl_data = tdl_data;
    }

    // TUTORIAL / GUIDE:
    // The adapter needs to create a layout for each row of the list.
    // The ListView instance calls the getView() method on the adapter for each data element.
    // In this method the adapter creates the row layout and maps the data to the views in the layout.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(inflate_res_id, parent, false);

        ToDoItem this_row;
        this_row = tdl_data.get(position);

        TextView tdl_dateView = (TextView) rowView.findViewById(R.id.td_date);
        TextView tdl_timeView = (TextView) rowView.findViewById(R.id.td_time);
        TextView tdl_descView = (TextView) rowView.findViewById(R.id.td_desc);
        tdl_dateView.setText(this_row.getToDoDate());
        tdl_timeView.setText(this_row.getToDoTime());
        tdl_descView.setText(this_row.getToDoDesc());

        return rowView;
    }
}
