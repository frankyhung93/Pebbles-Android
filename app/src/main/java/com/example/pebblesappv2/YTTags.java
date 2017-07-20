package com.example.pebblesappv2;

import io.realm.RealmObject;

public class YTTags extends RealmObject {
    private String tag_name;
    private int tag_order;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTag_name() {
        return tag_name;
    }

    public void setTag_name(String tag_name) {
        this.tag_name = tag_name;
    }

    public int getTag_order() {
        return tag_order;
    }

    public void setTag_order(int tag_order) {
        this.tag_order = tag_order;
    }
}
