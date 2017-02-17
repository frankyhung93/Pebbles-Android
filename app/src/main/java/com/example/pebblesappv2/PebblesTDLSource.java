package com.example.pebblesappv2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by ChunFaiHung on 2017/2/12.
 */

public class PebblesTDLSource {
    private SQLiteDatabase database;
    private PebbleSQLHelper dbHelper;
    private String[] allColumns = { PebbleSQLHelper.COLUMN_ID,
            PebbleSQLHelper.COLUMN_DESC,
            PebbleSQLHelper.COLUMN_TIME,
            PebbleSQLHelper.COLUMN_DATE,
            PebbleSQLHelper.COLUMN_LUPDATE_TIME};

    public PebblesTDLSource(Context context) {
        dbHelper = new PebbleSQLHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public ToDoItem CreateTDItem(String desc, String time, String date) {
        long milis = System.currentTimeMillis();
        ContentValues values = new ContentValues();
        values.put(PebbleSQLHelper.COLUMN_DATE, date);
        values.put(PebbleSQLHelper.COLUMN_TIME, time);
        values.put(PebbleSQLHelper.COLUMN_DESC, desc);
        values.put(PebbleSQLHelper.COLUMN_LUPDATE_TIME, milis);
        long insertID = database.insert(PebbleSQLHelper.TABLE_NAME, null, values);
        Cursor cursor = database.query(PebbleSQLHelper.TABLE_NAME, allColumns, PebbleSQLHelper.COLUMN_ID + " = " + insertID, null, null, null, null);
        cursor.moveToFirst();
        ToDoItem toDoItem = cursorToTDItem(cursor);
        cursor.close();
        return toDoItem;
    }

    public ArrayList<ToDoItem> getTDL() {
        ArrayList<ToDoItem> init_data = new ArrayList<ToDoItem>();
        Cursor cursor = database.query(PebbleSQLHelper.TABLE_NAME, allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ToDoItem tditem = cursorToTDItem(cursor);
            init_data.add(tditem);
            cursor.moveToNext();
        }
        cursor.close();
        return init_data;
    }

    private ToDoItem cursorToTDItem(Cursor cursor) {
        ToDoItem tdItem = new ToDoItem(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
        return tdItem;
    }

}
