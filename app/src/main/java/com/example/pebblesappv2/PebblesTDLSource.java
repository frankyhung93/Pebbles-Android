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
    private String[] allColumns_tdl = { PebbleSQLHelper.COLUMN_ID_TDL,
            PebbleSQLHelper.COLUMN_DESC_TDL,
            PebbleSQLHelper.COLUMN_TIME_TDL,
            PebbleSQLHelper.COLUMN_DATE_TDL,
            PebbleSQLHelper.COLUMN_LUPDATE_TIME_TDL};
    private String[] allColumns_routines = {
            PebbleSQLHelper.COLUMN_ID_ROUTINES,
            PebbleSQLHelper.COLUMN_ICON_ID_ROUTINES,
            PebbleSQLHelper.COLUMN_ICON_NAME_ROUTINES,
            PebbleSQLHelper.COLUMN_ICON_BG_COLOR_ROUTINES,
            PebbleSQLHelper.COLUMN_ICON_TX_COLOR_ROUTINES,
            PebbleSQLHelper.COLUMN_LUPDATE_TIME_ROUTINES
    };

    public PebblesTDLSource(Context context) {
        dbHelper = PebbleSQLHelper.getInstance(context);
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

    public ToDoItem UpdateTDItem(String desc, String time, String date, long org_id) {
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

    public void DeleteTDItem(long td_id) {
        database.delete(PebbleSQLHelper.TABLE_NAME_TDL, PebbleSQLHelper.COLUMN_ID_TDL + " = " + td_id, null);
    }

    public ArrayList<ToDoItem> getTDL() {
        ArrayList<ToDoItem> init_data = new ArrayList<ToDoItem>();
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
        ToDoItem tdItem = new ToDoItem(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
        return tdItem;
    }

    //////////////////////////////
    // Methods for RoutineItems //
    //////////////////////////////

    public RoutineItem CreateRoutineItem(long rt_icon_id, String rt_icon_name, long rt_bg_color, long rt_tx_color) {
        if (rt_icon_id == -1 || rt_bg_color == -1 || rt_tx_color == -1) {
            return null;
        }
        long milis = System.currentTimeMillis();
        ContentValues values = new ContentValues();
        values.put(PebbleSQLHelper.COLUMN_ICON_ID_ROUTINES, rt_icon_id);
        values.put(PebbleSQLHelper.COLUMN_ICON_NAME_ROUTINES, rt_icon_name);
        values.put(PebbleSQLHelper.COLUMN_ICON_BG_COLOR_ROUTINES, rt_bg_color);
        values.put(PebbleSQLHelper.COLUMN_ICON_TX_COLOR_ROUTINES, rt_tx_color);
        values.put(PebbleSQLHelper.COLUMN_LUPDATE_TIME_ROUTINES, milis);
        long insertID = database.insert(PebbleSQLHelper.TABLE_NAME_ROUTINES, null, values);
        Cursor cursor = database.query(PebbleSQLHelper.TABLE_NAME_ROUTINES, allColumns_routines, PebbleSQLHelper.COLUMN_ID_ROUTINES + " = " + insertID, null, null, null, null);
        cursor.moveToFirst();
        RoutineItem rtItem = cursorToRoutineItem(cursor);
        cursor.close();
        return rtItem;
    }

    public void DeleteRoutineItem(long rt_id) {
        database.delete(PebbleSQLHelper.TABLE_NAME_ROUTINES, PebbleSQLHelper.COLUMN_ID_ROUTINES + " = " + rt_id, null);
    }

    public ArrayList<RoutineItem> getRoutines() {
        ArrayList<RoutineItem> init_data = new ArrayList<RoutineItem>();
        Cursor cursor = database.query(PebbleSQLHelper.TABLE_NAME_ROUTINES, allColumns_routines, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            RoutineItem rtItem = cursorToRoutineItem(cursor);
            init_data.add(rtItem);
            cursor.moveToNext();
        }
        cursor.close();
        return init_data;
    }

    public ArrayList<RoutineItem> getRoutinesByIdList(String idList) {
        ArrayList<RoutineItem> init_data = new ArrayList<RoutineItem>();
        Cursor cursor = database.query(PebbleSQLHelper.TABLE_NAME_ROUTINES, allColumns_routines, "_id IN ("+idList+")", null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            RoutineItem rtItem = cursorToRoutineItem(cursor);
            init_data.add(rtItem);
            cursor.moveToNext();
        }
        cursor.close();
        return init_data;
    }

    private RoutineItem cursorToRoutineItem(Cursor cursor) {
        RoutineItem rtItem = new RoutineItem(cursor.getLong(0), cursor.getLong(1), cursor.getString(2), cursor.getLong(3), cursor.getLong(4));
        return rtItem;
    }

    public Integer getResIdFromRoutineId(Integer recordId) {
        Cursor cursor = database.query(PebbleSQLHelper.TABLE_NAME_ROUTINES, allColumns_routines, PebbleSQLHelper.COLUMN_ID_ROUTINES + " = " + recordId, null, null, null, null);
        cursor.moveToFirst();
        RoutineItem rtItem = cursorToRoutineItem(cursor);
        cursor.close();
        return (int)rtItem.getRtIconId();
    }

}
