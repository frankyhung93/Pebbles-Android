package com.example.pebblesappv2;

import android.util.Log;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;
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
    private int cha_price;
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
            Log.d("CUMON CHALLENGES", "Cannot set the next id..."+e.toString());
            return false;
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

    public int getCha_price() {
        return cha_price;
    }

    public void setCha_price(int cha_price) {
        this.cha_price = cha_price;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
