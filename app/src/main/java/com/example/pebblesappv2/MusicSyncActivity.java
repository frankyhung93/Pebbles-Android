package com.example.pebblesappv2;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import com.google.android.gms.auth.api.Auth;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.common.io.BaseEncoding;


public class MusicSyncActivity extends BaseACA {
    Realm realm;
    String staticIp = "192.168.1.130";
    String protocol = "http://";
    JSONArray jDlRsArray;
    JSONArray jTagRsArray;
    static YouTube youtube;
    static final String KEY
            = "AIzaSyA6BiT3OVlEvKqTvHL9slfPqYucIksgQuw";
    String folder_name = "youtube_music";
    String tn_folder_name = "youtube_thumbnails";
    YouTube.Videos.List query;
    String packageName;
    ArrayList<String> filenames_arr = new ArrayList<>();
    ArrayList<String> tn_filenames_arr = new ArrayList<>();
//    Map<String, Integer> vidid_index_map = new HashMap<>();
    Button button_sync;
    Button button_back;
    TextView label_status;
    Boolean sync_in_progress;
    String[] videoIds;

    private String getSHA1(String packageName){
        try {
            Signature[] signatures = this.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures;
            for (Signature signature: signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA-1");
                md.update(signature.toByteArray());
                return BaseEncoding.base16().encode(md.digest());
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_sync);
        packageName = getApplicationContext().getPackageName();

        // Setup realm
        Realm.init(this);
        realm = Realm.getDefaultInstance();

        sync_in_progress = false;

        // Find view ids
        button_sync = (Button) findViewById(R.id.button_sync);
        button_back = (Button) findViewById(R.id.button_back);
        label_status = (TextView) findViewById(R.id.label_status);

        button_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sync_in_progress) {
                    sync_in_progress = true;
                    updateStatusLabel("Populating the realm database with YTTags and YTDownloads");
                    populateData();
                    setVideoIds();
                    new MusicSyncWorker().execute(getDownloadURLs());
                }
            }
        });

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.putExtra("status", "Ready to Roll");
                setResult(RESULT_OK, i);
                finish();
            }
        });
    }

    void setVideoIds() {
        RealmQuery<YTDownloads> query = realm.where(YTDownloads.class);
        RealmResults<YTDownloads> result1 = query.findAll();
        videoIds = new String[result1.size()];
        for (int i = 0; i < result1.size(); i++) {
            videoIds[i] = result1.get(i).getVideo_id();
        }
    }

    public URL[] getDownloadURLs () {
        RealmQuery<YTDownloads> query = realm.where(YTDownloads.class);
        RealmResults<YTDownloads> result1 = query.findAll();
        URL[] urls = new URL[result1.size()];
        for (int i = 0; i < result1.size(); i++) {
            String urlString = protocol + staticIp + "/dl_music/" + result1.get(i).getVideo_id() + ".mp3";
            try {
                urls[i] = new URL(urlString);
            } catch (Exception e) {
                Log.d("URL MALFORM EXCEPTION", e.toString());
            }
        }
        return  urls;
    }

    public void populateData() {
        jDlRsArray = getJsonResult("dbsqlite_return_downloads");
        jTagRsArray = getJsonResult("dbsqlite_return_tags");
        createModel_tags(jTagRsArray);
        createModel_downloads(jDlRsArray);
    }

    public void startButtonRotation() {
        float deg = button_sync.getRotation() + 30000F;
        button_sync.animate().setDuration(300000);
        button_sync.animate().rotation(deg).setInterpolator(new LinearInterpolator());
    }

    public void stopButtonRotation() {
        button_sync.setRotation(0);
        button_sync.animate().cancel();
//        button_sync.clearAnimation();
    }

    public void updateStatusLabel(String status_msg) {
        label_status.setText(status_msg);
    }

    public void createModel_downloads(JSONArray jsonArray) {
        // First delete previous records
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(YTDownloads.class);
            }
        });
        Log.d("JSON", jsonArray.toString());
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject oneObject = jsonArray.getJSONObject(i);
                Log.d("JSON "+i, oneObject.toString());
                // Pulling items from the array
                final String video_title = oneObject.getString("video_title");
                final String video_id = oneObject.getString("video_id");
                final int id = oneObject.getInt("id");
                final String[] tags_string = oneObject.getString("video_tags").split(",");
                final Integer[] tags = new Integer[tags_string.length];
                for (int j = 0; j < tags_string.length; j++) {
                    tags[j] = Integer.parseInt(tags_string[j]);
                }

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        YTDownloads dl = realm.createObject(YTDownloads.class); // Create a new object
                        dl.setId(id);
                        dl.setVideo_id(video_id);
                        dl.setVideo_title(video_title);
                        RealmQuery<YTTags> query = realm.where(YTTags.class).in("id", tags);
                        RealmResults<YTTags> rs = query.findAll();
                        for (int k = 0; k < rs.size(); k++) {
//                            System.out.println(rs.get(k).getTag_name());
                            dl.getVideo_tags().add(rs.get(k));
                        }
                    }
                });

            } catch (JSONException e) {
                Log.d("EXCEPTION", e.getMessage());
            }
        }
    }

    public void createModel_tags(JSONArray jsonArray) {
        // First delete previous records
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(YTTags.class);
            }
        });

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject oneObject = jsonArray.getJSONObject(i);
                // Pulling items from the array
                final String tag_name = oneObject.getString("tag_name");
                final int tag_order = oneObject.getInt("tag_order");
                final int id = oneObject.getInt("id");

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        YTTags tag = realm.createObject(YTTags.class); // Create a new object
                        tag.setId(id);
                        tag.setTag_name(tag_name);
                        tag.setTag_order(tag_order);
                    }
                });

            } catch (JSONException e) {
                Log.d("EXCEPTION", e.getMessage());
            }
        }
    }

    public JSONArray getJsonResult(String action) {
        //Some url endpoint that you may have
        String domain = protocol + staticIp;
        String myUrl = domain + "/" + "ytdownloader/" + action;
        String result;
        JSONArray jArray;

        HttpGetRequest getRequest = new HttpGetRequest();

        try {
            result = getRequest.execute(myUrl).get();

            // Create JSON Object from result string
            jArray = new JSONArray(result);
            return jArray;

        } catch (Exception e) {
            Log.d("EXCEPTION", e.getMessage());
            return null;
        }
    }

    private class MusicSyncWorker extends AsyncTask<URL, String, String>
    {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            startButtonRotation();
            updateStatusLabel("Loading...");
        }

        protected String doInBackground(URL[] urls) {
            // Set up Filename String ArrayList
            String path = getFilesDir().toString() + File.separator + folder_name;
            File directory = new File(path);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File[] files = directory.listFiles();
            for (int i = 0; i < files.length; i++) {
                filenames_arr.add(files[i].getName());
            }
            path = getFilesDir().toString() + File.separator + tn_folder_name;
            directory = new File(path);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File[] tn_files = directory.listFiles();
            for (int i = 0; i < tn_files.length; i++) {
                tn_filenames_arr.add(getNameFromFileName(tn_files[i].getName()));
            }

            //this method will be running on background thread so don't update UI from here
            //do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            int fileLength;
            int download_count = 0;
            for (URL url : urls) { // Download the song files from the Ubuntu Server
                try {
                    String urlString = url.getFile();
                    String filename = urlString.substring( urlString.lastIndexOf('/')+1, urlString.length() );
                    if (filenames_arr.contains(filename)) {
                        Log.d("REMOVE", filename);
                        filenames_arr.remove(filename);
                        continue;
                    }

                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    // expect HTTP 200 OK, so we don't mistakenly save error report
                    // instead of the file
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        Log.d("HTTP CONNECTION", String.valueOf(connection.getResponseCode()));
                        return "Server returned HTTP " + connection.getResponseCode()
                                + " " + connection.getResponseMessage();
                    }

                    // this will be useful to display download percentage
                    // might be -1: server did not report the length
                    fileLength = connection.getContentLength();

                    // download the file
                    input = connection.getInputStream();
                    File f = new File(getFilesDir(), folder_name);
                    if (!f.exists()) {
                        if (!f.mkdirs()) {
                            Log.d("MKDIR FAILURE", f.toString());
                        }
                    }
                    Log.d("FILE PATH:", getFilesDir() + File.separator + folder_name + File.separator + filename);

                    // check if the file already exists
                    File file = new File(getFilesDir() + File.separator + folder_name, filename);
                    if (!file.exists()) {
                        output = new FileOutputStream(getFilesDir() + File.separator + folder_name + File.separator + filename);

                        byte data[] = new byte[4096];
                        long total = 0;
                        int count;
                        while ((count = input.read(data)) != -1) {
                            // allow canceling with back button
                            if (isCancelled()) {
                                input.close();
                                return null;
                            }
                            total += count;
                            // publishing the progress....
                            if (fileLength > 0) // only if total length is known
                                publishProgress("Downloading File " + filename + ", Progress: " + (int) (total * 100 / fileLength) + "%.");
                            try {
                                output.write(data, 0, count);
                                download_count++;
                            } catch (Exception e) {
                                Log.d("DOWNLOAD EXCEPTION 1", e.toString());
                                throw e;
                            }
                        }
                    } else {
                        // if file already exists, delete the file from the filenames_arr
                        Log.d("REMOVE", filename);
                        filenames_arr.remove(filename);
//                        Log.d("DOWNLOAD ALREADY EXISTS", filename);
                    }
                } catch (Exception e) {
                    Log.d("DOWNLOAD EXCEPTION 2", e.toString());
                    return e.toString();
                } finally {
                    try {
                        if (output != null)
                            output.close();
                        if (input != null)
                            input.close();
                    } catch (IOException ignored) {
                        Log.d("IOEXCEPTION IGNORED", "um...");
                    }

                    if (connection != null)
                        connection.disconnect();
                }

            }

            input = null;
            output = null;
            connection = null;
            download_count = 0;
//            tn_folder_name = "youtube_thumbnails";
            // Download the image thumbnails for the song files
            for (String videoId : videoIds) {
                try {
                    // First check videoId already in thumbnails photos folder first
                    if (tn_filenames_arr.contains(videoId)) {
                        Log.d("REMOVE", videoId);
                        tn_filenames_arr.remove(videoId);
                        continue; // skipping the whole download thumbnail process for this videoId
                    }

                    // This object is used to make YouTube Data API requests. The last
                    // argument is required, but since we don't need anything
                    // initialized when the HttpRequest is initialized, we override
                    // the interface and provide a no-op function.
                    youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                        public void initialize(HttpRequest request) throws IOException {
                            request.getHeaders().set("X-Android-Package", packageName);
                            Log.d("PACKAGE-NAME", packageName);
                            request.getHeaders().set("X-Android-Cert",getSHA1(packageName));
                        }
                    }).setApplicationName("pebblesappv2").build();

                    query = youtube.videos().list("snippet");
                    query.setKey(KEY);
                    query.setId(videoId);

                    VideoListResponse response = query.execute();
//                    Log.d("VIDEO ID", videoId);
//                    Log.d("YOUTUBE RESPONSE", response.getItems().toString());
                    if (response.getItems().size() == 0) { // If the video is removed or do not exist on youtube anymore
                        continue;
                    }
                    String thumbnailUrl = response.getItems().get(0).getSnippet().getThumbnails().getMedium().getUrl();
                    URL url = new URL(thumbnailUrl);

                    // Start the HttpConnection and download process
                    String urlString = url.getFile();
                    String temp = urlString.substring( urlString.lastIndexOf('/')+1, urlString.length() );
                    String ext = getExtFromFileName(temp);
                    String filename = videoId + "." + ext;

                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    // expect HTTP 200 OK, so we don't mistakenly save error report
                    // instead of the file
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        Log.d("HTTP CONNECTION", String.valueOf(connection.getResponseCode()));
                        return "Server returned HTTP " + connection.getResponseCode()
                                + " " + connection.getResponseMessage();
                    }

                    // this will be useful to display download percentage
                    // might be -1: server did not report the length
                    fileLength = connection.getContentLength();

                    // download the file
                    input = connection.getInputStream();
                    File f = new File(getFilesDir(), tn_folder_name);
                    if (!f.exists()) {
                        if (!f.mkdirs()) {
                            Log.d("MKDIR FAILURE", f.toString());
                        }
                    }
//                    Log.d("FILE PATH:", getFilesDir() + File.separator + folder_name + File.separator + filename);

                    // check if the file already exists
                    File file = new File(getFilesDir() + File.separator + tn_folder_name, filename);
                    if (!file.exists()) {
                        output = new FileOutputStream(getFilesDir() + File.separator + tn_folder_name + File.separator + filename);

                        byte data[] = new byte[4096];
                        long total = 0;
                        int count;
                        while ((count = input.read(data)) != -1) {
                            // allow canceling with back button
                            if (isCancelled()) {
                                input.close();
                                return null;
                            }
                            total += count;
                            // publishing the progress....
                            if (fileLength > 0) // only if total length is known
                                publishProgress("Downloading Thumbnail " + filename + ", Progress: " + (int) (total * 100 / fileLength) + "%.");
                            try {
                                output.write(data, 0, count);
                                download_count++;
                            } catch (Exception e) {
                                Log.d("DOWNLOAD EXCEPTION 3", e.toString());
                                throw e;
                            }
                        }
                    } else {
//                        Log.d("DOWNLOAD ALREADY EXISTS", filename);
                        Log.d("REMOVE", videoId);
                        tn_filenames_arr.remove(videoId);
                    }

                } catch (Exception e) {
                    Log.d("DOWNLOAD EXCEPTION 4", e.toString());
                    e.printStackTrace();
                    return e.toString();
                } finally {
                    try {
                        if (output != null)
                            output.close();
                        if (input != null)
                            input.close();
                    } catch (IOException ignored) {
                        Log.d("IOEXCEPTION IGNORED", "um...");
                    }

                    if (connection != null)
                        connection.disconnect();
                }
            }

            // Delete ubuntu removed download at this device
            try {
                for (String thefile : filenames_arr) {
                    File file_tbd = new File(getFilesDir() + File.separator + folder_name + File.separator + thefile);
                    if (file_tbd.exists()) {
                        Boolean isDeleted = file_tbd.delete();
                        Log.d("File Deletion", isDeleted.toString());
                    } else {
                        Log.d("File Deletion", "File does not exist: "+getFilesDir() + File.separator + folder_name + File.separator + thefile);
                    }
                }
                filenames_arr.clear();
                // To-do for thumbnails deletion
//                for (String thefile : tn_filenames_arr) {
//                    File file_tbd = new File(getFilesDir() + File.separator + tn_folder_name + File.separator + thefile);
//                    if (file_tbd.exists()) {
//                        Boolean isDeleted = file_tbd.delete();
//                        Log.d("Thumbnail Deletion", isDeleted.toString());
//                    } else {
//                        Log.d("Thumbnail Deletion", "TN does not exist: "+getFilesDir() + File.separator + tn_folder_name + File.separator + thefile);
//                    }
//                }
                tn_filenames_arr.clear();
            } catch (Exception e) {
                Log.d("File cannot be deleted", e.toString());
                e.printStackTrace();
            }

            return "Downloaded: "+download_count;
        }

        protected void onProgressUpdate(String... msg) {
            updateStatusLabel(msg[0]);
        }

        protected void onPostExecute(String result) {
            //this method will be running on UI thread
            Log.d("WHATS LEFT", filenames_arr.toString());
            Log.d("WHATS LEFT", tn_filenames_arr.toString());
            stopButtonRotation();
            sync_in_progress = false;
            updateStatusLabel("Music Sync is complete. "+result);
            button_back.setVisibility(View.VISIBLE);
        }

    }
}
