package com.example.pebblesappv2;

import android.util.Log;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

/**
 * Created by ChunFaiHung on 2017/10/22.
 */

public class Challenge_steps extends RealmObject {
    @PrimaryKey
    private int id;
    private String step_name;
    private Challenges challenge;
    private int status;

    public int getId() {
        return id;
    }

    public static int getNextId(Realm rm) {
        try {
            Number number = rm.where(Challenge_steps.class).max("id");
            if (number != null) {
                return number.intValue() + 1;
            } else {
                return 1;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.d("CUMON CHALLENGE_STEPS", "Cannot set the next id..."+e.toString());
            return 0;
        }
    }

    public static ArrayList<Challenge_steps> findStepsByChallenge(Realm rm, Challenges challenge) {
        RealmQuery<Challenge_steps> query = rm.where(Challenge_steps.class).equalTo("challenge.id", challenge.getId());
        RealmResults<Challenge_steps> rs = query.findAll();
        ArrayList<Challenge_steps> cha_steps_arr = new ArrayList<>();
        for (Challenge_steps step : rs) {
            cha_steps_arr.add(step);
        }
        return cha_steps_arr;
    }

    public String getStep_name() {
        return step_name;
    }

    public void setStep_name(String step_name) {
        this.step_name = step_name;
    }

    public Challenges getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenges challenge) {
        this.challenge = challenge;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
