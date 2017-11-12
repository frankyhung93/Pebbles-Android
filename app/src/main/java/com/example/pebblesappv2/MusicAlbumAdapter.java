package com.example.pebblesappv2;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MusicAlbumAdapter extends ArrayAdapter<YTTags> {
    private Context mContext;
    private ArrayList<YTTags> mDataSource;
    private LayoutInflater mInflater;

    // View lookup cache
    private static class ViewHolder {
        ImageView album_cover;
        TextView album_title;
        TextView album_desc;
    }

    public MusicAlbumAdapter(Context context, ArrayList<YTTags> albums) {
        super(context, R.layout.album_row, albums);
        mContext = context;
        mDataSource = albums;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public YTTags getItem(int position) {
        return mDataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        String albumTitle = getItem(position).getTag_name();
        Integer albumSize = getItem(position).getSongs().size();
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.album_row, parent, false);
            viewHolder.album_cover = (ImageView) convertView.findViewById(R.id.album_cover);
            viewHolder.album_title = (TextView) convertView.findViewById(R.id.album_title);
            viewHolder.album_desc = (TextView) convertView.findViewById(R.id.album_desc);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.album_title.setText(albumTitle);
        String songsText = "Songs: "+String.valueOf(albumSize);
        viewHolder.album_desc.setText(songsText);

//        runEnterAnimation(convertView, position);
        // Return the completed view to render on screen
        return convertView;
    }

    private void runEnterAnimation(View view, int order) {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        view.setTranslationX(-size.x);

        ObjectAnimator transAnim = ObjectAnimator.ofFloat(view, "translationX", 0);
        transAnim.setInterpolator(new DecelerateInterpolator(3f));
        transAnim.setDuration(500);
        transAnim.setStartDelay(order*50);
        transAnim.start();
    }
}
