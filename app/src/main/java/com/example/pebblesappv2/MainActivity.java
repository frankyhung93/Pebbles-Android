package com.example.pebblesappv2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends BaseACA
        implements NavigationView.OnNavigationItemSelectedListener {

    public final static String EXTRA_MESSAGE = "msg_one";
    public ArrayList<RoutineItem> routine_data = new ArrayList<>();
    public PebblesTDLSource rt_source;
    public RoutinesGridAdapter rt_adapter;


    public final String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Test Reset code
//        SharedPreferences wkdayPref = this.getSharedPreferences(this.getString(R.string.WeekPreferenceKey), Context.MODE_PRIVATE);
//        SharedPreferences.Editor prefEditor = wkdayPref.edit();
//        prefEditor.putString("Saturday", "");
//        prefEditor.commit();
        //

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        rt_source = new PebblesTDLSource(this);
        rt_source.open();

        initRoutineData();

        // Set Gridview for today !!
        GridView gridview = (GridView) findViewById(R.id.main_gridview);
        rt_adapter = new RoutinesGridAdapter(this, routine_data);
        gridview.setAdapter(rt_adapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("DEBUG", "THIS ICON ID = "+routine_data.get(i).getRtId());
            }
        });
    }

    public void initRoutineData() {
        // Fetch Pref for today
        Calendar c = Calendar.getInstance();
        Date today = new Date();
        Log.d("TODAY", today.toString());
        c.setTime(today);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        dayOfWeek -= 2; // Suit to our string positioning
        if (dayOfWeek == -1) { // That means original DAY_OF_WEEK is Sunday (e.g. integer: 1)
            dayOfWeek = 6;
        }
        SharedPreferences wkdayPref = this.getSharedPreferences(this.getString(R.string.WeekPreferenceKey), Context.MODE_PRIVATE);
        String iconIdList = wkdayPref.getString(daysOfWeek[dayOfWeek], "0");

        Log.d("DEBUG", "TODAY's PREF: "+iconIdList);

        routine_data.clear();
        routine_data.addAll(rt_source.getRoutinesByIdList(iconIdList));

        Log.d("DEBUG", "SIZE OF RT_DATA:"+routine_data.size());
    }

    @Override
    public void onRestart() {
        rt_source = new PebblesTDLSource(this);
        rt_source.open();

        initRoutineData();

        rt_adapter.notifyDataSetChanged();

        super.onRestart();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.tdl_page) {
            Intent intent = new Intent(this, TdlActivity.class);
            String message = "TDL So Many";
            intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);
        } else if (id == R.id.routines_page) {
            Intent intent = new Intent(this, RoutinesActivity.class);
            String message = "Grids So Many";
            intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);
        } else if (id == R.id.weekly_rt_page) {
            Intent intent = new Intent(this, WeeklyRtActivity.class);
            String message = "7 days";
            intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);
        } else if (id == R.id.my_music) {
            Intent intent = new Intent(this, MusicDashBoard.class);
            String message = "Testing Commence";
            intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onDestroy() {
        rt_source.close();
        super.onDestroy();
    }
}
