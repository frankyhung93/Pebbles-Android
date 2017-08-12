package com.example.pebblesappv2;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MusicDashBoard extends BaseACA implements PlayerBarFragment.OnFragmentInteractionListener {

    Realm realm;
    ListView mListView;
    ArrayList<YTTags> albums = new ArrayList<>();
    MusicAlbumAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_dash_board);

        // test code
//        showPlayerBar();

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

        // init listview
        mListView = (ListView) findViewById(R.id.music_albumview);
        RealmQuery<YTTags> query = realm.where(YTTags.class);
        RealmResults<YTTags> albums_rs = query.findAllSorted("tag_order");
        albums = new ArrayList<>();
        for (YTTags album : albums_rs) {
            albums.add(album);
        }
        adapter = new MusicAlbumAdapter(this, albums);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                YTTags album_clicked = adapter.getItem(position);
                Intent intoAlbum = new Intent(getApplicationContext(), AlbumPlayList.class);
                intoAlbum.putExtra(getMyString(R.string.intent_extra_tagId), album_clicked.getId());
                startActivityForResult(intoAlbum, 0);
            }
        });
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
            if (isNetworkAvailable(this.getApplicationContext())) {
                startActivityForResult(
                        new Intent(MusicDashBoard.this, MusicSyncActivity.class),
                        0);
                return true;
            } else {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("No Shit!");
                alert.setMessage("You need Internet Connection BRO!");
                alert.setNegativeButton("Fuck Off", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    // Handle the result once the activity returns a result, display contact
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
//                realm = null;
//
//                Realm.init(this);
//                realm = Realm.getDefaultInstance();
                RealmQuery<YTTags> query = realm.where(YTTags.class);
                RealmResults<YTTags> albums_rs = query.findAllSorted("tag_name");
                albums.clear();
                for (YTTags album : albums_rs) {
                    albums.add(album);
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

//    private void showPlayerBar() {
//        PlayerBarFragment pbfragment = new PlayerBarFragment();
//
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.playerbar_container, pbfragment).commit();
//    }

    // Handle all four types of events passed from the player bar fragment
    public void onCancelButtonClicked() {

    }
    public void onPlayButtonClicked() {

    }
    public void onBackButtonClicked() {

    }
    public void onNextButtonClicked() {

    }
}
