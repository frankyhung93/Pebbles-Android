package com.example.pebblesappv2;

class RoutineItem {
    private long rt_id;
    private long rt_icon_id;
    private String rt_icon_name;
    private long rt_bg_color;
    private long rt_tx_color;

    RoutineItem(long rt_id, long rt_icon_id, String rt_icon_name, long rt_bg_color, long rt_tx_color) {
        this.rt_id = rt_id;
        this.rt_icon_id = rt_icon_id;
        this.rt_icon_name = rt_icon_name;
        this.rt_bg_color = rt_bg_color;
        this.rt_tx_color = rt_tx_color;
    }

    RoutineItem() {
        this.rt_id = 0;
        this.rt_icon_id = 0;
        this.rt_icon_name = "";
        this.rt_bg_color = 0;
        this.rt_tx_color = 0;
    }

    // GETTERS
    long getRtId() {
        return this.rt_id;
    }
    long getRtIconId() {
        return this.rt_icon_id;
    }
    String getRtIconName() {
        return this.rt_icon_name;
    }
    long getRtBgColor() {
        return this.rt_bg_color;
    }
    long getRtTxColor() {
        return this.rt_tx_color;
    }

    // SETTERS
    void setRtId(long rt_id) {
        this.rt_id = rt_id;
    }
    void setRtIconId(long rt_icon_id) {
        this.rt_icon_id = rt_icon_id;
    }
    void setRtIconName(String rt_icon_name) {
        this.rt_icon_name = rt_icon_name;
    }
    void setRtBgColor(long rt_bg_color) {
        this.rt_bg_color = rt_bg_color;
    }
    void setRtTxColor(long rt_tx_color) {
        this.rt_tx_color = rt_tx_color;
    }
}
