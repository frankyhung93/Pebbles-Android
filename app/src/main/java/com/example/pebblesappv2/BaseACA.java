package com.example.pebblesappv2;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import io.realm.Realm;

/**
 * Created by ChunFaiHung on 2017/7/30.
 */

public class BaseACA extends AppCompatActivity {

    public int base_reward_id = 0;
    public Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        super.onCreate(savedInstanceState);
    }

    public String getMyString(int resid) {
        return getResources().getString(resid);
    }

    public String getExtFromFileName(String filename) {
        String filenameArray[] = filename.split("\\.");
        return filenameArray[filenameArray.length-1];
    }
    public String getNameFromFileName(String filename) {
        String filenameArray[] = filename.split("\\.");
        return filenameArray[filenameArray.length-2];
    }

    public String getNameFromFilePath(String filename) {
        String filepathArray[] = filename.split("\\.");
        String filenameArray[] = filepathArray[filepathArray.length-2].split("/");
        return filenameArray[filenameArray.length-1];
    }

    public boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public Bitmap squareCrop(Bitmap bitmap)
    {
        //use the smallest dimension of the image to crop to
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Log.d("WIDTH", ""+w);
        Log.d("HEIGHT", ""+h);
        if (w < h) {
            return Bitmap.createBitmap(bitmap, 0, (h-w)/2, w, w);
        } else if (w > h) {
            return Bitmap.createBitmap(bitmap, (w-h)/2, 0, h, h);
        } else {
            return bitmap;
        }
    }

    public interface UpdateRewardUI {
        public void updateRewardCover();
    }

    protected class CopyBitmapSyncRealm extends AsyncTask<Bitmap, Void, String> {
        private UpdateRewardUI listener;

        protected CopyBitmapSyncRealm(UpdateRewardUI listener) {
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            realm = Realm.getDefaultInstance();
            final String filename = Rewards.cover_prefix + base_reward_id;
            File myDir = getFilesDir();

            try {
                OutputStream fOut = null;
                File targetFile = new File(myDir + "/"+ Rewards.cover_dir + "/", filename);
                if (!targetFile.getParentFile().exists()) {
                    if (!targetFile.getParentFile().mkdirs()) {
                        Log.d("MKDIR FAILURE", targetFile.getParentFile().toString());
                    }
                }
                if (targetFile.exists()) {
                    targetFile.delete();
                }
                if (targetFile.getParentFile().exists()) {
                    fOut = new FileOutputStream(targetFile);
                    bitmaps[0].compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                }
                Rewards.setRewardPhotoPath(realm, filename+".png", base_reward_id);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "File "+filename+".png Copied from bitmap";
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            listener.updateRewardCover();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
