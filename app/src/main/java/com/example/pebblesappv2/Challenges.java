package com.example.pebblesappv2;

import android.util.Log;

import java.lang.reflect.Array;
import java.nio.channels.Channel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by ChunFaiHung on 2017/10/22.
 */

public class Challenges extends RealmObject {
    public static final int type_simple = 1;
    public static final int type_counter = 2;
    public static final int type_steps = 3;

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

    @Ignore
    private static boolean updateRwdFlag = false;
    @Ignore
    private static boolean updatePrizeFlag = false;

    public int getId() {
        return id;
    }

    public static Challenges getChallengeByRwd(Realm rm, Rewards rwd) {
        RealmQuery<Challenges> query = rm.where(Challenges.class).equalTo("linked_rwd.id", rwd.getId());
        return query.findFirst();
    }

    public static Challenges getChallengeById(Realm rm, int id) {
        RealmQuery<Challenges> query = rm.where(Challenges.class).equalTo("id", id);
        Challenges clg = query.findFirst();
        return clg;
    }

    public String findChallengeEndsIn() {
        if (this.getDeadline() != null) {
            Date now = new Date();
            long timediff = this.getDeadline().getTime() - now.getTime();
            int indays = Math.abs((int) Math.ceil((double)timediff/(86400 * 1000))) + 1;
            return indays+ " Day";
        } else {
            return "Forever";
        }
    }

    public static ArrayList<Challenges> returnAllPendingProgressingChallenges(Realm rm) {
        ArrayList<Challenges> chas = new ArrayList<>();
        Integer[] stillOKstatus = {pending, in_progress};
        RealmQuery<Challenges> query = rm.where(Challenges.class).in("status", stillOKstatus);
        RealmResults<Challenges> rs = query.findAll();
        for (Challenges chal : rs) {
            chas.add(chal);
        }
        return chas;
    }

    public static void updateAllChallengesStatus(Realm rm, ArrayList<Challenges> chas) {
        for (Challenges cha : chas) {
            updateChallengeStatus(rm, cha.getId());
        }
    }

    private static void checkUpdateRewardStatus(Realm rm, Challenges cha) {
        if (cha.getLinked_rwd() != null) {
            Rewards.updateRewardStatus(rm, cha.getLinked_rwd(), cha.getStatus());
        }
    }

    private static void checkGetPrize(Realm rm, Challenges cha) {
        if (cha.getCha_prize_gold() > 0) {
            MagCurrency.addCurrency(rm, 1, cha.getCha_prize_gold());
        } else if (cha.getCha_prize_diamond() > 0) {
            MagCurrency.addCurrency(rm, 2, cha.getCha_prize_diamond());
        }
    }

    public static void updateChallengeStatus(Realm rm, final int challenge_id) {
        try {
            rm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                Challenges cha = getChallengeById(realm, challenge_id);
                Long now = System.currentTimeMillis();

                if (cha.getStatus() == pending) {
                    if (cha.getStart_date().getTime() <= now && (cha.getTime_limit() == 0 || (new Date(cha.getDeadline().getTime() + (1000 * 60 * 60 * 24))).getTime() >= now)) {
                        cha.setStatus(in_progress);
                        updateRwdFlag = true;
                    }
                }

                if (cha.getStatus() == in_progress) {
                    if (cha.getStart_date().getTime() <= now && (cha.getTime_limit() == 0 || (new Date(cha.getDeadline().getTime() + (1000 * 60 * 60 * 24))).getTime() >= now)) { // within timeframe
                        if (cha.getMax_counter() == cha.getCurr_counter()) {
                            cha.setStatus(completed);
                            updateRwdFlag = true;
                            updatePrizeFlag = true;
                        }
                    }
                    if (cha.getTime_limit() == 1 && (new Date(cha.getDeadline().getTime() + (1000 * 60 * 60 * 24))).getTime() < now) { // already expired
                        cha.setStatus(failed);
                        updateRwdFlag = true;
                    }
                }
                }
            });

            if (updateRwdFlag) {
                updateRwdFlag = false;
                Challenges.checkUpdateRewardStatus(rm, getChallengeById(rm, challenge_id));
            }
            if (updatePrizeFlag) {
                updatePrizeFlag = false;
                Challenges.checkGetPrize(rm, getChallengeById(rm, challenge_id));
            }
        } catch (Exception e) {
            Log.d("CUMON UPDATE STATUS", e.toString());
        }
    }

    public static void scoreThisChallenge(Realm rm, final Challenges challenge) {
        try {
            rm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    switch (challenge.getType()) {
                        case type_simple:
                        case type_counter:
                            int current_counter = challenge.getCurr_counter();
                            challenge.setCurr_counter(current_counter + 1);
                            break;
                        case type_steps:
                            int current_counter_steps = challenge.getCurr_counter();
                            ArrayList<Challenge_steps> steps = Challenge_steps.findStepsByChallenge(realm, challenge);
                            for (int i = 0; i < steps.size(); i++) {
                                if (current_counter_steps == i) {
                                    steps.get(i).setStatus(Challenge_steps.done);
                                    break;
                                }
                            }
                            challenge.setCurr_counter(current_counter_steps + 1);
                            break;
                    }
                }
            });
            updateChallengeStatus(rm, challenge.getId());
        } catch (Exception e) {
            Log.d("CUMON SCORE CHALLENGE", e.toString());
        }
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
                    clg.setCha_prize_gold(0);
                    clg.setCha_prize_diamond(0);
                    break;
                case "Gold":
                    clg.setCha_prize_gold(Integer.parseInt(rwd_arr.get(1)));
                    clg.setLinked_rwd(null);
                    clg.setCha_prize_diamond(0);
                    break;
                case "Diamond":
                    clg.setCha_prize_diamond(Integer.parseInt(rwd_arr.get(1)));
                    clg.setLinked_rwd(null);
                    clg.setCha_prize_gold(0);
                    break;
            }

            clg.setCurr_counter(0);
            switch (challenge_arr.get(0)) { // challenge type
                case "Single":
                    clg.setType(type_simple);
                    clg.setMax_counter(1);
                    break;
                case "Counter":
                    clg.setType(type_counter);
                    clg.setMax_counter(Integer.parseInt(challenge_arr.get(1)));
                    break;
                case "Steps":
                    clg.setType(type_steps);
                    challenge_arr.remove(0);
                    if (challenge_arr.size() > 0) {
                        have_steps = true;
                        clg.setMax_counter(challenge_arr.size());
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
            updateChallengeStatus(rm, challenge_id);
            return challenge_id;
        } catch (Exception e) {
            Log.d("CUMON ADD CHALLENGE", e.toString());
            return 0;
        }
    }

    public static boolean editChallenge(Realm rm, ArrayList<String> str_arr, ArrayList<String> rwd_arr, ArrayList<String> challenge_arr, Challenges clg) {
        try {
            rm.beginTransaction();
            boolean have_steps = false;

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
                clg.setDeadline(null);
                clg.setTime_limit(0);
            }
            switch (rwd_arr.get(0)) { // reward type
                case "Listed":
                    clg.setLinked_rwd(Rewards.getPendingRewardByName(rm, rwd_arr.get(1)));
                    clg.setCha_prize_gold(0);
                    clg.setCha_prize_diamond(0);
                    break;
                case "Gold":
                    clg.setCha_prize_gold(Integer.parseInt(rwd_arr.get(1)));
                    clg.setLinked_rwd(null);
                    clg.setCha_prize_diamond(0);
                    break;
                case "Diamond":
                    clg.setCha_prize_diamond(Integer.parseInt(rwd_arr.get(1)));
                    clg.setLinked_rwd(null);
                    clg.setCha_prize_gold(0);
                    break;
            }

            clg.setCurr_counter(0);
            switch (challenge_arr.get(0)) { // challenge type
                case "Single":
                    clg.setType(type_simple);
                    clg.setMax_counter(1);
                    Challenge_steps.deleteStepsByChallenge(rm, clg);
                    break;
                case "Counter":
                    clg.setType(type_counter);
                    clg.setMax_counter(Integer.parseInt(challenge_arr.get(1)));
                    Challenge_steps.deleteStepsByChallenge(rm, clg);
                    break;
                case "Steps":
                    clg.setType(type_steps);
                    challenge_arr.remove(0);
                    if (challenge_arr.size() > 0) {
                        have_steps = true;
                        clg.setMax_counter(challenge_arr.size());
                    }
                    break;
            }
            clg.setStatus(pending);
            rm.commitTransaction();

            // Now add challenge_steps
            if (have_steps) {
                Challenge_steps.deleteStepsByChallenge(rm, clg);

                rm.beginTransaction();

                if (Challenge_steps.getNextId(rm) == 0) {
                    clg.deleteFromRealm();
                    return false;
                }
                int step_id = Challenge_steps.getNextId(rm);
                for (String step_name : challenge_arr) {
                    Challenge_steps step = rm.createObject(Challenge_steps.class, step_id);
                    step.setChallenge(clg);
                    step.setStatus(0);
                    step.setStep_name(step_name);
                    step_id++;
                }

                rm.commitTransaction();
            }
            updateChallengeStatus(rm, clg.getId());
            return true;
        } catch (Exception e) {
            Log.d("CUMON EDIT CHALLENGE", e.toString());
            return false;
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
