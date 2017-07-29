package com.example.pebblesappv2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MusicDashBoard extends AppCompatActivity {

    Realm realm;
    TextView textArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_dash_board);

        // Setup realm
        Realm.init(this);
        realm = Realm.getDefaultInstance();

        // Setting toolbar interface
        Toolbar toolbar = (Toolbar) findViewById(R.id.general_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // init views
        textArea = (TextView) findViewById(R.id.textArea);
        loadAreaInfo();
    }

    void loadAreaInfo() {
        textArea.setText("");
        RealmQuery<YTDownloads> query = realm.where(YTDownloads.class);
        RealmResults<YTDownloads> rs1 = query.findAll();

        for (YTDownloads result : rs1) {
            textArea.append(result.getVideo_title() + "\n");
            for (YTTags tag : result.getVideo_tags()) {
                textArea.append("      " + tag.getTag_name() + "\n");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.music_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_music_sync) {
            startActivityForResult(
                    new Intent(MusicDashBoard.this, MusicSyncActivity.class),
                    0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Handle the result once the activity returns a result, display contact
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                loadAreaInfo();
            }
        }
    }
}
