package com.example.pebblesappv2;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ChunFaiHung on 2017/7/30.
 */

public class AlbumPlayListAdapter extends ArrayAdapter<YTDownloads> {
    private Context mContext;
    private ArrayList<YTDownloads> mDataSource;
    private LayoutInflater mInflater;
    private String folder_name = "youtube_thumbnails";
    Map<String, String> vid_ext_map = new HashMap<String, String>();

    // View lookup cache
    private static class ViewHolder {
        CustomImageView song_cover;
        TextView song_title;
        TextView song_desc;
    }

    public AlbumPlayListAdapter(Context context, ArrayList<YTDownloads> songs) {
        super(context, R.layout.album_row, songs);
        mContext = context;
        mDataSource = songs;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // Set up videoId-extension Map object
        String path = Environment.getExternalStorageDirectory().toString() + File.separator + folder_name;
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {
            String part_name = getNameFromFileName(files[i].getName());
            String part_ext = getExtFromFileName(files[i].getName());
            vid_ext_map.put(part_name, part_ext);
        }
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
        String songId = getItem(position).getVideo_id();
        // Check if an existing view is being reused, otherwise inflate the view
        AlbumPlayListAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new AlbumPlayListAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.song_row, parent, false);
            viewHolder.song_cover = (CustomImageView) convertView.findViewById(R.id.song_cover);
            viewHolder.song_title = (TextView) convertView.findViewById(R.id.song_title);
            viewHolder.song_desc = (TextView) convertView.findViewById(R.id.song_desc);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (AlbumPlayListAdapter.ViewHolder) convertView.getTag();
        }

        viewHolder.song_title.setText(songTitle);
        String filename = songId + "." + vid_ext_map.get(songId);
        File imgFile = new File(Environment.getExternalStorageDirectory() + File.separator + folder_name, filename);
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            viewHolder.song_cover.setImageBitmap(myBitmap);
        }
        // Return the completed view to render on screen
        return convertView;
    }

    public String getExtFromFileName(String filename) {
        String filenameArray[] = filename.split("\\.");
        return filenameArray[filenameArray.length-1];
    }
    public String getNameFromFileName(String filename) {
        String filenameArray[] = filename.split("\\.");
        return filenameArray[filenameArray.length-2];
    }
}
