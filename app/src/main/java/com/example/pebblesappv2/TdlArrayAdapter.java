package com.example.pebblesappv2;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

class TdlArrayAdapter extends BaseSwipeAdapter {

    private final Context context;
    private ArrayList<ToDoItem> tdl_data = new ArrayList<>();
    private PebblesTDLSource tdl_source;
    private String[] dayOfWeekArr = {"Null","Sun", "Mon", "Tue", "Wed", "Thur", "Fri", "Sat"};

    // TUTORIAL / GUIDE:
    // The three parameters for the custom array adapter must be present
    // So as to provide a complete set of parameters for the super constructor
    // No warnings/errors will occur if you for example do this: super(context);
    TdlArrayAdapter(Context context, ArrayList<ToDoItem> tdl_data) {
        this.context = context;
        this.tdl_data = tdl_data;
    }

    @SuppressWarnings("InflateParams")
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
        TextView tdl_dayOfView = (TextView) swipeLayout.findViewById(R.id.td_dayOfWeek);
        TextView tdl_timeView = (TextView) swipeLayout.findViewById(R.id.td_time);
        TextView tdl_descView = (TextView) swipeLayout.findViewById(R.id.td_desc);
        tdl_dateView.setText(this_row.getToDoDate());
        Calendar c = Calendar.getInstance();
        int dayOfWeek;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date targetDate;
        try {
            targetDate = df.parse(this_row.getToDoDate());
        } catch (ParseException e) {
            Log.d("Exception", e.getMessage());
            targetDate = null;
        }
        c.setTime(targetDate);
        if (targetDate == null) {
            dayOfWeek = 0;
        } else {
            dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        }
        tdl_dayOfView.setText(dayOfWeekArr[dayOfWeek]);
        tdl_timeView.setText(this_row.getToDoTime());
        tdl_descView.setText(this_row.getToDoDesc());

        runEnterAnimation(rowView, position);
    }

    private void runEnterAnimation(View view, int order) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        view.setTranslationX(-size.x);

        ObjectAnimator transAnim = ObjectAnimator.ofFloat(view, "translationX", 0);
        transAnim.setInterpolator(new DecelerateInterpolator(2.5f));
        transAnim.setDuration(700);
        transAnim.setStartDelay(order*100);
        transAnim.start();
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
