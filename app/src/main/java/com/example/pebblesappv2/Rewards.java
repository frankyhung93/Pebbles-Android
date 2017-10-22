package com.example.pebblesappv2;

import android.util.Log;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;
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
    private int price;
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
            Log.d("CUMON REWARDS", "Cannot set the next id..."+e.toString());
            return false;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
