package com.example.pebblesappv2;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by ChunFaiHung on 2017/2/10.
 */

public class TdlActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    final static int GET_RESULT_TEXT = 0;
    public ArrayList<ToDoItem> tdl_data = new ArrayList<ToDoItem>();
    public PebblesTDLSource tdl_source;
    public TdlArrayAdapter tdl_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tdl);
        // Setting toolbar interface
        Toolbar toolbar = (Toolbar) findViewById(R.id.tdl_toolbar);
        setSupportActionBar(toolbar);
        // Setting nav drawer interface
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.tdl_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        // Setting nav item listener
        NavigationView navigationView = (NavigationView) findViewById(R.id.tdl_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        tdl_data.clear();
        // Opening sql connection and helper
        tdl_source = new PebblesTDLSource(this);
        tdl_source.open();
        // Initializing List Data
        tdl_data = tdl_source.getTDL();

        // Initializing custom adapter
        tdl_adapter = new TdlArrayAdapter(this, R.layout.inflate_td_item, tdl_data);

        ListView tdl_listView = (ListView) findViewById(R.id.tdl_listview);
        tdl_listView.setEmptyView(findViewById(R.id.empty_view_tdl_text));

        // Set adapter for the listView
        tdl_listView.setAdapter(tdl_adapter);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.tdl_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tdl_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_td) {
            startActivityForResult(
                    new Intent(TdlActivity.this, TdlAddNewActivity.class),
                    GET_RESULT_TEXT);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Handle the result once the activity returns a result, display contact
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                ToDoItem newTDItem = tdl_source.CreateTDItem(data.getStringExtra("descInput"),
                                        data.getStringExtra("timeInput"),
                                        data.getStringExtra("dateInput"));
                tdl_data.add(newTDItem);
                tdl_adapter.notifyDataSetChanged();
                Log.d("DEBUG", newTDItem.getToDoDesc());
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.tdl_page) {

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.tdl_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
