package com.example.pebblesappv2;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
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
    private Integer[] mIcons = {
            R.drawable.acoustic_guitar, R.drawable.analytics, R.drawable.beach, R.drawable.beach_1,
            R.drawable.bicycle, R.drawable.book, R.drawable.books, R.drawable.building,
            R.drawable.business, R.drawable.camping, R.drawable.chat, R.drawable.chef,
            R.drawable.cinema, R.drawable.cogwheel, R.drawable.commerce, R.drawable.compass,
            R.drawable.cooking, R.drawable.cooking_1, R.drawable.crayons, R.drawable.cutlery,
            R.drawable.date, R.drawable.diagram, R.drawable.diamond, R.drawable.dumbbell,
            R.drawable.favorite, R.drawable.folder, R.drawable.forest, R.drawable.gamepad,
            R.drawable.gaming, R.drawable.garbage_bin, R.drawable.gardening, R.drawable.gauge,
            R.drawable.goal, R.drawable.hourglass, R.drawable.idea, R.drawable.keyboard,
            R.drawable.learning, R.drawable.list, R.drawable.movies, R.drawable.mug,
            R.drawable.music, R.drawable.night, R.drawable.paint_palette, R.drawable.panel,
            R.drawable.paper_plane, R.drawable.pencil, R.drawable.piano, R.drawable.pie_chart,
            R.drawable.pie_chart_1, R.drawable.play_button, R.drawable.puzzle, R.drawable.puzzle_pieces,
            R.drawable.rest, R.drawable.running, R.drawable.summer,
            R.drawable.swimming, R.drawable.target, R.drawable.tool, R.drawable.virus,
            R.drawable.visa_card, R.drawable.weekly_calendar, R.drawable.weight, R.drawable.wrench,
            R.drawable.write, R.drawable.writing_1
    };

    public RoutinesDialogGridAdapter(Context c) {
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
        } else {
            itemGrid = convertView;
        }
        ImageView routine_iconView = (ImageView) itemGrid.findViewById(R.id.routine_dialog_icon);
        routine_iconView.setImageResource(mIcons[position]);
        return itemGrid;
    }

}
