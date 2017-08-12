package com.example.pebblesappv2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by ChunFaiHung on 2017/7/30.
 */

public class BaseACA extends AppCompatActivity {

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
}
