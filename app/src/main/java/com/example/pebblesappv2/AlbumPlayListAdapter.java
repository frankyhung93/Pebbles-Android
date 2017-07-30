package com.example.pebblesappv2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ChunFaiHung on 2017/7/30.
 */

public class AlbumPlayListAdapter extends ArrayAdapter<YTDownloads> {
    private Context mContext;
    private ArrayList<YTDownloads> mDataSource;
    private LayoutInflater mInflater;

    // View lookup cache
    private static class ViewHolder {
        ImageView song_cover;
        TextView song_title;
        TextView song_desc;
    }

    public AlbumPlayListAdapter(Context context, ArrayList<YTDownloads> songs) {
        super(context, R.layout.album_row, songs);
        mContext = context;
        mDataSource = songs;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public YTDownloads getItem(int position) {
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
        String songTitle = getItem(position).getVideo_title();
        // Check if an existing view is being reused, otherwise inflate the view
        AlbumPlayListAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new AlbumPlayListAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.song_row, parent, false);
            viewHolder.song_cover = (ImageView) convertView.findViewById(R.id.song_cover);
            viewHolder.song_title = (TextView) convertView.findViewById(R.id.song_title);
            viewHolder.song_desc = (TextView) convertView.findViewById(R.id.song_desc);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (AlbumPlayListAdapter.ViewHolder) convertView.getTag();
        }

        viewHolder.song_title.setText(songTitle);
        // Return the completed view to render on screen
        return convertView;
    }
}
