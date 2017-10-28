package com.example.pebblesappv2;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class RewardsList extends BaseACA {
    ArrayList<Rewards> rewards = new ArrayList<>();
    AlertDialog addform;
    RecyclerView.Adapter adapter = null;
    ImageButton choosePhoto = null;
    Bitmap bit_cover_photo = null;
    private static final int SELECT_PICTURE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards_list);
        // Setting toolbar interface
        Toolbar toolbar = (Toolbar) findViewById(R.id.general_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_reward);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addform.show();
                EditText eT_rwd_gold = (EditText) addform.findViewById(R.id.eT_gold);
                EditText eT_rwd_diamond = (EditText) addform.findViewById(R.id.eT_diamond);
                eT_rwd_gold.setText("");
                eT_rwd_diamond.setText("");
                choosePhoto = (ImageButton) addform.findViewById(R.id.choose_photo);
                choosePhoto.setBackgroundResource(android.R.drawable.btn_default);
                choosePhoto.setImageDrawable(getDrawable(R.drawable.add));
            }
        });

    }

    @Override
    protected void onResume() {
        renewList();
        super.onResume();
    }

    private void initViews(){
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.rewards_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        RealmQuery<Rewards> query = realm.where(Rewards.class);
        RealmResults<Rewards> rewards_rs = query.findAll();
        for (Rewards reward : rewards_rs) {
            rewards.add(reward);
        }

        adapter = new RewardsAdapter(rewards, this);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {

                @Override public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

            });
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if(child != null && gestureDetector.onTouchEvent(e)) {
                    int position = rv.getChildAdapterPosition(child);
                    Intent reward_intent = new Intent(getApplicationContext(), RewardDetail.class);
                    reward_intent.putExtra(getMyString(R.string.intent_extra_rewardId), rewards.get(position).getId());
                    startActivity(reward_intent);
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View dialogLayout = inflater.inflate(R.layout.reward_add_form, null);
        builder.setView(dialogLayout)
                // Add action buttons
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText eT_rwd_name = (EditText) addform.findViewById(R.id.eT_rewardname);
                        EditText eT_rwd_desc = (EditText) addform.findViewById(R.id.eT_rewarddesc);
                        EditText eT_rwd_gold = (EditText) addform.findViewById(R.id.eT_gold);
                        EditText eT_rwd_diamond = (EditText) addform.findViewById(R.id.eT_diamond);
                        ArrayList<String> str_arr = new ArrayList<>();
                        ArrayList<Integer> int_arr = new ArrayList<>();
                        str_arr.add(eT_rwd_name.getText().toString());
                        str_arr.add(eT_rwd_desc.getText().toString());
                        int_arr.add(Integer.parseInt(eT_rwd_gold.getText().toString()));
                        int_arr.add(Integer.parseInt(eT_rwd_diamond.getText().toString()));
                        base_reward_id = Rewards.addReward(realm, str_arr, int_arr);
                        if (base_reward_id > 0) {
                            renewList();
                        }
                        if (bit_cover_photo != null) {
                            new CopyBitmapSyncRealm(new UpdateRewardUI() {
                                @Override
                                public void updateRewardCover() {
                                    // nothing done here
                                }
                            }).execute(bit_cover_photo);
                        }
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        addform = builder.create();

        // Set onClick listener for choose_photo ImageButton
        choosePhoto = (ImageButton) dialogLayout.findViewById(R.id.choose_photo);
        choosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_PICTURE);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                try {
                    InputStream is = getContentResolver().openInputStream(selectedImageUri);
                    bit_cover_photo = BitmapFactory.decodeStream(is);
                    bit_cover_photo = squareCrop(bit_cover_photo);
                    if (choosePhoto != null) {
                        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bit_cover_photo);
                        choosePhoto.setBackground(bitmapDrawable);
                        choosePhoto.setImageDrawable(null);
                    }
                    is.close();
                } catch (Exception e) {
                    Log.d("CUMON FILE", e.toString());
                }
            }
        }
    }

    private void renewList() {
        rewards.clear();
        RealmQuery<Rewards> query = realm.where(Rewards.class);
        RealmResults<Rewards> rewards_rs = query.findAll();
        for (Rewards reward : rewards_rs) {
            rewards.add(reward);
            Log.d("REWARD_DETAIL", reward.toString());
        }
        adapter.notifyDataSetChanged();
    }
}
