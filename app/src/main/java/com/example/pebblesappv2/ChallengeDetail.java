package com.example.pebblesappv2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import io.realm.RealmQuery;
import io.realm.RealmResults;

public class ChallengeDetail extends BaseACA {
    private int challenge_id;
    private Challenges challenge;
    private ActionBar actionbar;
    private ArrayList<Challenge_steps> challenge_steps;
    ImageView iv_status;
    TextView tv_status;
    TextView tv_description;
    TextView tv_start_date;
    TextView tv_dead_line;
    TextView tv_progress;
    TextView tv_reward;
    LinearLayout steps_ll;
    Button action_btn;

    // Edit Form needed variables
    ArrayList<String> avail_rwds = new ArrayList<>();
    ArrayAdapter<String> spinner_adp;
    AlertDialog editform;
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
        setContentView(R.layout.activity_challenge_detail);

        // Get Intent
        Intent intent = getIntent();
        challenge_id = intent.getIntExtra(getMyString(R.string.intent_extra_challengeId), 0);
        Challenges.updateChallengeStatus(realm, challenge_id);
        challenge = Challenges.getChallengeById(realm, challenge_id);
        challenge_steps = Challenge_steps.findStepsByChallenge(realm, challenge);

        // Setting toolbar interface
        Toolbar toolbar = (Toolbar) findViewById(R.id.general_toolbar);
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        // Set up UI from challenge data
        iv_status = (ImageView) findViewById(R.id.challenge_status_pic);
        tv_status = (TextView) findViewById(R.id.challenge_status_cap);
        tv_description = (TextView) findViewById(R.id.challenge_description);
        tv_start_date = (TextView) findViewById(R.id.challenge_start_date);
        tv_dead_line = (TextView) findViewById(R.id.challenge_dead_line);
        tv_progress = (TextView) findViewById(R.id.challenge_progress);
        tv_reward = (TextView) findViewById(R.id.challenge_reward);
        steps_ll = (LinearLayout) findViewById(R.id.steps_ll);
        action_btn = (Button) findViewById(R.id.action_btn);

        fillUIData();

        // Set up Edit form UI
        initEditForm();

        action_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (challenge.getStatus() == Challenges.pending || challenge.getStatus() == Challenges.failed) {
                    editform.show();
                } else if (challenge.getStatus() ==  Challenges.in_progress) {
                    new AlertDialog.Builder(ChallengeDetail.this)
                            .setTitle("Score Operation")
                            .setMessage("Are you really sure you scored this?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Challenges.scoreThisChallenge(realm, challenge);
                                    dialog.cancel();
                                    renewUI();
                                }})
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.cancel();
                                }}).show();
                } else {
                    // do nothing
                }
            }
        });
    }

    private void addStepRow(Challenge_steps step) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.step_row, null);
        TextView step_name = (TextView) rowView.findViewById(R.id.step_textview);
        ImageView done_pic = (ImageView) rowView.findViewById(R.id.done_pic);

        step_name.setText(step.getStep_name());
        if (step.getStatus() == Challenge_steps.done) {
            done_pic.setImageDrawable(getDrawable(R.drawable.tick));
        } else {
            done_pic.setImageDrawable(getDrawable(R.drawable.hollow));
        }
        steps_ll.addView(rowView);
    }

    private void initEditForm() {
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

        RealmQuery<Rewards> query_rwd = realm.where(Rewards.class).equalTo("status", Rewards.pending);
        RealmResults<Rewards> rwds_rs = query_rwd.findAll();
        for (Rewards rwd : rwds_rs) {
            avail_rwds.add(rwd.getReward_name());
        }
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
                            Toast.makeText(ChallengeDetail.this, "No Pending Listed Rewards", Toast.LENGTH_SHORT).show();
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
                .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ArrayList<String> str_arr = new ArrayList<>();
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
                        if (Challenges.editChallenge(realm, str_arr, rwd_arr, challenge_arr, challenge)) {
                            renewUI();
                        }
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        editform = builder.create();


        // Fill up values
        challenge_name.setText(challenge.getCha_name());
        challenge_desc.setText(challenge.getCha_desc());
        switch (challenge.getType()) {
            case Challenges.type_simple:
                challenge_type.setSelection(2);
                break;
            case Challenges.type_counter:
                challenge_type.setSelection(0);
                break;
            case Challenges.type_steps:
                challenge_type.setSelection(1);
                for (Challenge_steps step_name : challenge_steps) {
                    inflateEditRow(step_name.getStep_name());
                }
                break;
        }
        if (challenge.getLinked_rwd() != null) {
            rewards_type_spinner.setSelection(0);
            for (int i = 0; i < avail_rwds.size(); i++) {
                if (avail_rwds.get(i) == challenge.getLinked_rwd().getReward_name()) {
                    rewards_spinner.setSelection(i);
                }
            }
        } else if (challenge.getCha_prize_gold() > 0) {
            rewards_type_spinner.setSelection(1);
            prize_gold.setText(challenge.getCha_prize_gold()+"");
        } else {
            rewards_type_spinner.setSelection(2);
            prize_diamond.setText(challenge.getCha_prize_diamond()+"");
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        start_date.setText(format.format(challenge.getStart_date()));
        if (challenge.getTime_limit() == 1) {
            time_limit.setChecked(true);
            dead_line.setText(format.format(challenge.getDeadline()));
        }
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

    private void fillUIData() {
        actionbar.setTitle(challenge.getCha_name());

        if (challenge.getType() == Challenges.type_steps) {
            steps_ll.setVisibility(View.VISIBLE);
            int steps_child = steps_ll.getChildCount();
            if (steps_child > 1) {
                for (int i = (steps_child-1); i > 0; i--) {
                    steps_ll.removeViewAt(i);
                }
            }
            for (int i = 0; i < challenge_steps.size(); i++) {
                addStepRow(challenge_steps.get(i));
            }

        } else {
            steps_ll.setVisibility(View.GONE);
        }

        switch (challenge.getStatus()) {
            case Challenges.pending:
                iv_status.setImageDrawable(getDrawable(R.drawable.pending));
                tv_status.setText("Pending");
                action_btn.setText("Edit");
                break;
            case Challenges.in_progress:
                iv_status.setImageDrawable(getDrawable(R.drawable.progress));
                tv_status.setText("In Progress");
                action_btn.setText("Score");
                break;
            case Challenges.completed:
                iv_status.setImageDrawable(getDrawable(R.drawable.completed));
                tv_status.setText("Completed");
                Log.d("RENEW UI", "action button should then disappear");
                action_btn.setVisibility(View.GONE);
                break;
            case Challenges.failed:
                iv_status.setImageDrawable(getDrawable(R.drawable.fail));
                tv_status.setText("Failed");
                action_btn.setText("Edit");
                break;
        }
        tv_description.setText(challenge.getCha_desc());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        tv_start_date.setText(format.format(challenge.getStart_date()));
        if (challenge.getTime_limit() == 1) {
            tv_dead_line.setText(format.format(challenge.getDeadline()));
        } else {
            tv_dead_line.setText("No Time Limit");
        }
        tv_progress.setText(String.format(Locale.ENGLISH,"%d/%d", challenge.getCurr_counter(), challenge.getMax_counter()));
        if (challenge.getCha_prize_gold() > 0) {
            tv_reward.setText(challenge.getCha_prize_gold()+" Gold");
        } else if (challenge.getCha_prize_diamond() > 0) {
            tv_reward.setText(challenge.getCha_prize_diamond()+" Diamond");
        } else {
            tv_reward.setText(challenge.getLinked_rwd().getReward_name());
        }
    }

    private void renewUI() {
        challenge = Challenges.getChallengeById(realm, challenge_id);
        challenge_steps = Challenge_steps.findStepsByChallenge(realm, challenge);

        fillUIData();
    }
}
