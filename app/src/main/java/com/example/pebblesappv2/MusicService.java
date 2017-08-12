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
    private ArrayList<Uri> playlist;
    private boolean isShuffling = false;

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
        } catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();
    }

    public void playShuffle(ArrayList<Uri> playlist) {
        this.playlist = playlist;
        try {
            player.setDataSource(getApplicationContext(), playlist.get(playListCounter));
            player.prepareAsync();
        } catch (Exception e) {
            Log.d("MUSIC PLAYER EXCEPTION", e.toString());
        }
        isShuffling = true;
    }
    public void playNext() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                player.reset();
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    player.setDataSource(getApplicationContext(), playlist.get(++playListCounter));
                    player.prepareAsync();
//                    sendMessage(playlist.get(playListCounter));
                } catch (Exception e) {
                    Log.d("MUSIC PLAYER EXCEPTION", e.toString());
                }
                if (playlist.size() > playListCounter+1) {
                    playNext();
                } else {
                    isShuffling = false;
                    player.release();
                }
            }
        },player.getDuration()+100);
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

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public boolean isShuffling() {
        return isShuffling;
    }

    public void stopSong() {
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
        player.stop();
        player.release();
        return false;
    }

//    @Override
//    public void onDestroy(){
//        player.stop();
//        player.release();
//    }

    @Override
    public void onCompletion(MediaPlayer mp) {
//        mp.release();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        mp.start();
        if (playlist.size() > 1) {
            playNext();
        }
    }
}
