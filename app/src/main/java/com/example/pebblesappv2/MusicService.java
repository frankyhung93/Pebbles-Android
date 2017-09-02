package com.example.pebblesappv2;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    //media player
    private MediaPlayer player;
    //song list
//    private ArrayList<Song> songs;
    //current position
    private int songPosn;
    private final IBinder musicBind = new MusicBinder();
    private Timer timer;
    private int playListCounter = 0;
    private ArrayList<Uri> playlist = new ArrayList<>();
    private boolean isShuffling = false;
    private Uri currentUri;
    private String playingType;
    private String playingAlbum;
    
    public MusicService() {
    }

    public void onCreate(){
        //create the service
        //create the service
        super.onCreate();
        //initialize position
        songPosn=0;
        timer = new Timer();
        //create player
        player = new MediaPlayer();
        initMusicPlayer();
    }

//    public void setList(ArrayList<Song> theSongs){
//        songs=theSongs;
//    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    public void initMusicPlayer(){
        //set player properties
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void playSong(Uri uri){
        player.reset();
        playlist = new ArrayList<Uri>();
        isShuffling = false;
        //play a song
        try {
            player.setDataSource(getApplicationContext(), uri);
            currentUri = uri;
        } catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();
    }

    public void playShuffle(ArrayList<Uri> playlist) {
        this.playlist = playlist;
        player.reset();
        try {
            player.setDataSource(getApplicationContext(), playlist.get(playListCounter));
            currentUri = playlist.get(playListCounter);
            player.prepareAsync();
        } catch (Exception e) {
            Log.d("MUSIC PLAYER EXCEPTION", e.toString());
            isShuffling = false;
        }
        isShuffling = true;
    }

    public void playNext() {
        player.reset();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            player.setDataSource(getApplicationContext(), playlist.get(++playListCounter));
            currentUri = playlist.get(playListCounter);
            player.prepareAsync();
            sendMessage(playlist.get(playListCounter));
        } catch (Exception e) {
            Log.d("MUSIC PLAYER EXCEPTION", e.toString());
        }
    }
    public void playPrevious() {
        player.reset();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            player.setDataSource(getApplicationContext(), playlist.get(--playListCounter));
            currentUri = playlist.get(playListCounter);
            player.prepareAsync();
            sendMessage(playlist.get(playListCounter));
        } catch (Exception e) {
            Log.d("MUSIC PLAYER EXCEPTION", e.toString());
        }
    }

    private void sendMessage(Uri uri) {
        // The string "my-integer" will be used to filer the intent
        Intent intent = new Intent("Next-Song");
        // Adding some data
        intent.putExtra("songPath", uri.getPath());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void pauseSong() {
        player.pause();
    }

    public void resumeSong() {
        int length = player.getCurrentPosition();
        player.seekTo(length);
        player.start();
    }

    public boolean isPlayerSet() {
        if (player == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public boolean isShuffling() {
        return isShuffling;
    }

    public Uri returnCurrentUri() { return currentUri; }
    
    public String returnPlayingType() { return playingType; }
    
    public String returnPlayingAlbum() { return playingAlbum; }

    public void setPlayingType(String playType) {
        playingType = playType;
    }

    public void setPlayingAlbum(String album) {
        playingAlbum = album;
    }
    
    public void stopSong() {
        isShuffling = false;
        player.reset();
    }

    public void setSong(int songIndex){
        songPosn=songIndex;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
//        player.stop();
//        player.release();
        return false;
    }

    @Override
    public void onDestroy(){
        player.stop();
        player.release();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
//        mp.release();
        if (playlist.size() > playListCounter+1) {
            playNext();
        } else {
            isShuffling = false;
            mp.reset();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        mp.start();
    }
}
