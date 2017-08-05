package com.example.pebblesappv2;

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
}
