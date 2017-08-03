package com.example.pebblesappv2;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class AlbumPlayList extends BaseACA {
    Realm realm;
    ListView mListView;
    TextView albumTitle;
    String receivedTitle;
    Boolean isPlaying = false;
    MediaPlayer player = null;
    String folder_name = "youtube_music";
    ArrayList<Uri> playlist = new ArrayList<>();
    Map<String, String> vid_ext_map = new HashMap<String, String>();
    Timer timer;
    int playListCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_play_list);

        timer = new Timer();

        // Setup realm
        Realm.init(this);
        realm = Realm.getDefaultInstance();

        // Get Intent
        Intent intent = getIntent();
        int albumId = intent.getIntExtra(getMyString(R.string.intent_extra_tagId), 0);
        RealmQuery<YTTags> query = realm.where(YTTags.class);
        YTTags album = query.equalTo("id", albumId).findFirst();
        receivedTitle = album.getTag_name();

        // Setting toolbar interface
        Toolbar toolbar = (Toolbar) findViewById(R.id.general_toolbar);
        setSupportActionBar(toolbar);
        final Drawable bkArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material); // can only change arrow color through code fuck it man, nothing useful changing it in the xml attributes
        bkArrow.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(bkArrow);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set toolbar title
        albumTitle = (TextView) findViewById(R.id.music_toolbar_title);
        albumTitle.setText(receivedTitle);

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

        // init listview and playlist
        mListView = (ListView) findViewById(R.id.album_playlist);
        ArrayList<YTDownloads> songs = new ArrayList<>();
        for (YTDownloads song : album.getSongs()) {
            songs.add(song);
            String song_id = song.getVideo_id();
            String song_file = song_id + "." + vid_ext_map.get(song_id);
            Uri myUri = Uri.parse(new File(Environment.getExternalStorageDirectory() + File.separator + folder_name, song_file).toString());
            playlist.add(myUri);
        }
        final AlbumPlayListAdapter adapter = new AlbumPlayListAdapter(this, songs);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                YTDownloads song = adapter.getItem(position);
                String song_id = song.getVideo_id();
                String song_file = song_id + vid_ext_map.get(song_id);
                if (!isPlaying) {
                    Uri myUri = Uri.parse(new File(Environment.getExternalStorageDirectory() + File.separator + folder_name, song_file).toString()); // initialize Uri here
                    player = new MediaPlayer();
                    player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    try {
                        player.setDataSource(getApplicationContext(), myUri);
                        player.prepare();
                        player.start();
                    } catch (Exception e) {
                        Log.d("MUSIC PLAYER EXCEPTION", e.toString());
                    }
                    isPlaying = true;
                } else {
                    isPlaying = false;
                    player.release();
                    player = null;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.playlist_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.play_shuffle) {
            Collections.shuffle(playlist);
            player = null;
            player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                player.setDataSource(getApplicationContext(), playlist.get(playListCounter));
                player.prepare();
                player.start();
            } catch (Exception e) {
                Log.d("MUSIC PLAYER EXCEPTION", e.toString());
            }
            if (playlist.size() > 1) {
                playNext();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void playNext() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                player.reset();
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    player.setDataSource(getApplicationContext(), playlist.get(++playListCounter));
                    player.prepare();
                    player.start();
                } catch (Exception e) {
                    Log.d("MUSIC PLAYER EXCEPTION", e.toString());
                }
                if (playlist.size() > playListCounter+1) {
                    playNext();
                }
            }
        },player.getDuration()+100);
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
