package com.example.pebblesappv2;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ChunFaiHung on 2017/2/12.
 */

public class ToDoItem {
    private long id;
    private String todoDesc;
    private String todoTime;
    private String todoDate;

    public ToDoItem(long id, String todoDesc, String todoTime, String todoDate) {
        this.id = id;
        this.todoDesc = todoDesc;
        this.todoTime = todoTime;
        this.todoDate = todoDate;
    }

    public ToDoItem() {
        this.id = 0;
        this.todoDate = "";
        this.todoDesc = "";
        this.todoTime = "";
    }

    // GETTERS
    public long getId() { return this.id; }
    public String getToDoDesc() {
        return this.todoDesc;
    }
    public String getToDoTime() {
        return this.todoTime;
    }
    public String getToDoDate() {
        return this.todoDate;
    }

    // SETTERS
    public void setID(long id) { this.id = id; }
    public void setToDoDesc(String setDesc) {
        this.todoDesc = setDesc;
    }
    public void setToDoTime(String setTime) {
        this.todoTime = setTime;
    }
    public void setToDoDate(String setDate) { this.todoDate = setDate; }

    public Date getDateFromString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date returnDate;
        try {
            returnDate = sdf.parse(this.todoDate);
            return returnDate;
        } catch (Exception e) {
            Log.d("ParseException", e.getMessage().toString());
            return null;
        }
    }
}
