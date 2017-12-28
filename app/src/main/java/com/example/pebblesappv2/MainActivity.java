package com.example.pebblesappv2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class MainActivity extends BaseACA
        implements NavigationView.OnNavigationItemSelectedListener {

    public final static String EXTRA_MESSAGE = "msg_one";
    public final String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    private int rb_index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Current Time", (new Date()).toString());

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.d("MagCurrency INIT", (MagCurrency.initCurrency(realm)?"true":"false"));
        // Update challenges status
        Challenges.updateAllChallengesStatus(realm, Challenges.returnAllPendingProgressingRecurrentChallenges(realm));

        TextView tv_mag_gold = (TextView) findViewById(R.id.tv_mag_gold);
        TextView tv_mag_diamond = (TextView) findViewById(R.id.tv_mag_diamond);
        ImageView rockbalance = (ImageView) findViewById(R.id.rock_balance);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/vintage.ttf");
        tv_mag_gold.setTypeface(custom_font);
        tv_mag_diamond.setTypeface(custom_font);
        tv_mag_gold.setText(MagCurrency.getGold(realm)+"");
        tv_mag_diamond.setText(MagCurrency.getDiamond(realm)+"");

        ImageView sc_journal = (ImageView) findViewById(R.id.shortcut_journal);
        ImageView sc_rewards = (ImageView) findViewById(R.id.shortcut_rewards);
        ImageView sc_challenges = (ImageView) findViewById(R.id.shortcut_challenges);
        ImageView sc_tunes = (ImageView) findViewById(R.id.shortcut_tunes);

        sc_journal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TdlActivity.class);
                startActivity(intent);
            }
        });
        sc_rewards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RewardsList.class);
                startActivity(intent);
            }
        });
        sc_challenges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChallengesList.class);
                startActivity(intent);
            }
        });
        sc_tunes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MusicDashBoard.class);
                startActivity(intent);
            }
        });

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String currentDate = settings.getString("rbDate", "1970-01-01");
        rb_index = settings.getInt("rb_index", 0);
        String[] rb_array = getResources().getStringArray(R.array.rb_array);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (!currentDate.equals(format.format(new Date()))) {
            Random rand = new Random();
            rb_index = rand.nextInt(rb_array.length);

            SharedPreferences.Editor edit = settings.edit();
            edit.putString("rbDate", format.format(new Date()));
            edit.putInt("rb_index", rb_index);
            edit.apply();
        }
        rockbalance.setImageResource(getResources().getIdentifier(rb_array[rb_index], "drawable", this.getPackageName()));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
    }

    @Override
    public void onRestart() {

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
        } else if (id == R.id.rewards_page) {
            Intent intent = new Intent(this, RewardsList.class);
            String message = "Get Rewards!";
            intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);
        } else if (id == R.id.challenges_page) {
            Intent intent = new Intent(this, ChallengesList.class);
            String message = "Never cease from growing";
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
        super.onDestroy();
    }
}
