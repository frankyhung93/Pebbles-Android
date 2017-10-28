package com.example.pebblesappv2;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by ChunFaiHung on 2017/10/21.
 */

public class Rewards extends RealmObject {
    public static final int pending = 1;
    public static final int targeted = 2;
    public static final int redeemable = 3;
    public static final int redeemed = 4;
    public static final String cover_dir = "reward_cover";
    public static final String cover_prefix = "reward_cover_";

    @PrimaryKey
    private int id;
    @Required
    private String reward_name;
    @Required
    private String reward_desc;
    @Required
    private Date want_day;
    private Date get_day;
    private String photo_path;
    private int mag_gold;
    private int mag_diamond;
    private int status;

    public static Rewards getRewardById(Realm rm, int id) {
        RealmQuery<Rewards> q = rm.where(Rewards.class).equalTo("id", id);
        Rewards rwd = q.findFirst();
        return rwd;
    }

    public static void setRewardPhotoPath(Realm rm, final String filename, final int id) {
        rm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Rewards target_rwd = getRewardById(realm, id);
                target_rwd.setPhoto_path(filename);
            }
        });
    }

    public static int addReward(Realm rm, ArrayList<String> str_arr, ArrayList<Integer> int_arr) {
        try {
            Log.d("Reward Strings", str_arr.toString());
            Log.d("Reward Integers", int_arr.toString());
            int reward_id;
            rm.beginTransaction();
            reward_id = getNextId(rm);
            if (reward_id==0) {
                return 0;
            }
            Rewards rwd = rm.createObject(Rewards.class, getNextId(rm));
            rwd.setReward_name(str_arr.get(0));
            rwd.setReward_desc(str_arr.get(1));
            rwd.setMag_gold(int_arr.get(0));
            rwd.setMag_diamond(int_arr.get(1));
            rwd.setWant_day(new Date());
            rwd.setStatus(pending);
//            rwd.setNextId(rm);
            rm.commitTransaction();
            return reward_id;
        } catch (Exception e) {
            Log.d("CUMON REWARDS", "Cannot add Reward..."+e.toString());
            return 0;
        }
    }

    public static void editReward(Realm rm, final ArrayList<String> str_arr, final ArrayList<Integer> int_arr, final int id) {
        try {
            rm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Rewards target_rwd = getRewardById(realm, id);
                    target_rwd.setReward_name(str_arr.get(0));
                    target_rwd.setReward_desc(str_arr.get(1));
                    target_rwd.setMag_gold(int_arr.get(0));
                    target_rwd.setMag_diamond(int_arr.get(1));
                }
            });
        } catch (Exception e) {
            Log.d("CUMON EDIT REWARD", e.toString());
        }
    }

    public static void deleteReward(Realm rm, final int id) {
        try {
            rm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Rewards target_rwd = getRewardById(realm, id);
                    target_rwd.deleteFromRealm();
                }
            });
        } catch (Exception e) {
            Log.d("CUMON DELETE REWARD", e.toString());
        }
    }

    public int getId() {
        return id;
    }

    public static int getNextId(Realm rm) {
        try {
            Number number = rm.where(Rewards.class).max("id");
            if (number != null) {
                return number.intValue() + 1;
            } else {
                return 1;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.d("CUMON REWARDS", "Cannot set the next id..."+e.toString());
            return 0;
        }
    }

    public String getReward_name() {
        return reward_name;
    }

    public void setReward_name(String reward_name) {
        this.reward_name = reward_name;
    }

    public String getReward_desc() {
        return reward_desc;
    }

    public void setReward_desc(String reward_desc) {
        this.reward_desc = reward_desc;
    }

    public Date getWant_day() {
        return want_day;
    }

    public void setWant_day(Date want_day) {
        this.want_day = want_day;
    }

    public Date getGet_day() {
        return get_day;
    }

    public void setGet_day(Date get_day) {
        this.get_day = get_day;
    }

    public String getPhoto_path() {
        return photo_path;
    }

    public void setPhoto_path(String photo_path) {
        this.photo_path = photo_path;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getMag_gold() {
        return mag_gold;
    }

    public void setMag_gold(int mag_gold) {
        this.mag_gold = mag_gold;
    }

    public int getMag_diamond() {
        return mag_diamond;
    }

    public void setMag_diamond(int mag_diamond) {
        this.mag_diamond = mag_diamond;
    }
}
