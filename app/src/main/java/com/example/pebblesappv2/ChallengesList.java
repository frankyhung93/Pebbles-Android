package com.example.pebblesappv2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;

import io.realm.RealmQuery;
import io.realm.RealmResults;

public class ChallengesList extends BaseACA {
    ArrayList<Challenges> challenges = new ArrayList<>();
    ArrayList<String> avail_rwds = new ArrayList<>();
    ArrayAdapter<String> spinner_adp;
    AlertDialog addform;
    RecyclerView.Adapter adapter;
    EditText challenge_name;
    EditText challenge_desc;
    EditText challenge_counter;
    LinearLayout steps_container;
    Button plus_btn;
    View mExclusiveEmptyView;
    Spinner challenge_type;
    Spinner rewards_type_spinner;
    Spinner rewards_spinner;
    LinearLayout rwd_ll;
    EditText prize_gold;
    EditText prize_diamond;
    CheckBox time_limit;
    EditText start_date;
    EditText dead_line;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenges_list);
        // Setting toolbar interface
        Toolbar toolbar = (Toolbar) findViewById(R.id.general_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // update challenges status
        Challenges.updateAllChallengesStatus(realm);

        initViews();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_challenge);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addform.show();
            }
        });

    }

    @Override
    protected void onResume() {
        renewList();
        super.onResume();
    }

    private void initViews(){
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.challenges_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        RealmQuery<Challenges> query = realm.where(Challenges.class);
        RealmResults<Challenges> challenges_rs = query.findAll();
        for (Challenges challenge : challenges_rs) {
            challenges.add(challenge);
        }
        RealmQuery<Rewards> query_rwd = realm.where(Rewards.class).equalTo("status", 1);
        RealmResults<Rewards> rwds_rs = query_rwd.findAll();
        for (Rewards rwd : rwds_rs) {
            avail_rwds.add(rwd.getReward_name());
        }

        adapter = new ChallengesAdapter(challenges, this);
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
                    Intent challenge_intent = new Intent(getApplicationContext(), ChallengeDetail.class);
                    challenge_intent.putExtra(getMyString(R.string.intent_extra_challengeId), challenges.get(position).getId());
                    startActivity(challenge_intent);
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
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.challenge_add_form, null);
        challenge_name = (EditText) dialogLayout.findViewById(R.id.eT_challengename);
        challenge_desc = (EditText) dialogLayout.findViewById(R.id.eT_challengedesc);
        challenge_type = (Spinner) dialogLayout.findViewById(R.id.challenge_type_spinner);
        rewards_type_spinner = (Spinner) dialogLayout.findViewById(R.id.reward_type_spinner);
        rewards_spinner = (Spinner) dialogLayout.findViewById(R.id.rewards_spinner);
        rwd_ll = (LinearLayout) dialogLayout.findViewById(R.id.rwd_ll);
        prize_gold = (EditText) dialogLayout.findViewById(R.id.eT_prize_gold);
        prize_diamond = (EditText) dialogLayout.findViewById(R.id.eT_prize_diamond);
        challenge_counter = (EditText) dialogLayout.findViewById(R.id.eT_challenge_counter);
        steps_container = (LinearLayout) dialogLayout.findViewById(R.id.steps_container);
        plus_btn = (Button) dialogLayout.findViewById(R.id.plus_btn);
        time_limit = (CheckBox) dialogLayout.findViewById(R.id.challenge_ckbox);
        start_date = (EditText) dialogLayout.findViewById(R.id.eT_startdate);
        dead_line = (EditText) dialogLayout.findViewById(R.id.eT_enddate);
        dead_line.setVisibility(View.GONE); // initially not visible

        setDatePicker(start_date, this);
        setDatePicker(dead_line, this);

        spinner_adp = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,avail_rwds);
        rewards_spinner.setAdapter(spinner_adp);

        rewards_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (parent.getItemAtPosition(position).toString()) {
                    case "Listed":
                        if (avail_rwds.size() > 0) {
                            rwd_ll.setVisibility(View.VISIBLE);
                        } else {
                            rwd_ll.setVisibility(View.GONE);
                            Toast.makeText(ChallengesList.this, "No Pending Listed Rewards", Toast.LENGTH_SHORT).show();
                        }
                        prize_gold.setVisibility(View.GONE);
                        prize_diamond.setVisibility(View.GONE);
                        break;
                    case "Gold":
                        rwd_ll.setVisibility(View.GONE);
                        prize_gold.setVisibility(View.VISIBLE);
                        prize_diamond.setVisibility(View.GONE);
                        break;
                    case "Diamond":
                        rwd_ll.setVisibility(View.GONE);
                        prize_gold.setVisibility(View.GONE);
                        prize_diamond.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        challenge_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("SELECTED", parent.getItemAtPosition(position).toString());
                switch (parent.getItemAtPosition(position).toString()) {
                    case "Counter":
                        challenge_counter.setVisibility(View.VISIBLE);
                        steps_container.setVisibility(View.GONE);
                        break;
                    case "Steps":
                        challenge_counter.setVisibility(View.GONE);
                        steps_container.setVisibility(View.VISIBLE);
                        break;
                    case "Single":
                        challenge_counter.setVisibility(View.GONE);
                        steps_container.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        plus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inflateEditRow(null);
                v.setVisibility(View.GONE);
            }
        });

        time_limit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if ( isChecked )
                {
                    dead_line.setVisibility(View.VISIBLE);
                } else {
                    dead_line.setVisibility(View.GONE);
                }
            }
        });

        builder.setView(dialogLayout)
                // Add action buttons
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        ArrayList<String> str_arr = new ArrayList<>();
//                        ArrayList<Integer> int_arr = new ArrayList<>();
                        ArrayList<String> rwd_arr = new ArrayList<>();
                        ArrayList<String> challenge_arr = new ArrayList<>();

                        str_arr.add(challenge_name.getText().toString()); // str - 0
                        str_arr.add(challenge_desc.getText().toString()); // str - 1
                        rwd_arr.add(rewards_type_spinner.getSelectedItem().toString());
                        switch (rewards_type_spinner.getSelectedItem().toString()) {
                            case "Listed":
                                rwd_arr.add(rewards_spinner.getSelectedItem().toString());
                                break;
                            case "Gold":
                                rwd_arr.add(prize_gold.getText().toString());
                                break;
                            case "Diamond":
                                rwd_arr.add(prize_diamond.getText().toString());
                                break;
                        }
                        challenge_arr.add(challenge_type.getSelectedItem().toString());
                        switch (challenge_type.getSelectedItem().toString()) {
                            case "Single":
                                break;
                            case "Counter":
                                challenge_arr.add(challenge_counter.getText().toString());
                                break;
                            case "Steps":
                                int steps_count = steps_container.getChildCount() - 1;
                                if (steps_count > 0) {
                                    for (int i = 0; i < steps_count; i++) {
                                        LinearLayout step_container = (LinearLayout) steps_container.getChildAt(i);
                                        EditText step_name = (EditText) step_container.getChildAt(0);
                                        challenge_arr.add(step_name.getText().toString());
                                        Log.d("STEPPING UP", step_name.getText().toString());
                                    }
                                }
                                break;
                        }
                        str_arr.add(start_date.getText().toString()); // str - 2
                        if (time_limit.isChecked()) {
                            str_arr.add(dead_line.getText().toString()); // str - 3
                        }
                        if (Challenges.addChallenge(realm, str_arr, rwd_arr, challenge_arr) > 0) {
                            renewList();
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
    }

    // Helper for inflating a row
    private void inflateEditRow(String name) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.dynamic_et_row, null);
        final ImageButton deleteButton = (ImageButton) rowView.findViewById(R.id.steps_del_btn);
        final EditText editText = (EditText) rowView.findViewById(R.id.steps_eT);
        if (name != null && !name.isEmpty()) {
            editText.setText(name);
        } else {
            mExclusiveEmptyView = rowView;
            deleteButton.setVisibility(View.INVISIBLE);
        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                steps_container.removeView((View) v.getParent());
            }
        });

        // A TextWatcher to control the visibility of the "Add new" button and
        // handle the exclusive empty view.
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

                // Some visibility logic control here:
                if (s.toString().isEmpty()) {
                    plus_btn.setVisibility(View.GONE);
                    deleteButton.setVisibility(View.INVISIBLE);
                    if (mExclusiveEmptyView != null
                            && mExclusiveEmptyView != rowView) {
                        steps_container.removeView(mExclusiveEmptyView);
                    }
                    mExclusiveEmptyView = rowView;
                } else {
                    if (mExclusiveEmptyView == rowView) {
                        mExclusiveEmptyView = null;
                    }
                    plus_btn.setVisibility(View.VISIBLE);
                    deleteButton.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }
        });

        // Inflate at the end of all rows but before the "Add new" button
        steps_container.addView(rowView, steps_container.getChildCount() - 1);
    }

    private void renewList() {
        challenges.clear();
        RealmQuery<Challenges> query = realm.where(Challenges.class);
        RealmResults<Challenges> challenges_rs = query.findAll();
        for (Challenges challenge : challenges_rs) {
            challenges.add(challenge);
            Log.d("REWARD_DETAIL", challenge.toString());
        }
        adapter.notifyDataSetChanged();

        avail_rwds.clear();
        RealmQuery<Rewards> query_rwd = realm.where(Rewards.class).equalTo("status", 1);
        RealmResults<Rewards> rwds_rs = query_rwd.findAll();
        for (Rewards rwd : rwds_rs) {
            avail_rwds.add(rwd.getReward_name());
        }
        spinner_adp.notifyDataSetChanged();
    }
}
