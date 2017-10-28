package com.example.pebblesappv2;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class SearchResultsActivity extends BaseACA implements PlayerBarFragment.OnFragmentInteractionListener {
    Realm realm;
    ListView mListView;
    TextView albumTitle;
    Boolean isPlaying = false;
    MediaPlayer player = null;
    String folder_name = "youtube_music";
    ArrayList<Uri> playlist = new ArrayList<>();
    Map<String, String> vid_ext_map = new HashMap<String, String>();
    Map<String, String> vid_title_map = new HashMap<String, String>();

    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;

    public final static String TYPE_PLAY = "play";
    public final static String TYPE_SHUFFLE = "shuffle";

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            musicBound = true;
            Log.d("DEBUGS", "onServiceConnected - musicBound is "+musicBound);
            if (musicSrv.isPlayerSet() && (musicSrv.isPlaying() || musicSrv.isShuffling())) {
                // show the appropriate song title
                showPlayerBar(musicSrv.returnPlayingType(), getTitleFromVidId(getNameFromFilePath(musicSrv.returnCurrentUri().getPath())));
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
//            musicSrv = null;
            Log.d("DEBUGS", "onServiceDisconnected - musicBound is "+musicBound);
            musicBound = false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        if(playIntent==null){
            playIntent = new Intent(getApplicationContext(), MusicService.class);
            startService(playIntent);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
        }

        // Setup realm
        Realm.init(this);
        realm = Realm.getDefaultInstance();

        // Setting toolbar interface
        Toolbar toolbar = (Toolbar) findViewById(R.id.general_toolbar);
        setSupportActionBar(toolbar);
        final Drawable bkArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material); // can only change arrow color through code fuck it man, nothing useful changing it in the xml attributes
        bkArrow.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(bkArrow);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set up videoId-extension Map object
        String path = getFilesDir().toString() + File.separator + folder_name;
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {
            String part_name = getNameFromFileName(files[i].getName());
            String part_ext = getExtFromFileName(files[i].getName());
            vid_ext_map.put(part_name, part_ext);
            vid_title_map.put(part_name, getTitleFromVidId(part_name));
        }

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            RealmQuery<YTDownloads> rquery = realm.where(YTDownloads.class);
            RealmResults<YTDownloads> results = rquery.contains("video_title", query, Case.INSENSITIVE).findAll();
            Log.d("FOUND SONGS", results.size()+"");
            // init listview and playlist
            mListView = (ListView) findViewById(R.id.search_playlist);
            ArrayList<YTDownloads> songs = new ArrayList<>();
            for (YTDownloads song : results) {
                songs.add(song);
                String song_id = song.getVideo_id();
                String song_file = song_id + "." + vid_ext_map.get(song_id);
                Uri myUri = Uri.parse(new File(getFilesDir() + File.separator + folder_name, song_file).toString());
                playlist.add(myUri);
            }
            final AlbumPlayListAdapter adapter = new AlbumPlayListAdapter(this, songs);
            mListView.setAdapter(adapter);

            // Simply play a song when you click on a list item(song)
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    YTDownloads song = adapter.getItem(position);
                    String song_id = song.getVideo_id();
                    String song_title = song.getVideo_title();
                    String song_file = song_id + "." + vid_ext_map.get(song_id);
                    Uri myUri = Uri.parse(new File(getFilesDir() + File.separator + folder_name, song_file).toString()); // initialize Uri here

                    // set song and play song, by calling the serv methods, also display the playerbar
                    musicSrv.setPlayingType(TYPE_PLAY);
                    musicSrv.setPlayingAlbum("search_list");
                    showPlayerBar(TYPE_PLAY, song_title);
                    musicSrv.playSong(myUri);

                }
            });
        }
    }

    public String getTitleFromVidId(String vidId) {
//        Log.d("ABC", vidId);
        RealmQuery<YTDownloads> query = realm.where(YTDownloads.class);
        YTDownloads vid = query.equalTo("video_id", vidId).findFirst();
        return vid.getVideo_title();
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
        musicSrv.playPrevious();
    }

    @Override
    public void onNextButtonClicked() {
        musicSrv.playNext();
    }

    private void setPlayerBarTitle(String playType, String title) {
        String barTitleText = "";
        View fview = returnPlayerBarView();
        TextView songTitle = (TextView) fview.findViewById(R.id.showPlaying);
        if (playType.equals(TYPE_PLAY)) {
            barTitleText = "<font color=#ffffff>Now Playing from</font> <font color=#0aff9d>"+musicSrv.returnPlayingAlbum()+"</font><font color=#ffffff>: "+title+" ...</font>";
        } else if (playType.equals(TYPE_SHUFFLE)) {
            barTitleText = "<font color=#ffffff>Now Shuffling from</font> <font color=#0aff9d>"+musicSrv.returnPlayingAlbum()+"</font><font color=#ffffff>: "+title+" ...</font>";
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            songTitle.setText(Html.fromHtml(barTitleText, Html.FROM_HTML_MODE_LEGACY));
        } else {
            songTitle.setText(Html.fromHtml(barTitleText));
        }
    }

    private void showPlayerBar(String playType, String songtitle) {
        Fragment pbf =  getFragmentManager().findFragmentByTag("playerbar");
        if (pbf == null) {
            Log.d("WTFTW", playType+" "+songtitle);
            PlayerBarFragment pbfragment = PlayerBarFragment.newInstance(playType, songtitle, "SearchResultsActivity");

            getFragmentManager().beginTransaction()
                    .add(R.id.playerbar_container, pbfragment, "playerbar").commit();
        } else {
            Log.d("WTFTW", "pbf is not null");
            setPlayerBarTitle(playType, songtitle);
        }
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

    public String returnPlayingAlbum() {
        return musicSrv.returnPlayingAlbum();
    }
}
