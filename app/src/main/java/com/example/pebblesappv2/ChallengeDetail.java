package com.example.pebblesappv2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class ChallengeDetail extends BaseACA {
    private int challenge_id;
    private Challenges challenge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_detail);

        // Get Intent
        Intent intent = getIntent();
        challenge_id = intent.getIntExtra(getMyString(R.string.intent_extra_challengeId), 0);
        challenge = Challenges.getChallengeById(realm, challenge_id);

        Toast.makeText(this, "This is challenge: "+challenge.getCha_name(), Toast.LENGTH_SHORT).show();
        Log.d("CHALLENGE", challenge.toString());
//        Log.d("CHALLENGE REWARD", challenge.getLinked_rwd().toString());
        ArrayList<Challenge_steps> cha_steps = Challenge_steps.findStepsByChallenge(realm, challenge);
        Log.d("CHALLENGE STEPS", cha_steps.toString());
    }
}
