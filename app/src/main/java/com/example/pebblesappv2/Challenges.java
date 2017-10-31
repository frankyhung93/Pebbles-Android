package com.example.pebblesappv2;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by ChunFaiHung on 2017/10/22.
 */

public class Challenges extends RealmObject {
    private static final int type_simple = 1;
    private static final int type_counter = 2;
    private static final int type_steps = 3;

    public static final int pending = 1;
    public static final int in_progress = 2;
    public static final int completed = 3;
    public static final int failed = 4;

    @PrimaryKey
    private int id;
    @Required
    private String cha_name;
    @Required
    private String cha_desc;

    private int type;
    private int max_counter;
    private int curr_counter;
    private Date start_date;
    private int time_limit;
    private Date deadline;
    private Rewards linked_rwd;
    private int cha_prize_gold;
    private int cha_prize_diamond;
    private int status;

    public int getId() {
        return id;
    }

    public static Challenges getChallengeById(Realm rm, int id) {
        RealmQuery<Challenges> query = rm.where(Challenges.class).equalTo("id", id);
        Challenges clg = query.findFirst();
        return clg;
    }

    public static int addChallenge(Realm rm, ArrayList<String> str_arr, ArrayList<String> rwd_arr, ArrayList<String> challenge_arr) {
        try {
            rm.beginTransaction();
            int challenge_id;
            boolean have_steps = false;
            challenge_id = getNextId(rm);
            if (challenge_id == 0) {
                return 0;
            }

            Challenges clg = rm.createObject(Challenges.class, getNextId(rm));
            clg.setCha_name(str_arr.get(0));
            clg.setCha_desc(str_arr.get(1));
            Date start_date = null;
            Date end_date = null;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                start_date = format.parse(str_arr.get(2));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            clg.setStart_date(start_date);
            if (str_arr.size() > 3) { // have deadline
                try {
                    end_date = format.parse(str_arr.get(3));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                clg.setTime_limit(1);
                clg.setDeadline(end_date);
            } else {
                clg.setTime_limit(0);
            }
            switch (rwd_arr.get(0)) { // reward type
                case "Listed":
                    clg.setLinked_rwd(Rewards.getPendingRewardByName(rm, rwd_arr.get(1)));
                    break;
                case "Gold":
                    clg.setCha_prize_gold(Integer.parseInt(rwd_arr.get(1)));
                    break;
                case "Diamond":
                    clg.setCha_prize_diamond(Integer.parseInt(rwd_arr.get(1)));
                    break;
            }
            switch (challenge_arr.get(0)) { // challenge type
                case "Single":
                    clg.setType(type_simple);
                    break;
                case "Counter":
                    clg.setType(type_counter);
                    clg.setMax_counter(Integer.parseInt(challenge_arr.get(1)));
                    clg.setCurr_counter(0);
                    break;
                case "Steps":
                    clg.setType(type_steps);
                    challenge_arr.remove(0);
                    if (challenge_arr.size() > 0) {
                        have_steps = true;
                    }
                    break;
            }
            clg.setStatus(pending);
            rm.commitTransaction();

            // Now add challenge_steps
            if (have_steps) {
                rm.beginTransaction();

                Challenges challenge = getChallengeById(rm, challenge_id);
                if (Challenge_steps.getNextId(rm) == 0) {
                    challenge.deleteFromRealm();
                    return 0;
                }
                int step_id = Challenge_steps.getNextId(rm);
                for (String step_name : challenge_arr) {
                    Challenge_steps step = rm.createObject(Challenge_steps.class, step_id);
                    step.setChallenge(challenge);
                    step.setStatus(0);
                    step.setStep_name(step_name);
                    step_id++;
                }

                rm.commitTransaction();
            }
            return challenge_id;
        } catch (Exception e) {
            Log.d("CUMON ADD CHALLENGE", e.toString());
            return 0;
        }
    }

    public static int getNextId(Realm rm) {
        try {
            Number number = rm.where(Challenges.class).max("id");
            if (number != null) {
                return number.intValue() + 1;
            } else {
                return 1;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.d("CUMON CHALLENGES", "Cannot set the next id..."+e.toString());
            return 0;
        }
    }

    public String getCha_name() {
        return cha_name;
    }

    public void setCha_name(String cha_name) {
        this.cha_name = cha_name;
    }

    public String getCha_desc() {
        return cha_desc;
    }

    public void setCha_desc(String cha_desc) {
        this.cha_desc = cha_desc;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMax_counter() {
        return max_counter;
    }

    public void setMax_counter(int max_counter) {
        this.max_counter = max_counter;
    }

    public int getCurr_counter() {
        return curr_counter;
    }

    public void setCurr_counter(int curr_counter) {
        this.curr_counter = curr_counter;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public int getTime_limit() {
        return time_limit;
    }

    public void setTime_limit(int time_limit) {
        this.time_limit = time_limit;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Rewards getLinked_rwd() {
        return linked_rwd;
    }

    public void setLinked_rwd(Rewards linked_rwd) {
        this.linked_rwd = linked_rwd;
    }

    public int getCha_prize_gold() {
        return cha_prize_gold;
    }

    public void setCha_prize_gold(int cha_prize_gold) {
        this.cha_prize_gold = cha_prize_gold;
    }

    public int getCha_prize_diamond() {
        return cha_prize_diamond;
    }

    public void setCha_prize_diamond(int cha_prize_diamond) {
        this.cha_prize_diamond = cha_prize_diamond;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
