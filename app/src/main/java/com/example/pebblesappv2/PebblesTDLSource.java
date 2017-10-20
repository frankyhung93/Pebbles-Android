package com.example.pebblesappv2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

class PebblesTDLSource {
    private SQLiteDatabase database;
    private PebbleSQLHelper dbHelper;
    private String[] allColumns_tdl = { PebbleSQLHelper.COLUMN_ID_TDL,
            PebbleSQLHelper.COLUMN_DESC_TDL,
            PebbleSQLHelper.COLUMN_TIME_TDL,
            PebbleSQLHelper.COLUMN_DATE_TDL,
            PebbleSQLHelper.COLUMN_LUPDATE_TIME_TDL};

    PebblesTDLSource(Context context) {
        dbHelper = PebbleSQLHelper.getInstance(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    void close() {
        dbHelper.close();
    }

    ToDoItem CreateTDItem(String desc, String time, String date) {
        long milis = System.currentTimeMillis();
        ContentValues values = new ContentValues();
        values.put(PebbleSQLHelper.COLUMN_DATE_TDL, date);
        values.put(PebbleSQLHelper.COLUMN_TIME_TDL, time);
        values.put(PebbleSQLHelper.COLUMN_DESC_TDL, desc);
        values.put(PebbleSQLHelper.COLUMN_LUPDATE_TIME_TDL, milis);
        long insertID = database.insert(PebbleSQLHelper.TABLE_NAME_TDL, null, values);
        Cursor cursor = database.query(PebbleSQLHelper.TABLE_NAME_TDL, allColumns_tdl, PebbleSQLHelper.COLUMN_ID_TDL + " = " + insertID, null, null, null, null);
        cursor.moveToFirst();
        ToDoItem toDoItem = cursorToTDItem(cursor);
        cursor.close();
        return toDoItem;
    }

    ToDoItem UpdateTDItem(String desc, String time, String date, long org_id) {
        long milis = System.currentTimeMillis();
        ContentValues values = new ContentValues();
        values.put(PebbleSQLHelper.COLUMN_DATE_TDL, date);
        values.put(PebbleSQLHelper.COLUMN_TIME_TDL, time);
        values.put(PebbleSQLHelper.COLUMN_DESC_TDL, desc);
        values.put(PebbleSQLHelper.COLUMN_LUPDATE_TIME_TDL, milis);
        database.update(PebbleSQLHelper.TABLE_NAME_TDL, values, PebbleSQLHelper.COLUMN_ID_TDL + " = " + org_id, null);
        Cursor cursor = database.query(PebbleSQLHelper.TABLE_NAME_TDL, allColumns_tdl, PebbleSQLHelper.COLUMN_ID_TDL + " = " + org_id, null, null, null, null);
        cursor.moveToFirst();
        ToDoItem toDoItem = cursorToTDItem(cursor);
        cursor.close();
        return toDoItem;
    }

    void DeleteTDItem(long td_id) {
        database.delete(PebbleSQLHelper.TABLE_NAME_TDL, PebbleSQLHelper.COLUMN_ID_TDL + " = " + td_id, null);
    }

    ArrayList<ToDoItem> getTDL() {
        ArrayList<ToDoItem> init_data = new ArrayList<>();
        Cursor cursor = database.query(PebbleSQLHelper.TABLE_NAME_TDL, allColumns_tdl, null, null, null, null, null);
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
        return new ToDoItem(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
    }

}
