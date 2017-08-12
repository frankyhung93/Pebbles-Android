package com.example.pebblesappv2;

import com.example.pebblesappv2.MusicService.MusicBinder;

import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
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

public class AlbumPlayList extends BaseACA implements PlayerBarFragment.OnFragmentInteractionListener {
    Realm realm;
    ListView mListView;
    TextView albumTitle;
    String receivedTitle;
    Boolean isPlaying = false;
    MediaPlayer player = null;
    String folder_name = "youtube_music";
    ArrayList<Uri> playlist = new ArrayList<>();
    Map<String, String> vid_ext_map = new HashMap<String, String>();
    Map<String, String> vid_title_map = new HashMap<String, String>();

    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicBinder binder = (MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            //pass list
//            musicSrv.setList(songList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_play_list);

        if(playIntent==null){
            playIntent = new Intent(getApplicationContext(), MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }

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
            vid_title_map.put(part_name, getTitleFromVidId(part_name));
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
                String song_title = song.getVideo_title();
                String song_file = song_id + "." + vid_ext_map.get(song_id);
                Uri myUri = Uri.parse(new File(Environment.getExternalStorageDirectory() + File.separator + folder_name, song_file).toString()); // initialize Uri here

                // set song and play song, by calling the serv methods, also display the playerbar
                showPlayerBar(song_title);
                musicSrv.playSong(myUri);

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
            playShuffle();
        }

        return super.onOptionsItemSelected(item);
    }

    public String getTitleFromVidId(String vidId) {
        Log.d("ABC", vidId);
        RealmQuery<YTDownloads> query = realm.where(YTDownloads.class);
        YTDownloads vid = query.equalTo("video_id", vidId).findFirst();
        return vid.getVideo_title();
    }

    private void setPlayerBarTitle(String title) {
        View fview = returnPlayerBarView();
        TextView songTitle = (TextView) fview.findViewById(R.id.showPlaying);
        songTitle.setText("Now Playing: "+title+" ...");
    }

    private void playShuffle() {
        Collections.shuffle(playlist);
        Log.d("PERFECTO", getNameFromFilePath(playlist.get(0).getPath()));
        showPlayerBar(getTitleFromVidId(getNameFromFilePath(playlist.get(0).getPath())));
        musicSrv.playShuffle(playlist);
    }

    private void showPlayerBar(String songtitle) {
        PlayerBarFragment pbfragment = PlayerBarFragment.newInstance(songtitle);

        getFragmentManager().beginTransaction()
                .add(R.id.playerbar_container, pbfragment, "playerbar").commit();
    }

    private void closePlayerBar() {
        // stop the current song
        musicSrv.stopSong();

        Fragment pbfragment =  getFragmentManager().findFragmentByTag("playerbar");
        FragmentTransaction ftrans = getFragmentManager().beginTransaction();
        if(pbfragment!=null) ftrans.remove(pbfragment);
        ftrans.commit();
    }

    public View returnPlayerBarView() {
        Fragment pbfragment =  getFragmentManager().findFragmentByTag("playerbar");
        return pbfragment.getView();
    }

    @Override
    public void onCancelButtonClicked() {
        closePlayerBar();
    }

    @Override
    public void onPlayButtonClicked() {
        if (musicSrv.isPlaying()) {
            musicSrv.pauseSong();
            ImageButton playBtn = (ImageButton) returnPlayerBarView().findViewById(R.id.playbutton);
            playBtn.setImageResource(R.drawable.playnow);
        } else {
            musicSrv.resumeSong();
            ImageButton playBtn = (ImageButton) returnPlayerBarView().findViewById(R.id.playbutton);
            playBtn.setImageResource(R.drawable.pausenow);
        }
    }

    @Override
    public void onBackButtonClicked() {

    }

    @Override
    public void onNextButtonClicked() {

    }

    // Handling the received Intents for the "my-integer" event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String songPath = intent.getStringExtra("songPath");
            setPlayerBarTitle(vid_title_map.get(getNameFromFilePath(songPath)));
        }
    };
    @Override
    public void onResume() {
        super.onResume();
        // This registers mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mMessageReceiver,
                        new IntentFilter("Next-Song"));
        // Show the appropriate playerBar

    }
    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicSrv=null;
        super.onDestroy();
    }
}
