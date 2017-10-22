package com.example.pebblesappv2;

import android.util.Log;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by ChunFaiHung on 2017/10/22.
 */

public class MagCurrency extends RealmObject {
    private static final String Gold = "Gold";
    private static final String Diamond = "Diamond";

    private int id;
    private int amount;
    private String name;

    public boolean initCurrency(Realm rm) {
        try {
            RealmResults<MagCurrency> rs = rm.where(MagCurrency.class).findAll();
            if (rs.size() == 0) {
                rm.beginTransaction();

                MagCurrency mc_gold = rm.createObject(MagCurrency.class);
                mc_gold.setId(1);
                mc_gold.setAmount(0);
                mc_gold.setName(Gold);
                MagCurrency mc_diamond = rm.createObject(MagCurrency.class);
                mc_diamond.setId(2);
                mc_diamond.setAmount(0);
                mc_diamond.setName(Diamond);

                rm.commitTransaction();
            }
            return true;
        } catch (Exception e) {
            Log.d("CUMON MAGCURRENCY", "Cannot init currency..."+e.toString());
            return false;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
