package com.example.pebblesappv2;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class ChallengeDetail extends BaseACA {
    private int challenge_id;
    private Challenges challenge;
    private ActionBar actionbar;
    ImageView iv_status;
    TextView tv_status;
    TextView tv_start_date;
    TextView tv_dead_line;
    TextView tv_progress;
    TextView tv_reward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_detail);

        // Get Intent
        Intent intent = getIntent();
        challenge_id = intent.getIntExtra(getMyString(R.string.intent_extra_challengeId), 0);
        Challenges.updateChallengeStatus(realm, challenge_id);
        challenge = Challenges.getChallengeById(realm, challenge_id);

        // Setting toolbar interface
        Toolbar toolbar = (Toolbar) findViewById(R.id.general_toolbar);
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setTitle(challenge.getCha_name());

//        Toast.makeText(this, "This is challenge: "+challenge.getCha_name(), Toast.LENGTH_SHORT).show();
//        Log.d("CHALLENGE", challenge.toString());
//        ArrayList<Challenge_steps> cha_steps = Challenge_steps.findStepsByChallenge(realm, challenge);
//        Log.d("CHALLENGE STEPS", cha_steps.toString());

        // Set up UI from challenge data
        iv_status = (ImageView) findViewById(R.id.challenge_status_pic);
        tv_status = (TextView) findViewById(R.id.challenge_status_cap);
        tv_start_date = (TextView) findViewById(R.id.challenge_start_date);
        tv_dead_line = (TextView) findViewById(R.id.challenge_dead_line);
        tv_progress = (TextView) findViewById(R.id.challenge_progress);
        tv_reward = (TextView) findViewById(R.id.challenge_reward);

        switch (challenge.getStatus()) {
            case Challenges.pending:
                iv_status.setImageDrawable(getDrawable(R.drawable.pending));
                tv_status.setText("Pending");
                break;
            case Challenges.in_progress:
                iv_status.setImageDrawable(getDrawable(R.drawable.progress));
                tv_status.setText("In Progress");
                break;
            case Challenges.completed:
                iv_status.setImageDrawable(getDrawable(R.drawable.completed));
                tv_status.setText("Completed");
                break;
            case Challenges.failed:
                iv_status.setImageDrawable(getDrawable(R.drawable.fail));
                tv_status.setText("Failed");
                break;
        }
        tv_start_date.setText(challenge.getStart_date().toString());
        if (challenge.getTime_limit() == 1) {
            tv_dead_line.setText(challenge.getDeadline().toString());
        } else {
            tv_dead_line.setText("No Time Limit");
        }
        tv_progress.setText(String.format(Locale.ENGLISH,"%d/%d", challenge.getCurr_counter(), challenge.getMax_counter()));
        if (challenge.getCha_prize_gold() > 0) {
            tv_reward.setText(challenge.getCha_prize_gold()+"");
        } else if (challenge.getCha_prize_diamond() > 0) {
            tv_reward.setText(challenge.getCha_prize_diamond()+"");
        } else {
            tv_reward.setText(challenge.getLinked_rwd().getReward_name());
        }

    }
}
