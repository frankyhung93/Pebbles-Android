package com.example.pebblesappv2;

import android.app.Application;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by ChunFaiHung on 2017/12/28.
 */

public class PebblesApplication extends Application {

    @Override
    public void onCreate() {
        // Set Realm Configuration
        Log.d("APP INIT", "Going to set realm Configuration");
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(3) // Must be bumped when the schema changes
                .migration(new MyMigration()) // Migration to run
                .build();
        Realm.setDefaultConfiguration(config);

        super.onCreate();
    }
}
