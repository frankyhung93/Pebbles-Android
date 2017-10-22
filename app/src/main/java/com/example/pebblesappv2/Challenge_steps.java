package com.example.pebblesappv2;

import android.util.Log;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 * Created by ChunFaiHung on 2017/10/22.
 */

public class Challenge_steps extends RealmObject {
    private int id;
    private String step_name;
    private Challenges challenge;
    private int status;

    public int getId() {
        return id;
    }

    public boolean setNextId(Realm rm) {
        try {
            Number number = rm.where(this.getClass()).max("id");
            if (number != null) {
                this.id = number.intValue() + 1;
            } else {
                this.id = 1;
            }
            return true;
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.d("CUMON STEPS", "Cannot set the next id..."+e.toString());
            return false;
        }
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
